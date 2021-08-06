package eu.solidcraft.hentai.catalogue;

import eu.solidcraft.hentai.catalogue.dto.FilmDto;

class FilmCreator {
    Film create(FilmDto filmDto) {
        return Film.builder()
                .title(filmDto.getTitle())
                .type(FilmType.valueOf(filmDto.getType().name()))
                .build();
    }
}
