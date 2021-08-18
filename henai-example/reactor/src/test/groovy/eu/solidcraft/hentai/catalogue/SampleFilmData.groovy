package eu.solidcraft.hentai.catalogue

import eu.solidcraft.hentai.catalogue.dto.FilmDto
import eu.solidcraft.hentai.catalogue.dto.FilmTypeDto
import groovy.transform.SelfType
import spock.lang.Specification

@SelfType(Specification)
trait SampleFilmData {
    FilmDto rambo = new FilmDto(UUID.randomUUID().toString(), FilmTypeDto.NEW)
    FilmDto commando = new FilmDto(UUID.randomUUID().toString(), FilmTypeDto.OLD)
}
