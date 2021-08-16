package eu.solidcraft.filmlisting

import eu.solidcraft.filmlisting.dto.FilmDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class FilmListingSpec extends Specification implements SampleFilms {
    FilmListingFacade filmListingFacade = new FilmListingConfiguration().filmListingFacade()

    def "should list films"() {
         given: "module has two films"
            filmListingFacade.addFilm(rambo, commando)

         when: "user asks for films"
            Page<FilmDto> foundFilms = filmListingFacade.showFilms(PageRequest.of(0, 10))

         then: "both films are returned"
            foundFilms.toSet() == [rambo, commando].toSet()
    }

    def "should return no films when no films are present in the module"() {
        given: "module has no films"

        when: "user asks for films"
            Page<FilmDto> foundFilms = filmListingFacade.showFilms(PageRequest.of(0, 10))

        then: "no films are returned"
            foundFilms.getContent().isEmpty()
    }

    def "should return film details"() {
        given: "module has rambo and commando"
            filmListingFacade.addFilm(rambo, commando)

        when: "user asks for rambo details"
            Optional<FilmDto> foundFilm = filmListingFacade.showDetails(rambo.title)

        then: "module returns rambo details"
            foundFilm.orElseThrow() == rambo
    }

    def "should not return details of a film that does not exist"() {
        when: "user asks for a film that does not exist"
            Optional<FilmDto> foundFilm = filmListingFacade.showDetails("half life 3")

        then: "module does not return anything"
            foundFilm.isEmpty()
    }


}
