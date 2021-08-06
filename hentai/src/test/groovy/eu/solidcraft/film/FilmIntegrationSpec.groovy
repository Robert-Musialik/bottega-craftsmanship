package eu.solidcraft.film

import eu.solidcraft.base.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FilmIntegrationSpec extends IntegrationSpec implements SampleFilms {
    @Autowired FilmFacade filmFacade

    @WithMockUser
    def "should get films"() {
        given: "module has two films"
            filmFacade.addFilm(commando, rambo)

        when: "client asks for all films"
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

        when: "client asks for details of Rambo"
            ResultActions getFilm = mockMvc.perform(get("/film/$rambo.title"))

        then: "module return first Rambo"
            getFilm.andExpect(status().isOk())
                .andExpect(content().json("""
                        {"title":"$rambo.title","type":"$rambo.type"},
                """))

        expect: "when client asks for details of a non existing film, system return 404"
            mockMvc.perform(get("/film/" + UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound())
    }
}
