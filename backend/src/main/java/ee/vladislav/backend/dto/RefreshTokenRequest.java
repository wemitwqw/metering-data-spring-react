package ee.vladislav.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
		@NotBlank(message = "Refresh token is required")
		String refreshToken
) {}