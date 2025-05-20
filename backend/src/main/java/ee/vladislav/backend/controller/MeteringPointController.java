package ee.vladislav.backend.controller;

import ee.vladislav.backend.dto.MeteringPointDTO;
import ee.vladislav.backend.service.MeteringPointService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metering-point")
public class MeteringPointController {
	private final MeteringPointService meteringPointService;

	public MeteringPointController(MeteringPointService meteringPointService) {
		this.meteringPointService = meteringPointService;
	}

	@GetMapping()
	public ResponseEntity<List<MeteringPointDTO>> fetchAllMeteringPoints() {
		return ResponseEntity.ok(meteringPointService.getMeteringPoints());
	}
}
