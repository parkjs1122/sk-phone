package skphone.service;

import reactor.core.publisher.Mono;
import skphone.domain.User;

public interface UserService {

    Mono<User> findById(String userId);

    Mono<User> addUser(String userId, String password);

}
