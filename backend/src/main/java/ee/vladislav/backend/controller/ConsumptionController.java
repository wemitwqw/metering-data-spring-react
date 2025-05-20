package ee.vladislav.backend.controller;

import ee.vladislav.backend.dto.ConsumptionDTO;
import ee.vladislav.backend.service.ConsumptionService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consumption")
public class ConsumptionController {
	private final ConsumptionService consumptionService;

	public ConsumptionController(ConsumptionService consumptionService) {
		this.consumptionService = consumptionService;
	}

	@GetMapping()
	public ResponseEntity<List<ConsumptionDTO>> fetchConsumption(@Valid @RequestParam Long year) {
		return ResponseEntity.ok(consumptionService.getConsumption(year));
	}
}
