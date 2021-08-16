package eu.solidcraft.filmlisting.infrastructure.web;

import eu.solidcraft.filmlisting.FilmListingFacade;
import eu.solidcraft.filmlisting.dto.FilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/film")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmListingController {
    FilmListingFacade filmListingFacade;

    @GetMapping
    Page<FilmDto> showFilms(Pageable pageable) {
        return filmListingFacade.showFilms(pageable);
    }

    @GetMapping("/{title}")
    ResponseEntity<FilmDto> showDetails(@PathVariable String title) {
        return ResponseEntity.of(filmListingFacade.showDetails(title));
    }

}
