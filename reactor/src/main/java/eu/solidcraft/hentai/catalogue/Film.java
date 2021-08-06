package eu.solidcraft.hentai.catalogue;

import eu.solidcraft.hentai.catalogue.dto.FilmDto;
import eu.solidcraft.hentai.catalogue.dto.FilmTypeDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Document("Films")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
class Film {
    @Id
    @Getter
    String title;
    FilmType type;

    FilmDto dto() {
        return new FilmDto(title, FilmTypeDto.valueOf(type.name()));
    }
}

enum FilmType {
    NEW, REGULAR, OLD
}
