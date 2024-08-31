package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody ClienteDto clienteDto) {
        Cliente novoCliente = clienteService.cadastrarCliente(clienteDto);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obterClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.obterClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodosClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Cliente> editarCliente(@PathVariable Long id, @RequestBody ClienteDto clienteDto) {
        Cliente clienteAtualizado = clienteService.editarCliente(id, clienteDto);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
