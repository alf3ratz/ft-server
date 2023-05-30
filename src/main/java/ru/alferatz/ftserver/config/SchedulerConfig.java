package ru.alferatz.ftserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.alferatz.ftserver.repository.TravelRepository;
import ru.alferatz.ftserver.service.TravelService;

@Configuration
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class SchedulerConfig {

  private final TravelService travelService;

  @Scheduled(fixedDelay  = 3600000)
  @Async
  public void closeTravelsIfNeeded() {
    travelService.closeTravelsIfNeeded();
  }

}
