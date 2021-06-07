package uk.gov.companieshouse.confirmationstatementapi.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.confirmationstatementapi.model.Shareholder;

@Component
public class OracleQueryApiClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ORACLE_QUERY_API_URL}")
    private String oracleQueryApiUrl;

    public int getShareholderCount(String companyNumber) {
        var shareholderCountUrl = String.format("%s/company/%s/shareholders/count", oracleQueryApiUrl, companyNumber);
        ResponseEntity<Integer> response = restTemplate.getForEntity(shareholderCountUrl, Integer.class);
        return response.getBody();
    }

    public List<Shareholder> getShareholders(String companyNumber) throws RestClientException {
        var shareholdersUrl = String.format("%s/company/%s/shareholders", oracleQueryApiUrl, companyNumber);

        // try {
        // } catch(RestClientException e){
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
            
        ResponseEntity<List<Shareholder>> ls = restTemplate.exchange(shareholdersUrl, 
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Shareholder>>(){});

        return ls.getBody();
        
    }
}