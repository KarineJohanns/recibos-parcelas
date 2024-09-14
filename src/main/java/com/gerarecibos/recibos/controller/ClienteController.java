package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.ApiResponseDTO;
import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.Exceptions.ClienteVinculadoException;
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
    public ResponseEntity<ApiResponseDTO> cadastrarCliente(@RequestBody ClienteDto clienteDto) {
        try {
            return ResponseEntity.ok(new ApiResponseDTO(clienteService.cadastrarCliente(clienteDto).getBody(), true));
        } catch (ClienteVinculadoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(e.getMessage(), false));
        }
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

    @GetMapping("/por-nome")
    public ResponseEntity<List<Cliente>> obterClientePorNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.obterClientePorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> editarCliente(@PathVariable Long id, @RequestBody ClienteDto clienteDto) {
        try {
            Cliente clienteAtualizado = clienteService.editarCliente(id, clienteDto);
            ApiResponseDTO response = new ApiResponseDTO("Cliente editado com sucesso.", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(e.getMessage(), false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.ok(new ApiResponseDTO("Cliente excluído com sucesso.", true));
        } catch (ClienteVinculadoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDTO("Erro ao excluir cliente.", false));
        }
    }
}
