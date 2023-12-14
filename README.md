# A simple bank account management application

## Running the app

Run this using [sbt](http://www.scala-sbt.org/).

```bash
sbt run
```

## Routes and parameters

- `/account/{account_id}`: GET endpoint to retrieve the balance and user details of a bank account
- `/transaction/`: POST endpoint to create a new transaction for a bank account
- `/transaction/history/{account_id}`: GET endpoint to retrieve the history of transactions for a bank account


```
