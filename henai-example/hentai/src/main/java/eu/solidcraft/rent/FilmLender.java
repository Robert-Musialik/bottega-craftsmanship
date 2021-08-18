package eu.solidcraft.rent;

import eu.solidcraft.rent.dto.OperationsOutcomeDto;
import eu.solidcraft.rent.dto.OperationsOutcomeDto.FilmOperationOutcome;
import eu.solidcraft.rent.dto.RentRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmLender {
    ApplicationEventPublisher publisher;
    RentCreator rentCreator;
    RentRepository rentRepository;

    OperationsOutcomeDto rent(RentRequestDto rentRequestDto) {
        Set<FilmOperationOutcome> rentedFilmsOutcomes = rentRequestDto.getTitles()
                .stream()
                .map(title -> rentSingleFilm(title, rentRequestDto.getHowManyDays()))
                .collect(Collectors.toSet());
        return new OperationsOutcomeDto(rentedFilmsOutcomes);
    }

    private FilmOperationOutcome rentSingleFilm(String title, int howManyDays) {
        return rentCreator.create(title, howManyDays)
            .map(rentedFilm -> rentSingleFilm(rentedFilm, title))
            .orElse(FilmOperationOutcome.failure(title));
    }

    private FilmOperationOutcome rentSingleFilm(RentedFilm rentedFilm, String title) {
        rentRepository.save(rentedFilm);
        publisher.publishEvent(rentedFilm.event());
        return FilmOperationOutcome.success(title);
    }
}
