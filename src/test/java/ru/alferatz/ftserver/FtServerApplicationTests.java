package ru.alferatz.ftserver;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.alferatz.ftserver.controller.TravelController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@RunWith(SpringRunner.class)
@WebMvcTest(TravelController.class)
class FtServerApplicationTests {

//  @Autowired
//  private MockMvc mockMvc;
//
//  @Test
//  public void testSayHelloWorld() throws Exception {
//    this.mockMvc.perform(get("/").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType("application/json"));
//  }
//
//  @Test
//  void contextLoads() {
//
//  }

}
