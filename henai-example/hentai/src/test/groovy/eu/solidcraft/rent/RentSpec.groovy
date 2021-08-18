package eu.solidcraft.rent

import eu.solidcraft.rent.dto.OperationsOutcomeDto

class RentSpec extends BaseRentSpec {
    //This spec assumes we are ok with users trying to rent a film that does not exists, as in
    //we don't care, we gonna ignore it
    //Whether this is what you want in production, depends very much on whether you control the client
    //and whether you like waking up at 3am
    //TODO: rent price

    def "should not be able to rent a film that doesn't exist in inventory"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        when: "user rents a film that is not in inventory"
            OperationsOutcomeDto rentOutcome = rentFacade.rent(rentRequest(3, rambo))

        then: "user is informed that the film was not rented"
            !rentOutcome.wasSuccessfulFor(rambo.title)

        and: "film was not rented"
            getMyRents().isEmpty()
    }

    def "should rent films"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        and: "two films exist in inventory"
            exist(rambo, commando)

        when: "user rents two films"
            rentFacade.rent(rentRequest(3, rambo, commando))

        then: "two films are rented"
            getMyRents()*.title.sort() == [rambo, commando]*.title.sort()

        and: "events about rented films were published"
            1 * eventPublisher.publishEvent({it.title == rambo.title} )
            1 * eventPublisher.publishEvent({it.title == commando.title} )
    }

    def "when user tries to rent films, of which only some exist, should only rent existing films"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        and: "only rambo is in inventory"
            exist(rambo)

        when: "user tries to rent rambo and commando"
            OperationsOutcomeDto rentOutcome = rentFacade.rent(rentRequest(3, rambo, commando))

        then: "commando is not rented"
            !rentOutcome.wasSuccessfulFor(commando.title)

        and: "rambo is rented"
            rentOutcome.wasSuccessfulFor(rambo.title)
            getMyRents()*.title == [rambo]*.title

        and: "only rent event for rambo is published"
            1 * eventPublisher.publishEvent({it.title == rambo.title} )
            0 * eventPublisher.publishEvent(_)
    }

    def "when user tries to rent films, and none of them exist, no films get rented"() {
        given: "user is logged in"
            userGetter.getSignedInUserNameOrAnonymous() >> username

        when: "user tries to rents films not in the inventory"
            rentFacade.rent(rentRequest(3, rambo, commando))

        then: "no films are rented"
            getMyRents().isEmpty()

        and: "no rent events were published"
            0 * eventPublisher.publishEvent(_ )
    }
}
