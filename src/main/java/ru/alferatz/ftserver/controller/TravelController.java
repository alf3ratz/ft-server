package ru.alferatz.ftserver.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.service.TravelService;

@RequestMapping("/api/travel")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TravelController {

  private final TravelService travelService;
  private final ConversionService conversionService;

  @GetMapping("/getAllTravels")
  public Page<TravelEntity> getOpenTravels(
      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(10) Integer limit) {
    return travelService.getAllOpenTravels(PageRequest.of(offset, limit));
  }

  @PostMapping("/createTravel")
  public TravelDto createTravel(@RequestBody TravelDto travelDto) {
    var newTravelEntity = travelService.createTravel(travelDto);
    //return conversionService.convert(newTravelEntity, TravelDto.class);
    return TravelDto.builder()
        .author(newTravelEntity.getAuthor())
        .countOfParticipants(newTravelEntity.getCountOfParticipants())
        .placeFrom(newTravelEntity.getPlaceFrom())
        .placeTo(newTravelEntity.getPlaceTo())
        .build();
  }

  @PostMapping("/updateTravel")
  public TravelDto updateTravel(@RequestBody TravelDto travelDto) {
    var resultPair = travelService.updateTravel(travelDto);
    var updatedTravelEntity = resultPair.getLeft();
    var userList = resultPair.getRight();
    return TravelDto.builder()
        .author(updatedTravelEntity.getAuthor())
        .countOfParticipants(updatedTravelEntity.getCountOfParticipants())
        .placeFrom(updatedTravelEntity.getPlaceFrom())
        .placeTo(updatedTravelEntity.getPlaceTo())
        .participants(userList)
        .build();
//    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }


  @PostMapping("/addTraveller")
  public TravelDto addParticipantToTravel(@RequestBody ConnectToTravelRequest request) {
    var resultPair = travelService.addParticipant(request);
    var updatedTravelEntity = resultPair.getLeft();
    var userList = resultPair.getRight();
    return TravelDto.builder()
        .author(updatedTravelEntity.getAuthor())
        .countOfParticipants(updatedTravelEntity.getCountOfParticipants())
        .placeFrom(updatedTravelEntity.getPlaceFrom())
        .placeTo(updatedTravelEntity.getPlaceTo())
        .participants(userList)
        .build();
  }

  @PostMapping("/reduceTravaller")
  public TravelDto reduceParticipantFromTravel(@RequestBody ConnectToTravelRequest request) {
    var resultPair = travelService.reduceParticipant(request);
    var updatedTravelEntity = resultPair.getLeft();
    var userList = resultPair.getRight();
    return TravelDto.builder()
        .author(updatedTravelEntity.getAuthor())
        .countOfParticipants(updatedTravelEntity.getCountOfParticipants())
        .placeFrom(updatedTravelEntity.getPlaceFrom())
        .placeTo(updatedTravelEntity.getPlaceTo())
        .participants(userList)
        .build();
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
    TravelEntity travelEntity = resultPair.getLeft();
    var userList = resultPair.getRight();
    return TravelDto.builder()
        .author(travelEntity.getAuthor())
        .countOfParticipants(travelEntity.getCountOfParticipants())
        .placeFrom(travelEntity.getPlaceFrom())
        .placeTo(travelEntity.getPlaceTo())
        .participants(userList)
        .build();
  }


}
