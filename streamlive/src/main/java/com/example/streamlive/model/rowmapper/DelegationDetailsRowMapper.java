package com.example.streamlive.model.rowmapper;

import com.example.streamlive.model.DelegationDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DelegationDetailsRowMapper implements RowMapper<DelegationDetails> {
    @Override
    public DelegationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        DelegationDetails delegationDetails = new DelegationDetails();

        delegationDetails.setId(rs.getInt("id"));
        delegationDetails.setDelegationId(rs.getInt("delegation_id"));
        delegationDetails.setClient(rs.getString("client"));
        delegationDetails.setAgent(rs.getString("agent"));
        delegationDetails.setProduct(rs.getString("product"));
        delegationDetails.setSalesPeriod(rs.getString("sales_period"));
        delegationDetails.setCommissionRate(rs.getString("commission_rate"));
        delegationDetails.setClientSignature(rs.getString("client_signature"));
        delegationDetails.setAgentSignature(rs.getString("agent_signature"));
        delegationDetails.setCreatedAt(rs.getString("created_at"));

        return delegationDetails;
    }
}
