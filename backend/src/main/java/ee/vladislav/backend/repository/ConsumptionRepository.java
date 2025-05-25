package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.Consumption;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
	List<Consumption> findByMeteringPoint_MeterIdAndConsumptionTimeBetween(
			String meterId, LocalDateTime startDate, LocalDateTime endDate
	);

	@Query("SELECT DISTINCT YEAR(c.consumptionTime) FROM Consumption c " +
			"JOIN c.meteringPoint mp " +
			"WHERE mp.meterId = :meterId " +
			"ORDER BY YEAR(c.consumptionTime)")
	List<Integer> findConsumptionYearsByMeterId(@Param("meterId") String meterId);
}