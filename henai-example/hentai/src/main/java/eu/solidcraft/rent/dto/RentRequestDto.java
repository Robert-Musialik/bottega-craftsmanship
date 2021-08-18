package eu.solidcraft.rent.dto;

import lombok.Value;

import java.util.List;

@Value
public class RentRequestDto {
    int howManyDays;
    List<String> titles;
}
