package br.edu.ifpe.pdsc.investCalc.investCalc.config;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityAuthorizationIntegrationTest.TestProtectedController.class)
public class SecurityAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    @RestController
    static class TestProtectedController {

        @GetMapping("/protected")
        public String protectedEndpoint() {
            return "Ok";
        }
    }

    @Test
    @DisplayName("GET /protected should return 401 when Authorization header is missing")
    void shouldReturn401WhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /protected should return 200 when Bearer token is valid")
    void shouldReturn200WhenBearerTokenIsValid() throws Exception {

        // ARRANGE
        String token = "VALID_TOKEN";
        String email = "user@email.com";

        when(jwtService.extractEmail(token)).thenReturn(email);

        UserDetails userDetails = User
                .withUsername(email)
                .password("encrypted")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // ACT & ASSERT
        mockMvc.perform(get("/protected")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }
}
