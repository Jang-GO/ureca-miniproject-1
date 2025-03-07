package repository;

import connection.DBConnectionUtil;
import domain.Phone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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

                return new Phone(brand, createdAt, modelName, phoneId, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Phone findByModelName(String modelName) {
        Phone phone = null;
        String sql = "SELECT * FROM phone WHERE model_name = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, modelName);  // 모델명 파라미터 설정

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 휴대폰 데이터가 존재하면 Phone 객체로 반환
                phone = new Phone();
                phone.setPhoneId(rs.getInt("phone_id"));
                phone.setModelName(rs.getString("model_name"));
                phone.setBrand(rs.getString("brand"));
                phone.setPrice(rs.getInt("price"));
                phone.setCreatedAt(rs.getDate("created_at").toLocalDate());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("휴대폰 조회 실패", e);
        }

        return phone;  // 해당 모델의 휴대폰 객체를 반환하거나, 없으면 null을 반환
    }
}
