package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserAccessesAdminEndpoint() throws Exception {

        mockMvc.perform(get("/auth/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccess() throws Exception {

        mockMvc.perform(get("/auth/admin"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/auth/admin"))
                .andExpect(status().isForbidden());
    }
}
