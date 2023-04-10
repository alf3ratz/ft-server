package ru.alferatz.travels.controller;

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
import ru.alferatz.travels.model.TravelDto;
import ru.alferatz.travels.repository.entity.TravelEntity;
import ru.alferatz.travels.service.TravelService;

@RequestMapping("/api/travel")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class TravelController {

  private final TravelService travelService;
  private final ConversionService conversionService;

  @GetMapping("/getAllTravels")
  public Page<TravelEntity> getOpenTravels(
      @RequestParam(value = "offset", defaultValue = "0")@Min(0)  Integer offset,
      @RequestParam(value = "limit", defaultValue = "10")@Min(1) @Max(10) Integer limit) {
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
    var updatedTravelEntity = travelService.updateTravel(travelDto);
    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }

  @PostMapping("/addTraveller")
  public TravelDto addParticipantToTravel(@RequestBody TravelDto travelDto) {
    var updatedTravelEntity = travelService.addParticipant(travelDto);
    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }

  @PostMapping("/reduceTravaller")
  public TravelDto reduceParticipantFromTravel(@RequestBody TravelDto travelDto) {
    var updatedTravelEntity = travelService.reduceParticipant(travelDto);
    return conversionService.convert(updatedTravelEntity, TravelDto.class);
  }


  @PostMapping("/deleteTravel")
  public Integer deleteTravel(@RequestBody TravelDto travelDto) {
    //var deletedTravelEntity =
    return travelService.deleteTravel(travelDto);
    //return conversionService.convert(deletedTravelEntity, TravelDto.class);
    //return TravelDto.builder().author(new UserDto()).build();
  }


}
