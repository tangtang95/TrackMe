package com.trackme.trackmeapplication.account.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackme.trackmeapplication.account.exception.InvalidDataLoginException;
import com.trackme.trackmeapplication.account.exception.UserAlreadyLogoutException;
import com.trackme.trackmeapplication.account.exception.UserAlreadySignUpException;
import com.trackme.trackmeapplication.account.login.TokenWrapper;
import com.trackme.trackmeapplication.baseUtility.ConnectionAsyncTask;
import com.trackme.trackmeapplication.baseUtility.Constant;
import com.trackme.trackmeapplication.baseUtility.exception.ConnectionException;
import com.trackme.trackmeapplication.home.Settings;
import com.trackme.trackmeapplication.sharedData.CompanyDetail;
import com.trackme.trackmeapplication.sharedData.PrivateThirdPartyDetail;
import com.trackme.trackmeapplication.sharedData.ThirdPartyCompanyWrapper;
import com.trackme.trackmeapplication.sharedData.ThirdPartyInterface;
import com.trackme.trackmeapplication.sharedData.ThirdPartyPrivateWrapper;
import com.trackme.trackmeapplication.sharedData.User;
import com.trackme.trackmeapplication.sharedData.exception.UserNotFoundException;
import com.trackme.trackmeapplication.sharedData.network.SharedDataNetworkImp;
import com.trackme.trackmeapplication.sharedData.network.SharedDataNetworkInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * AccountNetworkImp is a class that provide all the function to communicate with the account service
 * on the server.
 */
public class AccountNetworkImp implements AccountNetworkInterface {

    private static AccountNetworkImp instance = null;

    private Integer accountPort;
    private String IPAddress;

    private RestTemplate restTemplate;
    private HttpHeaders httpHeaders;
    private ObjectMapper mapper;

    private final Object lock = new Object();

    private String token;
    private static boolean isLock;


    /**
     * Constructor. (singleton)
     */
    private AccountNetworkImp() {
        restTemplate = new RestTemplate();
        httpHeaders = new HttpHeaders();
        mapper = new ObjectMapper();
        accountPort = Settings.getServerPort();
        IPAddress = Settings.getServerAddress();
    }

    public static void setIsLock(Boolean b) {
        isLock = b;
    }

    /**
     * Getter method.
     *
     * @return a new instance of the class if it is not just created.
     */
    public static AccountNetworkImp getInstance() {
        if(instance == null)
            instance = new AccountNetworkImp();
        return instance;
    }


    @Override
    public String userLogin(String username, String password) throws InvalidDataLoginException, ConnectionException {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        try {
            ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(createURLWithPort(
                    Constant.PUBLIC_USER_API + Constant.LOGIN_USER_API+"?username="+username+"&password="+password),
                    HttpMethod.POST, entity, lock);
            connectionAsyncTask.execute();
            isLock = true;
            synchronized (lock) {
                while (isLock)
                    lock.wait();
                token = mapper.readValue(connectionAsyncTask.getResponse().getBody(), TokenWrapper.class).getToken();
                return token;
            }
        } catch (IOException e) {
            throw new InvalidDataLoginException();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String thirdPartyLogin(String email, String password) throws InvalidDataLoginException {
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        try {
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(
                Constant.PUBLIC_TP_API + Constant.LOGIN_USER_API+"?email="+email+"&password="+password),
                HttpMethod.POST, entity, String.class);

            token = mapper.readValue(response.getBody(), TokenWrapper.class).getToken();
        } catch (IOException e) {
            throw new InvalidDataLoginException();
        }
        return token;
    }

    @Override
    public void userLogout() throws UserAlreadyLogoutException {
        httpHeaders.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(Constant.SECURED_USER_API + Constant.LOGOUT_USER_API),
                HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new UserAlreadyLogoutException();
    }

    @Override
    public void thirdPartyLogout() throws UserAlreadyLogoutException {
        httpHeaders.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(Constant.SECURED_USER_API + Constant.LOGOUT_USER_API),
                HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new UserAlreadyLogoutException();
    }

    @Override
    public void userSignUp(User user) throws UserAlreadySignUpException {
        HttpEntity<User> entity = new HttpEntity<>(user, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(Constant.PUBLIC_USER_API + "/newSsn"),
                HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.CREATED)
            throw new UserAlreadySignUpException();
    }

    @Override
    public void thirdPartySignUp(PrivateThirdPartyDetail privateThirdPartyDetail) throws UserAlreadySignUpException {
        ThirdPartyPrivateWrapper thirdPartyPrivateWrapper  = new ThirdPartyPrivateWrapper();
        thirdPartyPrivateWrapper.setPrivateThirdPartyDetail(privateThirdPartyDetail);

        HttpEntity<ThirdPartyPrivateWrapper> entity = new HttpEntity<>(thirdPartyPrivateWrapper, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(Constant.PUBLIC_TP_API + Constant.REGISTER_PRIVATE_TP_API),
                HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.CREATED)
            throw new UserAlreadySignUpException();
    }

    @Override
    public void companySignUp(CompanyDetail companyDetail) throws UserAlreadySignUpException {
        ThirdPartyCompanyWrapper thirdPartyCompanyWrapper = new ThirdPartyCompanyWrapper();
        thirdPartyCompanyWrapper.setCompanyDetail(companyDetail);

        HttpEntity<ThirdPartyCompanyWrapper> entity = new HttpEntity<>(thirdPartyCompanyWrapper, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(Constant.PUBLIC_TP_API + Constant.REGISTER_COMPANY_TP_API),
                HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.CREATED)
            throw new UserAlreadySignUpException();
    }

    @Override
    public User getUser() throws UserNotFoundException {
        SharedDataNetworkInterface sharedDataNetwork = SharedDataNetworkImp.getInstance();
        return sharedDataNetwork.getUser(token);
    }

    @Override
    public ThirdPartyInterface getThirdParty() throws UserNotFoundException {
        SharedDataNetworkInterface sharedDataNetwork = SharedDataNetworkImp.getInstance();
        return sharedDataNetwork.getThirdParty(token);
    }

    /**
     * Utility method to form the url with the injected port for a certain uri
     * @param uri uri that will access a certain resource of the application
     * @return url for accessing the resource identified by the uri
     */
    private String createURLWithPort(String uri) {
        return "https://"+ IPAddress + ":" + accountPort + uri;
    }
}