package com.ing.study.loan_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ing.study.loan_api.model.Customer;
import com.ing.study.loan_api.model.Loan;
import com.ing.study.loan_api.model.LoanInstallment;
import com.ing.study.loan_api.repository.CustomerRepository;
import com.ing.study.loan_api.repository.LoanInstallmentRepository;
import com.ing.study.loan_api.repository.LoanRepository;

@Service
public class LoanService {
    //TODO: Get values from application properties
    private static int MAX_PAYABLE_INSTALLMENT_COUNT = 3;
    private static double MIN_INTEREST_RATE = 0.1;
    private static double MAX_INTEREST_RATE = 0.5;
    private static List<Integer> ALLOWED_NUMBER_OF_INSTALLMENTS = List.of(6, 9, 12, 24);

    private CustomerRepository customerRepository;
    private LoanRepository loanRepository;
    private LoanInstallmentRepository loanInstallmentRepository;

    public LoanService(CustomerRepository custRepository, LoanRepository repository, LoanInstallmentRepository installmentRepository) {
        customerRepository = custRepository;
        loanRepository = repository;
        loanInstallmentRepository = installmentRepository;
    }

    public List<Loan> listLoans(UUID customerId, Boolean paidFilter) throws Exception {
        return loanRepository.findByCustomer(customerId, paidFilter);
    }

    public List<LoanInstallment> listLoanInstallments(UUID loanId, Boolean paidFilter) throws Exception {
        return loanInstallmentRepository.findByLoan(loanId, paidFilter);
    }

    public CreateLoanServiceResponse createLoan(UUID customerId, double loanAmount, double interestRate, int numberOfInstallment) throws Exception {
        if (interestRate < MIN_INTEREST_RATE || interestRate > MAX_INTEREST_RATE) {
            throw new IllegalArgumentException("This interest rate is not supported");
        }

        if (!ALLOWED_NUMBER_OF_INSTALLMENTS.contains(numberOfInstallment)) {
            throw new IllegalArgumentException("This installment number is not supported");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException("Customer with id (" + customerId.toString() + ") does not exist");
        }

        Customer customer = optionalCustomer.get();

        if ((customer.getCreditLimit() - customer.getUsedCreditLimit()) < loanAmount) {
            throw new IllegalArgumentException("Customer does not have enough credit limit");
        }

        // Ideally, all updates on customer, loan and installment tables should be made transactional, but I kept it simple for this study

        // Creating loan record
        Loan newLoan = new Loan();
        newLoan.setCustomerId(customerId);
        newLoan.setLoanAmount(loanAmount);
        newLoan.setNumberOfInstallment(numberOfInstallment);
        newLoan.setCreateDate(LocalDateTime.now());
        newLoan.setPaid(false);

        UUID newLoanId = loanRepository.createLoan(newLoan);

        // Calculating amount per installment
        double loanAmountWithInterest = loanAmount * (1 + interestRate);
        double amountPerInstallment = new BigDecimal(loanAmountWithInterest / numberOfInstallment).setScale(2, RoundingMode.HALF_UP).doubleValue();

        // Creating loan installment records
        LocalDate nextDueDate = LocalDate.now().withDayOfMonth(1);
        for (int i = 0; i < numberOfInstallment; i++) {
            nextDueDate = nextDueDate.plusMonths(1);

            LoanInstallment installment = new LoanInstallment();
            installment.setLoanId(newLoanId);
            installment.setAmount(amountPerInstallment);
            installment.setPaidAmount(0.0);
            installment.setDueDate(nextDueDate);
            installment.setPaymentDate(null);
            installment.setPaid(false);
            loanInstallmentRepository.save(installment);
        }

        // Updating customer record for the newly used credit limit
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + loanAmount);
        customerRepository.save(customer);

