package eu.solidcraft.hentai.rent

import eu.solidcraft.hentai.rent.dto.RentedFilmDto
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto
import groovy.transform.CompileStatic

@CompileStatic
class SampleRentedFilm {
    static RentedFilmDto rentedFilm(RentedFilmTypeDto type, int numberOfDays) {
        String title = UUID.randomUUID().toString()
        return new RentedFilmDto(numberOfDays, title, type, "Andrzej")
    }
}
