package eu.solidcraft.rent;

import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import eu.solidcraft.rent.dto.OperationsOutcomeDto;
import eu.solidcraft.rent.dto.OperationsOutcomeDto.FilmOperationOutcome;
import eu.solidcraft.rent.dto.ReturnRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmCollector {
    CurrentUserGetter currentUserGetter;
    ReturnRepository returnRepository;
    RentRepository rentRepository;

    OperationsOutcomeDto moveFilmsFromRentedToReturned(ReturnRequestDto returnRequest) {
        String loggedUer = currentUserGetter.getSignedInUserNameOrAnonymous();
        Set<FilmOperationOutcome> returnOutcomeDtos = returnRequest.getTitles()
                .stream()
                .map((title) -> moveFilmFromRentedToReturned(title, loggedUer))
                .collect(Collectors.toSet());
        return new OperationsOutcomeDto(returnOutcomeDtos);
    }

    private FilmOperationOutcome moveFilmFromRentedToReturned(String title, String loggedUer) {
        return rentRepository.findByRentingUserAndTitle(loggedUer, title)
            .map(this::deleteRentedFilm)
            .map(ReturnedFilm::create)
            .map(returnRepository::save)
            .map(returnedFilm -> FilmOperationOutcome.success(title))
            .orElse(FilmOperationOutcome.failure(title));
    }

    private RentedFilm deleteRentedFilm(RentedFilm rentedFilm) {
        rentRepository.delete(rentedFilm);
        return rentedFilm;
    }
}
