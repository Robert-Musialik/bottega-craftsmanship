package eu.solidcraft.hentai.bonus

import eu.solidcraft.hentai.IntegrationSpec
import eu.solidcraft.hentai.rent.dto.RentedFilmDto
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class BonusPointsIntegrationSpec extends IntegrationSpec {
    static final String LOGGED_USER = "Andrzej"

    @Autowired BonusFacade bonusFacade;
    RentedFilmDto rentedFilm = new RentedFilmDto(1, UUID.randomUUID().toString(), RentedFilmTypeDto.NEW, LOGGED_USER)

    @WithMockUser(username = LOGGED_USER)
    def "should calculate bonus points"() {
        given: "film was rented"
            bonusFacade.filmWasRented(rentedFilm).block()

        expect: "when user asks for points, 2 points were added"
            expectGet(
                    "/points",
                    """ {"amount": 2} """)
    }
}
