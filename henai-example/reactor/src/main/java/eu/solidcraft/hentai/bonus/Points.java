package eu.solidcraft.hentai.bonus;

import eu.solidcraft.hentai.bonus.dto.PointsDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Document("Points")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class Points {
    @Id
    @Getter
    String username;
    int amount;

    Points plus(Points newPoints) {
        //assuming we don't care whether these new points come from the same user
        this.amount += newPoints.amount;
        return this;
    }

    PointsDto dto() {
        return new PointsDto(username, amount);
    }
}
