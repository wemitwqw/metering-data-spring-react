package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.MeteringPoint;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MeteringPointRepository extends JpaRepository<MeteringPoint, Long> {
	List<MeteringPoint> findByCustomerId(Long customerId);

	boolean existsByIdAndCustomerId(Long id, Long customerId);
}
