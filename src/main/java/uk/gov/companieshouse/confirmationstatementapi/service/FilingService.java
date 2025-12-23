package uk.gov.companieshouse.confirmationstatementapi.service;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.DATE_FORMAT_YYYYMD;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.FILING_KIND_CS;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.FILING_KIND_LPCS;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.LIMITED_PARTNERSHIP_TYPE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.confirmationstatementapi.client.ApiClientService;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.TradingStatusDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.siccode.SicCodeJson;

@Service
public class FilingService {

    @Value("${CONFIRMATION_STATEMENT_DESCRIPTION_NO_UPDATES}")
    private String filingDescription;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

    private final ConfirmationStatementService confirmationStatementService;
    private final ApiClientService apiClientService;
    private final CompanyProfileService companyProfileService;

    @Autowired
    public FilingService(ConfirmationStatementService confirmationStatementService,
                         ApiClientService apiClientService,
                         CompanyProfileService companyProfileService) {
        this.confirmationStatementService = confirmationStatementService;
        this.apiClientService = apiClientService;
        this.companyProfileService = companyProfileService;
    }

    public FilingApi generateConfirmationFiling(String confirmationStatementId, Transaction transaction) throws SubmissionNotFoundException, ServiceException, CompanyNotFoundException {
        var filing = new FilingApi();
        filing.setKind(FILING_KIND_CS);
        setFilingApiData(filing, confirmationStatementId, transaction);
        return filing;
    }

    private void setFilingApiData(FilingApi filing, String confirmationStatementId, Transaction transaction) throws SubmissionNotFoundException, ServiceException, CompanyNotFoundException {
        Map<String, Object> data = new HashMap<>();
        var isPayable = null != transaction.getLinks().getPayment();

        if (isPayable) {
            var paymentReference = getPaymentReferenceFromTransaction(transaction.getLinks().getPayment());
            var payment = getPayment(paymentReference);

            data.put("payment_reference", paymentReference);
            data.put("payment_method", payment.getPaymentMethod());
        }

        Optional<ConfirmationStatementSubmissionJson> submissionOpt =
                confirmationStatementService.getConfirmationStatement(confirmationStatementId);
        ConfirmationStatementSubmissionJson submission = submissionOpt
                .orElseThrow(() ->
                        new SubmissionNotFoundException(
                                String.format("Empty submission returned when generating filing for %s", confirmationStatementId)));

        var submissionData = submission.getData();
        if (submissionData != null) {
            CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(transaction.getCompanyNumber());

            LocalDate madeUpToDate = submissionData.getMadeUpToDate();

            if (companyProfile != null && LIMITED_PARTNERSHIP_TYPE.equals(companyProfile.getType())) {
                filing.setKind(FILING_KIND_LPCS);
                setLimitedPartnershipFilingData(data, submissionData, madeUpToDate);
                madeUpToDate = getMadeUpToDate(submissionData, madeUpToDate);
            } else {
                setNoChangeJourneyFilingData(data, submissionData, madeUpToDate);
            }

            if (Boolean.TRUE.equals(submissionData.getAcceptLawfulPurposeStatement())) {
                data.put("accept_lawful_purpose_statement", true);
            }

            filing.setData(data);
            setDescription(filing, madeUpToDate);
        } else {
            throw new SubmissionNotFoundException(
                    String.format("Submission contains no data %s", confirmationStatementId));
        }
    }

    private void setNoChangeJourneyFilingData(Map<String, Object> data, ConfirmationStatementSubmissionDataJson submissionData, LocalDate madeUpToDate) {
        data.put("confirmation_statement_date", madeUpToDate );

        TradingStatusDataJson tradingStatusData = submissionData.getTradingStatusData();
        if (tradingStatusData != null && tradingStatusData.getTradingStatusAnswer() != null) {
            data.put("trading_on_market", !tradingStatusData.getTradingStatusAnswer());
        }

        data.put("dtr5_ind", false);

        RegisteredEmailAddressDataJson registeredEmailAddressData = submissionData.getRegisteredEmailAddressData();
        if ((registeredEmailAddressData != null) && (registeredEmailAddressData.getSectionStatus() == SectionStatus.INITIAL_FILING)) {
            data.put("registered_email_address", registeredEmailAddressData.getRegisteredEmailAddress());
        }

    }

    private void setLimitedPartnershipFilingData(Map<String, Object> data, ConfirmationStatementSubmissionDataJson submissionData, LocalDate madeUpToDate) {
        data.put("confirmation_statement_date", getMadeUpToDate(submissionData, madeUpToDate) );

        if (submissionData.getSicCodeData() != null && submissionData.getSicCodeData().getSicCode() != null) {
            List<SicCodeJson> sicCodeJsonList = submissionData.getSicCodeData().getSicCode();
            data.put("sic_codes",  sicCodeJsonList.stream().map(SicCodeJson::getCode).toList() );
        }
    }

    private void setDescription(FilingApi filing, LocalDate madeUpToDate) {
        String madeUpToDateStr = madeUpToDate.format(formatter);
        filing.setDescriptionIdentifier(filingDescription);
        filing.setDescription(filingDescription.replace("{made up date}", madeUpToDateStr));
        Map<String, String> values = new HashMap<>();
        values.put("made up date", madeUpToDateStr);
        filing.setDescriptionValues(values);
    }

    private LocalDate getMadeUpToDate(ConfirmationStatementSubmissionDataJson submissionData, LocalDate madeUpToDate) {
        if (submissionData.getNewConfirmationDate() != null) {
            madeUpToDate = LocalDate.parse(submissionData.getNewConfirmationDate(), DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        }
        return madeUpToDate;
    }

    private PaymentApi getPayment(String paymentReference) throws ServiceException {
        try {
            return apiClientService
                    .getApiKeyAuthenticatedClient().payment().get("/payments/" + paymentReference).execute().getData();
        } catch (URIValidationException | ApiErrorResponseException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private String getPaymentReferenceFromTransaction(String uri) throws ServiceException {
        try {
            var transactionPaymentInfo = apiClientService
                    .getApiKeyAuthenticatedClient().transactions().getPayment(uri).execute();

           return transactionPaymentInfo.getData().getPaymentReference();
        } catch (URIValidationException | ApiErrorResponseException e) {
            throw new ServiceException(e.getMessage(), e);
        }

    }
}
