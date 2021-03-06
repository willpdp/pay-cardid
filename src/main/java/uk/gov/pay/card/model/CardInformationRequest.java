package uk.gov.pay.card.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardInformationRequest {

    @JsonProperty("cardNumber")
    private String cardNumber;

    public CardInformationRequest() {}

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        return "CardInformation request. Card number not shown to avoid leaking the card number into logs";
    }
}
