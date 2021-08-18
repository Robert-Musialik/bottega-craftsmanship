package eu.solidcraft

import eu.solidcraft.base.IntegrationSpec
import eu.solidcraft.rent.RentFacade
import org.springframework.beans.factory.annotation.Autowired

class EventsSpec extends IntegrationSpec {
    @Autowired RentFacade rentFacade
//
//    def "should fire event and see logs"() {
//        when:
//            rentFacade.rent("Rambo", 5)
//        then:
//            notThrown(RuntimeException)
//    }

    //Please remember that when testing post on controllers, with spring security csrf has to be disabled
    // import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
//        this.mockMvc.perform(post("/login")
//            .param("username", "...")
//            .param("password", "...")
//            .with(csrf()))
}
