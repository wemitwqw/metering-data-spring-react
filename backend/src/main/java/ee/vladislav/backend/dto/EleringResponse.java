package ee.vladislav.backend.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EleringResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private BigDecimal centsPerKwh;
	private BigDecimal centsPerKwhWithVat;
	private BigDecimal eurPerMwh;
	private BigDecimal eurPerMwhWithVat;
	private OffsetDateTime fromDateTime;
	private OffsetDateTime toDateTime;
}
