package eu.solidcraft.hentai.rent.dto;

import lombok.Value;

@Value
public class RentedFilmDto {
    int days;
    String title;
    RentedFilmTypeDto type;
    String username;
}
