package com.isc.assignment.cms.repository;

import com.isc.assignment.cms.model.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByNationalCode(String nationalCode);
}