        CreateLoanServiceResponse response = new CreateLoanServiceResponse();
        response.setLoanId(newLoanId);
        response.setNumberOfInstallment(numberOfInstallment);
        response.setAmountPerInstallment(amountPerInstallment);
        return response;
    }

    public PayLoanServiceResponse payLoan(UUID loanId, double amount) throws Exception {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (!optionalLoan.isPresent()) {
            throw new IllegalArgumentException("Loan with id (" + loanId + ") does not exist");            
        }

        Loan loan = optionalLoan.get();
        if (loan.isPaid()) {
            throw new IllegalArgumentException("Loan is already paid");
        }

        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository.findByLoan(loanId, false);

        if (unpaidInstallments.isEmpty()) {
            // Should not reach here normally, it's a data consistency problem (there is no unpaid installment, but the loan record's "is_paid" column is false)
            loan.setPaid(true);
            loanRepository.update(loan);

            throw new IllegalArgumentException("Loan is already paid");
        }

        List<LoanInstallment> payableInstallments = findPayableInstallments(unpaidInstallments);

        PayLoanServiceResponse response = payInstallments(amount, payableInstallments);

        if (!payableInstallments.stream().anyMatch(p -> p.isPaid() == false) && payableInstallments.size() == unpaidInstallments.size()) {
            // All installments are paid, we should update loan record's is_paid column for data consistency
            loan.setPaid(true);
            loanRepository.update(loan);

            response.setLoanPaidCompletely(true);
        }

        // Updating customer record's used_credit_limit column
        Customer customer = customerRepository.findById(loan.getCustomerId()).get();
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() - response.getTotalPaidCreditLimit());
        customerRepository.save(customer);

        return response;
    }

    private List<LoanInstallment> findPayableInstallments(List<LoanInstallment> installmentList) {
        List<LoanInstallment> payableInstallments = new ArrayList<>();

        LocalDate lastDueDate = LocalDate.now().plusMonths(MAX_PAYABLE_INSTALLMENT_COUNT - 1).withDayOfMonth(1);

        for (LoanInstallment i : installmentList) {
            if (i.getDueDate().isBefore(lastDueDate) || i.getDueDate().isEqual(lastDueDate)) {
                payableInstallments.add(i);
            }
        }

        return payableInstallments;
    }

    private PayLoanServiceResponse payInstallments(double amount, List<LoanInstallment> installmentList) {
        PayLoanServiceResponse response = new PayLoanServiceResponse();
        double remainingAmount = amount;
        int numberOfPaidInstallments = 0;
        double totalPaidCreditLimit = 0.0;

        for (LoanInstallment i : installmentList) {
            double actualAmount = calculateActualInstallmentAmount(i.getAmount(), i.getDueDate());

            if (remainingAmount < actualAmount) {
                break;
            }

            i.setPaidAmount(actualAmount);
            i.setPaymentDate(LocalDate.now());
            i.setPaid(true);
            loanInstallmentRepository.save(i);

            remainingAmount -= actualAmount;
            numberOfPaidInstallments++;
            totalPaidCreditLimit += i.getAmount();
        }

        response.setNumberOfPaidInstallments(numberOfPaidInstallments);
        response.setTotalAmountSpent(amount - remainingAmount);
        response.setRemainingAmount(remainingAmount);
        response.setTotalPaidCreditLimit(totalPaidCreditLimit);

        return response;
    }

    private double calculateActualInstallmentAmount(double amount, LocalDate dueDate) {
        LocalDate now = LocalDate.now();

        if (now.isEqual(dueDate)) {
            return amount;
        } else if (now.isBefore(dueDate)) {
            long daysBeforeDueDate = now.until(dueDate, ChronoUnit.DAYS);
            double discountAmount = amount * 0.001 * daysBeforeDueDate;

            return amount - discountAmount;
        } else {
            long daysAfterDueDate = dueDate.until(now, ChronoUnit.DAYS);
            double penaltyAmount = amount * 0.001 * daysAfterDueDate;

            return amount + penaltyAmount;
        }
    }
}
