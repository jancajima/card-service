package com.nttdata.card.controller;

import com.nttdata.card.dto.BankAccountDto;
import com.nttdata.card.document.Card;
import com.nttdata.card.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller of Card.
 */
@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;

    //Method to get all the DebitCards
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Card> findAll() {
        return cardService.findAll();
    }

    //Method to insert a new Card
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<Card> register(@RequestBody Card card) {
        return  cardService.register(card);
    }

    //Method to update a Card
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<Card> modify(@RequestBody Card card) {
        return  cardService.update(card);
    }

    //Method to get a Card by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Card> findById(@PathVariable("id") String id) {
        return cardService.findById(id);
    }

    //Method to delete a Card
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return cardService.delete(id);
    }

    //Method to associate a primary account
    @PutMapping("/associatePrimaryAccount/{bankAccountId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Card> associatePrimaryAccount(@PathVariable("bankAccountId") String idAccount) {
        return cardService.associatePrimaryAccount(idAccount);
    }

    //Method to get amount of associated primary account
    @GetMapping("/primaryAccount/{debitCardId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Float> getPrimaryAccountAmount(@PathVariable("debitCardId") String debitCardId) {
        return cardService.getPrimaryAccountAmount(debitCardId);
    }

    //Method to make a payment with debit card
    @PutMapping("/payWithDebitCard/{debitCardId}/{amountToPay}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BankAccountDto> payWithDebitCard(@PathVariable("debitCardId") String debitCardId, @PathVariable("amountToPay") Float amountToPay) {
        return cardService.payWithDebitCard(debitCardId, amountToPay);
    }

    //Method to associate a primary account using kafka
    @PutMapping("/associatePrimaryAccountKafka/{bankAccountId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Card> associatePrimaryAccountKafka(@PathVariable("bankAccountId") String idAccount) {
        return cardService.associatePrimaryAccountKafka(idAccount);
    }
}
