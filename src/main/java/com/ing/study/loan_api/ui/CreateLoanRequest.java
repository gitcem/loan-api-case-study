package com.ing.study.loan_api.ui;

import java.util.UUID;

public class CreateLoanRequest {
    private UUID customerId;
    private double loanAmount;
    private double interestRate;
    private int numberOfInstallment;

    public UUID getCustomerId() {
        return customerId;
    }
    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
    public double getLoanAmount() {
        return loanAmount;
    }
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }
    public double getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    public int getNumberOfInstallment() {
        return numberOfInstallment;
    }
    public void setNumberOfInstallment(int numberOfInstallment) {
        this.numberOfInstallment = numberOfInstallment;
    }
}
