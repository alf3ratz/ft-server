package ru.alferatz.ftserver.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.request.ConnectToTravelRequest;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.TravelDtoFactory;
import ru.alferatz.ftserver.model.request.SetLeadershipRequest;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
import ru.alferatz.ftserver.service.TravelService;

@RequestMapping("/api/travel")
@RestController
@RequiredArgsConstructor
//@CrossOrigin(exposedHeaders = {"Access-Control-Allow-Origin","Access-Control-Allow-Credentials"})
@PreAuthorize("hasAuthority('COMPANY_CREATE')")
public class TravelController {

  private final TravelService travelService;
  private final TravelDtoFactory travelDtoFactory;

  @GetMapping("/getAllTravels")
  public Page<TravelDto> getOpenTravels(
      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(10) Integer limit) {

    SecurityContext context = SecurityContextHolder.getContext();
    Authentication token = SecurityContextHolder.getContext().getAuthentication();
    var openTravelsPair = travelService.getAllOpenTravels(PageRequest.of(offset, limit));
    Page<TravelEntity> openTravels = openTravelsPair.getLeft();
    var map = openTravelsPair.getRight();
    var openTravelList = openTravels
        .stream()
        .map(i -> travelDtoFactory.makeTravelDto(i, map.get(i.getAuthor())))
        .collect(Collectors.toList());
    return new PageImpl<>(openTravelList, PageRequest.of(offset, limit), openTravelList.size());

  }

  @GetMapping("/getTravelHistoryByAuthor")
  public Page<TravelDto> getTravelHistoryByAuthor(
      @RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(10) Integer limit,
      @RequestParam("authorEmail") String authorEmail) {
    var closedTravelsPair = travelService
        .getTravelHistoryByEmail(authorEmail);
    List<TravelEntity> closedTravels = closedTravelsPair.getLeft();
    var map = closedTravelsPair.getRight();
    var closedTravelList = closedTravels
        .stream()
        .map(i -> travelDtoFactory.makeTravelDto(i, map.get(i.getId())))
        .collect(Collectors.toList());
    return new PageImpl<>(closedTravelList, PageRequest.of(offset, limit), closedTravelList.size());

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
    return travelService.updateTravel(travelDto);
  }


  @PostMapping("/addTraveller")
  public TravelDto addParticipantToTravel(@RequestBody ConnectToTravelRequest request) {
    return travelService.addParticipant(request);
  }

  @PostMapping("/reduceTravaller")
  public TravelDto reduceParticipantFromTravel(@RequestBody ConnectToTravelRequest request) {
    return travelService.reduceParticipant(request);
  }

  @PostMapping("/deleteTravel")
  public Long deleteTravel(@RequestParam("travelId") Long travelId) {
    return travelService.deleteTravel(travelId);
  }

  @PostMapping("/startTravel")
  public TravelDto startTravel(@RequestParam("travelId") Long travelId) {
    return travelService.startTravel(travelId);
  }

  @PostMapping("/stopTravel")
  public TravelDto stopTravel(@RequestParam("travelId") Long travelId) {
    return travelService.stopTravel(travelId);
  }

  @GetMapping("/getTravelById")
  public TravelDto getTravelById(@RequestParam(value = "travelId") Long travelId) {
    return travelService.getTravelById(travelId);
  }

  @GetMapping("/getTravelByUserEmail")
  public TravelDto getTravelById(@RequestParam(value = "userEmail") String email) {
    return travelService.getTravelByUserEmail(email);
  }

  @PostMapping("/setLeadershipToParticipant")
  public TravelDto setLeadershipToParticipant(@RequestBody SetLeadershipRequest request) {
    return travelService
        .setLeadershipToParticipant(request.getTravelId(), request.getParticipantEmail());
  }


}
