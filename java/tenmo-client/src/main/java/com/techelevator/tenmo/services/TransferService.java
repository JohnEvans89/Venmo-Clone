package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class TransferService {

    private final String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    public static String AUTH_TOKEN = "";

    public TransferService(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    public void addNewTransfer(Transfer transfer) throws TransferServiceException {
        try {
            restTemplate.postForObject(BASE_URL + "transfers", makeTransferEntity(transfer), Transfer.class);
        } catch (RestClientResponseException ex) {
            throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }

    public Transfer getTransferByTransferId(int id) throws TransferServiceException {
        Transfer transfer = null;
        try {
            transfer = restTemplate.exchange(BASE_URL + "transfers/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }

        return transfer;
    }

    public List<Transfer> allUsersTransfers(int id) throws TransferServiceException {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(BASE_URL + "users/" + id + "/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return Arrays.asList(transfers);
    }

    public List<Transfer> allUsersPendingTransfers(int id, Integer transferStatus) throws TransferServiceException {
        Transfer[] transfers = null;
        try {
            transfers = restTemplate.exchange(BASE_URL + "users/" + id + "/transfers?transferStatusId=" + transferStatus, HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        } catch (RestClientResponseException ex) {
            throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
        return Arrays.asList(transfers);
    }

    public void update(Transfer updatedTransfer) throws TransferServiceException {
        try {
            restTemplate.put(BASE_URL + "transfers/" + updatedTransfer.getTransferId(), makeTransferEntity(updatedTransfer));
        } catch (RestClientResponseException ex) {
            throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
        }
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
