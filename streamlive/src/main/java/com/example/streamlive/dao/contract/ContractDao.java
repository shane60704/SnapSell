package com.example.streamlive.dao.contract;

import com.example.streamlive.model.SignatureData;

public interface ContractDao {
    Integer updateDelegationByProductId(String productId,String agentId,int status);
    String findDelegationIdByProductId(String productId);
    Integer createDelegationDetails(String delegationId, SignatureData signatureData);

}
