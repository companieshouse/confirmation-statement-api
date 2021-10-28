package uk.gov.companieshouse.confirmationstatementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class ConfirmationStatementApiApplication {

	public static final String APP_NAMESPACE = "confirmation-statement-api";

	public static void main(String[] args) {
		SpringApplication.run(ConfirmationStatementApiApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// This is to prevent times being out of time by an hour during British Summer Time in MongoDB
		// MongoDB stores UTC datetime, and LocalDate doesn't contain timezone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
