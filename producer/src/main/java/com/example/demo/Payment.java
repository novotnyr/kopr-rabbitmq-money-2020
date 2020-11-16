package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Payment {
    private int amount;

    private String currency;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static Payment randomPayment() {
        List<String> currencies = new ArrayList<>(List.of("EUR", "PLN", "CZK", "HUF"));

        Collections.shuffle(currencies);
        var currency = currencies.get(0);
        var amount = (int) (Math.random() * 1000);

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setCurrency(currency);
        return payment;
    }
}
