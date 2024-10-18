package com.example.streamlive.controller;

import com.example.streamlive.dao.contract.ContractDao;
import com.example.streamlive.model.SignatureData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContractSignatureController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ContractDao contractDao;

    @MessageMapping("/contract.sign")
    public void signContract(SignatureData signatureData) {
        switch (signatureData.getRequestType()){
            case "request":
                log.info("request:"+signatureData.getChatRoomId());
                messagingTemplate.convertAndSend("/topic/contract/" + signatureData.getChatRoomId(), signatureData);
                break;
            case "accept":
                log.info("accept:"+signatureData.getProductId());
                contractDao.updateDelegationByProductId(signatureData.getProductId(),signatureData.getAgentId(),1);
                String delegationId = contractDao.findDelegationIdByProductId(signatureData.getProductId());
                contractDao.createDelegationDetails(delegationId,signatureData);
                messagingTemplate.convertAndSend("/topic/contract/" + signatureData.getChatRoomId(), signatureData);
                break;
            case "reject":
                messagingTemplate.convertAndSend("/topic/contract/" + signatureData.getChatRoomId(), signatureData);
                break;
        }

    }

}
