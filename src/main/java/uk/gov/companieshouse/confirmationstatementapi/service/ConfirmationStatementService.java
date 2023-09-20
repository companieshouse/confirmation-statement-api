package uk.gov.companieshouse.confirmationstatementapi.service;

import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.FILING_KIND_CS;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.confirmationstatementapi.client.OracleQueryClient;
import uk.gov.companieshouse.confirmationstatementapi.eligibility.EligibilityStatusCode;
import uk.gov.companieshouse.confirmationstatementapi.exception.CompanyNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.SectionStatus;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDao;
import uk.gov.companieshouse.confirmationstatementapi.model.dao.ConfirmationStatementSubmissionDataDao;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.NextMadeUpToDateJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.SectionDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.json.registeredemailaddress.RegisteredEmailAddressDataJson;
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;
import uk.gov.companieshouse.confirmationstatementapi.utils.ApiLogger;

@Service
public class ConfirmationStatementService {

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${FEATURE_FLAG_ENABLE_PAYMENT_CHECK_26082021:true}")
    private boolean isPaymentCheckFeatureEnabled;

    @Value("${FEATURE_FLAG_VALIDATION_STATUS_02092021:true}")
    private boolean isValidationStatusEnabled;

    @Value("${FEATURE_FLAG_ECCT_START_DATE_14082023:2024-02-05}")
    private String ecctStartDateStr;

    private final CompanyProfileService companyProfileService;
    private final EligibilityService eligibilityService;
    private final ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository;
    private final TransactionService transactionService;
    private final ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper;
    private final OracleQueryClient oracleQueryClient;
    private final Supplier<LocalDate> localDateNow;

    @Autowired
    public ConfirmationStatementService(CompanyProfileService companyProfileService,
                                        EligibilityService eligibilityService,
                                        ConfirmationStatementSubmissionsRepository confirmationStatementSubmissionsRepository,
                                        TransactionService transactionService,
                                        ConfirmationStatementJsonDaoMapper confirmationStatementJsonDaoMapper,
                                        OracleQueryClient oracleQueryClient,
                                        Supplier<LocalDate> localDateNow) {
        this.companyProfileService = companyProfileService;
        this.eligibilityService = eligibilityService;
        this.confirmationStatementSubmissionsRepository = confirmationStatementSubmissionsRepository;
        this.transactionService = transactionService;
        this.confirmationStatementJsonDaoMapper = confirmationStatementJsonDaoMapper;
        this.oracleQueryClient = oracleQueryClient;
        this.localDateNow = localDateNow;
    }

    public ResponseEntity<Object> createConfirmationStatement(Transaction transaction, String passthroughHeader) throws ServiceException {
        CompanyProfileApi companyProfile;
        String companyNumber = transaction.getCompanyNumber();
        try {
            companyProfile = companyProfileService.getCompanyProfile(companyNumber);
        } catch (CompanyNotFoundException e) {
            throw new ServiceException("Error retrieving company profile", e);
        }
        var companyValidationResponse = eligibilityService.checkCompanyEligibility(companyProfile) ;

        if(EligibilityStatusCode.COMPANY_VALID_FOR_SERVICE != companyValidationResponse.getEligibilityStatusCode()) {
            return ResponseEntity.badRequest().body(companyValidationResponse);
        }

        if (hasExistingConfirmationSubmission(transaction)) {
            return ResponseEntity.badRequest().body("EXISTING CONFIRMATION STATEMENT SUBMISSION FOUND FOR TRANSACTION ID: " + transaction.getId());
        }

        var newSubmission = new ConfirmationStatementSubmissionDao();
        var insertedSubmission = confirmationStatementSubmissionsRepository.insert(newSubmission);

        String csInsertedSubmission = "/confirmation-statement/" + insertedSubmission.getId();
        String createdUri = "/transactions/" + transaction.getId() + csInsertedSubmission;
        insertedSubmission.setLinks(Collections.singletonMap("self", createdUri));

        var data = new ConfirmationStatementSubmissionDataDao();
        LocalDate madeUpToDate = getMadeUpToDate(companyNumber, companyProfile);
        data.setMadeUpToDate(madeUpToDate);
        insertedSubmission.setData(data);

        var updatedSubmission = confirmationStatementSubmissionsRepository.save(insertedSubmission);

        var csResource = new Resource();
        csResource.setKind(FILING_KIND_CS);
        Map<String, String> linksMap = new HashMap<>();
        linksMap.put("resource", createdUri);
        if (isValidationStatusEnabled) {
            String validationStatusLink = createdUri + "/validation-status";
            linksMap.put("validation_status", validationStatusLink);
        }
        if (isPaymentCheckFeatureEnabled) {
            makePayableResourceIfUnpaid(createdUri, linksMap, madeUpToDate, companyNumber);
        }

        csResource.setLinks(linksMap);
        transaction.setResources(Collections.singletonMap(createdUri, csResource));

        transactionService.updateTransaction(transaction, passthroughHeader);

        ApiLogger.info(String.format("Confirmation Statement created for transaction id: %s with Submission id: %s",  transaction.getId(), updatedSubmission.getId()));
        var responseObject = confirmationStatementJsonDaoMapper.daoToJson(updatedSubmission);
        return ResponseEntity.created(URI.create(createdUri)).body(responseObject);
    }

