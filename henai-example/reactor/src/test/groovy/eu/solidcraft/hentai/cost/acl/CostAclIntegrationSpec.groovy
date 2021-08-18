package eu.solidcraft.hentai.cost.acl

import eu.solidcraft.hentai.IntegrationSpec
import eu.solidcraft.hentai.catalogue.FilmFacade
import eu.solidcraft.hentai.catalogue.dto.FilmDto
import eu.solidcraft.hentai.catalogue.dto.FilmTypeDto
import eu.solidcraft.hentai.cost.dto.CostDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient

class CostAclIntegrationSpec extends IntegrationSpec {
    String title = UUID.randomUUID().toString()
    @Autowired FilmFacade filmFacade

    @WithMockUser
    def "should calculate cost given only titles and days for user"() {
        given:
            filmFacade.addFilm(new FilmDto(title, FilmTypeDto.NEW)).block()

        when:
            WebTestClient.ResponseSpec response = post("/cost", [title: title, days: 1])

        then:
            CostDto result = getBody(response, CostDto.class)
            result.value == 40
    }
}
