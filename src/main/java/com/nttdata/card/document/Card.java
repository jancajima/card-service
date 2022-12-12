package com.nttdata.card.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Card document.
 */
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Document(collection = "cards")
public class Card {
    @Id
    public String id;

    //type of card: Debit or Credit
    private String cardType;
    //Card Number
    private String cardNumber;
    //Date of creation of the card
    private String creationDate;
    //Expiration date of the card
    private String expirationDate;
    //Security code of the card
    private String securityCode;
    //ID of the primary account
    private String primaryAccountId;
    //ID of the card owner
    private String customerId;
}
