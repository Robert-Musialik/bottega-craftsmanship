package eu.solidcraft.rent;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

interface RentRepository extends Repository<RentedFilm, String> {
    RentedFilm save(RentedFilm rent);
    Page<RentedFilm> findAllByRentingUser(String loggedUer, Pageable pageable);
    Optional<RentedFilm> findByRentingUserAndTitle(String loggedUer, String title);
    void delete(RentedFilm rentedFilm);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryRentRepository implements RentRepository {
    Map<String, RentedFilm> store = new ConcurrentHashMap<>();

    @Override
    public RentedFilm save(RentedFilm rentedFilm) {
        store.put(rentedFilm.getId(), rentedFilm);
        return rentedFilm;
    }

    @Override
    public Page<RentedFilm> findAllByRentingUser(String loggedUer, Pageable pageable) {
        List<RentedFilm> rentsForUser = store.values().stream()
                .filter(rentedFilm -> rentedFilm.isRentedBy(loggedUer))
                .collect(Collectors.toList());
        return new PageImpl<>(rentsForUser);
    }

    @Override
    public Optional<RentedFilm> findByRentingUserAndTitle(String loggedUer, String title) {
        return store.values().stream()
                .filter(rent -> rent.isRentedBy(loggedUer) && rent.hasTitle(title))
                .findFirst();
    }

    @Override
    public void delete(RentedFilm rentedFilm) {
        store.remove(rentedFilm.getId());
    }
}