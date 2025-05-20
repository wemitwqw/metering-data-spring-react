package ee.vladislav.backend.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "consumption")
public class Consumption {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consumption_seq")
	@SequenceGenerator(name = "consumption_seq", sequenceName = "consumption_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "metering_point_id", nullable = false)
	private MeteringPoint meteringPoint;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "consumption_time", nullable = false)
	private LocalDateTime consumptionTime;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
}