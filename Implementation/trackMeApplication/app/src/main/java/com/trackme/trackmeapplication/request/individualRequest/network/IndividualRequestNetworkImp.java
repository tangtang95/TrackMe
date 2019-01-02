package com.trackme.trackmeapplication.request.individualRequest.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.trackme.trackmeapplication.httpConnection.BusinessURLManager;
import com.trackme.trackmeapplication.httpConnection.ConnectionBuilder;
import com.trackme.trackmeapplication.httpConnection.LockInterface;
import com.trackme.trackmeapplication.httpConnection.UserURLManager;
import com.trackme.trackmeapplication.httpConnection.exception.ConnectionException;
import com.trackme.trackmeapplication.request.exception.RequestNotWellFormedException;
import com.trackme.trackmeapplication.request.individualRequest.IndividualRequest;
import com.trackme.trackmeapplication.request.individualRequest.IndividualRequestWrapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class IndividualRequestNetworkImp implements IndividualRequestNetworkIInterface, LockInterface {

    private static IndividualRequestNetworkImp instance = null;

    private ObjectMapper mapper;

    private final Object lock = new Object();
    private boolean isLock;

    private IndividualRequestNetworkImp() {
        mapper = new ObjectMapper();
    }

    public static IndividualRequestNetworkImp getInstance() {
        if(instance == null)
            instance = new IndividualRequestNetworkImp();
        return instance;
    }

    @Override
    public List<IndividualRequestWrapper> getIndividualRequest(String token) throws ConnectionException {
        synchronized (lock) {
            isLock(true);
            BusinessURLManager businessURLManager = BusinessURLManager.getInstance();
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(businessURLManager.getIndividualRequestsLink())
                        .setHttpMethod(HttpMethod.GET).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                switch (connectionBuilder.getConnection().getStatusReturned()){
                    case OK:
                        List<LinkedHashMap<String, String>> list = JsonPath.read(
                                connectionBuilder.getConnection().getResponse(), "$..individualRequestWrapperList[*]");
                        List<String> links = JsonPath.read(
                                connectionBuilder.getConnection().getResponse(), "$..href");
                        List<IndividualRequestWrapper> individualRequestWrappers = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            IndividualRequestWrapper individualRequestWrapper = mapper.readValue(
                                    mapper.writeValueAsString(list.get(i)),
                                    IndividualRequestWrapper.class);
                            individualRequestWrapper.setResponseLink(links.get(i));
                            individualRequestWrappers.add(individualRequestWrapper);
                        }
                        return individualRequestWrappers;
                    case NOT_FOUND: return new ArrayList<>();
                    default: throw new ConnectionException();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    @Override
    public List<IndividualRequestWrapper> getOwnIndividualRequest(String token) throws ConnectionException {
        synchronized (lock) {
            isLock(true);
            UserURLManager userUrlManager = UserURLManager.getInstance();
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(userUrlManager.getPendingRequestsLink())
                        .setHttpMethod(HttpMethod.GET).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                switch (connectionBuilder.getConnection().getStatusReturned()){
                    case OK:
                        List<List<LinkedHashMap<String, String>>> list = JsonPath.read(
                                connectionBuilder.getConnection().getResponse(), "$..individualRequestWrapperList");
                        List<String> links = JsonPath.read(
                                connectionBuilder.getConnection().getResponse(), "$..href");
                        List<IndividualRequestWrapper> individualRequestWrappers = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            IndividualRequestWrapper individualRequestWrapper = mapper.readValue(
                                    mapper.writeValueAsString(list.get(0).get(i)),
                                    IndividualRequestWrapper.class);
                            individualRequestWrapper.setResponseLink(links.get(i));
                            individualRequestWrappers.add(individualRequestWrapper);
                        }
                        return individualRequestWrappers;
                    case NOT_FOUND: return new ArrayList<>();
                    default: throw new ConnectionException();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    @Override
    public void acceptIndividualRequest(String token, String url) throws ConnectionException {
        String responseUrl = getResponseLink(url);
        synchronized (lock) {
            isLock(true);
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>("ACCEPTED", httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(responseUrl)
                        .setHttpMethod(HttpMethod.POST).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                if (connectionBuilder.getConnection().getStatusReturned() != HttpStatus.OK)
                    throw new ConnectionException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String refuseIndividualRequest(String token,String url) throws ConnectionException {
        String responseUrl = getResponseLink(url);
        synchronized (lock) {
            isLock(true);
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>("REFUSED", httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(responseUrl)
                        .setHttpMethod(HttpMethod.POST).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                if (connectionBuilder.getConnection().getStatusReturned() == HttpStatus.OK) {
                    List<String> links = JsonPath.read(
                            connectionBuilder.getConnection().getResponse(), "$..blockThirdParty.href");
                    return links.get(0);
                } else throw new ConnectionException();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void blockThirdPartyCustomer(String token, String url) throws ConnectionException {
        synchronized (lock) {
            isLock(true);
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(url)
                        .setHttpMethod(HttpMethod.POST).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                if (connectionBuilder.getConnection().getStatusReturned() != HttpStatus.OK)
                    throw new ConnectionException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send(String token, IndividualRequest individualRequest, String userSSN) throws ConnectionException, RequestNotWellFormedException {
        synchronized (lock) {
            isLock(true);
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                ObjectMapper mapper = new ObjectMapper();
                BusinessURLManager businessURLManager = BusinessURLManager.getInstance();
                httpHeaders.add("Authorization", "Bearer " + token);
                HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(individualRequest), httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(businessURLManager.getNewIndividualRequestLink() + "/" + userSSN)
                        .setHttpMethod(HttpMethod.POST).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                switch (connectionBuilder.getConnection().getStatusReturned()){
                    case OK: break;
                    case CREATED: break;
                    case BAD_REQUEST: throw new RequestNotWellFormedException();
                    default: throw new ConnectionException();
                }
            } catch (InterruptedException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private String getResponseLink(String url) throws ConnectionException {
        synchronized (lock) {
            isLock(true);
            HttpHeaders httpHeaders = new HttpHeaders();
            try {
                HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

                ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
                connectionBuilder.setUrl(url)
                        .setHttpMethod(HttpMethod.GET).setEntity(entity).getConnection().start();
                while (isLock)
                    lock.wait();
                if (connectionBuilder.getConnection().getStatusReturned() == HttpStatus.OK){
                    List<String> links = JsonPath.read(
                            connectionBuilder.getConnection().getResponse(), "$..href");
                    return links.get(0);
                }
                throw new ConnectionException();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public Object getLock() {
        return lock;
    }

    @Override
    public void isLock(boolean b) {
        this.isLock = b;
    }
}
