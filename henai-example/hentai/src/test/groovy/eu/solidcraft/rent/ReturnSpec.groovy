package eu.solidcraft.rent

import eu.solidcraft.film.dto.FilmDto
import eu.solidcraft.rent.dto.OperationsOutcomeDto
import eu.solidcraft.rent.dto.ReturnRequestDto

class ReturnSpec extends BaseRentSpec {
    def "should return films"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        and: "two films exist in inventory"
            exist(rambo, commando)

        and: "user rented two films"
            rentFacade.rent(rentRequest(3, rambo, commando))

        when: "user returned both films"
            rentFacade.returnFilms(returnRequest(rambo, commando))

        then: "user has no rented films"
            getMyRents().isEmpty()
    }

    def "should not be able to return a film that was not rented"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        and: "two films exist in inventory"
            exist(rambo, commando)

        and: "user rented rambo"
            rentFacade.rent(rentRequest(3, rambo))

        when: "user tries to return rambo and commando"
            OperationsOutcomeDto returnOutcome = rentFacade.returnFilms(returnRequest(rambo, commando))

        then: "user is informed, only rambo was successfully returned"
            returnOutcome.wasSuccessfulFor(rambo.title)
            !returnOutcome.wasSuccessfulFor(commando.title)

        and: "user has no rented films"
            getMyRents().isEmpty()
    }

    private ReturnRequestDto returnRequest(FilmDto... filmDtos) {
        return new ReturnRequestDto( filmDtos.collect {it.title} )
    }
}
