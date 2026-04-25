package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 🔹 Se não tem token, segue fluxo normal
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔹 Extrai token
        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            // Aqui ainda não usamos UserDetails (simplificado)
            if (email != null) {

                var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        null);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            // Token inválido → ignora (ou pode bloquear depois)
        }

        filterChain.doFilter(request, response);
    }
}
