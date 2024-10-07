package com.gerarecibos.recibos.service;

import com.gerarecibos.recibos.model.Cliente;
import com.gerarecibos.recibos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Optional<Cliente> clienteOpt = clienteRepository.findByClienteCpf(cpf);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return new org.springframework.security.core.userdetails.User(cliente.getClienteCpf(), cliente.getSenha(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado com CPF: " + cpf);
        }
    }
}
