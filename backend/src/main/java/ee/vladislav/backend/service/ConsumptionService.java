package ee.vladislav.backend.service;

import ee.vladislav.backend.dao.Consumption;
import ee.vladislav.backend.dto.ConsumptionDTO;
import ee.vladislav.backend.dto.ConsumptionWithCostDTO;
import ee.vladislav.backend.dto.EleringResponse;
import ee.vladislav.backend.mapper.ConsumptionMapper;
import ee.vladislav.backend.repository.ConsumptionRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumptionService {

	private final ConsumptionRepository consumptionRepository;
	private final ConsumptionMapper consumptionMapper;
	private final MeteringPointService meteringPointService;
	private final PriceService priceService;

	@Transactional
	public List<ConsumptionDTO> getConsumptionByMeteringPoint(Long meteringPointId, Long year) {
		List<Consumption> consumptions = fetchConsumptionData(meteringPointId, year);

		return consumptions.stream()
				.map(consumptionMapper::consumptionToConsumptionDTO)
				.toList();
	}

	@Transactional
	public List<ConsumptionWithCostDTO> getConsumptionWithCostByMeteringPoint(Long meteringPointId, Long year) {
		List<Consumption> consumptions = fetchConsumptionData(meteringPointId, year);

		Map<Month, ConsumptionWithCostDTO> monthlyTotals = groupConsumptionByMonth(consumptions);

		applyPriceData(monthlyTotals, year.intValue());

		List<ConsumptionWithCostDTO> result = new ArrayList<>(monthlyTotals.values());
		result.sort(Comparator.comparing(ConsumptionWithCostDTO::getMonthNumber));

		return result;
	}

	private List<Consumption> fetchConsumptionData(Long meteringPointId, Long year) {
		meteringPointService.validateUserAccess(meteringPointId);

		LocalDateTime startDate = LocalDateTime.of(year.intValue(), 1, 1, 0, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(year.intValue(), 12, 31, 23, 59, 59);

		return consumptionRepository.findByMeteringPointIdAndConsumptionTimeBetween(
				meteringPointId, startDate, endDate
		);
	}

	private Map<Month, ConsumptionWithCostDTO> groupConsumptionByMonth(List<Consumption> consumptions) {
		Map<Month, ConsumptionWithCostDTO> monthlyTotals = new HashMap<>();

		for (Consumption consumption : consumptions) {
			Month month = consumption.getConsumptionTime().getMonth();

			ConsumptionWithCostDTO dto = monthlyTotals.computeIfAbsent(month, m -> {
				ConsumptionWithCostDTO newDto = new ConsumptionWithCostDTO();
				newDto.setAmount(BigDecimal.ZERO);
				newDto.setAmountUnit(consumption.getAmountUnit());
				newDto.setMonth(month);
				newDto.setMonthNumber(month.getValue());
				return newDto;
			});

			dto.setAmount(dto.getAmount().add(consumption.getAmount()));
		}

		return monthlyTotals;
	}

	private void applyPriceData(Map<Month, ConsumptionWithCostDTO> monthlyTotals, int year) {
		List<EleringResponse> pricesPerMonthOfYear = priceService.fetchElectricityPrices(year);

		for (EleringResponse price : pricesPerMonthOfYear) {
			Month month = price.getFromDateTime().getMonth();
			ConsumptionWithCostDTO dto = monthlyTotals.get(month);

			if (dto == null) continue;

			dto.setCentsPerKwh(price.getCentsPerKwh());
			dto.setCentsPerKwhWithVat(price.getCentsPerKwhWithVat());

			dto.setTotalCostEur(calculateCost(price.getCentsPerKwh(), dto.getAmount()));
			dto.setTotalCostEurWithVat(calculateCost(price.getCentsPerKwhWithVat(), dto.getAmount()));
		}
	}

	private BigDecimal calculateCost(BigDecimal centsPerKwh, BigDecimal amount) {
		return centsPerKwh
				.multiply(amount)
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	}
}