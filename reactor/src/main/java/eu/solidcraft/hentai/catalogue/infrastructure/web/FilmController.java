package eu.solidcraft.hentai.catalogue.infrastructure.web;

import eu.solidcraft.hentai.catalogue.FilmFacade;
import eu.solidcraft.hentai.catalogue.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@AllArgsConstructor
@RestController
@RequestMapping("/film")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmController {
    FilmFacade filmFacade;

    @GetMapping
    Flux<FilmDto> films() {
        return filmFacade.allFilms();
    }

    @GetMapping("/{title}")
    Mono<ResponseEntity<FilmDto>> details(@PathVariable String title) {
        return filmFacade.details(title)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.defer(this.notFound()));
    }

    private Supplier<Mono<ResponseEntity<FilmDto>>> notFound() {
        return () -> Mono.just(ResponseEntity.notFound().build());
    }
}
