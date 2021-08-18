package eu.solidcraft.bonus;

import eu.solidcraft.bonus.dto.BonusPointsDto;
import eu.solidcraft.rent.dto.FilmWasRentedEvent;
import eu.solidcraft.rent.dto.FilmWasRentedEvent.RentedFilmTypeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class BonusPoints {
    @Id
    @Getter
    String username;
    int points;

    static BonusPoints firstRent(String userRentingFilm) {
        return new BonusPoints(userRentingFilm, 0);
    }

    BonusPointsDto dto() {
        return new BonusPointsDto(points);
    }

    public BonusPoints newRentedFilm(FilmWasRentedEvent filmWasRentedEvent) {
        this.points += (filmWasRentedEvent.getType() == RentedFilmTypeDto.NEW) ? 2 : 1;
        return this;
    }
}
