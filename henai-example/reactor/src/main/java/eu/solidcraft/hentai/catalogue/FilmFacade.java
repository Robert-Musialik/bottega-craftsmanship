package eu.solidcraft.hentai.catalogue;

import eu.solidcraft.hentai.catalogue.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmFacade {
    FilmCreator filmCreator;
    FilmRepository filmRepository;
    FilmDtoValidator validator;

    public Mono<Void> addFilm(FilmDto... filmDtos) {
        return Flux.fromArray(filmDtos)
                .doOnNext(dto -> log.info("Adding film {}", dto))
                .flatMap(validator::validate)
                .map(filmCreator::create)
                .flatMap(filmRepository::save)
                .doOnNext(film -> log.info("Saved film {}", film))
                .then();
    }

    public Flux<FilmDto> allFilms() {
        return filmRepository.findAll()
                .map(Film::dto);
    }

    public Mono<FilmDto> details(String title) {
        return Mono.just(title)
                .doOnNext(t -> log.info("Searching for details of film " + t))
                .flatMap(filmRepository::findByTitle)
                .doOnNext(film -> log.info("Found film {}", film))
                .map(Film::dto);
    }

}
