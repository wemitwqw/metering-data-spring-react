package ee.vladislav.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private String accessToken;
	private long accessTokenExpiresInSeconds;
	private String refreshToken;
	private long refreshTokenExpiresInSeconds;
	private String email;
	private String firstName;
	private String lastName;
}