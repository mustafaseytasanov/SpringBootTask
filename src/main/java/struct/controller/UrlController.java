package struct.controller;

import jakarta.servlet.http.HttpServletResponse;
import net.datafaker.Faker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import struct.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import struct.service.UrlService;
import struct.validation.Validator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("")
public class UrlController {

    private final UrlService urlService;
    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    Faker faker = new Faker();

    /*
    Rest API methods
     */
    // 1 - Reading all urls (Rest API).
    @GetMapping(value = "/api/urls", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllRest() {
        List<Url> listOfUrls = urlService.getAll();
        return listOfUrls != null && !listOfUrls.isEmpty()
                ? ResponseEntity.ok(listOfUrls)
                : ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 2 - Reading the one url, and redirect (REST API).
    @GetMapping(value = "/api/urls/{shortUrl}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getUrlAndRedirectRest(@PathVariable String shortUrl,
                                         HttpServletResponse response) throws IOException {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            response.sendRedirect("https://" + url.getUrl());
            return ResponseEntity.ok(url);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 3 - Creating (Rest).
    @PostMapping(value = "/api/adding", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> addUrlRest(@RequestBody Url url) throws MalformedURLException {
        if (url.getUrl() == null) {
            return ResponseEntity.ok().body("null");
        }
        if (!url.getUrl().contains("://")) {
            url.setUrl("https://" + url.getUrl());
        }
        boolean isValidUrl = Validator.isValidURL(url.getUrl());
        if (isValidUrl) {
            if (url.getShortUrl() == null) {
                String randomShortUrl = faker.regexify("[a-z0-9]{10}");
                url.setShortUrl(randomShortUrl);
            }
            urlService.addUrl(url);
            return ResponseEntity.ok().body(HttpStatus.CREATED);
        }
        return ResponseEntity.ok().body(false);
    }

    // 4 - Deleting by shortUrl (Rest).
    @DeleteMapping(value = "/api/deleting/{shortUrl}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteUrlRest(@PathVariable String shortUrl) {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            urlService.delete(url);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    // 5 - Updating short url (Rest).
    @PutMapping(value = "/api/editing/{shortUrl}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> testEditUrlRest(@PathVariable String shortUrl) throws MalformedURLException {
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            String shortUrlNew = faker.regexify("[0-9]{10}");
            url.setShortUrl(shortUrlNew);
            urlService.editUrl(url);
            return ResponseEntity.ok(shortUrlNew);
        }
        return ResponseEntity.ok().body(HttpStatus.NOT_FOUND);
    }

    /*
    Html methods
     */
    // Get - methods (HTML).
    @GetMapping("/urls")
    public ModelAndView getUrlsHtml() {
        List<Url> listOfUrls = urlService.getAll();
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    @GetMapping("/create")
    public ModelAndView beforeAddUrlHtml() {
        List<Url> listOfUrls = urlService.getAll();
        ModelAndView mav = new ModelAndView("creating");
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    @GetMapping("/delete")
    public ModelAndView beforeDeleteUrlHtml() {
        List<Url> listOfUrls = urlService.getAll();
        ModelAndView mav = new ModelAndView("deleting");
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    @GetMapping("/update")
    public ModelAndView beforeUpdateUrlHtml() {
        List<Url> listOfUrls = urlService.getAll();
        ModelAndView mav = new ModelAndView("editing");
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    // Post - methods (HTML).
    // Creating (HTML).
    @PostMapping("/create")
    public ModelAndView addUrlHtml(@RequestParam("url") String fullUrl,
                                   @RequestParam("shortUrl") String shortUrl)
            throws MalformedURLException {

        Url url = new Url(shortUrl, fullUrl);
        if (!fullUrl.contains("://")) {
            url.setUrl("https://" + fullUrl);
        }
        boolean isValidUrl = Validator.isValidURL(url.getUrl());
        List<Url> listOfUrls = urlService.getAll();
        ModelAndView mav = new ModelAndView("creating");
        if (!isValidUrl) {
            mav.addObject("isValid", "Not valid Url");
        } else {
            if (Objects.equals(url.getShortUrl(), "")) {
                String randomShortUrl = faker.regexify("[a-z0-9]{10}");
                url.setShortUrl(randomShortUrl);
            }
            urlService.addUrl(url);
            mav.addObject("isValid", "");
            listOfUrls = urlService.getAll(); // list updating
        }
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    // Deleting (Html).
    @PostMapping("/delete")
    public ModelAndView deleteUrlHtml(@RequestParam("shortUrl") String shortUrl) {
        ModelAndView mav = new ModelAndView("deleting");
        Url url = urlService.getByShortUrl(shortUrl);
        if (url != null) {
            urlService.delete(url);
        }
        List<Url> listOfUrls = urlService.getAll();
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

    // Updating (Html)
    @PostMapping("/update")
    public ModelAndView editUrlHtml(@RequestParam("shortUrl") String shortUrlOld) {

        ModelAndView mav = new ModelAndView("editing");
        String shortUrlNew = faker.regexify("[0-9]{10}");
        Url url = urlService.getByShortUrl(shortUrlOld);
        if (url != null) {
            url.setShortUrl(shortUrlNew);
            urlService.editUrl(url);
        }
        List<Url> listOfUrls = urlService.getAll();
        mav.addObject("listOfUrls", listOfUrls);
        return mav;
    }

}