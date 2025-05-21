package ee.vladislav.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Month;

@Data
@NoArgsConstructor
public class ConsumptionWithCostDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer monthNumber;
	private Month month;

	private BigDecimal amount;
	private String amountUnit;

	private BigDecimal totalCostEur;
	private BigDecimal totalCostEurWithVat;

	private BigDecimal centsPerKwh;
	private BigDecimal centsPerKwhWithVat;
}