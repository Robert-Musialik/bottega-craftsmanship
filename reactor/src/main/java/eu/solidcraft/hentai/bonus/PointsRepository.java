package eu.solidcraft.hentai.bonus;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

interface PointsRepository extends Repository<Points, String> {
    Mono<Points> save(Points points);
    Mono<Points> findByUsername(String username);

    default Mono<Points> findOrCreateZero(String username) {
        return findByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.just(new Points(username, 0))));
    }
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryPointsRepository implements PointsRepository {
    Map<String, Points> store = new ConcurrentHashMap<>();

    @Override
    public Mono<Points> save(Points points) {
        store.put(points.getUsername(), points);
        return Mono.just(points);
    }

    @Override
    public Mono<Points> findByUsername(String username) {
        return Mono.justOrEmpty(store.get(username));
    }
}
