package eu.solidcraft.bonus;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

interface BonusPointsRepository extends Repository<BonusPoints, String>  {
    Optional<BonusPoints> findByUsername(String username);
    void save(BonusPoints bonusPoints);
}

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class InMemoryBonusPointsRepository implements BonusPointsRepository {
    Map<String, BonusPoints> store = new HashMap<>();

    @Override
    public Optional<BonusPoints> findByUsername(String username) {
        return Optional.ofNullable(store.get(username));
    }

    @Override
    public void save(BonusPoints bonusPoints) {
        store.put(bonusPoints.getUsername(), bonusPoints);
    }
}