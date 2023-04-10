package ru.alferatz.travels;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alferatz.travels.model.TravelDto;
import ru.alferatz.travels.repository.TravelRepository;
import ru.alferatz.travels.repository.entity.TravelEntity;
import ru.alferatz.travels.service.TravelService;
import ru.alferatz.travels.utils.enums.TravelStatus;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
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

    System.out.println(travelRepository);
    System.out.println(travelService);

    // when
    TravelDto travelDto = TravelDto.builder()
        .author(author)
        .countOfParticipants(1)
        .placeFrom("test_1")
        .placeTo("test_2")
        .build();
    TravelEntity savedTravel = travelService.createTravel(travelDto);

    System.out.println(savedTravel);
    // then - verify the output
    assertThat(savedTravel).isNotNull();
  }
}
