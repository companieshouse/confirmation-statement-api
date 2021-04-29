package uk.gov.companieshouse.confirmationstatementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfirmationStatementApiApplication {

	public static final String APP_NAMESPACE = "confirmation-statement-api";

	public static void main(String[] args) {
		SpringApplication.run(ConfirmationStatementApiApplication.class, args);
	}

}
