package eu.solidcraft.hentai.cost

import eu.solidcraft.hentai.cost.dto.CostDto
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

import static eu.solidcraft.hentai.rent.SampleRentedFilm.rentedFilm
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.NEW
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.OLD
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.REGULAR

class CostSpec extends Specification {
    CostFacade costFacade = new CostConfiguration().costFacade()

    @Unroll
    def "film of type #filmType rented for #numberOfDays days should cost #price"() {
        when:
            Mono<CostDto> cost = costFacade.calculateCost(rentedFilm(filmType, numberOfDays))

        then:
            cost.block().value == price

        where:
            filmType | numberOfDays || price
            OLD      | 7            || 90
            NEW      | 1            || 40
            REGULAR  | 5            || 90
            REGULAR  | 2            || 30
    }

    def "should summarize cost for several films"() {
        when:
            Mono<CostDto> cost = costFacade.calculateCost(rentedFilm(OLD, 1), rentedFilm(NEW, 1))

        then:
            cost.block().value == 70
    }
}
