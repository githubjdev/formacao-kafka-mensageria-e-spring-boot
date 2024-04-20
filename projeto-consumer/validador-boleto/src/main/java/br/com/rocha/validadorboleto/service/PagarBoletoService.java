package br.com.rocha.validadorboleto.service;

import br.com.rocha.validadorboleto.entity.BoletoEntity;
import br.com.rocha.validadorboleto.entity.enums.SituacaoBoleto;
import br.com.rocha.validadorboleto.mapper.BoletoMapper;
import br.com.rocha.validadorboleto.repository.BoletoRepository;
import br.com.rocha.validadorboleto.service.kafka.NotificacaoProducer;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PagarBoletoService {

    private final BoletoRepository boletoRepository;
    private final NotificacaoProducer notificacaoProducer;

    public PagarBoletoService(BoletoRepository boletoRepository, NotificacaoProducer notificacaoProducer) {
        this.boletoRepository = boletoRepository;
        this.notificacaoProducer = notificacaoProducer;
    }

    @SneakyThrows
    public void pagar(BoletoEntity boleto) {
        Thread.sleep(10000);
        String codigoBarrasNumeros = boleto.getCodigoBarras().replaceAll("[^0-9]", "");
        if (codigoBarrasNumeros.length() > 47) {
            complementarBoletoErro(boleto);
        } else {
            complementarBoletoSuccesso(boleto);
        }

        boletoRepository.save(boleto);
        notificacaoProducer.enviarMensagem(BoletoMapper.toAvro(boleto));
    }

    private void complementarBoletoErro(BoletoEntity boleto) {
        boleto.setDataAtualizacao(LocalDateTime.now());
        boleto.setSituacaoBoleto(SituacaoBoleto.ERRO_PAGAMENTO);
    }

    private void complementarBoletoSuccesso(BoletoEntity boleto) {
        boleto.setDataAtualizacao(LocalDateTime.now());
        boleto.setSituacaoBoleto(SituacaoBoleto.PAGO);
    }
}
