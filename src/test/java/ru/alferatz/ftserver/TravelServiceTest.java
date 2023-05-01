package ru.alferatz.ftserver;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alferatz.ftserver.model.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.service.TravelService;
import ru.alferatz.ftserver.utils.enums.TravelStatus;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@RunWith(SpringRunner.class)
class TravelServiceTest {

  @Autowired
  private TravelService travelService;
  @MockBean
  private TravelRepository travelRepository;

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
//    String authorEmail = randomAlphabetic(22);
//    String participantEmail = randomAlphabetic(22);
//    String placeFrom = randomAlphabetic(10);
//    String placeTo = randomAlphabetic(10);
//    Long travelId = Long.getLong(randomNumeric(10));
//    given(travelRepository.getTravelEntitiesByAuthor(authorEmail))
//        .willReturn(Optional.empty());
//    TravelEntity travel = TravelEntity.builder()
//        .author(authorEmail)
//        .countOfParticipants(1)
//        .placeFrom(placeFrom)
//        .placeTo(placeTo)
//        .id(travelId)
//        .travelStatus(TravelStatus.CREATED.name())
//        .build();
//    given(travelRepository.save(travel)).willReturn(travel);
//
//    ConnectToTravelRequest request = ConnectToTravelRequest.builder()
//        .email(participantEmail)
//        .travelId(travelId).build();
//    travelService.addParticipant(request);
//    // when
//
//    System.out.println(savedTravel);
//    // then - verify the output
//    assertThat(savedTravel).isNotNull();
  }
}
