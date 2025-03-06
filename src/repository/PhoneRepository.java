package repository;

import connection.DBConnectionUtil;
import domain.Phone;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;

public class PhoneRepository {
    // 휴대폰 ID로 휴대폰 정보 조회
    public Phone findById(int phoneId) {
        String query = "SELECT * FROM phone WHERE phone_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement psmt = con.prepareStatement(query)) {
            psmt.setInt(1, phoneId);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                String modelName = rs.getString("model_name");
                String brand = rs.getString("brand");
                int price = rs.getInt("price");
                // LocalDate로 변환
                LocalDate createdAt = rs.getDate("created_at").toLocalDate();

                return new Phone(brand, createdAt,modelName, phoneId, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
