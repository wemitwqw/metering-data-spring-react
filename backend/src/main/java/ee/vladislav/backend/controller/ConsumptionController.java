package ee.vladislav.backend.controller;

import ee.vladislav.backend.dto.ConsumptionDTO;
import ee.vladislav.backend.dto.ConsumptionWithCostDTO;
import ee.vladislav.backend.service.ConsumptionService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumptionController {
	private final ConsumptionService consumptionService;

	public ConsumptionController(ConsumptionService consumptionService) {
		this.consumptionService = consumptionService;
	}

	@GetMapping("raw")
	public ResponseEntity<List<ConsumptionDTO>> fetchRawConsumptions(
			@Valid @RequestParam String meterId,
			@Valid @RequestParam Long year
	) {
		return ResponseEntity.ok(consumptionService.getConsumptionByMeteringPoint(meterId, year));
	}

	@GetMapping
	public ResponseEntity<List<ConsumptionWithCostDTO>> fetchConsumptions(
			@Valid @RequestParam String meterId,
			@Valid @RequestParam Long year
	) {
		return ResponseEntity.ok(consumptionService.getConsumptionWithCostByMeteringPoint(meterId, year));
	}

	@GetMapping("years")
	public ResponseEntity<List<Integer>> fetchConsumptionYears(@Valid @RequestParam String meterId) {
		return ResponseEntity.ok(consumptionService.getConsumptionYearsByMeterId(meterId));
	}
}
