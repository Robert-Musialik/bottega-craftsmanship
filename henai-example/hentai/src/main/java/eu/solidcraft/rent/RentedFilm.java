package eu.solidcraft.rent;

import eu.solidcraft.film.dto.FilmDto;
import eu.solidcraft.rent.dto.FilmWasRentedEvent;
import eu.solidcraft.rent.dto.FilmWasRentedEvent.RentedFilmTypeDto;
import eu.solidcraft.rent.dto.RentedFilmDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
class RentedFilm {
    @Id @Getter
    String id;
    String rentingUser;
    String title;
    @Enumerated(EnumType.STRING)
    RentedFilmType type;
    int days;
    Instant rentedOn;
    @Embedded
    Money paidOnRent;

    static RentedFilm create(FilmDto filmDto, int howManyDays, String rentingUser) {
        RentedFilm rentedFilm = new RentedFilm();
        rentedFilm.id = UUID.randomUUID().toString();
        rentedFilm.days = howManyDays;
        rentedFilm.paidOnRent = Money.zero();
        rentedFilm.title = filmDto.getTitle();
        rentedFilm.rentedOn = Instant.now();
        rentedFilm.type = RentedFilmType.valueOf(filmDto.getType().name());
        rentedFilm.rentingUser = rentingUser;
        return rentedFilm;
    }

    FilmWasRentedEvent event() {
        return FilmWasRentedEvent.builder()
                .title(title)
                .type(RentedFilmTypeDto.valueOf(type.name()))
                .username(rentingUser)
                .build();
    }

    RentedFilmDto dto() {
        return new RentedFilmDto(title, days, rentedOn);
    }

    boolean isRentedBy(String username) {
        return this.rentingUser.equals(username);
    }

    boolean hasTitle(String title) {
        return this.title.equals(title);
    }
}

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReturnedFilm  {
    @Id @Getter
    String id;

    Instant returnedOn;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="amount", column = @Column(name = "surchargeAmount")),
            @AttributeOverride(name="currency", column = @Column(name = "surchargeCurrency"))
    })
    Money surchargeOnReturn;

    @Embedded
    RentedFilm rentedFilm;

    static ReturnedFilm create(RentedFilm rentedFilm) {
        ReturnedFilm film = new ReturnedFilm();
        film.id = rentedFilm.getId();
        film.returnedOn = Instant.now();
        film.surchargeOnReturn = Money.zero();
        film.rentedFilm = rentedFilm;
        return film;
    }
}

@NoArgsConstructor
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
class Money {
    BigDecimal amount;
    Currency currency;

    static Currency defaultCurrency = Currency.getInstance("EUR");

    static Money zero() {
        return new Money(BigDecimal.ZERO, defaultCurrency);
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }
}

enum RentedFilmType {
    OLD, NEW, REGULAR
}

