package com.example.streamlive.dao.contract.impl;

import com.example.streamlive.dao.contract.ContractDao;
import com.example.streamlive.model.SignatureData;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ContractDaoImpl implements ContractDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //更新代理人及商品代理狀態
    @Override
    public Integer updateDelegationByProductId(String productId,String agentId,int status) {
        String sql = "UPDATE delegation SET status = :status, agent_id = :agentId WHERE product_id = :productId";
        // 設定參數
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("agentId", agentId);
        params.put("status", status);
        // 執行更新操作，並返回受影響的行數
        return namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public String findDelegationIdByProductId(String productId) {
        // SQL 查詢語句
        String sql = "SELECT id FROM delegation WHERE product_id = :productId";
        // 設定參數
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Integer createDelegationDetails(String delegationId,SignatureData signatureData) {
        String sql = "INSERT INTO delegation_details (delegation_id,client,agent,product,sales_period,commission_rate,client_signature,agent_signature,created_at) VALUES " +
                "(:delegationId,:client,:agent,:product,:salesPeriod,:commissionRate,:clientSignature,:agentSignature,:createdAt)";
        // 設置參數映射
        Map<String, Object> params = new HashMap<>();
        params.put("delegationId", delegationId);
        params.put("client", signatureData.getClientName());
        params.put("agent", signatureData.getSellerName());
        params.put("product", signatureData.getProductName());
        params.put("salesPeriod", signatureData.getSalesPeriod());
        params.put("commissionRate", signatureData.getCommissionRate());
        params.put("clientSignature", signatureData.getSignatureImage());
        params.put("agentSignature", signatureData.getAgentSignatureImage());
        params.put("createdAt", signatureData.getTimestamp());

        // 執行插入並返回受影響的行數
        return namedParameterJdbcTemplate.update(sql, params);
    }
}
