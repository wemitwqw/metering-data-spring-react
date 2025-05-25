package ee.vladislav.backend.unit.service;

import ee.vladislav.backend.dao.Customer;
import ee.vladislav.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	private final long JWT_EXPIRATION = 86400000;

	private Customer testCustomer;
	private String validToken;

	@BeforeEach
	void setUp() {
		String SECRET_KEY = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdGVzdGluZy1wdXJwb3Nlcy1vbmx5LW5vdC1mb3ItcHJvZHVjdGlvbi11c2U=";
		ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
		ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", JWT_EXPIRATION);

		testCustomer = new Customer();
		testCustomer.setEmail("test@example.com");
		testCustomer.setPassword("password");
		testCustomer.setFirstName("John");
		testCustomer.setLastName("Doe");
		testCustomer.setEnabled(true);

		validToken = jwtService.generateToken(testCustomer);
	}

	@Test
	void generateToken_WithUserDetails_Success() {
		String token = jwtService.generateToken(testCustomer);

		assertNotNull(token);
		assertFalse(token.isEmpty());

		String extractedUsername = jwtService.extractUsername(token);
		assertEquals(testCustomer.getUsername(), extractedUsername);
	}

	@Test
	void generateToken_WithExtraClaims_Success() {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", "USER");
		extraClaims.put("department", "IT");

		String token = jwtService.generateToken(extraClaims, testCustomer);

		assertNotNull(token);
		assertFalse(token.isEmpty());

		Claims claims = jwtService.extractAllClaims(token);
		assertEquals("USER", claims.get("role"));
		assertEquals("IT", claims.get("department"));
	}

	@Test
	void extractUsername_ValidToken_ReturnsUsername() {
		String username = jwtService.extractUsername(validToken);
		assertEquals(testCustomer.getUsername(), username);
	}

	@Test
	void extractClaim_ValidToken_ReturnsClaim() {
		String subject = jwtService.extractClaim(validToken, Claims::getSubject);
		Date expiration = jwtService.extractClaim(validToken, Claims::getExpiration);

		assertEquals(testCustomer.getUsername(), subject);
		assertNotNull(expiration);
		assertTrue(expiration.after(new Date()));
	}

	@Test
	void isTokenValid_ValidTokenAndMatchingUser_ReturnsTrue() {
		boolean isValid = jwtService.isTokenValid(validToken, testCustomer);
		assertTrue(isValid);
	}

	@Test
	void isTokenValid_ValidTokenButDifferentUser_ReturnsFalse() {
		Customer differentUser = new Customer();
		differentUser.setEmail("different@example.com");

		boolean isValid = jwtService.isTokenValid(validToken, differentUser);

		assertFalse(isValid);
	}

	@Test
	void isTokenValid_ExpiredToken_ReturnsFalse() {
		ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", -1000); // Negative expiration
		String expiredToken = jwtService.generateToken(testCustomer);

		ReflectionTestUtils.setField(jwtService, "JWT_EXPIRATION", JWT_EXPIRATION);

		boolean isValid = jwtService.isTokenValid(expiredToken, testCustomer);

		assertFalse(isValid);
	}

	@Test
	void extractAllClaims_ValidToken_ReturnsAllClaims() {
		Claims claims = jwtService.extractAllClaims(validToken);

		assertNotNull(claims);
		assertEquals(testCustomer.getUsername(), claims.getSubject());
		assertNotNull(claims.getIssuedAt());
		assertNotNull(claims.getExpiration());
	}
}