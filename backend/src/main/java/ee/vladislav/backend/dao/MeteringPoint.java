package ee.vladislav.backend.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "metering_points")
public class MeteringPoint {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metering_points_seq")
	@SequenceGenerator(name = "metering_points_seq", sequenceName = "metering_points_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(name = "meter_id", nullable = false, unique = true)
	private String meterId;

	@Column(nullable = false)
	private String address;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}