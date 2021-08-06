package eu.solidcraft.film;

import eu.solidcraft.film.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmFacade {
    FilmCreator filmCreator;
    FilmRepository filmRepository;

    public void addFilm(@NonNull FilmDto... filmDtos) {
        Arrays.stream(filmDtos)
                .map(filmCreator::create)
                .forEach(filmRepository::save);
    }

    public Optional<FilmDto> showFilm(String title) {
        return filmRepository.findByTitle(title)
                .map(Film::dto);
    }

    public Page<FilmDto> showFilms(Pageable pageable) {
        return filmRepository.findAll(pageable)
                .map(Film::dto);
    }
}
