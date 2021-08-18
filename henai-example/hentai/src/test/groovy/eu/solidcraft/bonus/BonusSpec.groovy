package eu.solidcraft.bonus


import eu.solidcraft.film.SampleFilms
import eu.solidcraft.film.dto.FilmTypeDto
import eu.solidcraft.infrastructure.authentication.CurrentUserGetter
import eu.solidcraft.rent.dto.FilmWasRentedEvent
import spock.lang.Specification
import spock.lang.Unroll

import static eu.solidcraft.bonus.SampleRentFilmEvents.rentedFilmOfTypeEvent

class BonusSpec extends Specification implements SampleFilms {
    CurrentUserGetter currentUserGetter = Stub()
    BonusFacade facade = new BonusConfiguration().bonusFacade(currentUserGetter)
    String username = "Andrzej"

    def "when no film was rented, user should have zero points"() {
        given: "user is logged in"
            currentUserGetter.getSignedInUserNameOrAnonymous() >> username
        expect: "user asks for points, it is zero"
            facade.getMyPoints().getPoints() == 0
    }

    @Unroll
    def "when film of type #filmTypeDto was rented, user should have #expectedPoints points"() {
        given: "user is logged in"
            currentUserGetter.getSignedInUserNameOrAnonymous() >> username

        and: "Andrzej rented a movie"
            FilmWasRentedEvent rentedEvent = rentedFilmOfTypeEvent(filmTypeDto, username)

        when: "module was informed about the rent"
            facade.filmWasRented(rentedEvent)

        then: "points should be calculated"
            facade.getMyPoints().points == expectedPoints

        where:
            filmTypeDto         || expectedPoints
            FilmTypeDto.NEW     || 2
            FilmTypeDto.OLD     || 1
            FilmTypeDto.REGULAR || 1
    }
}
