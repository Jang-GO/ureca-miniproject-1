package repository;

import connection.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommonCodeRepository {
    public List<String> findBrandNames() {
        List<String> brands = new ArrayList<>();
        String query = "SELECT code_name FROM common_code WHERE parent_code = 'B01' AND use_yn = 'Y'";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                brands.add(rs.getString("code_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("브랜드 목록 조회 실패", e);
        }

        return brands;
    }
}
