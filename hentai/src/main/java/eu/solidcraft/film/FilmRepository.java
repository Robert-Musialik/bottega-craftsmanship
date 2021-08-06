package eu.solidcraft.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

interface FilmRepository extends Repository<Film, String> {
    void save(Film film);
    Optional<Film> findByTitle(String title);
    Page<Film> findAll(Pageable pageable);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryFilmRepository implements FilmRepository {
    List<Film> store = new ArrayList<>();

    @Override
    public void save(Film film) {
        store.add(film);
    }

    @Override
    public Optional<Film> findByTitle(String title) {
        return store.stream()
                .filter(film -> film.getTitle().equals(title))
                .findFirst();
    }

    @Override
    public Page<Film> findAll(Pageable pageable) {
        return new PageImpl<>(store);
    }
}