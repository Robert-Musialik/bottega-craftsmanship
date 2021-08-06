package eu.solidcraft.film.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class FilmDto {
    @NonNull String title;
    @NonNull FilmTypeDto type;
}
