# confirmation-statement-api

### Overview
API service for submitting Confirmation Statements to CHIPS

### Requirements
In order to run the service locally you will need the following:
- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
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
**GET**   |`/{confirmation_statement_id}/costs`                                          | Returns the payment amount required to submit
**GET**   |`/{confirmation_statement_id}/active-director-details"`            | Returns data to present to the user
**GET**   |`/{confirmation_statement_id}/persons-of-significant-control`      | Returns data to present to the user
**GET**   |`/{confirmation_statement_id}/register-locations`                  | Returns data to present to the user
**GET**   |`/{confirmation_statement_id}/shareholders`                        | Returns data to present to the user
**GET**   |`/{confirmation_statement_id}/statement-of-capital`                | Returns data to present to the user 
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

Private company endpoint to retrieve registered email address:
`${API_URL}/private/confirmation-statement/company/{company-number}`
Method    | Path                                                                         | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**GET**   |`/registered-email-address`                                                   | Returns company's registered email address

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        | filing-maintain                                     | ECS cluster (stack) the service belongs to
**Load balancer**      | {env}-chs-apichgovuk <br> {env}-chs-apichgovuk-private                                 | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/confirmation-statement-api) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/confirmation-statement-api)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)