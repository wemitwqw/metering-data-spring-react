package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.MeteringPoint;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeteringPointRepository extends JpaRepository<MeteringPoint, Long> {
	List<MeteringPoint> findByCustomerId(Long customerId);

	@Query("SELECT mp.id FROM MeteringPoint mp WHERE mp.customer.id = :customerId")
	List<Long> findMeteringPointIdsByCustomerId(@Param("customerId") Long customerId);
}
