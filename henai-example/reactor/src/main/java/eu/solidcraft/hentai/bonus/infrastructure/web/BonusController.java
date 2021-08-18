package eu.solidcraft.hentai.bonus.infrastructure.web;

import eu.solidcraft.hentai.bonus.BonusFacade;
import eu.solidcraft.hentai.bonus.dto.PointsDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BonusController {
    BonusFacade bonusFacade;

    @GetMapping("points")
    Mono<PointsDto> getMyPoints() {
        return bonusFacade.getMyPoints();
    }
}
