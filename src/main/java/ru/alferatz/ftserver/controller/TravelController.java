package ru.alferatz.ftserver.controller;

import java.util.List;
import java.util.stream.Collectors;
//import javax.validation.constraints.Max;
//import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.TravelDtoFactory;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.service.TravelService;

@RequestMapping("/api/travel")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TravelController {

  private final TravelService travelService;
  private final ConversionService conversionService;
  private final TravelDtoFactory travelDtoFactory;

  @GetMapping("/getAllTravels")
  public Page<TravelDto> getOpenTravels(
      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(10) Integer limit) {

    var openTravelsPair = travelService.getAllOpenTravels(PageRequest.of(offset, limit));
    Page<TravelEntity> openTravels = openTravelsPair.getLeft();
    var map = openTravelsPair.getRight();
    var openTravelList = openTravels
        .stream()
        .map(i -> travelDtoFactory.makeTravelDto(i, map.get(i.getAuthor())))
        .collect(Collectors.toList());
    return new PageImpl<>(openTravelList);

  }

  @PostMapping("/createTravel")
  public TravelDto createTravel(@RequestBody TravelDto travelDto) {
    var newTravelEntity = travelService.createTravel(travelDto);
    return TravelDto.builder()
        .id(newTravelEntity.getId())
        .authorEmail(newTravelEntity.getAuthor())
        .createTime(newTravelEntity.getCreateTime().toString())
        .startTime(newTravelEntity.getStartTime().toString())
        .countOfParticipants(newTravelEntity.getCountOfParticipants())
        .placeFrom(newTravelEntity.getPlaceFrom())
        .placeTo(newTravelEntity.getPlaceTo())
        .chatId(newTravelEntity.getChatId())
        .comment(newTravelEntity.getComment())
        .build();
  }

  @PostMapping("/updateTravel")
  public TravelDto updateTravel(@RequestBody TravelDto travelDto) {
    var resultPair = travelService.updateTravel(travelDto);
    return buildTravelDto(resultPair);
  }


  @PostMapping("/addTraveller")
  public TravelDto addParticipantToTravel(@RequestBody ConnectToTravelRequest request) {
    var resultPair = travelService.addParticipant(request);
    return buildTravelDto(resultPair);
  }

  @PostMapping("/reduceTravaller")
  public TravelDto reduceParticipantFromTravel(@RequestBody ConnectToTravelRequest request) {
    var resultPair = travelService.reduceParticipant(request);
    return buildTravelDto(resultPair);
  }

  @Deprecated
  @PostMapping("/addTravellerOld")
  public TravelDto addParticipantToTravelOld(@RequestBody TravelDto travelDto) {
    var updatedTravelEntity = travelService.addParticipantOld(travelDto);
    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }

  @Deprecated
  @PostMapping("/reduceTravallerOld")
  public TravelDto reduceParticipantFromTravelOld(@RequestBody TravelDto travelDto) {
    var updatedTravelEntity = travelService.reduceParticipantOld(travelDto);
    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }


  @PostMapping("/deleteTravel")
  public Integer deleteTravel(@RequestParam Long travelId) {
    return travelService.deleteTravel(travelId);
  }

  @GetMapping("/getTravelById")
  public TravelDto getTravelById(@RequestParam(value = "travelId") Long travelId) {
    var resultPair = travelService.getTravelById(travelId);
    return buildTravelDto(resultPair);
  }

  @GetMapping("/getTravelByUserEmail")
  public TravelDto getTravelById(@RequestParam(value = "userEmail") String email) {
    var resultPair = travelService.getTravelByUserEmail(email);
    return buildTravelDto(resultPair);
  }

  private TravelDto buildTravelDto(Pair<TravelEntity, List<UserDto>> resultPair) {
    TravelEntity travelEntity = resultPair.getLeft();
    var userList = resultPair.getRight();
    return TravelDto.builder()
        .id(travelEntity.getId())
        .authorEmail(travelEntity.getAuthor())
        .createTime(travelEntity.getCreateTime().toString())
        .startTime(travelEntity.getStartTime().toString())
        .countOfParticipants(travelEntity.getCountOfParticipants())
        .placeFrom(travelEntity.getPlaceFrom())
        .placeTo(travelEntity.getPlaceTo())
        .participants(userList)
        .chatId(travelEntity.getChatId())
        .comment(travelEntity.getComment())
        .build();
  }


}
