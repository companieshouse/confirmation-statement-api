package uk.gov.companieshouse.confirmationstatementapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CostServiceTest {

    @InjectMocks
    private CostService costService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(costService, "costAmount", "33.00");
    }
    @Test
    void getCosts() {
        var result = costService.getCosts();

        assertEquals("33.00", result.getAmount());
        assertEquals(Collections.singletonList("credit-card"), result.getAvailablePaymentMethods());
        assertEquals(Collections.singletonList("data-maintenance"), result.getClassOfPayment());
    }
}