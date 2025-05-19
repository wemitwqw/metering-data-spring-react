package ee.vladislav.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
		@NotBlank(message = "Email is required")
		@Email(
				regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
		)
		String email,

		@NotBlank(message = "Password is required")
		String password
) {}
