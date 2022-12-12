package com.nttdata.card.repository;

import com.nttdata.card.document.Card;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Card Repository.
 */
@Repository
public interface CardRepository extends ReactiveMongoRepository<Card, String> {
}
