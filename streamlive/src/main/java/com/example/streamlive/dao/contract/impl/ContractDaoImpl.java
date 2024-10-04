package com.example.streamlive.dao.contract.impl;

import com.example.streamlive.dao.contract.ContractDao;
import com.example.streamlive.model.Delegation;
import com.example.streamlive.model.DelegationDetails;
import com.example.streamlive.model.SignatureData;
import com.example.streamlive.model.rowmapper.DelegationDetailsRowMapper;
import com.example.streamlive.model.rowmapper.DelegationRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
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

        return namedParameterJdbcTemplate.update(sql, params);
    }

    // 取得自己是委託者的合約
    @Override
    public List<Delegation> findDelegationsByClientId(int clientId){
        String sql = "SELECT * FROM delegation WHERE client_id = :clientId AND agent_id != 0 ORDER BY id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", clientId);
        return namedParameterJdbcTemplate.query(sql, params, new DelegationRowMapper());
    }

    // 取得自己是代理者的合約
    @Override
    public List<Delegation> findDelegationsByAgentId(int agentId){
        String sql = "SELECT * FROM delegation WHERE agent_id = :agentId ORDER BY id DESC";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("agentId", agentId);

        return namedParameterJdbcTemplate.query(sql, params, new DelegationRowMapper());
    }

    // 取得合約的詳細資訊
    @Override
    public DelegationDetails findDelegationDetailsById(int id){
        String sql = "SELECT * FROM delegation_details WHERE delegation_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql, params, new DelegationDetailsRowMapper());
    }

}
