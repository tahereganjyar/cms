package com.isc.assignment.cms.repository;

import com.isc.assignment.cms.model.entity.Card;
import com.isc.assignment.cms.model.enums.CardTypeEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends CrudRepository<Card, Long> {

    long countByCustomerIdAndIssuerCodeAndTypeAndIsActive(Long customerId,
                                                          String issuerCode,
                                                          CardTypeEnum cardType,
                                                          Boolean active);

    List<Card> findAllByCustomerNationalCodeAndIsActive(String nationalCode, Boolean isActive);

    long countBySerial(String serial);
}