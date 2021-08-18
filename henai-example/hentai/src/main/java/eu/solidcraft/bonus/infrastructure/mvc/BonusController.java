package eu.solidcraft.bonus.infrastructure.mvc;

import eu.solidcraft.bonus.BonusFacade;
import eu.solidcraft.bonus.dto.BonusPointsDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BonusController {
    BonusFacade bonusFacade;

    @GetMapping("/points")
    BonusPointsDto getUserPoints() {
        return bonusFacade.getMyPoints();
    }
}
