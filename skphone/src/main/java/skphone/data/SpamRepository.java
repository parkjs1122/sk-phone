package skphone.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;
import skphone.domain.Spam;
import skphone.domain.Spam.LikeType;
import skphone.domain.User;

public interface SpamRepository extends ReactiveCrudRepository<Spam, String> {
	Mono<Spam> findFirstByPhoneNumber(String phoneNumber);
	Mono<Spam> findByUserAndPhoneNumber(User user, String phoneNumber);
	Mono<Long> countByPhoneNumberAndLikeType(String phoneNumber, LikeType likeType);
	Mono<Spam> deleteByPhoneNumber(String phoneNumber);
}
