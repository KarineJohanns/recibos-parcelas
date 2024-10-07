package com.gerarecibos.recibos.controller;

import com.gerarecibos.recibos.DTO.login.AlterarSenhaRequestDTO;
import com.gerarecibos.recibos.DTO.login.LoginRequestDTO;
import com.gerarecibos.recibos.DTO.login.LoginResponseDTO;
import com.gerarecibos.recibos.DTO.login.ResetSenhaRequestDTO;
import com.gerarecibos.recibos.Utils.JwtResponse;
import com.gerarecibos.recibos.Utils.JwtUtil;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.service.ClienteService;
import com.gerarecibos.recibos.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
                    new UsernamePasswordAuthenticationToken(request.getCpf(), request.getSenha()));

            // Obtenha o UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("Autenticado: " + userDetails.getUsername());

            // Gere o token JWT
            String jwt = jwtUtil.generateToken(userDetails);

            // Retorne o token
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (Exception e) {
            e.printStackTrace(); // Adicione o stack trace para entender onde está falhando
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        }
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(@RequestBody AlterarSenhaRequestDTO request) {
        // Chamar o método do ClienteService para atualizar a senha
        return clienteService.atualizarSenha(request.getClienteId(), request.getNovaSenha());
    }

    @PutMapping("/resetar-senha")
    public ResponseEntity<?> resetarSenha(@RequestBody ResetSenhaRequestDTO request) {
        return clienteService.resetarSenha(request.getClienteId());
    }

}
