package eu.solidcraft.hentai.cost

import eu.solidcraft.hentai.IntegrationSpec
import eu.solidcraft.hentai.cost.dto.CostDto
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

import static eu.solidcraft.hentai.rent.SampleRentedFilm.rentedFilm
import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.OLD

class CostIntegrationSpec extends IntegrationSpec {
    @Autowired CostFacade costFacade

    def "should calculate cost when called from rent"() {
        when:
            Mono<CostDto> cost = costFacade.calculateCost(rentedFilm(OLD, 1))

        then:
            cost.block().value == 30
    }
}
