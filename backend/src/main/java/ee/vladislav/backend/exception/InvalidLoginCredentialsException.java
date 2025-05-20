package ee.vladislav.backend.exception;

import java.io.Serial;

import lombok.Getter;

import org.springframework.security.authentication.BadCredentialsException;

@Getter
public class InvalidLoginCredentialsException extends BadCredentialsException {

	@Serial private static final long serialVersionUID = 1L;

	public InvalidLoginCredentialsException() {
		super("No account with matching credentials found.");
	}
}
