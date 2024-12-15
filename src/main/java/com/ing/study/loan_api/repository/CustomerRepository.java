package com.ing.study.loan_api.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.ing.study.loan_api.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {
}
