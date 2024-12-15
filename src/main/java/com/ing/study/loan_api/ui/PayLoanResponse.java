package com.ing.study.loan_api.ui;

public class PayLoanResponse {
    private int numberOfPaidInstallments;
    private double totalAmountSpent;
    private double remainingAmount;
    private double totalPaidCreditLimit;
    private boolean isLoanPaidCompletely;
    
    public int getNumberOfPaidInstallments() {
        return numberOfPaidInstallments;
    }
    public void setNumberOfPaidInstallments(int numberOfPaidInstallments) {
        this.numberOfPaidInstallments = numberOfPaidInstallments;
    }
    public double getTotalAmountSpent() {
        return totalAmountSpent;
    }
    public void setTotalAmountSpent(double totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }
    public double getRemainingAmount() {
        return remainingAmount;
    }
    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    public double getTotalPaidCreditLimit() {
        return totalPaidCreditLimit;
    }
    public void setTotalPaidCreditLimit(double totalPaidCreditLimit) {
        this.totalPaidCreditLimit = totalPaidCreditLimit;
    }
    public boolean isLoanPaidCompletely() {
        return isLoanPaidCompletely;
    }
    public void setLoanPaidCompletely(boolean isLoanPaidCompletely) {
        this.isLoanPaidCompletely = isLoanPaidCompletely;
    }
}
