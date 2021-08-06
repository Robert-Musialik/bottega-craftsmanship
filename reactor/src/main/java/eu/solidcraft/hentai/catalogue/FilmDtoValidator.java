package eu.solidcraft.hentai.catalogue;

import eu.solidcraft.hentai.catalogue.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmDtoValidator {
    Mono<FilmDto> validate(FilmDto filmDto) {
        return (Objects.isNull(filmDto.getTitle()))
            ? Mono.error(new ValidationException(filmDto))
            : Mono.just(filmDto);
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static class ValidationException extends RuntimeException {
        FilmDto filmDto;
    }
}
