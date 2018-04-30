package com.github.luismoramedina.meshless;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

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
        String proxyHost = System.getenv("HTTP_PROXY");
        if (proxyHost == null) {
            proxyHost = System.getenv("http_proxy");
        }
        log.info("proxy env var: " + proxyHost);
        if (proxyHost != null) {
            String[] proxyAndPort = proxyHost.split(":");
            if (proxyAndPort.length > 1) {
                log.info("Setting proxy: " + proxyHost);
                SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                requestFactory.setProxy(new Proxy(
                        Type.HTTP, new InetSocketAddress(proxyAndPort[0], Integer.parseInt(proxyAndPort[1]))));
                return new RestTemplate(requestFactory);
            }
        }
        log.info("NO PROXY");
        return new RestTemplate();
    }


   @RequestMapping(method = RequestMethod.GET)
   public List<Book> books(
      @RequestHeader(value="sec-istio-auth-userinfo", required = false) String userinfo,
      @RequestHeader(value="Authorization", required = false) String auth,
      @RequestHeader(value="X-B3-TraceId", required = false) String traceId,
      @RequestHeader(value="Plain-authorization", required = false) String plainAuth) {

      log.info("userinfo: " + userinfo);
      log.info("traceId: " + traceId);
      log.info("plainAuth: " + plainAuth);
      log.info("auth: " + auth);

      log.info("Before calling " + url);
      Star stars = restTemplate.getForObject(url, Star.class, 1);

      ArrayList<Book> books = new ArrayList<>();
      Book endersGame = new Book();
      endersGame.id = 1;
      endersGame.author = "orson scott card";
      endersGame.title = "Enders game";
      endersGame.year = "1985";
      endersGame.stars = stars.number;
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
