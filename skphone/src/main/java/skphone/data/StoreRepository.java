package skphone.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;
import skphone.domain.Store;

public interface StoreRepository extends ReactiveCrudRepository<Store, String> {
	Mono<Store> findByPhoneNumber(String phoneNumber);
	Mono<Store> deleteByPhoneNumber(String phoneNumber);
}
