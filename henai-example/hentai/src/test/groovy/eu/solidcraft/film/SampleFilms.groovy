package eu.solidcraft.film

import eu.solidcraft.film.dto.FilmDto
import eu.solidcraft.film.dto.FilmTypeDto
import groovy.transform.CompileStatic

@CompileStatic
trait SampleFilms {
    FilmDto rambo = new FilmDto("Rambo", FilmTypeDto.OLD)
    FilmDto commando = new FilmDto("Commando", FilmTypeDto.OLD)
}
