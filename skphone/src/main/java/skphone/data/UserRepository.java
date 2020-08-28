package skphone.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;
import skphone.domain.User;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
	Mono<User> findByUsername(String username);
	Mono<User> findByPhoneNumber(String phoneNumber);
	Mono<User> deleteByPhoneNumber(String phoneNumber);
}
