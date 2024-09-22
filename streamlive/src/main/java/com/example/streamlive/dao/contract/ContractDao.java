package com.example.streamlive.dao.contract;

import com.example.streamlive.model.Delegation;
import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.SignatureData;

import java.util.List;

public interface ContractDao {
    Integer updateDelegationByProductId(String productId,String agentId,int status);
    String findDelegationIdByProductId(String productId);
    Integer createDelegationDetails(String delegationId, SignatureData signatureData);
    List<Delegation> findDelegationsByClientId(int clientId);
    List<Delegation> findDelegationsByAgentId(int agentId);
    DelegationDetails findDelegationDetailsById(int id);
}
