package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.model.entity.Customer;
import com.isc.assignment.cms.repository.CustomerRepository;
import com.isc.assignment.cms.service.api.CustomerService;
import com.isc.assignment.cms.service.api.CustomerValidationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Object customerSaveLock = new Object();

    private final CustomerRepository customerRepository;
    private final CustomerValidationService customerValidationService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerValidationService customerValidationService) {

        this.customerRepository = customerRepository;
        this.customerValidationService = customerValidationService;
    }

    @Override
    public Customer getOrRegisterCustomer(Customer customer) {

        Customer savedCustomer;
        Optional<Customer> customerOptional = customerRepository.findByNationalCode(customer.getNationalCode());
        if (customerOptional.isPresent()) {
            savedCustomer = customerOptional.get();
        } else {
            synchronized (customerSaveLock) {
                savedCustomer = customerRepository.findByNationalCode(customer.getNationalCode())
                        .orElseGet(() -> customerRepository.save(customer));
            }
        }
        return savedCustomer;
    }
}
