package struct.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import struct.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import struct.service.UrlService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
public class UrlController {

    private final UrlService urlService;
    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // 1 - reading all urls
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        List<Url> listOfUrls = urlService.getAll();
        return listOfUrls != null && !listOfUrls.isEmpty()
                ? ResponseEntity.ok(listOfUrls)
                : ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 2 - reading of the one url, and redirect.
    @GetMapping("/all/{shortUrl}")
    public ResponseEntity<?> getUrlAndRedirect(@PathVariable String shortUrl,
                                         HttpServletResponse response) throws IOException {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            response.sendRedirect("https://" + url.getUrl());
            return ResponseEntity.ok().body(HttpStatus.MOVED_PERMANENTLY);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 3 - creating
    @PostMapping("/adding")
    public ResponseEntity<?> addUrl(@RequestBody Url url) {
        urlService.addUrl(url);
        return ResponseEntity.ok().body(HttpStatus.CREATED);
    }

    // 4 - deleting by shortUrl
    @DeleteMapping("/deleting/{shortUrl}")
    public ResponseEntity<?> deleteUrl(@PathVariable String shortUrl) {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            urlService.delete(url);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 5 -  updating
    @PutMapping("/editing")
    public ResponseEntity<?> editUrl(@RequestBody Url url) {
        Url url2 = urlService.getByShortUrl(url.getShortUrl());
        if (url2 != null) {
            url2.setUrl(url.getUrl());
            urlService.editUrl(url2);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

}