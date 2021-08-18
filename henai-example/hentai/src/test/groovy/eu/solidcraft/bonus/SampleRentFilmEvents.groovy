package eu.solidcraft.bonus

import eu.solidcraft.film.dto.FilmTypeDto
import eu.solidcraft.rent.dto.FilmWasRentedEvent

class SampleRentFilmEvents {
    static FilmWasRentedEvent rentedFilmOfTypeEvent(FilmTypeDto filmTypeDto, String rentedByWhom) {
        FilmWasRentedEvent rentedEvent = FilmWasRentedEvent.builder()
                .title(UUID.randomUUID().toString())
                .type(FilmWasRentedEvent.RentedFilmTypeDto.valueOf(filmTypeDto.name()))
                .username(rentedByWhom)
                .build()
        return rentedEvent
    }
}
