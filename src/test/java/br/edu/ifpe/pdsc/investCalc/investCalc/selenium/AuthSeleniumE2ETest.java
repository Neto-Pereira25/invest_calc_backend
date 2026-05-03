package br.edu.ifpe.pdsc.investCalc.investCalc.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthSeleniumE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1280,800");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        String chromeBinary = System.getenv("CHROME_BIN");
        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("E2E Selenium - deve cadastrar, logar e acessar /me com token JWT")
    void shouldRegisterLoginAndAccessMeWithSelenium() {
        String email = "selenium." + UUID.randomUUID() + "@email.com";
        String password = "12345678";

        ApiResult register = postJson("/api/v1/auth/register", Map.of(
                "name", "Maria Selenium",
                "email", email,
                "password", password));

        assertThat(register.status()).isEqualTo(200);
        assertThat(register.bodyAsMap().get("message")).isEqualTo("Usuário cadastrado com sucesso");

        ApiResult login = postJson("/api/v1/auth/login", Map.of(
                "email", email,
                "password", password));

        assertThat(login.status()).isEqualTo(200);
        assertThat(login.bodyAsMap().get("message")).isEqualTo("Login realizado com sucesso");

        Map<String, Object> data = asMap(login.bodyAsMap().get("data"));
        String token = String.valueOf(data.get("token"));
        String refreshToken = String.valueOf(data.get("refreshToken"));

        assertThat(token).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        ApiResult me = getJson("/api/v1/auth/me", token);

        assertThat(me.status()).isEqualTo(200);
        assertThat(me.body()).isEqualTo(email);
    }

    @Test
    @DisplayName("E2E Selenium - deve rejeitar login com senha inválida")
    void shouldRejectLoginWithWrongPassword() {
        String email = "selenium." + UUID.randomUUID() + "@email.com";

        ApiResult register = postJson("/api/v1/auth/register", Map.of(
                "name", "João Selenium",
                "email", email,
                "password", "12345678"));

        assertThat(register.status()).isEqualTo(200);

        ApiResult login = postJson("/api/v1/auth/login", Map.of(
                "email", email,
                "password", "senhaErrada"));

        assertThat(login.status()).isEqualTo(400);
        assertThat(login.bodyAsMap().get("message")).isEqualTo("Senha invalida");
    }

    private ApiResult postJson(String path, Map<String, Object> payload) {
        return fetch("POST", path, payload, null);
    }

    private ApiResult getJson(String path, String bearerToken) {
        return fetch("GET", path, null, bearerToken);
    }

    private ApiResult fetch(String method, String path, Map<String, Object> payload, String bearerToken) {
        // Abre uma URL da própria aplicação para o fetch rodar na mesma origem do backend.
        driver.get(baseUrl + "/api/v1/auth/public");

        String script = """
                const method = arguments[0];
                const url = arguments[1];
                const payload = arguments[2];
                const bearerToken = arguments[3];
                const done = arguments[arguments.length - 1];

                const headers = { 'Content-Type': 'application/json' };
                if (bearerToken) {
                    headers['Authorization'] = 'Bearer ' + bearerToken;
                }

                fetch(url, {
                    method: method,
                    headers: headers,
                    body: payload ? JSON.stringify(payload) : undefined
                })
                    .then(async response => {
                        const text = await response.text();
                        let body = text;
                        try {
                            body = JSON.parse(text);
                        } catch (e) {
                            // Respostas text/plain, como /api/v1/auth/me, continuam como texto.
                        }
                        done({ status: response.status, body: body });
                    })
                    .catch(error => done({ status: 0, body: error.message }));
                """;

        Object result = ((JavascriptExecutor) driver)
                .executeAsyncScript(script, method, baseUrl + path, payload, bearerToken);

        Map<String, Object> resultMap = asMap(result);
        int status = ((Number) resultMap.get("status")).intValue();
        Object body = resultMap.get("body");

        return new ApiResult(status, body);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        return (Map<String, Object>) value;
    }

    private record ApiResult(int status, Object body) {
        Map<String, Object> bodyAsMap() {
            return asMap(body);
        }
    }
}