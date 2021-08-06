package eu.solidcraft.hentai.bonus

import eu.solidcraft.hentai.rent.dto.RentedFilmDto
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto
import eu.solidcraft.hentai.user.UserFacade
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.NEW
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.OLD
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.REGULAR

class BonusSpec extends Specification {
    UserFacade userFacade = Stub()
    BonusFacade bonusFacade = new BonusConfiguration().bonusFacade(userFacade)
    String username = "Andrzej"

    void setup() {
        userIsLoggedIn()
    }

    void userIsLoggedIn() {
        userFacade.getCurrentUserName() >> Mono.just(username)
    }

    @Unroll
    def "renting a film of type #filmType should create #expectedPoints bonus points"() {
        when: "user rented a film"
            bonusFacade.filmWasRented(rentedFilm(filmType)).block()

        then: "bonus points were calculated"
            bonusFacade.getMyPoints().block().amount == expectedPoints
        println "dupa"
        where:
            filmType || expectedPoints
            OLD      || 1
            NEW      || 2
            REGULAR  || 1
    }

    def "renting many films add points to existing amount"() {
        given: "user rented a film"
            bonusFacade.filmWasRented(rentedFilm(NEW)).block()

        when: "user rents another film"
            bonusFacade.filmWasRented(rentedFilm(OLD)).block()

        then: "user has the sum of points of both rents"
            bonusFacade.getMyPoints().block().amount == 3
    }

    def "asking for points without renting anything returns zero points"() {
        expect:
            bonusFacade.getMyPoints().block().amount == 0
    }

    RentedFilmDto rentedFilm(RentedFilmTypeDto type) {
        return new RentedFilmDto(1, UUID.randomUUID().toString(), type, username)
    }
}
