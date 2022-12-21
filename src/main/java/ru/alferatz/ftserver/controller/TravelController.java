package ru.alferatz.ftserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.repository.entity.TravelEntity;
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
        return conversionService.convert(newTravelEntity, TravelDto.class);
    }

    @PostMapping("/update")
    public TravelDto updateTravel(@RequestBody TravelDto travelDto) {
        var updatedTravelEntity = travelService.updateTravel(travelDto);
        return conversionService.convert(updatedTravelEntity, TravelDto.class);
    }


}
