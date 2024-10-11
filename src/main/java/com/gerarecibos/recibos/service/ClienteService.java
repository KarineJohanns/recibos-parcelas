package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.DTO.ClienteDto;
import com.gerarecibos.recibos.DTO.login.AlterarSenhaPrimeiroAcessoDTO;
import com.gerarecibos.recibos.DTO.login.AlterarSenhaRequestDTO;
import com.gerarecibos.recibos.Exceptions.ClienteVinculadoException;
import com.gerarecibos.recibos.Exceptions.ResourceNotFoundException;
import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.model.CustomUserDetails;
import com.gerarecibos.recibos.repository.ClienteRepository;
import com.gerarecibos.recibos.repository.ParcelaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.gerarecibos.recibos.Utils.SenhaUtil;
import org.springframework.web.client.RestTemplate;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> cadastrarCliente(ClienteDto clienteDto) {
        // Verificar se já existe um cliente com o mesmo CPF
        Optional<Cliente> clienteExistente = clienteRepository.findByClienteCpf(clienteDto.getClienteCpf());

        if (clienteExistente.isPresent()) {
            // Se já existir, lança uma exceção
            throw new ClienteVinculadoException("Já existe um cliente cadastrado com esse CPF.");
        }

        // Gerar a senha temporária e criptografá-la
        String senhaTemporaria = SenhaUtil.gerarSenhaTemporaria();
        String senhaCriptografada = passwordEncoder.encode(senhaTemporaria);

        // Caso contrário, cria e salva o novo cliente
        Cliente cliente = new Cliente();
        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());
        cliente.setSenha(senhaCriptografada); // Atribuir a senha criptografada

        clienteRepository.save(cliente);

        // Retorna uma mensagem de sucesso
        return ResponseEntity.ok("Cliente cadastrado com sucesso. Senha temporária: " + senhaTemporaria);
    }

    public Cliente obterClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public List<Cliente> obterClientePorNome(String nome) {
        return clienteRepository.buscarPorNomeParcial(nome);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Cliente editarCliente(Long id, ClienteDto clienteDto) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteVinculadoException("Cliente não encontrado."));

        // Verifica se o CPF é diferente do CPF atual e já está cadastrado
        if (!cliente.getClienteCpf().equals(clienteDto.getClienteCpf()) &&
                clienteRepository.existsByClienteCpf(clienteDto.getClienteCpf())) {
            throw new ClienteVinculadoException("CPF já cadastrado.");
        }

        cliente.setClienteNome(clienteDto.getClienteNome());
        cliente.setClienteCpf(clienteDto.getClienteCpf());
        cliente.setClienteEndereco(clienteDto.getClienteEndereco());
        cliente.setClienteTelefone(clienteDto.getClienteTelefone());

        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) {
        Cliente cliente = obterClientePorId(id);

        // Verificar se o cliente está vinculado a alguma parcela
        boolean isClienteVinculado = parcelaRepository.existsByCliente(cliente);

        if (isClienteVinculado) {
            throw new ClienteVinculadoException("Não é possível excluir o cliente, pois ele está vinculado a uma ou mais parcelas.");
        }

        clienteRepository.delete(cliente);
    }

    public ResponseEntity<String> atualizarSenha(Long clienteId, String senhaAtual, String novaSenha) {
        // Buscar o cliente pelo ID
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        // Verificar se a senha atual informada corresponde à senha armazenada
        if (!passwordEncoder.matches(senhaAtual, cliente.getSenha())) {
            return ResponseEntity.badRequest().body("Senha atual informada está incorreta.");
        }

        // Validar a nova senha (ex: força da senha, tamanho mínimo, etc.)
        if (!validarNovaSenha(novaSenha)) {
            return ResponseEntity.badRequest().body("A nova senha não atende aos requisitos mínimos de segurança.");
        }

        // Atualizar a senha criptografada
        cliente.setSenha(passwordEncoder.encode(novaSenha));

        // Se for o primeiro acesso, alterar o estado
        if (cliente.isPrimeiroAcesso()) {
            cliente.setPrimeiroAcesso(false);
        }

        // Salvar as alterações
        clienteRepository.save(cliente);

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }

    public ResponseEntity<String> alterarSenhaPrimeiroAcesso(AlterarSenhaPrimeiroAcessoDTO request) {
        // Buscar cliente pelo CPF
        Cliente cliente = clienteRepository.findByClienteCpf(request.getCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        // Verificar se é o primeiro acesso
        if (!cliente.isPrimeiroAcesso()) {
            return ResponseEntity.badRequest().body("Este cliente não é elegível para primeiro acesso.");
        }

        // Verificar se a senha atual informada corresponde à senha armazenada
        if (!passwordEncoder.matches(request.getSenhaAtual(), cliente.getSenha())) {
            return ResponseEntity.badRequest().body("Senha atual informada está incorreta.");
        }

        // Validar a nova senha
        if (!validarNovaSenha(request.getNovaSenha())) {
            return ResponseEntity.badRequest().body("A nova senha não atende aos requisitos mínimos de segurança.");
        }

        // Atualizar a senha criptografada
        cliente.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        cliente.setPrimeiroAcesso(false); // Marcar como não sendo o primeiro acesso

        // Salvar as alterações
        clienteRepository.save(cliente);

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }

    private boolean validarNovaSenha(String senha) {
        // Validação simples: por exemplo, senha deve ter pelo menos 8 caracteres
        return senha != null && senha.length() >= 8;
    }

    private Long getClienteIdFromUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getClienteId(); // Retorna o ID do cliente
        }
        throw new RuntimeException("Cliente não autenticado.");
    }


    public String recuperarSenha(String cpf) {
        Cliente cliente = clienteRepository.findByClienteCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        // Gerar e criptografar a senha temporária
        String senhaTemporaria = SenhaUtil.gerarSenhaTemporaria();
        String senhaCriptografada = passwordEncoder.encode(senhaTemporaria);
        cliente.setSenha(senhaCriptografada); // Atualiza a senha com a versão criptografada
        cliente.setPrimeiroAcesso(true); // Força o cliente a trocar a senha no próximo login

        // Salva o cliente com a nova senha temporária
        clienteRepository.save(cliente);

        // Enviar a senha temporária via WhatsApp
        String mensagem = "Sua senha temporária é: " + senhaTemporaria;
        String phoneNumber = cliente.getClienteTelefone(); // supondo que você tenha o número de telefone no cliente

        enviarMensagemWhatsApp(phoneNumber, mensagem);

        // Retorna a senha temporária na resposta (somente para exibição, não será armazenada)
        return "Senha redefinida com sucesso. Senha temporária: " + senhaTemporaria;
    }

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl; // URL será lida da variável de ambiente

    private void enviarMensagemWhatsApp(String phoneNumber, String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Cria o corpo da requisição
        String jsonBody = String.format("{\"phoneNumber\":\"%s\", \"message\":\"%s\"}", phoneNumber, message);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // Faz a chamada ao endpoint do Node.js
        ResponseEntity<String> response = restTemplate.exchange(whatsappApiUrl, HttpMethod.POST, entity, String.class);

        // Você pode verificar a resposta se necessário
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Erro ao enviar a mensagem pelo WhatsApp.");
        }
    }
}
