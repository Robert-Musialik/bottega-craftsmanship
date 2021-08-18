package eu.solidcraft.bonus;

import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import eu.solidcraft.infrastructure.config.Profiles;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableAsync
@Configuration
class BonusConfiguration {
    final static String BONUS_POINTS_TASK_EXECUTOR_NAME = "bonusPointsTaskExecutor";

    BonusFacade bonusFacade(CurrentUserGetter currentUserGetter) {
        BonusPointsRepository bonusPointsRepository = new InMemoryBonusPointsRepository();
        return bonusFacade(currentUserGetter, bonusPointsRepository);
    }

    @Bean
    BonusFacade bonusFacade(CurrentUserGetter currentUserGetter, BonusPointsRepository bonusPointsRepository) {
        return new BonusFacade(currentUserGetter, bonusPointsRepository);
    }

    @Profile(Profiles.EXCEPT_INTEGRATION)
    @Bean
    TaskExecutor bonusPointsTaskExecutor(MeterRegistry meterRegistry) {
        String name = "bonusPointsTaskExecutor";
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setThreadGroupName(name);
        threadPoolTaskScheduler.setPoolSize(30);
        ExecutorServiceMetrics.monitor(meterRegistry, threadPoolTaskScheduler, name, Tags.empty());
        return threadPoolTaskScheduler;
    }

    @Profile(Profiles.INTEGRATION)
    @Bean(BONUS_POINTS_TASK_EXECUTOR_NAME)
    TaskExecutor integrationTaskExecutor() {
        return Runnable::run;
    }
}
