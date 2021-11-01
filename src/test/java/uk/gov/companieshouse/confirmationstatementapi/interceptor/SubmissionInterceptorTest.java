package uk.gov.companieshouse.confirmationstatementapi.interceptor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.confirmationstatementapi.exception.ServiceException;
import uk.gov.companieshouse.confirmationstatementapi.exception.SubmissionNotFoundException;
import uk.gov.companieshouse.confirmationstatementapi.model.json.ConfirmationStatementSubmissionJson;
import uk.gov.companieshouse.confirmationstatementapi.service.ConfirmationStatementService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.CONFIRMATION_STATEMENT_ID_KEY;
import static uk.gov.companieshouse.confirmationstatementapi.utils.Constants.TRANSACTION_ID_KEY;

@ExtendWith(MockitoExtension.class)
public class SubmissionInterceptorTest {

    private static final String SUBMISSION_ID = "ABCDEFG";

    @Mock
    private ConfirmationStatementService confirmationStatementService;

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @InjectMocks
    private SubmissionInterceptor submissionInterceptor;

    @Test
    void preHandle() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();
        ConfirmationStatementSubmissionJson dummyConfirmationStatementSubmission = new ConfirmationStatementSubmissionJson();
        dummyConfirmationStatementSubmission.setId(SUBMISSION_ID);

        var pathParams = new HashMap<String, String>();
        pathParams.put(CONFIRMATION_STATEMENT_ID_KEY, SUBMISSION_ID);

        when(confirmationStatementService.getConfirmationStatement(SUBMISSION_ID)).thenReturn(Optional.of(dummyConfirmationStatementSubmission));
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertTrue(submissionInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
    }

    @Test
    void exceptionPreHandle() throws SubmissionNotFoundException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Object mockHandler = new Object();

        var pathParams = new HashMap<String, String>();
        pathParams.put(CONFIRMATION_STATEMENT_ID_KEY, SUBMISSION_ID);

        when(confirmationStatementService.getConfirmationStatement(SUBMISSION_ID)).thenThrow(SubmissionNotFoundException.class);
        when(mockHttpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(pathParams);

        assertFalse(submissionInterceptor.preHandle(mockHttpServletRequest, mockHttpServletResponse, mockHandler));
        assertEquals(404,  mockHttpServletResponse.getStatus());
    }
}
