package eu.solidcraft.hentai

import eu.solidcraft.hentai.infrastructure.Profiles
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity

@TypeChecked
@SpringBootTest(classes = [AppRunner])
@ActiveProfiles([Profiles.TEST])
abstract class IntegrationSpec extends Specification {
    @Autowired
    ApplicationContext context
    WebTestClient webTestClient

    void setup() {
        webTestClient = WebTestClient
                .bindToApplicationContext(this.context)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    /**
     * To perform a post, watch out for csrf()
     * You need to do this like this
     *
     * import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
     *
     * webTestClient
     *         .mutateWith(csrf())
     *         .post()
     *         ...
     *
     * Below you'll find a few generic helping methods (extracted out)
     */

    WebTestClient.ResponseSpec post(String uri, Map<String, Serializable> body) {
        return webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
    }

    static <T> T getBody(WebTestClient.ResponseSpec response, Class<T> aClass, HttpStatus httpStatus = HttpStatus.OK) {
        return response.expectStatus()
                .isEqualTo(httpStatus)
                .expectBody(aClass)
                .returnResult()
                .getResponseBody()
    }

    void expectGet(String uri, String json, HttpStatus httpStatus = HttpStatus.OK) {
        webTestClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(httpStatus)
                .expectBody()
                .json(json)
    }

    static <T> T getBody(WebTestClient webTestClient, String uri, Class<T> aClass, HttpStatus httpStatus = HttpStatus.OK) {
        return webTestClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(httpStatus)
                .expectBody(aClass)
                .returnResult()
                .getResponseBody()
    }

}
