package ee.vladislav.backend.service;

import ee.vladislav.backend.dao.Customer;
import ee.vladislav.backend.dao.RefreshToken;
import ee.vladislav.backend.exception.InvalidRefreshTokenException;
import ee.vladislav.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.refresh.expiration:604800000}")
	private long refreshTokenExpirationMs;

	@Transactional
	public RefreshToken createRefreshToken(Customer customer) {
		RefreshToken refreshToken = refreshTokenRepository.findByCustomer(customer)
				.orElse(new RefreshToken());

		refreshToken.setCustomer(customer);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000));

		return refreshTokenRepository.save(refreshToken);
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.isExpired()) {
			refreshTokenRepository.delete(token);
			throw new InvalidRefreshTokenException("Refresh token expired. Please sign in again.");
		}
		return token;
	}

	public void deleteByCustomer(Customer customer) {
		refreshTokenRepository.deleteByCustomer(customer);
	}

	@Scheduled(fixedRate = 3600000)
	public void deleteExpiredTokens() {
		refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
	}
}