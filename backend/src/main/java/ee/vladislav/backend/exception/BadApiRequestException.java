package ee.vladislav.backend.exception;

import java.io.Serial;

public class BadApiRequestException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public BadApiRequestException() {
		super("Error during API request to Elering.");
	}
}
