package eu.solidcraft.hentai.catalogue;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

interface FilmRepository extends Repository<Film, String> {
    Mono<Film> save(Film filmDto);
    Flux<Film> findAll();
    Mono<Film> findByTitle(String title);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryFilmRepository implements FilmRepository {
    Map<String, Film> store = new HashMap<>();

    @Override
    public Mono<Film> save(Film film) {
        return Mono.just(film)
                    .doOnNext(filmToBeSaved ->
                        store.put(filmToBeSaved.getTitle(), filmToBeSaved));
    }

    @Override
    public Flux<Film> findAll() {
        return Flux.fromIterable(store.values());
    }

    @Override
    public Mono<Film> findByTitle(String title) {
        return Mono.justOrEmpty(store.get(title));
    }
}
