package ee.vladislav.backend.controller;

import ee.vladislav.backend.dto.EleringResponse;
import ee.vladislav.backend.dto.PriceResponse;
import ee.vladislav.backend.helper.MaxCurrentYear;
import ee.vladislav.backend.service.PriceService;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

	private final PriceService priceService;

	@GetMapping("/raw")
	public ResponseEntity<List<EleringResponse>> getRawElectricityPrices(
			@RequestParam(required = false)
			@Min(2013)
			@MaxCurrentYear
			Integer year
	) {

		if (year != null) {
			return ResponseEntity.ok(priceService.fetchElectricityPrices(year));
		} else {
			return ResponseEntity.ok(priceService.fetchElectricityPrices(
					LocalDateTime.now().getYear()));
		}
	}

	@GetMapping
	public ResponseEntity<PriceResponse> getFormattedElectricityPrices(
			@RequestParam(required = false) Integer year) {

		if (year != null) {
			return ResponseEntity.ok(priceService.getFormattedElectricityPrices(year));
		} else {
			return ResponseEntity.ok(priceService.getCurrentYearFormattedPrices());
		}
	}
}