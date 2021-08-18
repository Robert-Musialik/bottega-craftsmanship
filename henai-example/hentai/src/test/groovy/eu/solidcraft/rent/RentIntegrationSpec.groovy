package eu.solidcraft.rent

import eu.solidcraft.base.IntegrationSpec
import eu.solidcraft.film.FilmFacade
import eu.solidcraft.film.SampleFilms
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RentIntegrationSpec extends IntegrationSpec implements SampleFilms {
    @Autowired FilmFacade filmFacade

    @WithMockUser
    def "should rent and return a film"() {
        given: "system has a film"
            filmFacade.addFilm(rambo)

        when: "user rents a film"
            ResultActions rentAction = mockMvc.perform(post("/rent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {
                        "howManyDays": 3,
                        "titles": ["$rambo.title"]
                    }
                    """)
                    .with(csrf()))
        then: "system answers ok"
            rentAction
                    .andExpect(status().isCreated())
                    .andExpect(content().json("""{ 
                        "outcomes": [ 
                            { "filmTitle": "$rambo.title", "successful": true} 
                        ] 
                    }"""))

        when: "user asks for her rents"
            ResultActions getRentedAction = mockMvc.perform(get("/rent"))
        then: "system return rented film"
            getRentedAction
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                    {
                        "content": [
                            { "title": "$rambo.title" }
                        ]   
                    }
                    """))

        when: "user returns the film"
            ResultActions returnAction = mockMvc.perform(delete("/rent/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""" { "titles": ["$rambo.title"] } """)
                    .with(csrf()))
        then: "system answers ok"
            returnAction
                    .andExpect(status().isOk())
                    .andExpect(content().json("""{ 
                        "outcomes": [ 
                            { "filmTitle": "$rambo.title", "successful": true} 
                        ] 
                    }"""))

        when: "user asks for her rents"
            getRentedAction = mockMvc.perform(get("/rent"))
        then: "no films are rented"
            getRentedAction
                    .andExpect(status().isOk())
                    .andExpect(content().json(""" { "content": [] } """))
    }
}
