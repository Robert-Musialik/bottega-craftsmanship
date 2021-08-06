package eu.solidcraft.hentai.cost;

import eu.solidcraft.hentai.cost.dto.CostDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CostFacade {
    List<CostCalculator> calculators;

    public Mono<CostDto> calculateCost(RentedFilmDto... rentedFilmDtos) {
        return Flux.fromArray(rentedFilmDtos)
                .flatMap(this::calculateWithSupportedCalculators)
                .reduce(BigDecimal::add)
                .map(CostDto::new);
    }

    private Mono<BigDecimal> calculateWithSupportedCalculators(RentedFilmDto dto) {
        return Flux.fromIterable(calculators)
                    .filter(costCalculator -> costCalculator.supports(dto))
                    .map(costCalculator -> costCalculator.calculateCost(dto))
                    .next();
    }
}
