package com.poianitibaldizhou.trackme.individualrequestservice;

import com.poianitibaldizhou.trackme.individualrequestservice.entity.IndividualRequest;
import com.poianitibaldizhou.trackme.individualrequestservice.entity.User;
import com.poianitibaldizhou.trackme.individualrequestservice.exception.RequestNotFoundException;
import com.poianitibaldizhou.trackme.individualrequestservice.exception.UserNotFoundException;
import com.poianitibaldizhou.trackme.individualrequestservice.repository.IndividualRequestRepository;
import com.poianitibaldizhou.trackme.individualrequestservice.util.IndividualRequestStatus;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the individual request service application
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IndividualRequestServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class IndividualRequestServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private IndividualRequestRepository requestRepository;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();

    // TEST GET SINGLE REQUEST METHOD

    /**
     * Test the get of a single request with id 1 (this is present, since it is loaded with data.sql with the loading
     * of the whole application)
     *
     * @throws Exception due to json assertEquals method
     */
    @Test
    public void testGetSingleRequest() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/1"),
                HttpMethod.GET, entity, String.class);

        String expectedBody  = "{\n" +
                "  \"status\" : \"PENDING\",\n" +
                "  \"timestamp\" : \"2000-01-01T00:00:00.000+0000\",\n" +
                "  \"startDate\" : \"2000-01-01\",\n" +
                "  \"endDate\" : \"2000-01-01\",\n" +
                "  \"thirdPartyID\" : 1,\n" +
                "  \"_links\" : {\n" +
                "    \"self\" : {\n" +
                "      \"href\" : \"http://localhost:"+port+"/individualrequestservice/requests/1\"\n" +
                "    },\n" +
                "    \"thirdPartyRequest\" : {\n" +
                "      \"href\" : \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/1\"\n" +
                "    },\n" +
                "    \"userPendingRequest\" : {\n" +
                "      \"href\" : \"http://localhost:"+port+"/individualrequestservice/requests/users/user1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedBody, response.getBody(), false);

    }

    /**
     * Test the get of a single request when it is not present
     */
    @Test
    public void testGetSingleRequestWhenNotPresent() {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/1000"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(new RequestNotFoundException(1000L).getMessage(), responseEntity.getBody());
    }

    // TEST GET BY THIRD PARTY ID METHOD

    /**
     * Test the get of all the requests performed by a third party customer when that requests are empty
     *
     * @throws JSONException due to json assertEquals method
     */
    @Test
    public void testGetByThirdPartyIDWhenNoRequestArePresent() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/thirdparty/1000"),
                HttpMethod.GET, entity, String.class);

        String expectedBody = "{\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/1000\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expectedBody, responseEntity.getBody(), false);
    }

    /**
     * Test the get of all the requests performed by a third party customer when the list of the request is non empty
     *
     * @throws JSONException due to json assertEquals method
     */
    @Test
    public void testGetByThirdPartyID() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/thirdparty/2"),
                HttpMethod.GET, entity, String.class);


        String expectedBody = "{\n" +
                "  \"_embedded\": {\n" +
                "    \"individualRequests\": [\n" +
                "      {\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"timestamp\": \"2000-01-01T00:00:00.000+0000\",\n" +
                "        \"startDate\": \"2000-01-01\",\n" +
                "        \"endDate\": \"2000-01-01\",\n" +
                "        \"thirdPartyID\": 2,\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/4\"\n" +
                "          },\n" +
                "          \"thirdPartyRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/2\"\n" +
                "          },\n" +
                "          \"userPendingRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/users/user1\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"timestamp\": \"2000-01-01T00:00:00.000+0000\",\n" +
                "        \"startDate\": \"2000-01-01\",\n" +
                "        \"endDate\": \"2000-01-01\",\n" +
                "        \"thirdPartyID\": 2,\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/5\"\n" +
                "          },\n" +
                "          \"thirdPartyRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/2\"\n" +
                "          },\n" +
                "          \"userPendingRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/users/user2\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/2\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expectedBody, responseEntity.getBody(), false);
    }

    // TEST GET PENDING REQUEST OF A CERTAIN USER

    /**
     * Test the get of all the pending request of a certain user that is registered
     *
     * @throws JSONException due to json assertEquals method
     */
    @Test
    public void testGetPendingRequest() throws JSONException {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/users/user2"),
                HttpMethod.GET, entity, String.class);


        String expectedBody = "{\n" +
                "  \"_embedded\": {\n" +
                "    \"individualRequests\": [\n" +
                "      {\n" +
                "        \"status\": \"PENDING\",\n" +
                "        \"timestamp\": \"2000-01-01T00:00:00.000+0000\",\n" +
                "        \"startDate\": \"2000-01-01\",\n" +
                "        \"endDate\": \"2000-01-01\",\n" +
                "        \"thirdPartyID\": 2,\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/5\"\n" +
                "          },\n" +
                "          \"thirdPartyRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/thirdparty/2\"\n" +
                "          },\n" +
                "          \"userPendingRequest\": {\n" +
                "            \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/users/user2\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:"+port+"/individualrequestservice/requests/users/user2\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JSONAssert.assertEquals(expectedBody, responseEntity.getBody(), false);
    }

    /**
     * Test the get of all the pending request of a certain user that is not registered
     */
    @Test
    public void testGetPendingRequestWhenUserNotRegistered() {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(createURLWithPort("/individualrequestservice/requests/users/notregistered"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(new UserNotFoundException(new User("notregistered")).getMessage(), responseEntity.getBody());
    }

    // TEST THE ADD OF A NEW REQUEST

    /**
     * Test the add of a new individual request on an existing user that is non blocked
     *
     * @throws Exception unsuccessful insertion of the new request
     */
    @Test
    public void testAddRequest() throws Exception {
        IndividualRequest individualRequest = new IndividualRequest(new Timestamp(0), new Date(0), new Date(0), new User("user1"), 1L);
        HttpEntity<IndividualRequest> entity = new HttpEntity<>(individualRequest, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/individualrequestservice/requests/user1"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(actual);
        String requestId = "";
        if(m.find()) {
            requestId = m.group();
        }

        assertTrue(requestRepository.findById(Long.parseLong(requestId)).isPresent());
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStatus().equals(IndividualRequestStatus.PENDING));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getUser().getSsn().equals(individualRequest.getUser().getSsn()));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStartDate().equals(individualRequest.getStartDate()));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStartDate().equals(individualRequest.getEndDate()));
    }


    /**
     * Test the add of a request on a user that is not registered
     */
    @Test
    public void testAddRequestOnNonRegisteredUser() {
        IndividualRequest individualRequest = new IndividualRequest(new Timestamp(0), new Date(0), new Date(0),
                new User("nonRegisteredUser"), 1L);
        HttpEntity<IndividualRequest> entity = new HttpEntity<>(individualRequest, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/individualrequestservice/requests/nonRegisteredUser"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test the add of a request on a user that is registered but that has already blocked the requesting third party
     *
     * @throws Exception unsuccessful insertion of the new request
     */
    @Test
    public void testAddRequestWhenBlocked() throws Exception {
        IndividualRequest individualRequest = new IndividualRequest(new Timestamp(0), new Date(0), new Date(0), new User("user5"), 4L);
        HttpEntity<IndividualRequest> entity = new HttpEntity<>(individualRequest, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/individualrequestservice/requests/user5"),
                HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(actual);
        String requestId = "";
        if(m.find()) {
            requestId = m.group();
        }

        assertTrue(requestRepository.findById(Long.parseLong(requestId)).isPresent());
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStatus().equals(IndividualRequestStatus.REFUSED));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getUser().getSsn().equals(individualRequest.getUser().getSsn()));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStartDate().equals(individualRequest.getStartDate()));
        assertTrue(requestRepository.findById(Long.parseLong(requestId)).
                orElseThrow(Exception::new).getStartDate().equals(individualRequest.getEndDate()));

    }


    // UTILITY FUNCTIONS

    /**
     * Utility method to form the url with the injected port for a certain uri
     * @param uri uri that will access a certain resource of the application
     * @return url for accesing the resource identified by the uri
     */
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
