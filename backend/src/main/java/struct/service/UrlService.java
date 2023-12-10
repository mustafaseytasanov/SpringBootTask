package struct.service;

import struct.model.Url;

import java.util.List;

public interface UrlService {

    void addUrl(Url url);
    void delete(Url url);
    Url getByShortUrl(String shortUrl);
    void editUrl(Url url);
    List<Url> getAll();

}