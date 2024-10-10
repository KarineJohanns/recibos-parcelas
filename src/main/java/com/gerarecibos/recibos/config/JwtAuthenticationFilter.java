package com.gerarecibos.recibos.config;

import com.gerarecibos.recibos.Utils.JwtUtil;
import com.gerarecibos.recibos.service.CustomUserDetailsService;
import com.gerarecibos.recibos.service.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer "
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Obtenha o UserDetails baseado no username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verifique se o token é válido
            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("Token JWT válido para o usuário: {}"+ username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Defina o usuário autenticado no contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                System.out.println("Token JWT inválido para o usuário: {}"+ username);
            }
        }

        filterChain.doFilter(request, response);
    }

    private static final Set<String> EXCLUDED_PATHS = new HashSet<>();

    static {
        EXCLUDED_PATHS.add("/api/login");
        EXCLUDED_PATHS.add("/api/alterar-senha");
        // Adicione mais rotas que não devem passar pelo filtro
        EXCLUDED_PATHS.add("/api/alterar-senha-primeiro-acesso");
        EXCLUDED_PATHS.add("/api/recuperar-senha");
        // Adicione mais rotas conforme necessário
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Verifica se o caminho está no conjunto de rotas excluídas
        return EXCLUDED_PATHS.contains(path);
    }
}
