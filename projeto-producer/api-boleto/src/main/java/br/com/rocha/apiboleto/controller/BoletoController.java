package br.com.rocha.apiboleto.controller;

import br.com.rocha.apiboleto.dto.BoletoDTO;
import br.com.rocha.apiboleto.dto.BoletoRequestDTO;
import br.com.rocha.apiboleto.service.BoletoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boleto")
public class BoletoController {
    private final BoletoService boletoService;

    public BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }

    @GetMapping("/{codigoBarras}")
    public ResponseEntity<BoletoDTO> buscarBoletoCodigoBarras(@PathVariable("codigoBarras") String codigoBarras) {
        var boletoDTO = boletoService.buscarBoletoPorCodigoBarras(codigoBarras);
        return ResponseEntity.ok(boletoDTO);
    }

    @PostMapping
    public ResponseEntity<BoletoDTO> salvar(@Valid @RequestBody BoletoRequestDTO boletoRequestDTO) {
        var boleto = boletoService.salvar(boletoRequestDTO.getCodigoBarras());
        return new ResponseEntity<>(boleto, HttpStatus.CREATED);
    }
}
