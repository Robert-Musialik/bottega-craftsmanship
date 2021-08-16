package eu.solidcraft.filmlisting

import eu.solidcraft.filmlisting.dto.FilmDto
import eu.solidcraft.filmlisting.dto.FilmTypeDto
import groovy.transform.CompileStatic
import groovy.transform.SelfType
import spock.lang.Specification

@SelfType(Specification)
@CompileStatic
trait SampleFilms {
    FilmDto rambo = new FilmDto("Rambo V", FilmTypeDto.NEW_RELEASE)
    FilmDto commando = new FilmDto("Commando", FilmTypeDto.OLD)
}
