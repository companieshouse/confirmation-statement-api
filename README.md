# confirmation-statement-api

### Overview
API service for submitting Confirmation Statements to CHIPS

### Requirements
In order to run the service locally you will need the following:
- [Java 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

### Getting started
To checkout and build the service:
1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.
2. Run ./bin/chs-dev modules enable confirmation-statement
3. Run ./bin/chs-dev development enable confirmation-statement-api
4. Run docker using "tilt up" in the docker-chs-development directory.
5. Use spacebar in the command line to open tilt window - wait for confirmation-statement-api to become green.
6. Open your browser and go to page http://chs.local/confirmation-statement/

These instructions are for a local docker environment.

### Endpoints

The full path for each public endpoints that requires a transaction id begins with the app url:
`${API_URL}/transactions/{TRANSACTION_ID}/confirmation-statement`

Method    | Path                                                                         | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**GET**   |`/{confirmation_statement_id}`                                                | Returns confirmation-statement based on confirmationStatementId
**GET**   |`/{confirmation_statement_id}/validation-status`                              | Returns flags to indicate that all required tasks are complete
**GET**   |`/{confirmation-statement-id}/costs`                                          | Returns the payment amount required to submit
**GET**   |`/{confirmation_statement_submission_id}/active-director-details"`            | Returns data to present to the user
**GET**   |`/{confirmation_statement_submission_id}/persons-of-significant-control`      | Returns data to present to the user
**GET**   |`/{confirmation_statement_submission_id}/register-locations`                  | Returns data to present to the user
**GET**   |`/{confirmation_statement_submission_id}/shareholders`                        | Returns data to present to the user
**GET**   |`/{confirmation_statement_submission_id}/statement-of-capital`                | Returns data to present to the user 
**POST**  |`/`                                                                           | Creates the confirmation statement
**POST**  |`/{confirmation_statement_id}`                                                | Updates the confirmation statement

For filing:
`${API_URL}/private/transactions/{transaction_id}/confirmation-statement`

Method    | Path                                                                         | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**GET**   |`{confirmation_statement_id}/filings`                                         | Returns filing data required for the CHIPS filing backend

For company endpoints:
`${API_URL}/confirmation-statement/company/{companyNumber}`

Method    | Path                                                                         | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**GET**   |`/eligibility`                                                                | Check company is eligible for filing
**GET**   |`/next-made-up-to-date`                                                       | Get due date
