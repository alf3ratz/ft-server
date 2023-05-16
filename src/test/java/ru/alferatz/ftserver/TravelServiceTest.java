//package ru.alferatz.ftserver;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import lombok.extern.slf4j.Slf4j;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import ru.alferatz.ftserver.chat.entity.ChatRoom;
//import ru.alferatz.ftserver.chat.repository.ChatRoomRepository;
//import ru.alferatz.ftserver.model.request.ConnectToTravelRequest;
//import ru.alferatz.ftserver.model.TravelDto;
//import ru.alferatz.ftserver.repository.TravelRepository;
//import ru.alferatz.ftserver.repository.UserRepository;
//import ru.alferatz.ftserver.repository.entity.TravelEntity;
//import ru.alferatz.ftserver.repository.entity.UserEntity;
//import ru.alferatz.ftserver.service.TravelService;
//import ru.alferatz.ftserver.service.utils.TravelServiceUtils;
//import ru.alferatz.ftserver.utils.enums.TravelStatus;
//
//import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
//import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.willReturn;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
////@SpringBootTest
////@WebAppConfiguration
//@Slf4j
////@TestPropertySource("/application.yml")
////@RunWith(SpringRunner.class)
//@ExtendWith(MockitoExtension.class)
//class TravelServiceTest {
//
//  @Autowired
//  @InjectMocks
//  private TravelService travelService;
//  @Mock
//  private UserRepository userRepository;
//  @Mock
//  private TravelRepository travelRepository;
//  @Mock
//  private ChatRoomRepository chatRoomRepository;
//
//  @Autowired
//  @Mock
//  private TravelServiceUtils travelServiceUtils;
//
//  private UserEntity userEntity;
//  private UserEntity participantEntity;
//  private TravelEntity travelEntity;
//  private ChatRoom chatRoom;
//  private TravelDto travelDto;
//  private String authorEmail;
//  private String participantEmail;
//
//  @BeforeEach
//  void beforeEach() {
//    participantEmail = randomAlphabetic(22);
//    authorEmail = randomAlphabetic(22);
//    String placeFrom = randomAlphabetic(10);
//    String placeTo = randomAlphabetic(10);
//    Long travelId = Long.parseLong(randomNumeric(10));
//    userEntity = UserEntity.builder()
//        .chatId(null)
//        .email(authorEmail)
//        .travelId(null)
//        .build();
//    participantEntity = UserEntity.builder()
//        .chatId(null)
//        .email(participantEmail)
//        .travelId(null)
//        .build();
//    chatRoom = ChatRoom.builder()
//        .author(authorEmail)
//        .build();
//    travelDto = TravelDto.builder()
//        .authorEmail(authorEmail)
//        .countOfParticipants(1)
//        .placeFrom(placeFrom)
//        .startTime(LocalDateTime.now().toString())
//        .placeTo(placeTo)
//        .build();
//    travelEntity = TravelEntity.builder()
//        .id(1L)
//        .author(travelDto.getAuthorEmail())
//        .createTime(LocalDateTime.now())
//        .startTime(LocalDateTime.parse(travelDto.getStartTime()))
//        .placeFrom(travelDto.getPlaceFrom())
//        .placeTo(travelDto.getPlaceTo())
//        .countOfParticipants(travelDto.getCountOfParticipants())
//        .travelStatus(TravelStatus.CREATED.name())
//        .comment(travelDto.getComment())
//        .chatId(chatRoom.getId())
//        .build();
//  }
//
//  @Test
//  void create_travel_test() {
////    String participantEmail = randomAlphabetic(22);
////    String authorEmail = randomAlphabetic(22);
////    String placeFrom = randomAlphabetic(10);
////    String placeTo = randomAlphabetic(10);
////    Long travelId = Long.parseLong(randomNumeric(10));
////    UserEntity author = UserEntity.builder()
////        .chatId(null)
////        .email(authorEmail)
////        .travelId(null)
////        .build();
////    ChatRoom newChatRoom = ChatRoom.builder()
////        .author(authorEmail)
////        .build();
//    given(chatRoomRepository.save(Mockito.any(ChatRoom.class))).willReturn(chatRoom);
//    given(userRepository.getUserEntityByEmail(authorEmail))
//        .willReturn(Optional.of(userEntity));
////    TravelDto travelDto = TravelDto.builder()
////        .authorEmail(authorEmail)
////        .countOfParticipants(1)
////        .placeFrom(placeFrom)
////        .startTime(LocalDateTime.now().toString())
////        .placeTo(placeTo)
////        .build();
////    TravelEntity travelEntity = TravelEntity.builder()
////        .author(travelDto.getAuthorEmail())
////        .createTime(LocalDateTime.now())
////        .startTime(LocalDateTime.parse(travelDto.getStartTime()))
////        .placeFrom(travelDto.getPlaceFrom())
////        .placeTo(travelDto.getPlaceTo())
////        .countOfParticipants(travelDto.getCountOfParticipants())
////        .travelStatus(TravelStatus.CREATED.name())
////        .comment(travelDto.getComment())
////        .chatId(newChatRoom.getId())
////        .build();
//    given(travelRepository.save(Mockito.any(TravelEntity.class))).willReturn(travelEntity);
//    assertNotNull(travelService.createTravel(travelDto));
//  }
//
//  @Test
//  void delete_travel_test() {
//    // Given
//    given(chatRoomRepository.save(Mockito.any(ChatRoom.class))).willReturn(chatRoom);
//    given(userRepository.getUserEntityByEmail(authorEmail))
//        .willReturn(Optional.of(userEntity)).willReturn(Optional.of(participantEntity));
//    given(travelRepository.save(Mockito.any(TravelEntity.class))).willReturn(travelEntity);
//    given(travelRepository.findById(Mockito.any(Long.class))).willReturn(Optional.of(travelEntity));
//    ConnectToTravelRequest request = ConnectToTravelRequest.builder()
//        .email(participantEmail)
//        .travelId(travelEntity.getId())
//        .build();
//    travelService.addParticipant(request);
//    assertNotNull(travelService.deleteTravel(travelEntity.getId()));
//  }
////
////  private UserEntity userEntity;
////  private String authorEmail;
////
////  @BeforeEach
////  public void setup() {
////    authorEmail = randomAlphabetic(22);
////    userEntity = UserEntity.builder()
////        .id(1L)
////        .chatId(null)
////        .travelId(null)
////        .email(authorEmail)
////        .build();
////  }
////
////  @Test
////  public void createTravelTest() {
////    // given
////    String author = randomAlphabetic(22);
////    given(travelRepository.getTravelEntitiesByAuthor(author))
////        .willReturn(Optional.empty());
////    TravelEntity travel = TravelEntity.builder()
////        .author(author)
////        .countOfParticipants(1)
////        .placeFrom("test_1")
////        .placeTo("test_2")
////        .id(1L)
////        .travelStatus(TravelStatus.CREATED.name())
////        .build();
////    given(travelRepository.save(travel)).willReturn(travel);
////
////    // when
////    TravelDto travelDto = TravelDto.builder()
////        .authorEmail(author)
////        .countOfParticipants(1)
////        .placeFrom("test_1")
////        .placeTo("test_2")
////        .build();
////    TravelEntity savedTravel = travelService.createTravel(travelDto);
////
////    // then
////    assertThat(savedTravel).isNotNull();
////  }
////
////  @Test
////  public void deleteTravelAndUnlinkParticipantsTest() {
////    // given
////    String participantEmail = randomAlphabetic(22);
////    String placeFrom = randomAlphabetic(10);
////    String placeTo = randomAlphabetic(10);
////    Long travelId = Long.parseLong(randomNumeric(10));
////    given(travelRepository.getTravelEntitiesByAuthor(authorEmail))
////        .willReturn(Optional.empty());
////    UserEntity participant = UserEntity.builder()
////        .chatId(null)
////        .email(participantEmail)
////        .travelId(travelId)
////        .build();
////    given(userRepository.save(userEntity)).willReturn(userEntity);
////    given(userRepository.save(participant)).willReturn(participant);
////
////    TravelEntity travel = TravelEntity.builder()
////        .author(authorEmail)
////        .countOfParticipants(1)
////        .placeFrom(placeFrom)
////        .placeTo(placeTo)
////        .id(travelId)
////        .travelStatus(TravelStatus.CREATED.name())
////        .build();
////    given(travelRepository.save(travel)).willReturn(travel);
////
////    ConnectToTravelRequest request = ConnectToTravelRequest.builder()
////        .email(participantEmail)
////        .travelId(travelId).build();
////    travelService.addParticipant(request);
////    given(userRepository.getUserEntityByEmail(participantEmail)).willReturn(
////        Optional.of(participant));
////    assertThat(participant.getTravelId()).isEqualTo(travelId);
////    // when
////    travelService.deleteTravel(travelId);
////
////    // then
////    given(userRepository.getUserEntityByEmail(participantEmail)).willReturn(
////        Optional.of(participant));
////    assertThat(participant.getTravelId()).isNull();
////  }
//}
