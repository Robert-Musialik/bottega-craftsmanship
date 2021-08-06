package eu.solidcraft.hentai.cost.acl;

import eu.solidcraft.hentai.catalogue.FilmFacade;
import eu.solidcraft.hentai.catalogue.dto.FilmDto;
import eu.solidcraft.hentai.cost.CostFacade;
import eu.solidcraft.hentai.cost.dto.CostDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CostAcl {
    CostFacade costFacade;
    FilmFacade filmFacade;

    public Mono<CostDto> calculateCost(String title, Integer numberOfDays) {
            return Mono.just(title)
                    .doOnNext(t -> log.info("Calculating cost for title {}", t))
                    .flatMap(filmFacade::details)
                    .switchIfEmpty(Mono.error(() -> new FilmNotFoundException(title)))
                    .map(filmDto -> createFakeRent(numberOfDays, filmDto))
                    .flatMap(costFacade::calculateCost);
    }

    private RentedFilmDto createFakeRent(Integer numberOfDays, FilmDto filmDto) {
        return new RentedFilmDto(numberOfDays, filmDto.getTitle(), RentedFilmTypeDto.valueOf(filmDto.getType().name()), "anonymous");
    }

    @AllArgsConstructor
    static class FilmNotFoundException extends RuntimeException {
        private String title;
    }
}
