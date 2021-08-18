package eu.solidcraft.bonus

import eu.solidcraft.base.IntegrationSpec
import eu.solidcraft.film.dto.FilmTypeDto
import eu.solidcraft.rent.dto.FilmWasRentedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.test.context.support.WithMockUser

import static eu.solidcraft.bonus.SampleRentFilmEvents.rentedFilmOfTypeEvent
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BonusIntegrationSpec extends IntegrationSpec {
    @Autowired ApplicationEventPublisher publisher;
    static final String loggedInUser = "Andrzej"

    @WithMockUser(loggedInUser)
    def "should calculate points for rent"() {
        given: "film was rented"
            FilmWasRentedEvent rentedEvent = rentedFilmOfTypeEvent(FilmTypeDto.NEW, loggedInUser)
            publisher.publishEvent(rentedEvent)

        expect: "user gets his points"
            mockMvc.perform(get("/points"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                { "points": 2 }
                """))
    }
}
