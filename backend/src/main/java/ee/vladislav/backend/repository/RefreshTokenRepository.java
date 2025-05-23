
package ee.vladislav.backend.repository;

import ee.vladislav.backend.dao.RefreshToken;
import ee.vladislav.backend.dao.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByCustomer(Customer customer);

	void deleteByCustomer(Customer customer);

	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
	void deleteExpiredTokens(LocalDateTime now);
}