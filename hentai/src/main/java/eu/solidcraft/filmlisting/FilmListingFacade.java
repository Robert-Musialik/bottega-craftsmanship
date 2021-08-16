package eu.solidcraft.filmlisting;

import eu.solidcraft.filmlisting.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmListingFacade {
    FilmCreator filmCreator;
    FilmRepository filmRepository;

    public void addFilm(FilmDto... filmDtos) {
        List<Film> films = Arrays.stream(filmDtos)
                .map(filmCreator::create)
                .collect(Collectors.toList());
        filmRepository.saveAll(films);
    }

    public Page<FilmDto> showFilms(Pageable pageable) {
        return filmRepository.findAll(pageable)
                .map(Film::dto);
    }

    public Optional<FilmDto> showDetails(String title) {
        return filmRepository.findByTitle(title)
                .map(Film::dto);
    }
}
