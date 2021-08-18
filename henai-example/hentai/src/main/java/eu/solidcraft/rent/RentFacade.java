package eu.solidcraft.rent;

import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import eu.solidcraft.rent.dto.OperationsOutcomeDto;
import eu.solidcraft.rent.dto.RentRequestDto;
import eu.solidcraft.rent.dto.RentedFilmDto;
import eu.solidcraft.rent.dto.ReturnRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RentFacade {
    RentRepository rentRepository;
    CurrentUserGetter currentUserGetter;
    FilmCollector filmCollector;
    FilmLender filmLender;

    public OperationsOutcomeDto rent(@NonNull RentRequestDto rentRequestDto) {
        return filmLender.rent(rentRequestDto);
    }

    public Page<RentedFilmDto> getMyRents(@NonNull Pageable pageable) {
        String loggedUer = currentUserGetter.getSignedInUserNameOrAnonymous();
        return rentRepository.findAllByRentingUser(loggedUer, pageable)
                .map(RentedFilm::dto);
    }

    public OperationsOutcomeDto returnFilms(@NonNull ReturnRequestDto returnRequest) {
        return filmCollector.moveFilmsFromRentedToReturned(returnRequest);
    }
}
