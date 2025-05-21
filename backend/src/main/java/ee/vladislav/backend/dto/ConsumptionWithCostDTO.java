package ee.vladislav.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ConsumptionWithCostDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private BigDecimal amount;
	private String amountUnit;
	private LocalDateTime consumptionTime;
	private BigDecimal centsPerKwh;
	private BigDecimal centsPerKwhWithVat;
	private BigDecimal totalCostEur;
	private BigDecimal totalCostEurWithVat;
}