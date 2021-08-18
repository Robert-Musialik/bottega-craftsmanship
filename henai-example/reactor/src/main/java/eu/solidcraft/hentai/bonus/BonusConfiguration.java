package eu.solidcraft.hentai.bonus;

import eu.solidcraft.hentai.user.UserFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BonusConfiguration {

    BonusFacade bonusFacade(UserFacade userFacade) {
        PointsRepository pointsRepository = new InMemoryPointsRepository();
        return bonusFacade(userFacade, pointsRepository);
    }

    @Bean
    BonusFacade bonusFacade(UserFacade userFacade, PointsRepository pointsRepository) {
        PointsCalculator pointsCalculator = new PointsCalculator(pointsRepository);
        return new BonusFacade(userFacade, pointsRepository, pointsCalculator);
    }
}
