package ee.vladislav.backend.service;

import ee.vladislav.backend.dao.Consumption;
import ee.vladislav.backend.dto.ConsumptionDTO;
import ee.vladislav.backend.mapper.ConsumptionMapper;
import ee.vladislav.backend.repository.ConsumptionRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ConsumptionService {
	private final AuthService authService;
	private final ConsumptionRepository consumptionRepository;
	private final ConsumptionMapper consumptionMapper;
	private final MeteringPointService meteringPointService;

	public ConsumptionService(
			AuthService authService,
			ConsumptionRepository consumptionRepository,
			ConsumptionMapper consumptionMapper,
			MeteringPointService meteringPointService
	) {
		this.authService = authService;
		this.consumptionRepository = consumptionRepository;
		this.consumptionMapper = consumptionMapper;
		this.meteringPointService = meteringPointService;
	}

	@Transactional
	public List<ConsumptionDTO> getConsumption(Long year) {
		LocalDateTime startDate = LocalDateTime.of(year.intValue(), 1, 1, 0, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(year.intValue(), 12, 31, 23, 59, 59);

		List<Long> meteringPointIds = meteringPointService.getMeteringPointIds();

		List<Consumption> consumptions = consumptionRepository.findByMeteringPointIdInAndConsumptionTimeBetween(
				meteringPointIds, startDate, endDate
		);

		return consumptions.stream()
				.map(consumptionMapper::consumptionToConsumptionDTO)
				.toList();
	}
}
