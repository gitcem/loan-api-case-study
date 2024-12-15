package com.ing.study.loan_api.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.ing.study.loan_api.model.LoanInstallment;

public interface LoanInstallmentCrudRepository extends CrudRepository<LoanInstallment, UUID> {
}
