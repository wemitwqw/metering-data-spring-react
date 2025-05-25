package ee.vladislav.backend.unit.controller;

import ee.vladislav.backend.controller.ConsumptionController;
import ee.vladislav.backend.dto.ConsumptionDTO;
import ee.vladislav.backend.dto.ConsumptionWithCostDTO;
import ee.vladislav.backend.service.ConsumptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsumptionControllerTest {

	@Mock
	private ConsumptionService consumptionService;

	@InjectMocks
	private ConsumptionController consumptionController;

	private String testMeterId;
	private Long testYear;
	private List<ConsumptionDTO> mockConsumptionDTOs;
	private List<ConsumptionWithCostDTO> mockConsumptionWithCostDTOs;

	@BeforeEach
	void setUp() {
		testMeterId = "METER123";
		testYear = 2024L;

		ConsumptionDTO consumptionDTO1 = new ConsumptionDTO();
		consumptionDTO1.setAmount(new BigDecimal("150.50"));
		consumptionDTO1.setAmountUnit("kWh");
		consumptionDTO1.setConsumptionTime(LocalDateTime.of(2024, 1, 15, 12, 0));
		consumptionDTO1.setCreatedAt(LocalDateTime.of(2024, 1, 16, 10, 0));

		ConsumptionDTO consumptionDTO2 = new ConsumptionDTO();
		consumptionDTO2.setAmount(new BigDecimal("200.75"));
		consumptionDTO2.setAmountUnit("kWh");
		consumptionDTO2.setConsumptionTime(LocalDateTime.of(2024, 2, 15, 12, 0));
		consumptionDTO2.setCreatedAt(LocalDateTime.of(2024, 2, 16, 10, 0));

		mockConsumptionDTOs = Arrays.asList(consumptionDTO1, consumptionDTO2);

		ConsumptionWithCostDTO consumptionWithCostDTO1 = new ConsumptionWithCostDTO();
		consumptionWithCostDTO1.setMonthNumber(1);
		consumptionWithCostDTO1.setMonth(Month.JANUARY);
		consumptionWithCostDTO1.setAmount(new BigDecimal("150.50"));
		consumptionWithCostDTO1.setAmountUnit("kWh");
		consumptionWithCostDTO1.setTotalCostEur(new BigDecimal("25.75"));
		consumptionWithCostDTO1.setTotalCostEurWithVat(new BigDecimal("31.17"));
		consumptionWithCostDTO1.setCentsPerKwh(new BigDecimal("17.10"));
		consumptionWithCostDTO1.setCentsPerKwhWithVat(new BigDecimal("20.71"));

		ConsumptionWithCostDTO consumptionWithCostDTO2 = new ConsumptionWithCostDTO();
		consumptionWithCostDTO2.setMonthNumber(2);
		consumptionWithCostDTO2.setMonth(Month.FEBRUARY);
		consumptionWithCostDTO2.setAmount(new BigDecimal("200.75"));
		consumptionWithCostDTO2.setAmountUnit("kWh");
		consumptionWithCostDTO2.setTotalCostEur(new BigDecimal("34.33"));
		consumptionWithCostDTO2.setTotalCostEurWithVat(new BigDecimal("41.56"));
		consumptionWithCostDTO2.setCentsPerKwh(new BigDecimal("17.10"));
		consumptionWithCostDTO2.setCentsPerKwhWithVat(new BigDecimal("20.71"));

		mockConsumptionWithCostDTOs = Arrays.asList(consumptionWithCostDTO1, consumptionWithCostDTO2);
	}

	@Test
	void fetchRawConsumptions_ShouldReturnConsumptionDTOList() {
		when(consumptionService.getConsumptionByMeteringPoint(testMeterId, testYear))
				.thenReturn(mockConsumptionDTOs);

		ResponseEntity<List<ConsumptionDTO>> response =
				consumptionController.fetchRawConsumptions(testMeterId, testYear);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).hasSize(2);
		assertThat(response.getBody().get(0).getAmount()).isEqualTo(new BigDecimal("150.50"));
		assertThat(response.getBody().get(0).getAmountUnit()).isEqualTo("kWh");
		assertThat(response.getBody().get(1).getAmount()).isEqualTo(new BigDecimal("200.75"));
		assertThat(response.getBody().get(1).getAmountUnit()).isEqualTo("kWh");

		verify(consumptionService).getConsumptionByMeteringPoint(testMeterId, testYear);
	}

	@Test
	void fetchRawConsumptions_ShouldReturnEmptyList_WhenNoConsumptionsFound() {
		when(consumptionService.getConsumptionByMeteringPoint(testMeterId, testYear))
				.thenReturn(List.of());

		ResponseEntity<List<ConsumptionDTO>> response =
				consumptionController.fetchRawConsumptions(testMeterId, testYear);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).isEmpty();

		verify(consumptionService).getConsumptionByMeteringPoint(testMeterId, testYear);
	}

	@Test
	void fetchConsumptions_ShouldReturnConsumptionWithCostDTOList() {
		when(consumptionService.getConsumptionWithCostByMeteringPoint(testMeterId, testYear))
				.thenReturn(mockConsumptionWithCostDTOs);

		ResponseEntity<List<ConsumptionWithCostDTO>> response =
				consumptionController.fetchConsumptions(testMeterId, testYear);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).hasSize(2);

		ConsumptionWithCostDTO firstResult = response.getBody().getFirst();
		assertThat(firstResult.getMonthNumber()).isEqualTo(1);
		assertThat(firstResult.getMonth()).isEqualTo(Month.JANUARY);
		assertThat(firstResult.getAmount()).isEqualTo(new BigDecimal("150.50"));
		assertThat(firstResult.getAmountUnit()).isEqualTo("kWh");
		assertThat(firstResult.getTotalCostEur()).isEqualTo(new BigDecimal("25.75"));
		assertThat(firstResult.getTotalCostEurWithVat()).isEqualTo(new BigDecimal("31.17"));

		ConsumptionWithCostDTO secondResult = response.getBody().get(1);
		assertThat(secondResult.getMonthNumber()).isEqualTo(2);
		assertThat(secondResult.getMonth()).isEqualTo(Month.FEBRUARY);
		assertThat(secondResult.getAmount()).isEqualTo(new BigDecimal("200.75"));

		verify(consumptionService).getConsumptionWithCostByMeteringPoint(testMeterId, testYear);
	}

	@Test
	void fetchConsumptions_ShouldReturnEmptyList_WhenNoConsumptionsFound() {
		when(consumptionService.getConsumptionWithCostByMeteringPoint(testMeterId, testYear))
				.thenReturn(List.of());

		ResponseEntity<List<ConsumptionWithCostDTO>> response =
				consumptionController.fetchConsumptions(testMeterId, testYear);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody()).isEmpty();

		verify(consumptionService).getConsumptionWithCostByMeteringPoint(testMeterId, testYear);
	}

	@Test
	void fetchRawConsumptions_ShouldCallServiceWithCorrectParameters() {
		String specificMeterId = "METER456";
		Long specificYear = 2023L;

		when(consumptionService.getConsumptionByMeteringPoint(specificMeterId, specificYear))
				.thenReturn(mockConsumptionDTOs);

		consumptionController.fetchRawConsumptions(specificMeterId, specificYear);

		verify(consumptionService).getConsumptionByMeteringPoint(specificMeterId, specificYear);
	}

	@Test
	void fetchConsumptions_ShouldCallServiceWithCorrectParameters() {
		String specificMeterId = "METER789";
		Long specificYear = 2022L;

		when(consumptionService.getConsumptionWithCostByMeteringPoint(specificMeterId, specificYear))
				.thenReturn(mockConsumptionWithCostDTOs);

		consumptionController.fetchConsumptions(specificMeterId, specificYear);

		verify(consumptionService).getConsumptionWithCostByMeteringPoint(specificMeterId, specificYear);
	}
}