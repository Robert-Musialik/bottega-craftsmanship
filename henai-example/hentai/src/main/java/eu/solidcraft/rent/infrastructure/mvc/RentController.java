package eu.solidcraft.rent.infrastructure.mvc;

import eu.solidcraft.rent.RentFacade;
import eu.solidcraft.rent.dto.OperationsOutcomeDto;
import eu.solidcraft.rent.dto.RentRequestDto;
import eu.solidcraft.rent.dto.RentedFilmDto;
import eu.solidcraft.rent.dto.ReturnRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/rent")
class RentController {
    RentFacade rentFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OperationsOutcomeDto rent(@RequestBody RentRequestDto rentRequestDto) {
        OperationsOutcomeDto rent = rentFacade.rent(rentRequestDto);
        return rent;
    }

    @GetMapping
    Page<RentedFilmDto> getMyRents(Pageable pageable) {
        return rentFacade.getMyRents(pageable);
    }

    @DeleteMapping
    OperationsOutcomeDto returnFilm(@RequestBody ReturnRequestDto returnRequestDto, Errors errors) {
        return rentFacade.returnFilms(returnRequestDto);
    }

}
