# About This Application

This is a backend REST API application (Loan API) that works with a H2 in-memory database. The application simulates banking operations like creating and listing loans, listing loan installments and paying them while implying some business logic for these transactions.

# Building The Application

A ".jar" package has to be built to run and/or deploy the application. The Maven wrapper inside the project files enables us to easily create packages. Please run the following commands on the project directory:

- For MacOS users -> ```./mvnw clean package```
- For Windows users -> ```Run mvnw.cmd with "clean package" parameters```

After a successful build, "loan-api-1.0.0.jar" file should be created on the "target" folder in the project files.

# Running The Application

The following command should be executed to run the application. If the jar file is moved somewhere else to run it on a different server, the address of the file on the command should be changed accordingly.

- For MacOS users -> ```java -jar target/loan-api-1.0.0.jar```
- For Windows users -> ```Run java program with "-jar" parameter for the project's jar file```

The application will run on the port 8080.

# Inspecting data on the database

The application adds initial data for customer, loan and loanInstallment tables at every run. H2 database provides a UI interface to query data on this address: ```http://localhost:8080/h2-console/```

H2 Console requires user & password authentication. Used credentials are as follows: ```User:SA, Password is empty```

# API Documentation

All endpoints are authorized with an admin user and password (Bonus 1 of the study is not completed). Requests should be made by adding Basic Authentication with this user and password:

User:```loan-api-admin```
Password:```iV&dtLK6fUbxt7Jz``` (They are defined on the application.properties file).

##  Create Loan
Creates a new loan for a given customer, amount, interest rate and number of installments

### Sample request
```POST /loans```

```
{
    "customerId": "ce6b02cb-1479-4f1a-9f08-9098c55bc871",
    "loanAmount": 600.0,
    "interestRate": 0.1,
    "numberOfInstallment": 6
}
```
### Sample response
```
{
    "loanId": "9703ab6e-bc32-42ac-ad17-ff412546b309",
    "numberOfInstallment": 6,
    "amountPerInstallment": 110.0
}
```

##  List Loans
List loans for a given customer

### Sample request
```GET /loans?customerId={customerId}&is_paid=false```

| Field               | Type                        | Is Optional | Description                                              |
|---------------------|-----------------------------|-------------|----------------------------------------------------------|
| customerId          | UUID                        | No          | Identifier of the customer                               |
| is_paid             | boolean                     | Yes         | Filter to is_paid info of the loans                      |
### Sample response
```
[
  {
    "id": "ce3da7ac-b131-4037-aaee-309424aaf4ea",
    "customerId": "ce6b02cb-1479-4f1a-9f08-9098c55bc871",
    "loanAmount": 300,
    "numberOfInstallment": 6,
    "createDate": "2024-12-12T14:05:00",
    "paid": false
  },
  ...
]
```

##  List Loan Installments
List loan installments for a given loan

### Sample request
```GET /loans/{loanId}/installments```

| Field               | Type                        | Is Optional | Description                                              |
|---------------------|-----------------------------|-------------|----------------------------------------------------------|
| loanId              | UUID                        | No          | Identifier of the loan                                   |
| is_paid             | boolean                     | Yes         | Filter to is_paid info of the loan installments          |
### Sample response
```
[
  {
    "id": "4a1082ee-170f-4af4-adcc-07ca4c8e2793",
    "loanId": "d6adf188-8b04-47a8-a45c-3fd04e9a87bb",
    "amount": 44,
    "paidAmount": 0,
    "dueDate": "2024-12-01",
    "paymentDate": null,
    "paid": false
  },
  {
    "id": "f6e372ed-8c13-4290-b22f-226936310919",
    "loanId": "d6adf188-8b04-47a8-a45c-3fd04e9a87bb",
    "amount": 44,
    "paidAmount": 0,
    "dueDate": "2025-01-01",
    "paymentDate": null,
    "paid": false
  },
  ...
]
```

##  Pay Loan
Pay installment(s) for a given loan and amount

### Sample request
```POST /loans/{loanId}```

| Field               | Type                        | Is Optional | Description                                              |
|---------------------|-----------------------------|-------------|----------------------------------------------------------|
| loanId              | UUID                        | No          | Identifier of the loan                                   |

```
{
    "amount": 250.0
}
```
### Sample response
```
{
    "numberOfPaidInstallments": 3,
    "totalAmountSpent": 117.84,
    "remainingAmount": 132.16,
    "totalPaidCreditLimit": 120.0,
    "loanPaidCompletely": true
}
```