package com.ing.study.loan_api.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ing.study.loan_api.model.LoanInstallment;

@Component
public class LoanInstallmentRepository {
    private DatabaseManager databaseManager;
    private LoanInstallmentCrudRepository crudRepository;

    public LoanInstallmentRepository(DatabaseManager dbManager, LoanInstallmentCrudRepository loanInstallmentCrudRepository) {
        databaseManager = dbManager;
        crudRepository = loanInstallmentCrudRepository;
    }

    public List<LoanInstallment> findByLoan(UUID loanId, Boolean paidFilter) throws Exception {
        ArrayList<LoanInstallment> installmentList = new ArrayList<LoanInstallment>();

        try (Connection connection = databaseManager.getConnection()) {
            if (!connection.isValid(0)) {
                throw new Exception("Cannot connect to database");
            }

            String selectQuery = "SELECT * FROM loaninstallments WHERE loan_id = ?";
            if (paidFilter != null) {
                selectQuery = selectQuery + " AND is_paid = " + paidFilter.toString();
            }

            selectQuery = selectQuery + " ORDER BY DUE_DATE";

            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, loanId.toString());

            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                LoanInstallment installmentResult = new LoanInstallment();
                installmentResult.setId((UUID) resultSet.getObject("id"));
                installmentResult.setLoanId((UUID) resultSet.getObject("loan_id"));
                installmentResult.setAmount(resultSet.getDouble("amount"));
                installmentResult.setPaidAmount(resultSet.getDouble("paid_amount"));
                installmentResult.setDueDate(((Date) resultSet.getObject("due_date")).toLocalDate());
                Date rsDate = (Date) resultSet.getObject("payment_date");
                installmentResult.setPaymentDate(rsDate == null ? null : rsDate.toLocalDate());
                installmentResult.setPaid(resultSet.getBoolean("is_paid"));

                installmentList.add(installmentResult);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return installmentList;
    }

    public LoanInstallment save(LoanInstallment installment) {
        return crudRepository.save(installment);
    }
}
