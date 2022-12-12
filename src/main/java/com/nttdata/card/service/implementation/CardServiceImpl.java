package com.nttdata.card.service.implementation;

import com.google.common.util.concurrent.AtomicDouble;
import com.nttdata.card.dto.BankAccountDto;
import com.nttdata.card.document.Card;
import com.nttdata.card.dto.TransactionDto;
import com.nttdata.card.repository.CardRepository;
import com.nttdata.card.service.CardService;
import com.nttdata.card.service.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Card service implementation.
 */
@Service
public class CardServiceImpl implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    private final Producer producer;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(Producer producer) {
        this.producer = producer;
    }

    @Override
    public Flux<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public Mono<Card> register(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public Mono<Card> update(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public Mono<Void> delete(String id) {
        return cardRepository.deleteById(id);
    }

    @Override
    public Mono<Card> findById(String id) {
        return cardRepository.findById(id);
    }

    @Override
    public Mono<Card> associatePrimaryAccount(String accountId) {
        return this.webClient.build().get().uri("/bankAccount/{bankAccountId}", accountId).retrieve().bodyToMono(BankAccountDto.class)
                .flatMap( x -> this.findById(x.getDebitCardId()))
                .filter(debitcard -> debitcard.getPrimaryAccountId()==null)
                .flatMap(x -> {
                    x.setPrimaryAccountId(accountId);
                    this.webClient.build().put().uri("/bankAccount/primaryAccount/{bankAccountId}", accountId).retrieve().bodyToMono(BankAccountDto.class).subscribe();
                    return update(x);
                });
    }

    @Override
    public Mono<Float> getPrimaryAccountAmount(String debitCardId) {
        return findById(debitCardId).flatMap(card -> {
            return this.webClient.build().get().uri("/bankAccount/{bankAccountId}", card.getPrimaryAccountId()).retrieve().bodyToMono(BankAccountDto.class);
        }).map(BankAccountDto::getAmount);
    }

    @Override
    public Flux<BankAccountDto> payWithDebitCard(String debitCardId, Float amountToPay) {
        AtomicDouble sum = new AtomicDouble();
        AtomicDouble sum2 = new AtomicDouble(amountToPay);
        Flux<BankAccountDto> accounts = this.webClient.build().get().uri("/bankAccount/findAccounts/{debitCardId}",debitCardId).retrieve().bodyToFlux(BankAccountDto.class)
                .filter(account -> account.getAmount() > 0)
                .sort(Comparator.comparing(BankAccountDto::isPrimaryAccount).reversed().thenComparing(x -> LocalDateTime.parse(x.getAssociationDate())))
                .takeUntil(x -> sum.addAndGet(x.getAmount()) >= amountToPay)
                .flatMapSequential(account -> {
                    float transactionAmount = (account.getAmount() - (float)sum2.get()) <= 0 ? account.getAmount() :  (float)sum2.get();
                    float newAmount = account.getAmount() - transactionAmount;
                    account.setAmount(newAmount);
                    sum2.getAndAdd(transactionAmount*-1);
                    TransactionDto t = new TransactionDto(null, LocalDate.now().toString(), transactionAmount, "card payment" , account.getCustomerId(), account.getId(), account.getAmount(), debitCardId);
                    return this.webClient.build().post().uri("/transaction/")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(Mono.just(t), TransactionDto.class)
                            .retrieve()
                            .bodyToFlux(TransactionDto.class)
                            .flatMap(x -> this.webClient.build().put().uri("/bankAccount/update")
                                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                    .body(Mono.just(account), BankAccountDto.class)
                                    .retrieve()
                                    .bodyToFlux(BankAccountDto.class)
                                    .next());
                });

        return accounts;
    }

    @Override
    public Mono<Card> associatePrimaryAccountKafka(String accountId) {
        return this.webClient.build().get().uri("/bankAccount/{bankAccountId}", accountId).retrieve().bodyToMono(BankAccountDto.class)
                .flatMap( x -> this.findById(x.getDebitCardId()))
                .filter(debitCard -> debitCard.getPrimaryAccountId()==null)
                .flatMap(x -> {
                    x.setPrimaryAccountId(accountId);
                    producer.sendPrimaryAccount(accountId);
                    return update(x);
                });
    }




}
