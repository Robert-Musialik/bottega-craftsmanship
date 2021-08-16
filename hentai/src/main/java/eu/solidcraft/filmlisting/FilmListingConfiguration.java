package eu.solidcraft.filmlisting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FilmListingConfiguration {

    FilmListingFacade filmListingFacade() {
        FilmRepository filmRepository =  new InMemoryFilmRepository();
        return filmListingFacade(filmRepository);
    }

    @Bean
    FilmListingFacade filmListingFacade(FilmRepository filmRepository) {
        FilmCreator filmCreator = new FilmCreator();
        return new FilmListingFacade(filmCreator, filmRepository);
    }

}
