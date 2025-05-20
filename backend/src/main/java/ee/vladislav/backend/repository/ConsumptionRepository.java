package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.Consumption;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {}
