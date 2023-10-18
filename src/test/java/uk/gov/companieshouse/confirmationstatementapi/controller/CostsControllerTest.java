package uk.gov.companieshouse.confirmationstatementapi.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import uk.gov.companieshouse.confirmationstatementapi.service.CostService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CostsControllerTest {

    @Mock
    private CostService costService;

    @InjectMocks
    private CostsController costsController;

    @Test
    void getCosts() {
        var response = costsController.getCosts();

        verify(costService, times(1)).getCosts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}