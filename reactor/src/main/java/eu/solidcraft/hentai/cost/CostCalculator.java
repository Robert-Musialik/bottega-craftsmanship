package eu.solidcraft.hentai.cost;

import eu.solidcraft.hentai.rent.dto.RentedFilmDto;
import eu.solidcraft.hentai.rent.dto.RentedFilmTypeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

interface CostCalculator {
    boolean supports(RentedFilmDto rentedFilm);
    BigDecimal calculateCost(RentedFilmDto rentedFilm);
}

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class PeriodPlusOverflowCalculator implements  CostCalculator {
    RentedFilmTypeDto supportedType;
    BigDecimal basePrice;
    int daysForSinglePrice;

    @Override
    public boolean supports(RentedFilmDto rentedFilm) {
        return supportedType.equals(rentedFilm.getType());
    }

    @Override
    public BigDecimal calculateCost(RentedFilmDto rentedFilm) {
        int numberOfDays = rentedFilm.getDays();
        int overflowDays = Math.max(numberOfDays - daysForSinglePrice, 0);
        BigDecimal overflowCost = new BigDecimal(overflowDays).multiply(basePrice);
        return basePrice.add(overflowCost);
    }
}
