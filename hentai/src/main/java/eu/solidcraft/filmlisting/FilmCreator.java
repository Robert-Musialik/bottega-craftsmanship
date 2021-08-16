package eu.solidcraft.filmlisting;

import eu.solidcraft.filmlisting.dto.FilmDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmCreator {
    Film create(FilmDto filmDto) {
        return Film.builder()
                .title(filmDto.getTitle())
                .type(FilmType.valueOf(filmDto.getType().name()))
                .build();
    }
}
