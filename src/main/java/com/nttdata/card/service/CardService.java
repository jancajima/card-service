package com.nttdata.card.service;

import com.nttdata.card.dto.BankAccountDto;
import com.nttdata.card.document.Card;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Card service interface.
 */
public interface CardService {

    public Flux<Card> findAll();

    public Mono<Card> register(Card card);

    public Mono<Card> update(Card card);

    public Mono<Void> delete(String id);

    public Mono<Card> findById(String id);

    public Mono<Card> associatePrimaryAccount(String accountId);

    public Mono<Float> getPrimaryAccountAmount(String debitCardId);

    public Flux<BankAccountDto> payWithDebitCard(String debitCardId, Float amountToPay);

    public Mono<Card> associatePrimaryAccountKafka(String accountId);
}
