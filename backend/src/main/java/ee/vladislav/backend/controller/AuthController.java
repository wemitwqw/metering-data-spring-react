package ee.vladislav.backend.controller;

import ee.vladislav.backend.dto.AuthRequest;
import ee.vladislav.backend.dto.AuthResponse;
import ee.vladislav.backend.dto.RefreshTokenRequest;
import ee.vladislav.backend.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(authService.refreshToken(request));
	}
}