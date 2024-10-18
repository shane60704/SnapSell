package com.example.streamlive.controller;

import com.example.streamlive.dto.response.APIResponse;
import com.example.streamlive.service.contract.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/1.0/contract")
public class ContractController {
    private final ContractService contractService;

    // 取得自己是委託者的合約
    @GetMapping("/as-client")
    public ResponseEntity<?> getClientDelegations(@RequestParam int clientId) {
        return ResponseEntity.ok(new APIResponse<>(contractService.getClientDelegations(clientId)));
    }

    // 取得自己是代理者的合約
    @GetMapping("/as-agent")
    public ResponseEntity<?> getAgentDelegations(@RequestParam int agentId) {
        return ResponseEntity.ok(new APIResponse<>(contractService.getAgentDelegations(agentId)));
    }

    // 取得合約的詳細資訊
    @GetMapping("/{id}")
    public ResponseEntity<?> getDelegationDetails(@PathVariable int id) {
        return ResponseEntity.ok(new APIResponse<>(contractService.getDelegationDetails(id)));
    }
}