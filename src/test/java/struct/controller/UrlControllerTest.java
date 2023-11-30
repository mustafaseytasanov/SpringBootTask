package struct.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import struct.model.Url;
import struct.repository.UrlRepository;
import struct.validation.Validator;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UrlRepository urlRepository;

	@Autowired
	private ObjectMapper om;


	@Test
	public void testGetAll() throws Exception {

		List<Url> list = urlRepository.findAll();
		if (list.isEmpty()) {
			assert true;
			return;
		}
		var result = mockMvc.perform(get("/api/urls"))
				.andExpect(status().isOk())
				.andReturn();
		var body = result.getResponse().getContentAsString();
		assertThatJson(body).isEqualTo(list);
	}

	@Test
	@Transactional
	public void testGetUrlAndRedirect() throws Exception {
		// Init
		String shortUrl = "uber";
		String fullUrl = "www.uber.com";
		Url url = new Url(shortUrl, fullUrl);
		urlRepository.save(url);
		// Request
		var request = get("/api/urls/" + shortUrl);
		var result = mockMvc.perform(request)
				.andExpect(status().is(302))
				.andReturn();
		var body = result.getResponse().getContentAsString();

		// Checking
		assertThatJson(body).isEqualTo(url);

		// Deleting from DB
		urlRepository.delete(url);
	}

	@Test
	@Transactional
	public void testAddUrl() throws Exception {
		// Init
		String shortUrl = "uber";
		String fullUrl = "www.uber.com";
		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", shortUrl);
		data.put("url", fullUrl);
		var request = post("/api/adding")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		// Checking of Url parameters
		var url = urlRepository.findByName(shortUrl);
		assertThat(url.getUrl()).isEqualTo(fullUrl);
		assertThat(url.getShortUrl()).isEqualTo(shortUrl);

		// Deleting of created url from DB
		urlRepository.delete(url);
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {

		var url = Instancio.of(Url.class)
				.ignore(field(Url::getId))
				.create();
		String shortUrl = url.getShortUrl();
		urlRepository.save(url);

		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", shortUrl);
		data.put("url", "www.arsenal.com");

		var request = put("/api/editing")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		// Checking after Url updating
		url = urlRepository.findByName(url.getShortUrl());
		assertThat(url.getUrl()).isEqualTo("www.arsenal.com");

		// Deleting from DB
		urlRepository.delete(url);
	}

	@Test
	@Transactional
	public void testDeleteUrl() throws Exception {

		// Init
		var url = Instancio.of(Url.class)
				.ignore(field(Url::getId))
				.create();
		String shortUrl = url.getShortUrl();
		String fullUrl = url.getUrl();
		urlRepository.save(url);

		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", shortUrl);
		data.put("url", fullUrl);

		var request = delete("/api/deleting/" + shortUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		// Checking status code.
		mockMvc.perform(request)
				.andExpect(status().isOk());
	}
}