package ru.alferatz.ftserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.utils.enums.TravelStatusEnum;

import java.util.Objects;


@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fellow")
public class TravelService {
    private final TravelRepository travelRepository;

    @PostMapping("/create")
    public void createTravel(@RequestBody TravelDto travelDto) {
        if (travelDto.getPlaceFrom().isEmpty() || Objects.isNull(travelDto.getPlaceFrom())) {
            throw new BadRequestException("PlaceFrom parameter can not be null or empty");
        }
        if (travelDto.getPlaceTo().isEmpty() || Objects.isNull(travelDto.getPlaceTo())) {
            throw new BadRequestException("PlaceTo parameter can not be null or empty");
        }
        if (travelDto.getCountOfParticipants() < 0 || travelDto.getCountOfParticipants() > 4) {
            throw new BadRequestException("Count of passengers can not be more than sits in a car");
        }
        TravelEntity travelEntity = travelRepository
                .getTravelEntitiesByAuthorEqualsAndTravelStatusEquals(
                        travelDto.getAuthor().getUsername(),
                        TravelStatusEnum.IN_PROGRESS.name());
        if (travelEntity != null) {
            throw new BadRequestException("У пользователя уже имеется активная поездка");
        }
        travelEntity = new TravelEntity(1L, travelDto.getAuthor().getUsername(),
                travelDto.getPlaceFrom(), travelDto.getPlaceTo(),
                travelDto.getCountOfParticipants(), TravelStatusEnum.CREATED.name());
        travelRepository.saveAndFlush(travelEntity);
    }


}
