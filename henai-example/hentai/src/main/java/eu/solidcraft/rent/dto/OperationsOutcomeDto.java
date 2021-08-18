package eu.solidcraft.rent.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

@Value
public class OperationsOutcomeDto {
    Set<FilmOperationOutcome> outcomes;

    public boolean wasSuccessfulFor(@NonNull String title) {
        return outcomes.stream()
                .filter(filmRentOutcome -> filmRentOutcome.getFilmTitle().equals(title))
                .map(FilmOperationOutcome::isSuccessful)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You didn't try to rent " + title + " did you?"));
    }

    @Getter
    public static class FilmOperationOutcome {
        String filmTitle;
        boolean isSuccessful;

        public static FilmOperationOutcome success(String title) {
            return new FilmOperationOutcome(title, true);
        }

        public static FilmOperationOutcome failure(String title) {
            return new FilmOperationOutcome(title, false);
        }

        private FilmOperationOutcome(String filmTitle, boolean isSuccessful) {
            this.filmTitle = filmTitle;
            this.isSuccessful = isSuccessful;
        }
    }
}