package eu.solidcraft.rent.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class RentedFilmDto {
    String title;
    int days;
    Instant rentedOn;
}
