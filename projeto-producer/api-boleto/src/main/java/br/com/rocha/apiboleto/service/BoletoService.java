package br.com.rocha.apiboleto.service;

import br.com.rocha.apiboleto.controller.exception.ApplicationException;
import br.com.rocha.apiboleto.controller.exception.NotFoundException;
import br.com.rocha.apiboleto.dto.BoletoDTO;
import br.com.rocha.apiboleto.entity.BoletoEntity;
import br.com.rocha.apiboleto.entity.enums.SituacaoBoleto;
import br.com.rocha.apiboleto.mapper.BoletoMapper;
import br.com.rocha.apiboleto.repository.BoletoRepository;
import br.com.rocha.apiboleto.service.kafka.BoletoProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BoletoService {

    private final BoletoRepository boletoRepository;
    private final BoletoProducer boletoProducer;

    public BoletoService(BoletoRepository boletoRepository, BoletoProducer boletoProducer) {
        this.boletoRepository = boletoRepository;
        this.boletoProducer = boletoProducer;
    }

    public BoletoDTO salvar(String codigoBarras) {
        var boletoOptional = boletoRepository.findByCodigoBarras(codigoBarras);
        if (boletoOptional.isPresent()) {
            throw new ApplicationException("Já existe uma solicitação de pagamento para esse boleto");
        }

        var boletoEntity = BoletoEntity.builder()
                .codigoBarras(codigoBarras)
                .situacaoBoleto(SituacaoBoleto.INICIALIZADO)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        boletoRepository.save(boletoEntity);
        boletoProducer.enviarMensagem(BoletoMapper.toAvro(boletoEntity));
        return BoletoMapper.toDTO(boletoEntity);
    }

    public BoletoDTO buscarBoletoPorCodigoBarras(String codigoBarras) {
        return BoletoMapper.toDTO(recuperaBoleto(codigoBarras));
    }

    private BoletoEntity recuperaBoleto(String codigoBarras) {
        return boletoRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new NotFoundException("Boleto não encontrado"));
    }

    public void atualizar(BoletoEntity boleto) {
        var boletoAtual = recuperaBoleto(boleto.getCodigoBarras());

        boletoAtual.setSituacaoBoleto(boleto.getSituacaoBoleto());
        boletoAtual.setDataAtualizacao(LocalDateTime.now());
        boletoRepository.save(boletoAtual);
    }


}
