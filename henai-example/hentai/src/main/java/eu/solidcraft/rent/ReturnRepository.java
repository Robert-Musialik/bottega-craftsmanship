package eu.solidcraft.rent;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

interface ReturnRepository extends Repository<ReturnedFilm, String> {
    ReturnedFilm save(ReturnedFilm returnedFilm);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryReturnRepository implements ReturnRepository {
    Map<String, ReturnedFilm> store = new ConcurrentHashMap<>();

    @Override
    public ReturnedFilm save(ReturnedFilm returnedFilm) {
        store.put(returnedFilm.getId(), returnedFilm);
        return returnedFilm;
    }
}