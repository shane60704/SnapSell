package com.example.streamlive.service.contract;

import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.DelegationList;

import java.util.List;

public interface ContractService {
    List<DelegationList> getClientDelegations(int clientId);
    List<DelegationList> getAgentDelegations(int AgentId);
    DelegationDetails getDelegationDetails(int id);
}
