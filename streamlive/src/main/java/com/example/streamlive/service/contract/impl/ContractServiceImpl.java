package com.example.streamlive.service.contract.impl;

import com.example.streamlive.dao.contract.ContractDao;
import com.example.streamlive.dao.product.ProductDao;
import com.example.streamlive.dao.user.UserDao;
import com.example.streamlive.model.Delegation;
import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.DelegationList;
import com.example.streamlive.service.contract.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractDao contractDao;
    private final ProductDao productDao;
    private final UserDao userDao;
    // 取得自己是委託者的合約
    @Override
    public List<DelegationList> getClientDelegations(int clientId) {
        List<DelegationList> delegationLists = new LinkedList<>();
        List<Delegation> delegations= contractDao.findDelegationsByClientId(clientId);
        for (int i = 0; i < delegations.size(); i++) {
            DelegationList delegationList = new DelegationList();
            delegationList.setDelegationId(delegations.get(i).getId());
            delegationList.setProduct(productDao.findProductById(delegations.get(i).getProductId()));
            delegationList.setClient(userDao.getClientById(delegations.get(i).getClientId()));
            delegationList.setAgent(userDao.getAgentById(delegations.get(i).getAgentId()));
            delegationLists.add(delegationList);
        }
        return delegationLists;
    }

    // 取得自己是代理者的合約
    @Override
    public List<DelegationList> getAgentDelegations(int AgentId) {
        List<DelegationList> delegationLists = new LinkedList<>();
        List<Delegation> delegations= contractDao.findDelegationsByAgentId(AgentId);
        for (int i = 0; i < delegations.size(); i++) {
            DelegationList delegationList = new DelegationList();
            delegationList.setDelegationId(delegations.get(i).getId());
            delegationList.setProduct(productDao.findProductById(delegations.get(i).getProductId()));
            delegationList.setClient(userDao.getClientById(delegations.get(i).getClientId()));
            delegationList.setAgent(userDao.getAgentById(delegations.get(i).getAgentId()));
            delegationLists.add(delegationList);
        }
        return delegationLists;
    }

    // 取得合約的詳細資訊
    @Override
    public DelegationDetails getDelegationDetails(int id) {
        return contractDao.findDelegationDetailsById(id);
    }

}
