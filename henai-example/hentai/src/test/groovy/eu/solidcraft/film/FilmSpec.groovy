package eu.solidcraft.film

import eu.solidcraft.film.dto.FilmDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class FilmSpec extends Specification implements SampleFilms {
    FilmFacade filmFacade = new FilmConfiguration().filmFacade()

    def "should return film"() {
        given: "module has film Rambo"
            filmFacade.addFilm(rambo)
        when: "user asks for details of Rambo"
            Optional<FilmDto> returnedFilm = filmFacade.showFilm(rambo.title)
        then: "module return film Rambo details"
            returnedFilm.get().title == rambo.title
    }

    def "when there is no such film, should not return this film"() {
        when: "user asks for Commando"
            Optional<FilmDto> returnedFilm = filmFacade.showFilm("Commando")
        then: "moduler returns nothing"
            returnedFilm.isEmpty()
    }

    def "should return films"() {
        given: "module has two films"
            filmFacade.addFilm(commando, rambo)
        when: "user asks for films"
            Page<FilmDto> foundFilms = filmFacade.showFilms(PageRequest.of(0, 10))
        then: "module should return films"
            foundFilms.sort()*.title == [rambo, commando].sort()*.title
    }

}
