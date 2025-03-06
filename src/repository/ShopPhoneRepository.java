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
}
