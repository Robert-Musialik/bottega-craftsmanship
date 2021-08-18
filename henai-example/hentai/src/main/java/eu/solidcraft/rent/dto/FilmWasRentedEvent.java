package eu.solidcraft.rent.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FilmWasRentedEvent {
    String username;
    RentedFilmTypeDto type;
    String title;

    public enum RentedFilmTypeDto {
        OLD, NEW, REGULAR
    }
}
