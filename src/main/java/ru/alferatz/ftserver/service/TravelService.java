package ru.alferatz.ftserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alferatz.ftserver.exceptions.AlreadyExistException;
import ru.alferatz.ftserver.exceptions.BadRequestException;
import ru.alferatz.ftserver.exceptions.InternalServerError;
import ru.alferatz.ftserver.exceptions.NotFoundException;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.utils.enums.TravelStatusEnum;

import java.util.*;


@Service
@Transactional
@RequiredArgsConstructor
public class TravelService {
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final Set<String> statuses = new HashSet<>(Arrays.asList(TravelStatusEnum.CREATED.name(), TravelStatusEnum.PROCESSING.name(), TravelStatusEnum.IN_PROGRESS.name()));

    public TravelEntity createTravel(TravelDto travelDto) {
        if (!checkTravelDto(travelDto)) {
            throw new BadRequestException("Wrong parameter value");
        }
        TravelEntity travelEntity = travelRepository
                .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthor(), statuses);
        if (travelEntity != null) {
            throw new AlreadyExistException("У пользователя уже имеется активная поездка");
        }
        travelEntity = new TravelEntity(1L, travelDto.getAuthor(),
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

    private boolean checkTravelDto(TravelDto travelDto) {
        try {
            return !travelDto.getPlaceFrom().isEmpty() && !Objects.isNull(travelDto.getPlaceFrom()) &&
                    !travelDto.getPlaceTo().isEmpty() && !Objects.isNull(travelDto.getPlaceTo()) &&
                    travelDto.getCountOfParticipants() >= 0 && travelDto.getCountOfParticipants() <= 4 &&
                    !travelDto.getAuthor().isEmpty();
        } catch (RuntimeException ex) {
            throw new BadRequestException("Wrong parameter value");
        }
    }

    public TravelEntity updateTravel(TravelDto travelDto) {
        if (!checkTravelDto(travelDto)) {
            throw new BadRequestException("Wrong parameter value");
        }
        TravelEntity travelEntity = travelRepository
                .getTravelEntityByAuthorAndTravelStatusIn(travelDto.getAuthor(), statuses);

        travelEntity.setCountOfParticipants(travelDto.getCountOfParticipants());
        // TODO: подумать, как обновлять участников без полного цикла for (может быть лишняя работа)
        travelDto.getParticipants().forEach(i -> linkParticipantToTravel(i.getEmail(), travelEntity.getId()));
        try {
            travelRepository.save(travelEntity);
        } catch (RuntimeException ex) {
            throw new InternalServerError("Не удалось обновить поездку в базе");
        } finally {
            travelRepository.flush();
        }
        return travelEntity;
    }

    private void linkParticipantToTravel(String participantEmail, Long travelId) {
        var user = userRepository.getUserEntityByEmail(participantEmail);
        user.setTravelId(travelId);
        userRepository.saveAndFlush(user);
    }

    public Integer deleteTravel(TravelDto travelDto) {
        if (!checkTravelDto(travelDto)) {
            throw new BadRequestException("Wrong parameter value");
        }
        var deletedTravelCount = travelRepository.deleteTravelEntityByAuthor(travelDto.getAuthor());
        if (deletedTravelCount == 0) {
            throw new NotFoundException("Can not delete travel");
        }
        travelRepository.flush();
        return deletedTravelCount;
    }


}
