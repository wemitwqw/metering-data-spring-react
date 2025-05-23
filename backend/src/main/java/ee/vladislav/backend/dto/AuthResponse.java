package ee.vladislav.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
	private String accessToken;
	private long accessTokenExpiresInSeconds;
	private String refreshToken;
	private long refreshTokenExpiresInSeconds;
	private String email;
	private String firstName;
	private String lastName;
}