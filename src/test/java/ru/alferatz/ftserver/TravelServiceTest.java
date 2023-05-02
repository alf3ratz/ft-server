package ru.alferatz.ftserver;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alferatz.ftserver.model.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.UserRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.repository.entity.UserEntity;
import ru.alferatz.ftserver.service.TravelService;
import ru.alferatz.ftserver.utils.enums.TravelStatus;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
//@RunWith(SpringRunner.class)
//@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

  @InjectMocks
  private TravelService travelService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TravelRepository travelRepository;

  private UserEntity userEntity;
  private String authorEmail;

  @BeforeEach
  public void setup() {
    authorEmail = randomAlphabetic(22);
    userEntity = UserEntity.builder()
        .id(1L)
        .chatId(null)
        .travelId(null)
        .email(authorEmail)
        .build();
  }

  @Test
  public void createTravelTest() {
    // given
    String author = randomAlphabetic(22);
    given(travelRepository.getTravelEntitiesByAuthor(author))
        .willReturn(Optional.empty());
    TravelEntity travel = TravelEntity.builder()
        .author(author)
        .countOfParticipants(1)
        .placeFrom("test_1")
        .placeTo("test_2")
        .id(1L)
        .travelStatus(TravelStatus.CREATED.name())
        .build();
    given(travelRepository.save(travel)).willReturn(travel);

    // when
    TravelDto travelDto = TravelDto.builder()
        .authorEmail(author)
        .countOfParticipants(1)
        .placeFrom("test_1")
        .placeTo("test_2")
        .build();
    TravelEntity savedTravel = travelService.createTravel(travelDto);

    // then
    assertThat(savedTravel).isNotNull();
  }

  @Test
  public void deleteTravelAndUnlinkParticipantsTest() {
    // given
    String participantEmail = randomAlphabetic(22);
    String placeFrom = randomAlphabetic(10);
    String placeTo = randomAlphabetic(10);
    Long travelId = Long.parseLong(randomNumeric(10));
    given(travelRepository.getTravelEntitiesByAuthor(authorEmail))
        .willReturn(Optional.empty());
    UserEntity participant = UserEntity.builder()
        .chatId(null)
        .email(participantEmail)
        .travelId(travelId)
        .build();
    given(userRepository.save(userEntity)).willReturn(userEntity);
    given(userRepository.save(participant)).willReturn(participant);

    TravelEntity travel = TravelEntity.builder()
        .author(authorEmail)
        .countOfParticipants(1)
        .placeFrom(placeFrom)
        .placeTo(placeTo)
        .id(travelId)
        .travelStatus(TravelStatus.CREATED.name())
        .build();
    given(travelRepository.save(travel)).willReturn(travel);

    ConnectToTravelRequest request = ConnectToTravelRequest.builder()
        .email(participantEmail)
        .travelId(travelId).build();
    travelService.addParticipant(request);
    given(userRepository.getUserEntityByEmail(participantEmail)).willReturn(
        Optional.of(participant));
    assertThat(participant.getTravelId()).isEqualTo(travelId);
    // when
    travelService.deleteTravel(travelId);

    // then
    given(userRepository.getUserEntityByEmail(participantEmail)).willReturn(
        Optional.of(participant));
    assertThat(participant.getTravelId()).isNull();
  }
}
