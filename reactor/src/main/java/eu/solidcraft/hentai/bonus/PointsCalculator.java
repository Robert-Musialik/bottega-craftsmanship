package eu.solidcraft.hentai.bonus;

import eu.solidcraft.hentai.rent.dto.RentedFilmDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class PointsCalculator {
    PointsRepository pointsRepository;

    Mono<Points> calculate(RentedFilmDto dto) {
        return Mono.just(dto)
                .map(this::calculateNewPoints)
                .flatMap(this::addToExistingPoints)
                .flatMap(pointsRepository::save);
    }

    //this is the changing part, on the first business change I'd extract this to external algorithm
    private Points calculateNewPoints(RentedFilmDto dto) {
        int amount = dto.getType().equals(RentedFilmTypeDto.NEW) ? 2 : 1;
        return new Points(dto.getUsername(), amount);
    }

    private Mono<Points> addToExistingPoints(Points newPoints) {
        return pointsRepository.findByUsername(newPoints.getUsername())
                .map(existingPoints -> existingPoints.plus(newPoints))
                .defaultIfEmpty(newPoints);
    }
}
