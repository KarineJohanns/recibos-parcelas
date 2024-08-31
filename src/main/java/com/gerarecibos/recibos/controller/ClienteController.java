package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody ClienteDto clienteDto) {
        Cliente cliente = clienteService.cadastrarCliente(clienteDto);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable Long clienteId) {
        Cliente cliente = clienteService.obterClientePorId(clienteId);
        return ResponseEntity.ok(cliente);
    }
}