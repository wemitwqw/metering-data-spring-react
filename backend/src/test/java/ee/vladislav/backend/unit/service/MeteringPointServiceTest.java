package ee.vladislav.backend.unit.service;

import ee.vladislav.backend.dao.Customer;
import ee.vladislav.backend.dao.MeteringPoint;
import ee.vladislav.backend.dto.MeteringPointDTO;
import ee.vladislav.backend.mapper.MeteringPointMapper;
import ee.vladislav.backend.repository.MeteringPointRepository;
import ee.vladislav.backend.service.AuthService;
import ee.vladislav.backend.service.MeteringPointService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeteringPointServiceTest {

	@Mock
	private AuthService authService;

	@Mock
	private MeteringPointRepository meteringPointRepository;

	@Mock
	private MeteringPointMapper meteringPointMapper;

	@InjectMocks
	private MeteringPointService meteringPointService;

	private Customer testCustomer;
	private MeteringPoint testMeteringPoint1;
	private MeteringPoint testMeteringPoint2;
	private MeteringPointDTO testMeteringPointDTO1;
	private MeteringPointDTO testMeteringPointDTO2;

	@BeforeEach
	void setUp() {
		testCustomer = new Customer();
		testCustomer.setId(1L);
		testCustomer.setEmail("test@example.com");
		testCustomer.setFirstName("John");
		testCustomer.setLastName("Doe");

		testMeteringPoint1 = new MeteringPoint();
		testMeteringPoint1.setId(1L);
		testMeteringPoint1.setMeterId("MP001");
		testMeteringPoint1.setAddress("123 Main St");
		testMeteringPoint1.setCustomer(testCustomer);
		testMeteringPoint1.setCreatedAt(LocalDateTime.now());
		testMeteringPoint1.setUpdatedAt(LocalDateTime.now());

		testMeteringPoint2 = new MeteringPoint();
		testMeteringPoint2.setId(2L);
		testMeteringPoint2.setMeterId("MP002");
		testMeteringPoint2.setAddress("456 Oak Ave");
		testMeteringPoint2.setCustomer(testCustomer);
		testMeteringPoint2.setCreatedAt(LocalDateTime.now());
		testMeteringPoint2.setUpdatedAt(LocalDateTime.now());

		testMeteringPointDTO1 = new MeteringPointDTO();
		testMeteringPointDTO1.setMeterId("MP001");
		testMeteringPointDTO1.setAddress("123 Main St");

		testMeteringPointDTO2 = new MeteringPointDTO();
		testMeteringPointDTO2.setMeterId("MP002");
		testMeteringPointDTO2.setAddress("456 Oak Ave");
	}

	@Test
	void getMeteringPoints_AuthenticatedUser_ReturnsUserMeteringPoints() {
		List<MeteringPoint> userMeteringPoints = Arrays.asList(testMeteringPoint1, testMeteringPoint2);
		List<MeteringPointDTO> expectedDTOs = Arrays.asList(testMeteringPointDTO1, testMeteringPointDTO2);

		when(authService.getCurrentUser()).thenReturn(Optional.of(testCustomer));
		when(meteringPointRepository.findByCustomerId(testCustomer.getId()))
				.thenReturn(userMeteringPoints);
		when(meteringPointMapper.meteringPointsToMeteringPointDTOs(userMeteringPoints))
				.thenReturn(expectedDTOs);

		List<MeteringPointDTO> result = meteringPointService.getMeteringPoints();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("MP001", result.get(0).getMeterId());
		assertEquals("MP002", result.get(1).getMeterId());
		assertEquals("123 Main St", result.get(0).getAddress());
		assertEquals("456 Oak Ave", result.get(1).getAddress());

		verify(authService).getCurrentUser();
		verify(meteringPointRepository).findByCustomerId(testCustomer.getId());
		verify(meteringPointMapper).meteringPointsToMeteringPointDTOs(userMeteringPoints);
	}

	@Test
	void getMeteringPoints_NotAuthenticated_ThrowsAccessDeniedException() {
		when(authService.getCurrentUser()).thenReturn(Optional.empty());

		AccessDeniedException exception = assertThrows(AccessDeniedException.class,
				() -> meteringPointService.getMeteringPoints());

		assertEquals("User not authenticated or not a customer", exception.getMessage());

		verify(authService).getCurrentUser();
		verifyNoInteractions(meteringPointRepository);
		verifyNoInteractions(meteringPointMapper);
	}

	@Test
	void getMeteringPoints_NoMeteringPoints_ReturnsEmptyList() {
		// Given
		when(authService.getCurrentUser()).thenReturn(Optional.of(testCustomer));
		when(meteringPointRepository.findByCustomerId(testCustomer.getId()))
				.thenReturn(List.of());
		when(meteringPointMapper.meteringPointsToMeteringPointDTOs(anyList()))
				.thenReturn(List.of());

		List<MeteringPointDTO> result = meteringPointService.getMeteringPoints();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(authService).getCurrentUser();
		verify(meteringPointRepository).findByCustomerId(testCustomer.getId());
	}

	@Test
	void validateUserAccess_UserHasAccess_DoesNotThrowException() {
		String meteringPointId = "MP001";
		Long userId = 1L;

		when(authService.getCurrentUserId()).thenReturn(userId);
		when(meteringPointRepository.existsByMeterIdAndCustomerId(meteringPointId, userId))
				.thenReturn(true);

		// When & Then
		assertDoesNotThrow(() -> meteringPointService.validateUserAccess(meteringPointId));

		verify(authService).getCurrentUserId();
		verify(meteringPointRepository).existsByMeterIdAndCustomerId(meteringPointId, userId);
	}

	@Test
	void validateUserAccess_UserDoesNotHaveAccess_ThrowsAccessDeniedException() {
		// Given
		String meteringPointId = "MP001";
		Long userId = 1L;

		when(authService.getCurrentUserId()).thenReturn(userId);
		when(meteringPointRepository.existsByMeterIdAndCustomerId(meteringPointId, userId))
				.thenReturn(false);

		// When & Then
		AccessDeniedException exception = assertThrows(AccessDeniedException.class,
				() -> meteringPointService.validateUserAccess(meteringPointId));

		assertEquals("User does not have access to this metering point", exception.getMessage());

		verify(authService).getCurrentUserId();
		verify(meteringPointRepository).existsByMeterIdAndCustomerId(meteringPointId, userId);
	}

	@Test
	void validateUserAccess_AuthServiceThrowsException_PropagatesException() {
		// Given
		String meteringPointId = "MP001";

		when(authService.getCurrentUserId())
				.thenThrow(new AccessDeniedException("User not authenticated"));

		// When & Then
		AccessDeniedException exception = assertThrows(AccessDeniedException.class,
				() -> meteringPointService.validateUserAccess(meteringPointId));

		assertEquals("User not authenticated", exception.getMessage());

		verify(authService).getCurrentUserId();
		verifyNoInteractions(meteringPointRepository);
	}
}
