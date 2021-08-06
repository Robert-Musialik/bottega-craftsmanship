package eu.solidcraft.hentai.cost;

import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CostConfiguration {

    @Bean
    CostFacade costFacade() {
        BigDecimal basePrice = BigDecimal.valueOf(30);
        BigDecimal premiumPrice = BigDecimal.valueOf(40);
        CostCalculator oldFilmCalculator = new PeriodPlusOverflowCalculator(RentedFilmTypeDto.OLD, basePrice, 5);
        CostCalculator regularFilmCalculator = new PeriodPlusOverflowCalculator(RentedFilmTypeDto.REGULAR, basePrice, 3);
        CostCalculator newFilmCalculator = new PeriodPlusOverflowCalculator(RentedFilmTypeDto.NEW, premiumPrice, 1);
        List<CostCalculator> calculators = List.of(oldFilmCalculator, regularFilmCalculator, newFilmCalculator);
        return new CostFacade(calculators);
    }
}
