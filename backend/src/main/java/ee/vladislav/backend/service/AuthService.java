package ee.vladislav.backend.service;

import ee.vladislav.backend.exception.InvalidLoginCredentialsException;
import ee.vladislav.backend.dto.AuthRequest;
import ee.vladislav.backend.dto.AuthResponse;
import ee.vladislav.backend.dao.Customer;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthResponse login(AuthRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.email(),
							request.password()
					)
			);

			Customer user = (Customer) authentication.getPrincipal();

			String jwtToken = jwtService.generateToken(user);

			return AuthResponse.builder()
					.token(jwtToken)
					.email(user.getEmail())
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.build();
		} catch (BadCredentialsException e) {
			throw new InvalidLoginCredentialsException();
		}
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