    private LocalDate getMadeUpToDate(String companyNumber, CompanyProfileApi companyProfileApi) throws ServiceException {
        var nextMadeUpToDateJson = getNextMadeUpToDateJson(companyNumber, companyProfileApi);

        if (nextMadeUpToDateJson.getNewNextMadeUpToDate() != null) {
            return nextMadeUpToDateJson.getNewNextMadeUpToDate();
        }

        return nextMadeUpToDateJson.getCurrentNextMadeUpToDate();
    }

    private void makePayableResourceIfUnpaid(String createdUri,
                                             Map<String, String> linksMap,
                                             LocalDate madeUpToDate, String companyNumber) throws ServiceException {
        if (!oracleQueryClient.isConfirmationStatementPaid(companyNumber,
                madeUpToDate.format(DATE_TIME_FORMATTER))) {
            String costsLink = createdUri + "/costs";
            linksMap.put("costs", costsLink);
        }
    }

    public ResponseEntity<Object> updateConfirmationStatement(String submissionId, ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            // Save updated submission to database
            ApiLogger.info(String.format("%s: Confirmation Statement Submission found. About to update",  submission.get().getId()));
            var dao = confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson);
            var savedResponse = confirmationStatementSubmissionsRepository.save(dao);
            ApiLogger.info(String.format("%s: Confirmation Statement Submission updated",  savedResponse.getId()));
            return ResponseEntity.ok(savedResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ValidationStatusResponse isValid(String submissionId) throws SubmissionNotFoundException {
        Optional<ConfirmationStatementSubmissionJson> submissionJsonOptional = getConfirmationStatement(submissionId);

        if (submissionJsonOptional.isPresent()) {
            var validationStatus = new ValidationStatusResponse();
            ConfirmationStatementSubmissionJson submission = submissionJsonOptional.get();
            ConfirmationStatementSubmissionDataJson submissionData = submission.getData();

            if (submissionData == null) {
                validationStatus.setValid(false);
            } else {
                boolean isValid = isConfirmed(submissionData.getShareholdersData()) &&
                        isConfirmed(submissionData.getSicCodeData()) &&
                        isConfirmed(submissionData.getActiveOfficerDetailsData()) &&
                        isConfirmed(submissionData.getStatementOfCapitalData()) &&
                        isConfirmed(submissionData.getRegisteredOfficeAddressData()) &&
                        isConfirmed(submissionData.getPersonsSignificantControlData()) &&
                        isConfirmed(submissionData.getRegisterLocationsData()) &&
                        isConfirmed(submissionData.getRegisteredEmailAddressData(), submissionData.getMadeUpToDate()) &&
                        Boolean.TRUE.equals(submissionData.getTradingStatusData().getTradingStatusAnswer()) &&
                        isBeforeOrEqual(localDateNow.get(), submissionData.getMadeUpToDate());
                validationStatus.setValid(isValid);
            }

            if (!validationStatus.isValid()) {
                var errors = new ValidationStatusError[1];
                var error = new ValidationStatusError();
                error.setType("ch:validation");
                errors[0] = error;
                validationStatus.setValidationStatusError(errors);
            }

            return validationStatus;
        } else  {
            throw new SubmissionNotFoundException(String.format("Could not find submission data for submission %s", submissionId));
        }
    }

    private boolean isConfirmed(SectionDataJson sectionData) {
        return (sectionData != null) &&
                (sectionData.getSectionStatus() == SectionStatus.CONFIRMED ||
                 sectionData.getSectionStatus() == SectionStatus.RECENT_FILING);
    }

    private boolean isConfirmed(RegisteredEmailAddressDataJson sectionData, LocalDate madeUpToDate) {
        if (isEcctEnabled(madeUpToDate)) {
            return (sectionData != null) &&
                    (sectionData.getSectionStatus() == SectionStatus.CONFIRMED ||
                    sectionData.getSectionStatus() == SectionStatus.RECENT_FILING ||
                    sectionData.getSectionStatus() == SectionStatus.INITIAL_FILING);
        }

        return true;
    }

    private boolean isEcctEnabled(LocalDate madeUpToDate) {
        var ecctStartDate = LocalDate.parse(ecctStartDateStr, DATE_TIME_FORMATTER);

        return isBeforeOrEqual(madeUpToDate, ecctStartDate);
    }

    public Optional<ConfirmationStatementSubmissionJson> getConfirmationStatement(String submissionId) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            ApiLogger.info(String.format("%s: Confirmation Statement Submission found. About to return",  submission.get().getId()));

            var json = confirmationStatementJsonDaoMapper.daoToJson(submission.get());
            return Optional.of(json);
        } else {
            return Optional.empty();
        }
    }

    public NextMadeUpToDateJson getNextMadeUpToDate(String companyNumber) throws CompanyNotFoundException, ServiceException {
        var companyProfileApi = companyProfileService.getCompanyProfile(companyNumber);

        return getNextMadeUpToDateJson(companyNumber, companyProfileApi);
    }

    private NextMadeUpToDateJson getNextMadeUpToDateJson(String companyNumber, CompanyProfileApi companyProfileApi) throws ServiceException {
        if (companyProfileApi == null) {
            throw new ServiceException(String.format("Unable to find company profile for company %s", companyNumber));
        }

        var nextMadeUpToDateJson = new NextMadeUpToDateJson();

        if (companyProfileApi.getConfirmationStatement() == null
            || companyProfileApi.getConfirmationStatement().getNextMadeUpTo() == null) {
                nextMadeUpToDateJson.setCurrentNextMadeUpToDate(null);
                nextMadeUpToDateJson.setDue(null);
                nextMadeUpToDateJson.setNewNextMadeUpToDate(null);
                return nextMadeUpToDateJson;
        }

        LocalDate nextMadeUpToDate = companyProfileApi.getConfirmationStatement().getNextMadeUpTo();
        nextMadeUpToDateJson.setCurrentNextMadeUpToDate(nextMadeUpToDate);
        var today = localDateNow.get();

        if (today.isBefore(nextMadeUpToDate)) {
            nextMadeUpToDateJson.setDue(false);
            nextMadeUpToDateJson.setNewNextMadeUpToDate(today);
        } else {
            nextMadeUpToDateJson.setDue(true);
            nextMadeUpToDateJson.setNewNextMadeUpToDate(null);
        }

        return nextMadeUpToDateJson;
    }

    private boolean isBeforeOrEqual(LocalDate date, LocalDate compareToDate) {
        if (date == null || compareToDate == null) {
            return false;
        }
        return !compareToDate.isAfter(date);
    }

    private boolean hasExistingConfirmationSubmission (Transaction transaction) {
        if (transaction.getResources() != null) {
            return transaction.getResources().entrySet().stream().anyMatch(resourceEntry -> FILING_KIND_CS.equals(resourceEntry.getValue().getKind()));
        }
        return false;
    }
}
