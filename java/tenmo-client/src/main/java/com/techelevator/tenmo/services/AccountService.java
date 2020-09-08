package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public static String AUTH_TOKEN = "";
    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

//    public double accountBalance(int id) throws AccountServiceException {
//        double balance = 0;
//        try {
//            balance = restTemplate.exchange(BASE_URL + "accounts/" + id, HttpMethod.GET, makeAuthEntity(), Double.class).getBody();
//        } catch (RestClientResponseException ex) {
//            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
//        }
//        return balance;
//    }

    public Account getOne(int id) throws AccountServiceException {
        Account account = null;
        try {
            account = restTemplate.exchange(BASE_URL + "accounts/" + id, HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return account;
    }

    public void update(Account updatedAccount) throws AccountServiceException {
        try {
            restTemplate.put(BASE_URL + "accounts/" + updatedAccount.getAccountId(), makeAccountEntity(updatedAccount));
        } catch (RestClientResponseException ex) {
            throw new AccountServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Account> entity = new HttpEntity<>(account, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
