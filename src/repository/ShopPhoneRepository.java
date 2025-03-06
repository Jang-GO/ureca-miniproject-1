package repository;

import connection.DBConnectionUtil;
import domain.ShopPhone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopPhoneRepository {
    public List<ShopPhone> findPhonesByShopId(int shopId) {
        List<ShopPhone> list = new ArrayList<>();
        String query = "SELECT * FROM shop_phone WHERE shop_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, shopId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ShopPhone shopPhone = new ShopPhone();
                shopPhone.setShopId(rs.getInt("shop_id"));
                shopPhone.setPhoneId(rs.getInt("phone_id"));
                shopPhone.setStock(rs.getInt("stock"));

                list.add(shopPhone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void decreaseStock(int shopId, int phoneId, int quantity) {
        String query = "UPDATE shop_phone SET stock = stock - ? WHERE shop_id = ? AND phone_id = ? AND stock >= ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, shopId);
            pstmt.setInt(3, phoneId);
            pstmt.setInt(4, quantity);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("재고를 감소시킬 수 없습니다. 해당 가맹점과 휴대폰이 일치하지 않습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("재고 감소 실패", e);
        }
    }
}
