package eu.solidcraft.rent;

import eu.solidcraft.film.FilmFacade;
import eu.solidcraft.infrastructure.authentication.CurrentUserGetter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class RentCreator {
    FilmFacade filmFacade;
    CurrentUserGetter currentUserGetter;

    Optional<RentedFilm> create(String title, int howManyDays) {
        String rentingUser = currentUserGetter.getSignedInUserNameOrAnonymous();
        return filmFacade.showFilm(title)
                .map(filmToRent -> RentedFilm.create(filmToRent, howManyDays, rentingUser));
    }
}