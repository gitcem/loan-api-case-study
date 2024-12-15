package com.ing.study.loan_api.ui;

import org.springframework.web.bind.annotation.RestController;

import com.ing.study.loan_api.model.Loan;
import com.ing.study.loan_api.model.LoanInstallment;
import com.ing.study.loan_api.service.CreateLoanServiceResponse;
import com.ing.study.loan_api.service.LoanService;
import com.ing.study.loan_api.service.PayLoanServiceResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class LoanController {
    private LoanService loanService;

    public LoanController(LoanService service) {
        loanService = service;
    }

    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> listLoans(@RequestParam UUID customerId, @RequestParam(name = "is_paid", required = false) Boolean paidFilter) {
        try {
            return new ResponseEntity<>(loanService.listLoans(customerId, paidFilter), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/loans/{id}/installments")
    public ResponseEntity<List<LoanInstallment>> listInstallments(@PathVariable UUID id, @RequestParam(name = "is_paid", required = false) Boolean paidFilter) {
        try {
            return new ResponseEntity<>(loanService.listLoanInstallments(id, paidFilter), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/loans")
    public ResponseEntity<CreateLoanResponse> createLoan(@RequestBody CreateLoanRequest request) {
        try {
            CreateLoanServiceResponse serviceResponse = loanService.createLoan(request.getCustomerId(), request.getLoanAmount(), request.getInterestRate(), request.getNumberOfInstallment());
            
            CreateLoanResponse response = new CreateLoanResponse();
            response.setLoanId(serviceResponse.getLoanId());
            response.setNumberOfInstallment(serviceResponse.getNumberOfInstallment());
            response.setAmountPerInstallment(serviceResponse.getAmountPerInstallment());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/loans/{id}")
    public ResponseEntity<PayLoanResponse> payLoan(@PathVariable UUID id, @RequestBody PayLoanRequest request) {
        try {
            PayLoanServiceResponse serviceResponse = loanService.payLoan(id, request.getAmount());

            PayLoanResponse response = new PayLoanResponse();
            response.setNumberOfPaidInstallments(serviceResponse.getNumberOfPaidInstallments());
            response.setTotalAmountSpent(serviceResponse.getTotalAmountSpent());
            response.setRemainingAmount(serviceResponse.getRemainingAmount());
            response.setTotalPaidCreditLimit(serviceResponse.getTotalPaidCreditLimit());
            response.setLoanPaidCompletely(serviceResponse.isLoanPaidCompletely());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
