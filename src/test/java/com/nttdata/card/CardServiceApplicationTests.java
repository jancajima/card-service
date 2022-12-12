package com.nttdata.card;

import com.nttdata.card.document.Card;
import com.nttdata.card.service.CardService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CardServiceApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private CardService cardService;

	@Test
	void register() {
		Card card = new Card("638e2a5282a74f71937756f3", "debit", "4232355687869999", "2022-12-05" , "12-2027", "420", null, "638e04b944c0f53635d3a496");
		webTestClient.post()
				.uri("/card")
				.body(Mono.just(card), Card.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Card.class)
				.consumeWith( response ->{
					Card ca = response.getResponseBody();
					Assertions.assertThat(ca.getCardNumber().equals("4232355687869999")).isTrue();
				});
	}

	@Test
	void findAll() {
		webTestClient.get()
				.uri("/card")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Card.class)
				.consumeWith(response ->{
					Flux<Card> cards = Flux.fromIterable(response.getResponseBody());
					Assertions.assertThat(cards.hasElements());
				});
	}

	@Test
	void findById() {
		Card card = cardService.findById("638e2a5282a74f71937756f3").block();
		webTestClient.get()
				.uri("/card/{id}", Collections.singletonMap("id", card.getId()))
				.exchange()
				.expectStatus().isOk()
				.expectBody(Card.class)
				.consumeWith( response ->{
					Mono<Card> cl = Mono.just(response.getResponseBody());
					Assertions.assertThat(card.getCardNumber()).isEqualTo("4232355687869999");
				});
	}

	@Test
	void modify() {
		Card card = cardService.findById("638e2a5282a74f71937756f3").block();
		card.setExpirationDate("12-2028");
		webTestClient.put()
				.uri("/card")
				.body(Mono.just(card), Card.class)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Card.class)
				.consumeWith( response ->{
					Card ca = response.getResponseBody();
					Assertions.assertThat(ca.getCardNumber().equals("4232355687869999")).isTrue();
					Assertions.assertThat(ca.getExpirationDate().equals("12-2028")).isTrue();
				});
	}

	@Test
	void delete() {
		Card card = cardService.findById("638e2a5282a74f71937756f3").block();

		webTestClient.delete()
				.uri("/card/{id}", Collections.singletonMap("id",card.getId()))
				.exchange()
				.expectStatus().isOk()
				.expectBody();
	}

}
