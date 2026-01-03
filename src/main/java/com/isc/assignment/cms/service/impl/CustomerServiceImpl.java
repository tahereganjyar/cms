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

    /**
     * Retrieves an existing customer by national code or registers a new one if none exists.
     * <p>
     * This method provides an idempotent way to obtain a {@link Customer} instance.
     * If a customer with the given national code already exists, it is returned.
     * Otherwise, a new customer is safely created and persisted.
     * </p>
     *
     * <p>
     * To prevent duplicate customer creation in concurrent environments,
     * a synchronization lock is used during the registration process.
     * </p>
     *
     * @param customer the customer entity to retrieve or register;
     *                 must not be {@code null}
     * @return the existing or newly registered {@link Customer};
     *         never {@code null}
     */
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
