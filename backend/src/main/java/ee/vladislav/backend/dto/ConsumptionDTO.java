package ee.vladislav.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConsumptionDTO {
	private BigDecimal amount;
	private String amountUnit;
	private LocalDateTime consumptionTime;
	private LocalDateTime createdAt;
}