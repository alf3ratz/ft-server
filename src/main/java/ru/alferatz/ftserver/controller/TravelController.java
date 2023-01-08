package ru.alferatz.ftserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.service.TravelService;

@RequestMapping("/api/fellow")
@RestController
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;
    private final ConversionService conversionService;

    @PostMapping("/create")
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

    @PostMapping("/update")
    public TravelDto updateTravel(@RequestBody TravelDto travelDto) {
        var updatedTravelEntity = travelService.updateTravel(travelDto);
        return conversionService.convert(updatedTravelEntity, TravelDto.class);

    }

    @PostMapping("/delete")
    public Integer deleteTravel(@RequestBody TravelDto travelDto) {
        //var deletedTravelEntity =
        return travelService.deleteTravel(travelDto);
        //return conversionService.convert(deletedTravelEntity, TravelDto.class);
        //return TravelDto.builder().author(new UserDto()).build();
    }


}
