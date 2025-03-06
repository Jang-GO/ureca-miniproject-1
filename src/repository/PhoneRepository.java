package repository;

import connection.DBConnectionUtil;
import domain.Phone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                LocalDate date = rs.getDate("created_at").toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                return new Phone(brand, date,modelName, phoneId, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
