package eu.solidcraft.bonus;

import eu.solidcraft.bonus.dto.BonusPointsDto;
import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import eu.solidcraft.rent.dto.FilmWasRentedEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import javax.transaction.Transactional;

@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BonusFacade {
    CurrentUserGetter currentUserGetter;
    BonusPointsRepository bonusPointsRepository;

    @EventListener
    @Async(BonusConfiguration.BONUS_POINTS_TASK_EXECUTOR_NAME)
    public void filmWasRented(@NonNull FilmWasRentedEvent filmWasRentedEvent) {
        String userRentingFilm = filmWasRentedEvent.getUsername();
        BonusPoints bonusPointsSoFar  = bonusPointsRepository.findByUsername(userRentingFilm)
                .orElse(BonusPoints.firstRent(userRentingFilm));
        BonusPoints pointsAfterRent = bonusPointsSoFar.newRentedFilm(filmWasRentedEvent);
        bonusPointsRepository.save(pointsAfterRent);
    }

    public BonusPointsDto getMyPoints() {
        String username = currentUserGetter.getSignedInUserNameOrAnonymous();
        return bonusPointsRepository.findByUsername(username)
            .map(BonusPoints::dto)
            .orElse(new BonusPointsDto(0));
    }
}
