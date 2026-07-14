package uk.gov.companieshouse.confirmationstatementapi.configuration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MongoConfigTest {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("condensedSicCodeMongoTemplate")
    private MongoTemplate condensedSicCodeMongoTemplate;

    @Test
    void shouldLoadMongoTemplate() {
        assertNotNull(mongoTemplate);
    }

    @Test
    void shouldLoadTestCodeMongoTemplate() {
        assertNotNull(condensedSicCodeMongoTemplate);
    }
}
