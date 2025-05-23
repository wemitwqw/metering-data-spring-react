package ee.vladislav.backend.service;

import ee.vladislav.backend.dao.RefreshToken;
import ee.vladislav.backend.dto.RefreshTokenRequest;
import ee.vladislav.backend.exception.InvalidLoginCredentialsException;
import ee.vladislav.backend.dto.AuthRequest;
import ee.vladislav.backend.dto.AuthResponse;
import ee.vladislav.backend.dao.Customer;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;

	@Value("${jwt.expiration}")
	private long accessTokenExpirationMs;

	@Value("${jwt.refresh.expiration}")
	private long refreshTokenExpirationMs;

	public AuthResponse login(AuthRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.email(),
							request.password()
					)
			);

			Customer user = (Customer) authentication.getPrincipal();

			String accessToken = jwtService.generateToken(user);
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

			return AuthResponse.builder()
					.accessToken(accessToken)
					.accessTokenExpiresInSeconds(accessTokenExpirationMs / 1000)
					.refreshToken(refreshToken.getToken())
					.refreshTokenExpiresInSeconds(refreshTokenExpirationMs / 1000)
					.email(user.getEmail())
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.build();
		} catch (BadCredentialsException e) {
			throw new InvalidLoginCredentialsException();
		}
	}

	public AuthResponse refreshToken(RefreshTokenRequest request) {
		RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));

		refreshToken = refreshTokenService.verifyExpiration(refreshToken);

		Customer customer = refreshToken.getCustomer();

		return AuthResponse.builder()
				.accessToken(jwtService.generateToken(customer))
				.accessTokenExpiresInSeconds(accessTokenExpirationMs / 1000)
				.refreshToken(refreshTokenService.createRefreshToken(customer).getToken())
				.refreshTokenExpiresInSeconds(refreshTokenExpirationMs / 1000)
				.email(customer.getEmail())
				.firstName(customer.getFirstName())
				.lastName(customer.getLastName())
				.build();
	}

	public void logout() {
		getCurrentUser().ifPresent(refreshTokenService::deleteByCustomer);
	}

	public Optional<Customer> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||
				authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof Customer) {
			return Optional.of((Customer) principal);
		}

		return Optional.empty();
	}

	public Long getCurrentUserId() {
		Customer currentUserId = getCurrentUser().orElseThrow(() ->
				new AccessDeniedException("User not authenticated or not a customer"));

		return currentUserId.getId();
	}
}