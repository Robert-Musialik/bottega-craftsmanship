package eu.solidcraft.rent

import eu.solidcraft.film.FilmFacade
import eu.solidcraft.film.SampleFilms
import eu.solidcraft.film.dto.FilmDto
import eu.solidcraft.infrastructure.authentication.CurrentUserGetter
import eu.solidcraft.rent.dto.RentRequestDto
import eu.solidcraft.rent.dto.RentedFilmDto
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

abstract class BaseRentSpec extends Specification implements SampleFilms {
    CurrentUserGetter userGetter = Stub()
    FilmFacade filmFacade = Stub()
    ApplicationEventPublisher eventPublisher = Mock()
    RentFacade rentFacade = new RentConfiguration().rentFacade(filmFacade, userGetter, eventPublisher)
    String username = "Andrzej"

    protected List<RentedFilmDto> getMyRents() {
        return rentFacade.getMyRents(PageRequest.of(0, 10)).getContent()
    }

    protected void exist(FilmDto... filmDtos) {
        filmDtos.each { FilmDto film ->
            filmFacade.showFilm(film.title) >> Optional.of(film)
        }
    }

    protected RentRequestDto rentRequest(int howManyDays, FilmDto... filmDtos) {
        return new RentRequestDto(howManyDays, filmDtos*.title)
    }
}
