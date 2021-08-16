package eu.solidcraft.filmlisting;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

interface FilmRepository extends Repository<Film, String> {
    default void saveAll(List<Film> films) {
        films.stream()
                .forEach(this::save);
    }
    void save(Film film);
    Page<Film> findAll(Pageable pageable);
    Optional<Film> findByTitle(String title);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryFilmRepository implements FilmRepository {
    Map<String, Film> store = new HashMap<>();

    @Override
    public void save(Film film) {
        store.put(film.getTitle(), film);
    }

    @Override
    public Page<Film> findAll(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(store.values()));
    }

    @Override
    public Optional<Film> findByTitle(String title) {
        return Optional.ofNullable(store.get(title));
    }
}
