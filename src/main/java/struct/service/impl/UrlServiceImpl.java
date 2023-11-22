package struct.service.impl;

import struct.model.Url;
import struct.repository.UrlRepository;
import struct.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Url addUrl(Url url) {
        return urlRepository.saveAndFlush(url);
    }

    @Override
    public void delete(Url url) {
        urlRepository.delete(url);
    }

    @Override
    public Url getByShortUrl(String shortUrl) {
        return urlRepository.findByName(shortUrl);
    }

    @Override
    public Url editUrl(Url url) {
        return urlRepository.saveAndFlush(url);
    }

    @Override
    public List<Url> getAll() {
        return urlRepository.findAll();
    }
}
