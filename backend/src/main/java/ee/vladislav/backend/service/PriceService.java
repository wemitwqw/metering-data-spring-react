package ee.vladislav.backend.service;

import ee.vladislav.backend.dto.EleringResponse;
import ee.vladislav.backend.dto.MonthlyPriceDTO;
import ee.vladislav.backend.dto.PriceResponse;
import ee.vladislav.backend.exception.BadApiRequestException;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PriceService {

	private final RestTemplate restTemplate;
	private static final String ELERING_API_URL = "https://estfeed.elering.ee/api/public/v1/energy-price/electricity";

	public PriceService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Cacheable(value = "rawElectricityPrices", key = "#year")
	public List<EleringResponse> fetchElectricityPrices(int year) {

		String url = ELERING_API_URL +
				"?startDateTime=" + year + "-01-01T00:00:01Z" +
				"&endDateTime=" + year + "-12-31T23:59:59Z" +
				"&resolution=one_month";

		ResponseEntity<List<EleringResponse>> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<>(){}
		);

		if (response.getBody() == null || response.getStatusCode() != HttpStatus.OK) {
			throw new BadApiRequestException();
		}

		return response.getBody().stream()
				.filter(item -> item.getFromDateTime().getYear() == year)
				.collect(Collectors.toList());
	}

	@Cacheable(value = "formattedElectricityPrices", key = "#year")
	public PriceResponse getFormattedElectricityPrices(int year) {
		List<EleringResponse> eleringResponse = fetchElectricityPrices(year);

		PriceResponse priceResponse = new PriceResponse();
		priceResponse.setYear(year);

		List<MonthlyPriceDTO> monthlyPrices = new ArrayList<>();

		for (EleringResponse resp : eleringResponse) {
			MonthlyPriceDTO monthlyPrice = new MonthlyPriceDTO();

			Month month = resp.getFromDateTime().getMonth();
			int monthValue = month.getValue();
			String monthName = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

			monthlyPrice.setMonth(monthValue);
			monthlyPrice.setMonthName(monthName);
			monthlyPrice.setCentsPerKwh(resp.getCentsPerKwh());
			monthlyPrice.setCentsPerKwhWithVat(resp.getCentsPerKwhWithVat());
			monthlyPrice.setEurPerMwh(resp.getEurPerMwh());
			monthlyPrice.setEurPerMwhWithVat(resp.getEurPerMwhWithVat());

			monthlyPrices.add(monthlyPrice);
		}

		priceResponse.setMonths(monthlyPrices);

		return priceResponse;
	}

	@Cacheable(value = "formattedElectricityPrices", key = "'currentYear'")
	public PriceResponse getCurrentYearFormattedPrices() {
		return getFormattedElectricityPrices(LocalDateTime.now().getYear());
	}
}