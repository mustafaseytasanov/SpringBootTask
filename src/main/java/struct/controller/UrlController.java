package struct.controller;

import jakarta.servlet.http.HttpServletResponse;
import net.datafaker.Faker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import struct.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import struct.service.UrlService;
import struct.validation.Validator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("")
public class UrlController {

    private final UrlService urlService;
    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    Faker faker = new Faker();

    // 1 - reading all urls
    @GetMapping("/api/urls")
    public ResponseEntity<?> getAll() {
        List<Url> listOfUrls = urlService.getAll();
        return listOfUrls != null && !listOfUrls.isEmpty()
                ? ResponseEntity.ok(listOfUrls)
                : ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 2 - reading of the one url, and redirect.
    @GetMapping("/api/urls/{shortUrl}")
    public ResponseEntity<?> getUrlAndRedirect(@PathVariable String shortUrl,
                                         HttpServletResponse response) throws IOException {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            response.sendRedirect("https://" + url.getUrl());
            return ResponseEntity.ok(url);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 3 - creating
    @PostMapping("/api/adding")
    public ResponseEntity<?> addUrl(@RequestBody Url url) throws MalformedURLException {
        boolean isValidUrl = Validator.isValidURL(url.getUrl());
        if (isValidUrl) {
            if (url.getShortUrl() == null) {
                String randomShortUrl = faker.regexify("[a-z0-9]{10}");
                url.setShortUrl(randomShortUrl);
            }
            urlService.addUrl(url);
            return ResponseEntity.ok().body(HttpStatus.CREATED);
        }
        return ResponseEntity.ok().body("Url is not valid");

    }

    // 4 - deleting by shortUrl
    @DeleteMapping("/api/deleting/{shortUrl}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortUrl) {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            urlService.delete(url);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 5 - updating
    @PutMapping("/api/editing")
    public ResponseEntity<?> editUrl(@RequestBody Url url) throws MalformedURLException {
        if (url.getUrl() == null) {
            return ResponseEntity.ok().body("URL name is empty");
        }
        boolean isValidUrl = Validator.isValidURL(url.getUrl());
        if (!isValidUrl) {
            return ResponseEntity.ok().body("Url is not valid");
        }
        Url url2 = urlService.getByShortUrl(url.getShortUrl());
        if (url2 != null) {
            url2.setUrl(url.getUrl());
            urlService.editUrl(url2);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

}