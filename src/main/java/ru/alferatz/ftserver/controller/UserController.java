package ru.alferatz.ftserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alferatz.ftserver.model.TravelDto;
import ru.alferatz.ftserver.model.UserDto;
import ru.alferatz.ftserver.model.request.AddUserRequest;
import ru.alferatz.ftserver.service.UserService;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

  private final UserService userService;

  @PostMapping("/addUserToSystem")
  public UserDto addUserToSystem(@RequestBody AddUserRequest request) {
    return userService.addUserToSystem(request);
  }
}
