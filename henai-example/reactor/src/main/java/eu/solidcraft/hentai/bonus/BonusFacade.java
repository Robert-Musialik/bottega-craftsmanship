package eu.solidcraft.hentai.bonus;

import eu.solidcraft.hentai.bonus.dto.PointsDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmDto;
import eu.solidcraft.hentai.user.UserFacade;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BonusFacade {
    UserFacade userFacade;
    PointsRepository pointsRepository;
    PointsCalculator pointsCalculator;

    public Mono<Void> filmWasRented(RentedFilmDto rentedFilmDto) {
        return Mono.justOrEmpty(rentedFilmDto)
                .flatMap(pointsCalculator::calculate)
                .then();
    }

    public Mono<PointsDto> getMyPoints() {
        return userFacade.getCurrentUserName()
                .flatMap(pointsRepository::findOrCreateZero)
                .map(Points::dto);
    }
}
