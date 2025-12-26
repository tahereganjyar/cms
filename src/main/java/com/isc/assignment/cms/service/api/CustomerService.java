package com.isc.assignment.cms.service.api;

import com.isc.assignment.cms.model.entity.Customer;

public interface CustomerService {

    Customer getOrRegisterCustomer(Customer customer);
}
