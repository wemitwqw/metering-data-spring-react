package ee.vladislav.backend.service;

import ee.vladislav.backend.repository.CustomerRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomerDetailsService implements UserDetailsService {
	private final CustomerRepository customerRepository;

	public CustomerDetailsService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return customerRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
	}
}
