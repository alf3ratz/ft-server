package ru.alferatz.ftserver.service;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.utils.enums.TravelStatusEnum;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    public TravelEntity createTravel(TravelDto travelDto) {
        if (!checkNewTravelDto(travelDto)) {
            throw new BadRequestException("Wrong parameter value");
        }
        TravelEntity travelEntity = travelRepository
                .getCurrentTravelByAuthor(
                        travelDto.getAuthor().getUsername());
        if (travelEntity != null) {
            throw new BadRequestException("У пользователя уже имеется активная поездка");
        }
        travelEntity = new TravelEntity(1L, travelDto.getAuthor().getUsername(),
                travelDto.getPlaceFrom(), travelDto.getPlaceTo(),
                travelDto.getCountOfParticipants(), TravelStatusEnum.CREATED.name());
        try {
            travelRepository.save(travelEntity);
        } catch (RuntimeException ex) {
            throw new InternalServerError("Не удалось добавить поездку в базу");
        } finally {
            travelRepository.flush();
        }
        return travelEntity;
    }

    private boolean checkNewTravelDto(TravelDto travelDto) {
        return !travelDto.getPlaceFrom().isEmpty() && !Objects.isNull(travelDto.getPlaceFrom()) &&
                !travelDto.getPlaceTo().isEmpty() && !Objects.isNull(travelDto.getPlaceTo()) &&
                travelDto.getCountOfParticipants() >= 0 && travelDto.getCountOfParticipants() <= 4;
    }

    public TravelEntity updateTravel(TravelDto travelDto) {
        if (!checkNewTravelDto(travelDto)) {
            throw new BadRequestException("Wrong parameter value");
        }
        TravelEntity travelEntity = travelRepository
                .getCurrentTravelByAuthor(
                        travelDto.getAuthor().getUsername());
        // TODO: дописать проверку
        travelEntity = TravelEntity.builder()
                .countOfParticipants(travelDto.getCountOfParticipants())
                .build();
        travelDto.getParticipants().forEach(i->linkParticipantToTravel(i.getEmail(), travelEntity.getId()));
        try {
            travelRepository.save(travelEntity);
        } catch (RuntimeException ex) {
            throw new InternalServerError("Не удалось обновить поездку в базе");
        } finally {
            travelRepository.flush();
        }
        return travelEntity;
    }

    private void linkParticipantToTravel(String participantEmail, Long travelId){
        var user = userRepository.getUserEntityByEmail(participantEmail);
        user.setTravelId(travelId);
        userRepository.update(user);
    }


}
