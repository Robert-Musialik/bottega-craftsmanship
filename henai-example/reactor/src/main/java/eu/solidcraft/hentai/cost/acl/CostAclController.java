package eu.solidcraft.hentai.cost.acl;

import eu.solidcraft.hentai.cost.dto.CostDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CostAclController {
    @Autowired CostAcl costAcl;

    @PostMapping("cost")
    Mono<CostDto> calculatePriceForUser(@RequestBody CostCalculationRequest calculationRequest) {
        return costAcl.calculateCost(calculationRequest.getTitle(), calculationRequest.getDays());
    }
}

@Value
class CostCalculationRequest {
    String title;
    Integer days;
}
