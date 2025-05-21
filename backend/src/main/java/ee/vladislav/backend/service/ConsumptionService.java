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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumptionService {
	private final AuthService authService;
	private final ConsumptionRepository consumptionRepository;
	private final ConsumptionMapper consumptionMapper;
	private final MeteringPointService meteringPointService;
	private final PriceService priceService;

	@Transactional
	public List<ConsumptionDTO> getConsumptionByMeteringPoint(Long meteringPointId, Long year) {
		boolean hasAccess = meteringPointService.validateMeteringPointAccess(meteringPointId, authService.getCurrentUserId());
		if (!hasAccess) {
			throw new AccessDeniedException("User does not have access to this metering point");
		}

		LocalDateTime startDate = LocalDateTime.of(year.intValue(), 1, 1, 0, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(year.intValue(), 12, 31, 23, 59, 59);

		List<Consumption> consumptions = consumptionRepository.findByMeteringPointIdAndConsumptionTimeBetween(
				meteringPointId, startDate, endDate
		);

		return consumptions.stream()
				.map(consumptionMapper::consumptionToConsumptionDTO)
				.toList();
	}

	@Transactional
	public List<ConsumptionWithCostDTO> getConsumptionWithCostByMeteringPoint(Long meteringPointId, Long year) {
		boolean hasAccess = meteringPointService.validateMeteringPointAccess(meteringPointId, authService.getCurrentUserId());
		if (!hasAccess) {
			throw new AccessDeniedException("User does not have access to this metering point");
		}

		LocalDateTime startDate = LocalDateTime.of(year.intValue(), 1, 1, 0, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(year.intValue(), 12, 31, 23, 59, 59);

		List<Consumption> consumptions = consumptionRepository.findByMeteringPointIdAndConsumptionTimeBetween(
				meteringPointId, startDate, endDate
		);

		List<EleringResponse> pricesPerMonthOfYear = priceService.fetchElectricityPrices(year.intValue());
		Map<Month, EleringResponse> priceByMonth = pricesPerMonthOfYear.stream()
				.collect(Collectors.toMap(
						p -> p.getFromDateTime().getMonth(),
						p -> p
				));

		List<ConsumptionWithCostDTO> result = new ArrayList<>();
		for (Consumption consumption : consumptions) {
			ConsumptionWithCostDTO dto = new ConsumptionWithCostDTO();

			dto.setAmount(consumption.getAmount());
			dto.setAmountUnit(consumption.getAmountUnit());
			dto.setConsumptionTime(consumption.getConsumptionTime());

			EleringResponse price = priceByMonth.get(consumption.getConsumptionTime().getMonth());
			if (price != null) {
				dto.setTotalCostEur(price.getCentsPerKwh()
						.multiply(consumption.getAmount())
						.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
				);

				dto.setTotalCostEurWithVat(price.getCentsPerKwhWithVat()
						.multiply(consumption.getAmount())
						.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
				);

				dto.setCentsPerKwh(price.getCentsPerKwh());

				dto.setCentsPerKwhWithVat(price.getCentsPerKwhWithVat());
			}

			result.add(dto);
		}

		return result;
	}
}
