package struct.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import struct.model.Url;
import struct.repository.UrlRepository;

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

	Faker faker = new Faker();

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
		String randomShortUrl = faker.regexify("[a-z0-9]{10}");
		String randomUrlName = faker.internet().url();
		Url url = new Url(randomShortUrl, randomUrlName);
		urlRepository.save(url);
		// Request
		var request = get("/api/urls/" + randomShortUrl);
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
		String randomShortUrl = faker.regexify("[a-z0-9]{10}");
		String randomUrlName = faker.internet().url();
		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", randomShortUrl);
		data.put("url", randomUrlName);
		var request = post("/api/adding")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		// Checking of Url parameters
		var url = urlRepository.findByName(randomShortUrl);
		assertThat(url.getUrl()).isEqualTo(randomUrlName);
		assertThat(url.getShortUrl()).isEqualTo(randomShortUrl);

		// Deleting of created url from DB
		urlRepository.delete(url);
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {

		var url = Instancio.of(Url.class)
				.ignore(field(Url::getId))
				.supply(Select.field(Url::getUrl), () -> faker.internet().url())
				.create();
		String shortUrl = url.getShortUrl();
		urlRepository.save(url);

		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", shortUrl);
		String randomUrlName = faker.internet().url();
		data.put("url", randomUrlName);

		var request = put("/api/editing")
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		mockMvc.perform(request)
				.andExpect(status().isOk());

		// Checking after Url updating
		url = urlRepository.findByName(url.getShortUrl());
		assertThat(url.getUrl()).isEqualTo(randomUrlName);

		// Deleting from DB
		urlRepository.delete(url);
	}

	@Test
	@Transactional
	public void testDeleteUrl() throws Exception {

		// Init
		var url = Instancio.of(Url.class)
				.ignore(field(Url::getId))
				.supply(Select.field(Url::getUrl), () -> faker.internet().url())
				.create();
		String shortUrl = url.getShortUrl();
		urlRepository.save(url);

		// Actions
		var data = new HashMap<>();
		data.put("shortUrl", shortUrl);

		var request = delete("/api/deleting/" + shortUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(data));

		// Checking status code.
		mockMvc.perform(request)
				.andExpect(status().isOk());
	}
}