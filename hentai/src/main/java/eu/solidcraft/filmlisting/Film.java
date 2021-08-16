package eu.solidcraft.filmlisting;

import eu.solidcraft.filmlisting.dto.FilmDto;
import eu.solidcraft.filmlisting.dto.FilmTypeDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    NEW_RELEASE, REGULAR, OLD
}
