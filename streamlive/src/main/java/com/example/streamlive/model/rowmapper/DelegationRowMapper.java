package com.example.streamlive.model.rowmapper;

import com.example.streamlive.model.Delegation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DelegationRowMapper implements RowMapper<Delegation> {
    @Override
    public Delegation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Delegation delegation = new Delegation();
        delegation.setId(rs.getInt("id"));
        delegation.setProductId(rs.getInt("product_id"));
        delegation.setClientId(rs.getInt("client_id"));
        delegation.setAgentId(rs.getInt("agent_id"));
        delegation.setStatus(rs.getInt("status"));
        return delegation;
    }
}
