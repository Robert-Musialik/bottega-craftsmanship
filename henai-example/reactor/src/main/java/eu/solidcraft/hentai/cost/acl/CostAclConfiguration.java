package eu.solidcraft.hentai.cost.acl;

import eu.solidcraft.hentai.catalogue.FilmFacade;
import eu.solidcraft.hentai.cost.CostFacade;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CostAclConfiguration {

    @Bean
    CostAcl costAcl(CostFacade costFacade, FilmFacade filmFacade) {
        return new CostAcl(costFacade, filmFacade);
    }
}
