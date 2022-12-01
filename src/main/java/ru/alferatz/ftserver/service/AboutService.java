package ru.alferatz.ftserver.service;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequestMapping("/about")
public class AboutService {


    @GetMapping()
    public String showInfo() {
        return "Приложение по поиску попутчиков.";
    }

}
