package eu.solidcraft.hentai.catalogue

import eu.solidcraft.hentai.catalogue.dto.FilmDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

class CatalogueSpec extends Specification implements SampleFilmData {
    FilmFacade filmFacade = new FilmConfiguration().filmFacade()

    def "should return films"() {
        given:
            filmFacade.addFilm(rambo, commando).block()

        when:
            Flux<FilmDto> foundFilms = filmFacade.allFilms()

        then:
            foundFilms.toIterable().sort() == [rambo, commando].sort()
    }

    def "should return film details"() {
        given:
            filmFacade.addFilm(rambo).block()

        when:
            Mono<FilmDto> foundFilm = filmFacade.details(rambo.getTitle())

        then:
            foundFilm.block() == rambo
    }

    def "when module doesn't a a film, should not return details"() {
        expect:
            filmFacade.details("Halflife 3").block() == null
    }
}
