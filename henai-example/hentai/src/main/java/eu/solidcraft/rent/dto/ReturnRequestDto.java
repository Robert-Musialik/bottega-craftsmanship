package eu.solidcraft.rent.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Value
public class ReturnRequestDto {
    @NotNull List<String> titles;
}
