package eu.solidcraft.hentai.cost.acl

import eu.solidcraft.hentai.catalogue.FilmFacade
import eu.solidcraft.hentai.catalogue.dto.FilmDto
import eu.solidcraft.hentai.catalogue.dto.FilmTypeDto
import eu.solidcraft.hentai.cost.CostFacade
import eu.solidcraft.hentai.cost.dto.CostDto
import reactor.core.publisher.Mono
import spock.lang.Specification

import static eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto.OLD

class CostAclSpec extends Specification {
    FilmFacade filmFacade = Stub()
    CostFacade costFacade = Mock()
    CostAcl costAcl = new CostAclConfiguration().costAcl(costFacade, filmFacade)
    String title = UUID.randomUUID().toString()

    def "should calculate cost given only titles and days"() {
        given: "there is a film with title"
            filmFacade.details(title) >> Mono.just(new FilmDto(title, FilmTypeDto.OLD))

        when: "users asks for price of that film for 1 day"
            CostDto costDto = costAcl.calculateCost(title, 1).block()

        then: "user receives the price"
            costDto.value == 30

        and: "costFacade was called"
            costFacade.calculateCost{ it.type == OLD && it.days == 1 } >> Mono.just(new CostDto(30))
    }

    def "should not calculate cost when film doesn't exist"() {
        given: "there is no film with given title"
            filmFacade.details(_) >> Mono.empty()
            costFacade.calculateCost(_) >> Mono.empty()

        when: "users asks for price of non existing film for 1 day"
            costAcl.calculateCost("non existing film", 1).block()

        then: "user receives no price"
            thrown(CostAcl.FilmNotFoundException.class)
    }
}
