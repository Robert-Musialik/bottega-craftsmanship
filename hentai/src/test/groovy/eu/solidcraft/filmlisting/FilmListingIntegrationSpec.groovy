package eu.solidcraft.filmlisting

import eu.solidcraft.base.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FilmListingIntegrationSpec extends IntegrationSpec implements SampleFilms {
    @Autowired FilmListingFacade filmListingFacade

    @WithMockUser
    def "should show films"() {
        given: "there is rambo and commando in the module"
            filmListingFacade.addFilm(rambo, commando)

        when: "user asks for all films"
            ResultActions films = mockMvc.perform(get("/film"))

        then: "module returns both films"
            films.andExpect(status().isOk())
            films.andExpect(content().json("""
            {
                "content": [
                    { "title": "$rambo.title" },
                    { "title": "$commando.title" }
                ]
            }
            """))

        when: "user asks for rambo"
            ResultActions getFilm = mockMvc.perform(get("/film/$rambo.title"))

        then: "module returns rambo details"
            getFilm.andExpect(status().isOk())
            getFilm.andExpect(content().json("""
                { "title": "$rambo.title" },
            """))
    }

    @WithMockUser
    def "should return no film"() {
        when: "user asks for non existing film"
            ResultActions getFilm = mockMvc.perform(get("/film/halflife3"))

        then: "module returns not found"
            getFilm.andExpect(status().isNotFound())
    }
}
