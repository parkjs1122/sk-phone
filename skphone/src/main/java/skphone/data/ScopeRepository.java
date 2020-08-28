package skphone.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import skphone.domain.Scope;

@Repository
public interface ScopeRepository extends ReactiveCrudRepository<Scope, String> {
}
