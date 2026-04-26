package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.ExpiredJwtException;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should continue filter chain when Authorization header is missing")
    void shouldContinueChainWhenAuthorizationHeaderIsMissing() throws Exception {

        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Act
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(200, response.getStatus());
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("Should continue filter chain when Authorization header does not start with Bearer")
    void shouldContinueChainWhenAuthorizationHeaderIsNotBearer() throws Exception {

        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic sometoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Act
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(200, response.getStatus());
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("Should set authentication in SecurityContext when Bearer tokenis valid")
    void shouldAuthenticateWhenBearerTokenIsValid() throws Exception {

        // Arrange
        String token = "VALID_TOKEN";
        String email = "email@email.com";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extractEmail(token)).thenReturn(email);

        UserDetails userDetails = new User(email, "encrypted", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(email, authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertEquals(200, response.getStatus());

        verify(jwtService).extractEmail(token);
        verify(userDetailsService).loadUserByUsername(email);
    }

    @Test
    @DisplayName("Should return 401 and 'Token expirado' when JWT is expired")
    void shouldReturn401WhenJwtIsExpired() throws Exception {
        // Arrange
        String token = "EXPIRED_TOKEN";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = spy(new MockFilterChain());

        when(jwtService.extractEmail(token)).thenThrow(new ExpiredJwtException(null, null, "expired"));

        // Act
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // Assert
        assertEquals(401, response.getStatus());
        String body = response.getContentAsString(StandardCharsets.UTF_8);
        assertTrue(body.contains("Token expirado"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should return 401 and 'Token inválido' when JWT is invalid")
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        // Arrange
        String token = "INVALID_TOKEN";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = spy(new MockFilterChain());

        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("bad token"));

        // Act
        jwtAuthenticationFilter.doFilter(request, response, chain);

        // Assert
        assertEquals(401, response.getStatus());
        String body = response.getContentAsString(StandardCharsets.UTF_8);
        assertTrue(body.contains("Token invalido"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(chain, never()).doFilter(any(), any());
    }
}
