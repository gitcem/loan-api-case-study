package com.ing.study.loan_api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ing.study.loan_api.model.Loan;

@Component
public class LoanRepository {
    private DatabaseManager databaseManager;
    private LoanCrudRepository crudRepository;

    public LoanRepository(DatabaseManager dbManager, LoanCrudRepository loanCrudRepository) {
        databaseManager = dbManager;
        crudRepository = loanCrudRepository;
    }

    public Optional<Loan> findById(UUID id) {
        return crudRepository.findById(id);
    }

    public void update(Loan loan) {
        crudRepository.save(loan);
    }

    public List<Loan> findByCustomer(UUID customerId, Boolean paidFilter) throws Exception {
        ArrayList<Loan> loanList = new ArrayList<Loan>();

        try (Connection connection = databaseManager.getConnection()) {
            if (!connection.isValid(0)) {
                throw new Exception("Cannot connect to database");
            }

            String selectQuery = "SELECT * FROM loans WHERE customer_id = ?";
            if (paidFilter != null) {
                selectQuery = selectQuery + " AND is_paid = " + paidFilter.toString();
            }

            selectQuery = selectQuery + " ORDER BY CREATE_DATE";

            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, customerId.toString());

            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                Loan loanResult = new Loan();
                loanResult.setId((UUID) resultSet.getObject("id"));
                loanResult.setCustomerId((UUID) resultSet.getObject("customer_id"));
                loanResult.setLoanAmount(resultSet.getDouble("loan_amount"));
                loanResult.setNumberOfInstallment(resultSet.getInt("number_of_installment"));
                loanResult.setCreateDate(resultSet.getTimestamp("create_date").toLocalDateTime());
                loanResult.setPaid(resultSet.getBoolean("is_paid"));

                loanList.add(loanResult);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return loanList;
    }

    public UUID createLoan(Loan newLoan) {
        Loan newEntity = crudRepository.save(newLoan);

        return newEntity.getId();
    }
}
