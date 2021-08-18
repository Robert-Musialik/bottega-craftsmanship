package eu.solidcraft.rent;

import eu.solidcraft.film.FilmFacade;
import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RentConfiguration {

    RentFacade rentFacade(
            FilmFacade filmFacade,
            CurrentUserGetter currentUserGetter,
            ApplicationEventPublisher publisher) {
        RentRepository rentRepository = new InMemoryRentRepository();
        ReturnRepository returnRepository = new InMemoryReturnRepository();
        return rentFacade(filmFacade, currentUserGetter, publisher, rentRepository, returnRepository);
    }

    @Bean
    RentFacade rentFacade(
            FilmFacade filmFacade,
            CurrentUserGetter currentUserGetter,
            ApplicationEventPublisher publisher,
            RentRepository rentRepository,
            ReturnRepository returnRepository) {
        RentCreator rentCreator =  new RentCreator(filmFacade, currentUserGetter);
        FilmCollector filmCollector = new FilmCollector(currentUserGetter, returnRepository, rentRepository);
        FilmLender filmLender = new FilmLender(publisher, rentCreator, rentRepository);
        return new RentFacade(rentRepository, currentUserGetter, filmCollector, filmLender);
    }
}
