package com.isc.assignment.cms.service.api;

import com.isc.assignment.cms.model.entity.Card;
import com.isc.assignment.cms.model.entity.Customer;

import java.util.List;

public interface CardService {

    void registerCard(Card card, Customer customer);

    List<Card> getActiveCardsOfCustomer(String customerNationalCode);

}
