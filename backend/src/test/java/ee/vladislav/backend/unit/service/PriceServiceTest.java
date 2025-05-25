package ee.vladislav.backend.unit.service;

import ee.vladislav.backend.dto.EleringResponse;
import ee.vladislav.backend.dto.MonthlyPriceDTO;
import ee.vladislav.backend.dto.PriceResponse;
import ee.vladislav.backend.exception.BadApiRequestException;
import ee.vladislav.backend.service.PriceService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private PriceService priceService;

	private EleringResponse createEleringResponse(int month, int year) {
		EleringResponse response = new EleringResponse();
		response.setCentsPerKwh(BigDecimal.valueOf(12.5));
		response.setCentsPerKwhWithVat(BigDecimal.valueOf(15.5));
		response.setEurPerMwh(BigDecimal.valueOf(125.0));
		response.setEurPerMwhWithVat(BigDecimal.valueOf(155.0));
		response.setFromDateTime(OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.UTC));
		response.setToDateTime(OffsetDateTime.of(year, month, 28, 23, 59, 59, 0, ZoneOffset.UTC));
		return response;
	}

	@Test
	void fetchElectricityPrices_Success() {
		int year = 2023;
		List<EleringResponse> mockResponses = Arrays.asList(
				createEleringResponse(1, year),
				createEleringResponse(2, year)
		);

		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(mockResponses, HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		List<EleringResponse> result = priceService.fetchElectricityPrices(year);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(year, result.getFirst().getFromDateTime().getYear());

		verify(restTemplate).exchange(
				contains("2023-01-01T00:00:01Z"),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		);
	}

	@Test
	void fetchElectricityPrices_BadApiRequest_ThrowsException() {
		int year = 2023;
		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		assertThrows(BadApiRequestException.class, () ->
				priceService.fetchElectricityPrices(year));
	}

	@Test
	void fetchElectricityPrices_NullBody_ThrowsException() {
		int year = 2023;
		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(null, HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		assertThrows(BadApiRequestException.class, () ->
				priceService.fetchElectricityPrices(year));
	}

	@Test
	void fetchElectricityPrices_FiltersCorrectYear() {
		int year = 2023;
		List<EleringResponse> mockResponses = Arrays.asList(
				createEleringResponse(1, 2023),
				createEleringResponse(2, 2022),
				createEleringResponse(3, 2023)
		);

		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(mockResponses, HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		List<EleringResponse> result = priceService.fetchElectricityPrices(year);

		assertEquals(2, result.size());
		assertTrue(result.stream().allMatch(r -> r.getFromDateTime().getYear() == year));
	}

	@Test
	void getFormattedElectricityPrices_Success() {
		int year = 2023;
		List<EleringResponse> mockResponses = Arrays.asList(
				createEleringResponse(1, year),
				createEleringResponse(2, year)
		);

		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(mockResponses, HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		PriceResponse result = priceService.getFormattedElectricityPrices(year);

		assertNotNull(result);
		assertEquals(year, result.getYear());
		assertEquals(2, result.getMonths().size());

		MonthlyPriceDTO firstMonth = result.getMonths().getFirst();
		assertEquals(1, firstMonth.getMonth());
		assertEquals("January", firstMonth.getMonthName());
		assertEquals(BigDecimal.valueOf(12.5), firstMonth.getCentsPerKwh());
		assertEquals(BigDecimal.valueOf(15.5), firstMonth.getCentsPerKwhWithVat());
	}

	@Test
	void getCurrentYearFormattedPrices_Success() {
		int currentYear = LocalDateTime.now().getYear();
		List<EleringResponse> mockResponses = List.of(
				createEleringResponse(1, currentYear)
		);

		ResponseEntity<List<EleringResponse>> mockResponseEntity =
				new ResponseEntity<>(mockResponses, HttpStatus.OK);

		when(restTemplate.exchange(
				anyString(),
				eq(HttpMethod.GET),
				isNull(),
				ArgumentMatchers.<ParameterizedTypeReference<List<EleringResponse>>>any()
		)).thenReturn(mockResponseEntity);

		PriceResponse result = priceService.getCurrentYearFormattedPrices();

		assertNotNull(result);
		assertEquals(currentYear, result.getYear());
	}
}