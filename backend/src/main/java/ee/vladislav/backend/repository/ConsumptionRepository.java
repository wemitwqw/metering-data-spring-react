package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.Consumption;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
	List<Consumption> findByMeteringPointIdInAndConsumptionTimeBetween(
			List<Long> meteringPointIds, LocalDateTime startDate, LocalDateTime endDate);
}