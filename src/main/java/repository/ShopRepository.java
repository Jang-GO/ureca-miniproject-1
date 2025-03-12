package repository;

import connection.DBConnectionUtil;
import domain.Shop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopRepository {

    public List<Shop> findByOwnerId(int ownerId) {
        List<Shop> list = new ArrayList<>();
        String query = """
                    SELECT s.shop_id, s.name, c.code_name AS location_name, s.owner_id, s.created_at 
                    FROM shop s
                    JOIN common_code c ON s.location_code = c.code_id
                    WHERE s.owner_id = ?;
                """;
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Shop shop = new Shop();
                shop.setShopId(rs.getInt("shop_id"));
                shop.setName(rs.getString("name"));
                shop.setLocation(rs.getString("location_name"));
                shop.setOwnerId(rs.getInt("owner_id"));
                // LocalDate로 변환
                Date createdAt = rs.getDate("created_at");
                if (createdAt != null) {
                    shop.setCreatedAt(createdAt.toLocalDate());
                }

                list.add(shop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
