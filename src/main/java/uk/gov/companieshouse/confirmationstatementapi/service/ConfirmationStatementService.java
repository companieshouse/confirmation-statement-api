package uk.gov.companieshouse.confirmationstatementapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import uk.gov.companieshouse.confirmationstatementapi.model.mapping.ConfirmationStatementJsonDaoMapper;
import uk.gov.companieshouse.confirmationstatementapi.repository.ConfirmationStatementSubmissionsRepository;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ConfirmationStatementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationStatementService.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${FEATURE_FLAG_ENABLE_PAYMENT_CHECK_26082021:true}")
    private boolean isPaymentCheckFeatureEnabled;

    @Value("${FEATURE_FLAG_VALIDATION_STATUS_02092021:true}")
    private boolean isValidationStatusEnabled;

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

        var newSubmission = new ConfirmationStatementSubmissionDao();
        var insertedSubmission = confirmationStatementSubmissionsRepository.insert(newSubmission);

        String csInsertedSubmission = "/confirmation-statement/" + insertedSubmission.getId();
        String createdUri = "/transactions/" + transaction.getId() + csInsertedSubmission;
        insertedSubmission.setLinks(Collections.singletonMap("self", createdUri));

        ConfirmationStatementSubmissionDataDao data = new ConfirmationStatementSubmissionDataDao();
        LocalDate madeUpToDate = getMadeUpToDate(companyNumber, companyProfile);
        data.setMadeUpToDate(madeUpToDate);
        insertedSubmission.setData(data);

        var updatedSubmission = confirmationStatementSubmissionsRepository.save(insertedSubmission);

        var csResource = new Resource();
        csResource.setKind("confirmation-statement");
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

        LOGGER.info("Confirmation Statement created for transaction id: {} with Submission id: {}", transaction.getId(), updatedSubmission.getId());
        var responseObject = confirmationStatementJsonDaoMapper.daoToJson(updatedSubmission);
        return ResponseEntity.created(URI.create(createdUri)).body(responseObject);
    }

    private LocalDate getMadeUpToDate(String companyNumber, CompanyProfileApi companyProfileApi) throws ServiceException {
        NextMadeUpToDateJson nextMadeUpToDateJson = getNextMadeUpToDateJson(companyNumber, companyProfileApi);

        if (nextMadeUpToDateJson.getNewNextMadeUpToDate() != null) {
            return nextMadeUpToDateJson.getNewNextMadeUpToDate();
        }

        return nextMadeUpToDateJson.getCurrentNextMadeUpToDate();
    }

    private void makePayableResourceIfUnpaid(String createdUri,
                                             Map<String, String> linksMap,
                                             LocalDate madeUpToDate, String companyNumber) throws ServiceException {
        if (!oracleQueryClient.isConfirmationStatementPaid(companyNumber,
                madeUpToDate.format(dateTimeFormatter))) {
            String costsLink = createdUri + "/costs";
            linksMap.put("costs", costsLink);
        }
    }

    public ResponseEntity<Object> updateConfirmationStatement(String submissionId, ConfirmationStatementSubmissionJson confirmationStatementSubmissionJson) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            // Save updated submission to database
            LOGGER.info("{}: Confirmation Statement Submission found. About to update", submission.get().getId());
            var dao = confirmationStatementJsonDaoMapper.jsonToDao(confirmationStatementSubmissionJson);
            var savedResponse = confirmationStatementSubmissionsRepository.save(dao);
            LOGGER.info("{}: Confirmation Statement Submission updated", savedResponse.getId());
            return ResponseEntity.ok(savedResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ValidationStatusResponse areTasksComplete(String submissionId) throws SubmissionNotFoundException {
        Optional<ConfirmationStatementSubmissionJson> submissionJsonOptional = getConfirmationStatement(submissionId);
        if (submissionJsonOptional.isEmpty()) {
            throw new SubmissionNotFoundException(
                    String.format("Could not find submission data for submission %s", submissionId));
        }

        ValidationStatusResponse validationStatus = new ValidationStatusResponse();
        ConfirmationStatementSubmissionJson submission = submissionJsonOptional.get();
        ConfirmationStatementSubmissionDataJson submissionData = submission.getData();

        if (submissionData == null) {
            validationStatus.setValid(false);
        } else {

           boolean isValid = isConfirmed(submissionData.getShareholdersData()) &&
                    isConfirmed(submissionData.getSicCodeData()) &&
                    isConfirmed(submissionData.getActiveDirectorDetailsData()) &&
                    isConfirmed(submissionData.getStatementOfCapitalData()) &&
                    isConfirmed(submissionData.getRegisteredOfficeAddressData()) &&
                    isConfirmed(submissionData.getPersonsSignificantControlData());
           validationStatus.setValid(isValid);
        }
        if (!validationStatus.isValid()) {
            ValidationStatusError[] errors = new ValidationStatusError[1];
            ValidationStatusError error = new ValidationStatusError();
            error.setType("ch:validation");
            errors[0] = error;
            validationStatus.setValidationStatusError(errors);
        }

        return validationStatus;
    }

    private boolean isConfirmed(SectionDataJson sectionData) {
        return sectionData != null &&
                (sectionData.getSectionStatus() == SectionStatus.CONFIRMED ||
                 sectionData.getSectionStatus() == SectionStatus.RECENT_FILING);
    }

    public Optional<ConfirmationStatementSubmissionJson> getConfirmationStatement(String submissionId) {
        // Check Submission exists
        var submission = confirmationStatementSubmissionsRepository.findById(submissionId);

        if (submission.isPresent()) {
            LOGGER.info("{}: Confirmation Statement Submission found. About to return", submission.get().getId());

            var json = confirmationStatementJsonDaoMapper.daoToJson(submission.get());
            return Optional.of(json);
        } else {
            return Optional.empty();
        }
    }


    public NextMadeUpToDateJson getNextMadeUpToDate(String companyNumber) throws CompanyNotFoundException, ServiceException {
        CompanyProfileApi companyProfileApi = companyProfileService.getCompanyProfile(companyNumber);

        return getNextMadeUpToDateJson(companyNumber, companyProfileApi);
    }

    private NextMadeUpToDateJson getNextMadeUpToDateJson(String companyNumber, CompanyProfileApi companyProfileApi) throws ServiceException {
        if (companyProfileApi == null) {
            throw new ServiceException(String.format("Unable to find company profile for company %s", companyNumber));
        }

        NextMadeUpToDateJson nextMadeUpToDateJson = new NextMadeUpToDateJson();

        if (companyProfileApi.getConfirmationStatement() == null
            || companyProfileApi.getConfirmationStatement().getNextMadeUpTo() == null) {
                nextMadeUpToDateJson.setCurrentNextMadeUpToDate(null);
                nextMadeUpToDateJson.setDue(null);
                nextMadeUpToDateJson.setNewNextMadeUpToDate(null);
                return nextMadeUpToDateJson;
        }

        LocalDate nextMadeUpToDate = companyProfileApi.getConfirmationStatement().getNextMadeUpTo();
        nextMadeUpToDateJson.setCurrentNextMadeUpToDate(nextMadeUpToDate);
        LocalDate today = localDateNow.get();

        if (today.isBefore(nextMadeUpToDate)) {
            nextMadeUpToDateJson.setDue(false);
            nextMadeUpToDateJson.setNewNextMadeUpToDate(today);
        } else {
            nextMadeUpToDateJson.setDue(true);
            nextMadeUpToDateJson.setNewNextMadeUpToDate(null);
        }

        return nextMadeUpToDateJson;
    }


}
