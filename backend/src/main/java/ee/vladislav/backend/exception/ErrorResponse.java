package ee.vladislav.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
		int status,
		LocalDateTime timestamp,
		String message,
		Map<String, String> errors
) {}