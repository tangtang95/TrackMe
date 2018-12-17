package com.poianitibaldizhou.trackme.sharedataservice.controller;

import com.poianitibaldizhou.trackme.sharedataservice.ShareDataServiceApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShareDataServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles(profiles = {"test"})
@Sql("classpath:sql/testAccessDataService.sql")
public class AccessDataControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    private HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        restTemplate = new TestRestTemplate();
        httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.ALL));
        httpHeaders.setContentType(MediaTypes.HAL_JSON);
    }

    @After
    public void tearDown() {
        restTemplate = null;
        httpHeaders = null;
    }

    // TEST GET INDIVIDUAL REQUEST DATA METHOD

    /**
     * Test the get individual request data with individual id and third party id matching in individual request
     *
     * @throws Exception due to json assertEquals method
     */
    @Test
    public void getIndividualRequestDataSuccessful() throws Exception {
        httpHeaders.set("thirdPartyId", "1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/individualrequests/1/1"),
                HttpMethod.GET, entity, String.class);

        String expectedBody = "{\n" +
                "  \"positionDataList\": [\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T06:00:00.000+0000\",\n" +
                "      \"longitude\": 60.0,\n" +
                "      \"latitude\": 50.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T08:00:00.000+0000\",\n" +
                "      \"longitude\": 68.2,\n" +
                "      \"latitude\": 70.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T10:00:00.000+0000\",\n" +
                "      \"longitude\": 65.42,\n" +
                "      \"latitude\": -10.0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"healthDataList\": [\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T06:00:00.000+0000\",\n" +
                "      \"heartBeat\": 50,\n" +
                "      \"bloodOxygenLevel\": 75,\n" +
                "      \"pressureMin\": 60,\n" +
                "      \"pressureMax\": 100\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T08:00:00.000+0000\",\n" +
                "      \"heartBeat\": 70,\n" +
                "      \"bloodOxygenLevel\": 90,\n" +
                "      \"pressureMin\": 68,\n" +
                "      \"pressureMax\": 101\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T10:00:00.000+0000\",\n" +
                "      \"heartBeat\": 120,\n" +
                "      \"bloodOxygenLevel\": 79,\n" +
                "      \"pressureMin\": 65,\n" +
                "      \"pressureMax\": 110\n" +
                "    }\n" +
                "  ],\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:" + port + "/dataretrieval/individualrequests/1/1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedBody, response.getBody(), false);
    }

    /**
     * Test the get individual request data with a third party not existing
     */
    @Test
    public void getIndividualDataWithNotExistingThirdPartyId() {
        httpHeaders.set("thirdPartyId", "3");

        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/individualrequests/3/1"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test the get individual request data with an existing third party but not matching individual request id
     */
    @Test
    public void getIndividualDataWithNotMatchingRequestId() {
        httpHeaders.set("thirdPartyId", "1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/individualrequests/1/4"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // TEST GET INDIVIDUAL REQUEST DATA METHOD

    /**
     * Test the get group request data when it is successful
     *
     * @throws Exception due to json assertEquals method
     */
    @Test
    public void getGroupRequestDataSuccessful() throws Exception {
        httpHeaders.set("thirdPartyId", "1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/grouprequests/1/1"),
                HttpMethod.GET, entity, String.class);

        String expectedBody = "{\n" +
                "  \"value\": 8.0,\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:" + port + "/dataretrieval/grouprequests/1/1\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedBody, response.getBody(), false);
    }

    /**
     * Test the get group request data with a not existing third party
     */
    @Test
    public void getGroupRequestDataWithNotExistingThirdParty()  {
        httpHeaders.set("thirdPartyId", "4");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/grouprequests/4/1"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test the get group request data with a non matching request id and existing third party
     */
    @Test
    public void getGroupRequestDataWithNotMatchingRequestId()  {
        httpHeaders.set("thirdPartyId", "1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/dataretrieval/grouprequests/1/2"),
                HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test the get own data with existing user
     *
     * @throws Exception due to json assertEquals method
     */
    @Test
    public void getOwnDataWithExistingUser() throws Exception{
        httpHeaders.set("userSsn", "user1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate
                .exchange(createURLWithPort("/dataretrieval/users/user1?from=2010-01-01&to=2010-01-01"),
                HttpMethod.GET, entity, String.class);

        String expectedBody = "{\n" +
                "  \"positionDataList\": [\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T06:00:00.000+0000\",\n" +
                "      \"longitude\": 60.0,\n" +
                "      \"latitude\": 50.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T08:00:00.000+0000\",\n" +
                "      \"longitude\": 68.2,\n" +
                "      \"latitude\": 70.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T10:00:00.000+0000\",\n" +
                "      \"longitude\": 65.42,\n" +
                "      \"latitude\": -10.0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"healthDataList\": [\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T06:00:00.000+0000\",\n" +
                "      \"heartBeat\": 50,\n" +
                "      \"bloodOxygenLevel\": 75,\n" +
                "      \"pressureMin\": 60,\n" +
                "      \"pressureMax\": 100\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T08:00:00.000+0000\",\n" +
                "      \"heartBeat\": 70,\n" +
                "      \"bloodOxygenLevel\": 90,\n" +
                "      \"pressureMin\": 68,\n" +
                "      \"pressureMax\": 101\n" +
                "    },\n" +
                "    {\n" +
                "      \"timestamp\": \"2010-01-01T10:00:00.000+0000\",\n" +
                "      \"heartBeat\": 120,\n" +
                "      \"bloodOxygenLevel\": 79,\n" +
                "      \"pressureMin\": 65,\n" +
                "      \"pressureMax\": 110\n" +
                "    }\n" +
                "  ],\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:" + port + "/dataretrieval/users/user1?from=2010-01-01&to=2010-01-01\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedBody, response.getBody(), false);
    }

    /**
     * Test the get own data method with not existing user
     */
    @Test
    public void getOwnDataWithNotExistingUser() {
        httpHeaders.set("userSsn", "user3");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate
                .exchange(createURLWithPort("/dataretrieval/users/user3?from=2010-01-01&to=2010-01-01"),
                        HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * Test the get own data method when there is no upper bound date
     */
    @Test
    public void getOwnDataWithExistingUserButNoDateAboutUpperBound() {
        httpHeaders.set("userSsn", "user1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate
                .exchange(createURLWithPort("/dataretrieval/users/user1?from=2010-01-01"),
                        HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Test the get own data method when there is no lower bound date
     */
    @Test
    public void getOwnDataWithExistingUserButNoDateAboutLowerBound() {
        httpHeaders.set("userSsn", "user1");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate
                .exchange(createURLWithPort("/dataretrieval/users/user1?to=2010-01-01"),
                        HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // UTILITY FUNCTIONS

    /**
     * Utility method to form the url with the injected port for a certain uri
     *
     * @param uri uri that will access a certain resource of the application
     * @return url for accesing the resource identified by the uri
     */
    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
