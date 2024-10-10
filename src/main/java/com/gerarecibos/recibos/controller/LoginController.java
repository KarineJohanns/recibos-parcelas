package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.login.*;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.Utils.JwtResponse;
import com.gerarecibos.recibos.Utils.JwtUtil;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.CustomUserDetails;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.service.ClienteService;
import com.gerarecibos.recibos.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService; // Para carregar UserDetails

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            // Autenticar o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCpf(), request.getSenha())
            );

            // Obtenha o UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("Autenticado: " + userDetails.getUsername());

            // Busque o cliente pelo CPF (assumindo que CPF é o campo de login)
            Cliente cliente = clienteRepository.findByClienteCpf(request.getCpf())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

            // Gere o token JWT
            String jwt = jwtUtil.generateToken(userDetails);

            // Verifique se é o primeiro acesso
            if (cliente.isPrimeiroAcesso()) {
                // Retorne a resposta indicando que é o primeiro acesso
                LoginResponseDTO response = new LoginResponseDTO();
                response.setPrimeiroAcesso(true);
                response.setSenhaTemporaria(request.getSenha()); // Enviar a senha temporária
                response.setToken(jwt); // Enviar o token, caso o frontend precise dele

                return ResponseEntity.ok(response);
            }

            // Caso não seja o primeiro acesso, retornar o token normalmente
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (BadCredentialsException e) {
            // Captura a exceção de credenciais incorretas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta.");
        } catch (ResourceNotFoundException e) {
            // Captura a exceção de cliente não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        } catch (Exception e) {
            e.printStackTrace(); // Adicione o stack trace para entender onde está falhando
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no servidor.");
        }
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaRequestDTO request) {
        Long clienteId = request.getClienteId(); // Obtém o ID do cliente do request
        return clienteService.atualizarSenha(clienteId, request.getSenhaAtual(), request.getNovaSenha());
    }

    private Long getClienteIdFromUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getClienteId(); // Retorna o ID do cliente
        }
        throw new RuntimeException("Cliente não autenticado.");
    }

    @PutMapping("/alterar-senha-primeiro-acesso")
    public ResponseEntity<?> alterarSenhaPrimeiroAcesso(@RequestBody AlterarSenhaPrimeiroAcessoDTO request) {
        // Chamar o método para alterar a senha no primeiro acesso
        return clienteService.alterarSenhaPrimeiroAcesso(request);
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@RequestBody RecuperarSenhaRequestDTO request) {
        try {
            String resultado = clienteService.recuperarSenha(request.getCpf());
            return ResponseEntity.ok(Map.of("mensagem", resultado)); // Retorna um JSON com a mensagem de sucesso
        } catch (ResourceNotFoundException ex) {
            // Retorna 404 com uma mensagem clara ao usuário
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensagem", "Cliente não encontrado. Por favor, verifique o CPF informado.")); // Mensagem amigável
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Ocorreu um erro ao processar sua solicitação. Tente novamente mais tarde."));
        }
    }

}
