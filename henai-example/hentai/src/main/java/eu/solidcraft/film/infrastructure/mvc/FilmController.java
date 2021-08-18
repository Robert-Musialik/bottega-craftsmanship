package eu.solidcraft.film.infrastructure.mvc;

import eu.solidcraft.film.FilmFacade;
import eu.solidcraft.film.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/film")
class FilmController {
    FilmFacade filmFacade;

    @GetMapping("/{title}")
    ResponseEntity<FilmDto> showFilm(@PathVariable String title) {
        return filmFacade.showFilm(title)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    Page<FilmDto> showFilms(Pageable pageable) {
        return filmFacade.showFilms(pageable);
    }

}
