package struct.service;

import struct.model.Url;

import java.util.List;

public interface UrlService {

    Url addUrl(Url url);
    void delete(Url url);
    Url getByShortUrl(String shortUrl);
    Url editUrl(Url url);
    List<Url> getAll();

}