package eu.solidcraft.hentai.catalogue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class FilmConfiguration {

    FilmFacade filmFacade() {
        FilmRepository filmRepository = new InMemoryFilmRepository();
        return filmFacade(filmRepository);
    }

    @Bean
    FilmFacade filmFacade(FilmRepository filmRepository) {
        FilmCreator filmCreator = new FilmCreator();
        FilmDtoValidator validator = new FilmDtoValidator();
        return new FilmFacade(filmCreator, filmRepository, validator);
    }

}
