package com.ing.study.loan_api.service;

import java.util.UUID;

public class CreateLoanServiceResponse {
    private UUID loanId;
    private int numberOfInstallment;
    private double amountPerInstallment;

    public UUID getLoanId() {
        return loanId;
    }
    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }
    public int getNumberOfInstallment() {
        return numberOfInstallment;
    }
    public void setNumberOfInstallment(int numberOfInstallment) {
        this.numberOfInstallment = numberOfInstallment;
    }
    public double getAmountPerInstallment() {
        return amountPerInstallment;
    }
    public void setAmountPerInstallment(double amountPerInstallment) {
        this.amountPerInstallment = amountPerInstallment;
    }
}
