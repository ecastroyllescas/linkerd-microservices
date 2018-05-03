package com.github.luismoramedina.meshless;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/books")
@Slf4j
public class MeshlessBooksApplication {

    @Autowired
    RestTemplate restTemplate;

	@Value("${stars.service.uri}")
	private String url;

	public static void main(String[] args) {
		SpringApplication.run(MeshlessBooksApplication.class, args);
	}

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


   @RequestMapping(method = RequestMethod.GET)
   public List<Book> books(@RequestHeader HttpHeaders httpHeaders) {

       log.info("New request!");
       HttpHeaders forwardedHeaders = new HttpHeaders();
       Map<String,String> headerMap = httpHeaders.toSingleValueMap();
       headerMap.keySet().forEach(key -> {
           String value = headerMap.get(key);
           log.info("header: " + key + "->" + value);
           if (key.startsWith("l5d-ctx-")) {
               forwardedHeaders.put(key, Collections.singletonList(value));
           }
       });

      log.info("Before calling " + url);

      HttpEntity entity = new HttpEntity(forwardedHeaders);

      ResponseEntity<Star> stars = restTemplate.exchange(
               url, HttpMethod.GET, entity, Star.class, 1);

      ArrayList<Book> books = new ArrayList<>();
      Book endersGame = new Book();
      endersGame.id = 1;
      endersGame.author = "orson scott card";
      endersGame.title = "Enders game";
      endersGame.year = "1985";
      endersGame.stars = stars.getBody().number;
      books.add(endersGame);
      return books;
   }

   @RequestMapping(value = "/books-no-dep", method = RequestMethod.GET)
   public List<Book> booksNoDep(
      @RequestHeader(value="sec-istio-auth-userinfo", required = false) String userinfo,
      @RequestHeader(value="Authorization", required = false) String auth,
      @RequestHeader(value="X-B3-TraceId", required = false) String traceId,
      @RequestHeader(value="Plain-authorization", required = false) String plainAuth) {

      log.info("userinfo: " + userinfo);
      log.info("traceId: " + traceId);
      log.info("plainAuth: " + plainAuth);
      log.info("auth: " + auth);


      ArrayList<Book> books = new ArrayList<>();
      Book endersGame = new Book();
      endersGame.id = 1;
      endersGame.author = "orson scott card";
      endersGame.title = "Enders game";
      endersGame.year = "1985";
      endersGame.stars = 0;
      books.add(endersGame);
      return books;
   }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Book newBook(@RequestBody Book book) {
        log.info("new book: " + book);
        return book;
    }
}
