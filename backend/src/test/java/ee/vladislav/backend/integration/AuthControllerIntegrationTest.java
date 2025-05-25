package ee.vladislav.backend.integration;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.vladislav.backend.dto.AuthRequest;
import ee.vladislav.backend.dto.AuthResponse;
import ee.vladislav.backend.dto.RefreshTokenRequest;
import ee.vladislav.backend.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private String validEmail;
	private String validPassword;
	private String invalidEmail;
	private String invalidPassword;

	@BeforeEach
	void setUp() {
		validEmail = "user1@test.ee";
		validPassword = "password";
		invalidEmail = "nonexistent@test.ee";
		invalidPassword = "wrongpassword";
	}

	@Test
	void refreshToken_withExpiredToken_shouldReturnUnauthorized() throws Exception {
		AuthRequest loginRequest = new AuthRequest(validEmail, validPassword);
		String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequestJson))
				.andExpect(status().isOk())
				.andReturn();

		String loginResponseJson = loginResult.getResponse().getContentAsString();
		AuthResponse loginResponse = objectMapper.readValue(loginResponseJson, AuthResponse.class);
		String refreshToken = loginResponse.getRefreshToken();

		refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
			token.setExpiresAt(LocalDateTime.now().minusHours(1));
			refreshTokenRepository.save(token);
		});

		RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
		String refreshRequestJson = objectMapper.writeValueAsString(refreshRequest);

		mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(refreshRequestJson))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status", is(401)))
				.andExpect(jsonPath("$.message", is("Refresh token expired. Please sign in again.")));
	}

	@Test
	void login_withValidCredentials_shouldReturnTokens() throws Exception {
		AuthRequest request = new AuthRequest(validEmail, validPassword);
		String requestJson = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken", not(emptyString())))
				.andExpect(jsonPath("$.refreshToken", not(emptyString())))
				.andExpect(jsonPath("$.accessTokenExpiresInSeconds", greaterThan(0)))
				.andExpect(jsonPath("$.refreshTokenExpiresInSeconds", greaterThan(0)))
				.andExpect(jsonPath("$.email", is(validEmail)))
				.andExpect(jsonPath("$.firstName", is("John")))
				.andExpect(jsonPath("$.lastName", is("Doe")));
	}

	@Test
	void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
		AuthRequest request = new AuthRequest(validEmail, invalidPassword);
		String requestJson = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message", is("No account with matching credentials found.")));
	}

	@Test
	void login_withNonexistentUser_shouldReturnUnauthorized() throws Exception {
		AuthRequest request = new AuthRequest(invalidEmail, validPassword);
		String requestJson = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message", is("No account with matching credentials found.")));
	}

	@Test
	void login_withInvalidRequest_shouldReturnBadRequest() throws Exception {
		AuthRequest request = new AuthRequest("invalid-email", "");
		String requestJson = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status", is(400)))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.email", notNullValue()))
				.andExpect(jsonPath("$.errors.password", notNullValue()));
	}

	@Test
	void refreshToken_withValidToken_shouldReturnNewTokens() throws Exception {
		AuthRequest loginRequest = new AuthRequest(validEmail, validPassword);
		String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequestJson))
				.andExpect(status().isOk())
				.andReturn();

		String loginResponseJson = loginResult.getResponse().getContentAsString();
		AuthResponse loginResponse = objectMapper.readValue(loginResponseJson, AuthResponse.class);
		String refreshToken = loginResponse.getRefreshToken();

		RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);
		String refreshRequestJson = objectMapper.writeValueAsString(refreshRequest);

		MvcResult firstRefreshResult = mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(refreshRequestJson))
				.andExpect(status().isOk())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken", not(emptyString())))
				.andExpect(jsonPath("$.refreshToken", not(emptyString())))
				.andExpect(jsonPath("$.accessTokenExpiresInSeconds", greaterThan(0)))
				.andExpect(jsonPath("$.refreshTokenExpiresInSeconds", greaterThan(0)))
				.andExpect(jsonPath("$.email", is(validEmail)))
				.andExpect(jsonPath("$.firstName", is("John")))
				.andExpect(jsonPath("$.lastName", is("Doe")))
				.andReturn();

		String firstRefreshResponseJson = firstRefreshResult.getResponse().getContentAsString();
		AuthResponse firstRefreshResponse = objectMapper.readValue(firstRefreshResponseJson, AuthResponse.class);
		String newRefreshToken = firstRefreshResponse.getRefreshToken();

		RefreshTokenRequest secondRefreshRequest = new RefreshTokenRequest(newRefreshToken);
		String secondRefreshRequestJson = objectMapper.writeValueAsString(secondRefreshRequest);

		MvcResult secondRefreshResult = mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(secondRefreshRequestJson))  // Use NEW token here
				.andExpect(status().isOk())
				.andReturn();

		String secondRefreshResponseJson = secondRefreshResult.getResponse().getContentAsString();
		AuthResponse secondRefreshResponse = objectMapper.readValue(secondRefreshResponseJson, AuthResponse.class);

		assertNotEquals(refreshToken, secondRefreshResponse.getRefreshToken(),
				"New refresh token should be different from the original one");
	}

	@Test
	void refreshToken_withInvalidToken_shouldReturnUnauthorized() throws Exception {
		RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
		String requestJson = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status", is(401)))
				.andExpect(jsonPath("$.message", is("Invalid refresh token.")));
	}

	@Test
	void logout_withAuthenticatedUser_shouldSucceed() throws Exception {
		AuthRequest loginRequest = new AuthRequest(validEmail, validPassword);
		String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

		MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginRequestJson))
				.andExpect(status().isOk())
				.andReturn();

		String loginResponseJson = loginResult.getResponse().getContentAsString();
		AuthResponse loginResponse = objectMapper.readValue(loginResponseJson, AuthResponse.class);
		String accessToken = loginResponse.getAccessToken();

		int tokenCountBefore = refreshTokenRepository.findAll().size();
		assertTrue(tokenCountBefore > 0, "Refresh token should exist in DB after login");

		mockMvc.perform(post("/api/auth/logout")
						.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk());

		int tokenCountAfter = refreshTokenRepository.findAll().size();
		assertEquals(tokenCountBefore - 1, tokenCountAfter,
				"Refresh token should be removed from DB after logout");
	}

	@Test
	void logout_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
		mockMvc.perform(post("/api/auth/logout"))
				.andExpect(status().isUnauthorized());
	}
}