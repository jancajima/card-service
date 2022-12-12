package com.nttdata.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bank Account Document.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDto {

    private String id;
    //number account of the bank account
    private String numberAccount;
    //amount of the bank account
    private float amount;
    //end date of the bank account
    private String endDate;
    //id of the Customer
    private String customerId;
    //type of the bank account
    private String type;
    //quantity of transactions done by the account
    private int numberOfTransactions;
    //Commission of the bank account
    private Integer commission;
    //transaction limit of the bank account
    private Integer transactionLimit;
    //Associated debit card id
    private String debitCardId;
    //If the account is the primary one on debit card
    private boolean isPrimaryAccount;
    //Date of association to debit card
    private String associationDate;
    //creation date of the bank account
    private String creationDate;

}
