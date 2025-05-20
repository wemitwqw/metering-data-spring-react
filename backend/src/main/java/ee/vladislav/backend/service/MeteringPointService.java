package ee.vladislav.backend.service;

import ee.vladislav.backend.dao.Customer;
import ee.vladislav.backend.dao.MeteringPoint;
import ee.vladislav.backend.dto.MeteringPointDTO;
import ee.vladislav.backend.mapper.MeteringPointMapper;
import ee.vladislav.backend.repository.MeteringPointRepository;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class MeteringPointService {
	private final AuthService authService;
	private final MeteringPointRepository meteringPointRepository;
	private final MeteringPointMapper meteringPointMapper;

	public MeteringPointService(
			AuthService authService,
			MeteringPointRepository meteringPointRepository,
			MeteringPointMapper meteringPointMapper) {
		this.authService = authService;
		this.meteringPointRepository = meteringPointRepository;
		this.meteringPointMapper = meteringPointMapper;
	}

	public List<MeteringPointDTO> getMeteringPoints() {
		Customer currentUser = authService.getCurrentUser().orElseThrow(() ->
				new AccessDeniedException("User not authenticated or not a customer"));

		List<MeteringPoint> userMeteringPoints = meteringPointRepository.findByCustomerId(currentUser.getId());

		return meteringPointMapper.meteringPointsToMeteringPointDTOs(userMeteringPoints);
	}

	public boolean validateMeteringPointAccess(Long meteringPointId, Long customerId) {
		return meteringPointRepository.existsByIdAndCustomerId(meteringPointId, customerId);
	}
}
