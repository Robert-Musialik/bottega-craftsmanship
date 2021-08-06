package eu.solidcraft.hentai.catalogue

import eu.solidcraft.hentai.IntegrationSpec
import eu.solidcraft.hentai.catalogue.dto.FilmDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser

class CatalogueIntegrationSpec extends IntegrationSpec implements SampleFilmData {
    @Autowired FilmFacade filmFacade

    @WithMockUser
    def "get return added films"() {
        given: "there is rambo and commando in the module"
            filmFacade.addFilm(rambo, commando).block()

        when: "user asks for all films"
            List<FilmDto> foundFilms = getBody(webTestClient, "/film", List.class)

        then: "module returns films"
            foundFilms.find { it.title == rambo.title } != null
            foundFilms.find { it.title == commando.title } != null

        and: "user asks for details, module returns details of a film"
            expectGet(
                    "/film/$rambo.title",
                    """                        
                            {"title": "$rambo.title"}                                             
                        """)
    }

    @WithMockUser
    def "should not return film that doesn't exist"() {
        expect:
            webTestClient.get()
                    .uri("/film/HalfLife3")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isNotFound()
    }
}
