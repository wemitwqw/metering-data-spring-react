package ee.vladislav.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MonthlyPriceDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private int month;
	private String monthName;
	private BigDecimal centsPerKwh;
	private BigDecimal centsPerKwhWithVat;
	private BigDecimal eurPerMwh;
	private BigDecimal eurPerMwhWithVat;
}