package repository;

import connection.DBConnectionUtil;
import domain.Shop;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ShopRepository {

    public List<Shop> findByOwnerId(int ownerId){
        List<Shop> list = new ArrayList<>();
        String query = "SELECT * FROM shop WHERE owner_id = ?; ";
        try(Connection con = DBConnectionUtil.getConnection();
            PreparedStatement pstmt = con.prepareStatement(query)){
            pstmt.setInt(1,ownerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println(query);
            while (rs.next()) {
                Shop shop = new Shop();
                shop.setShopId(rs.getInt("shop_id"));
                shop.setShopName(rs.getString("name"));
                shop.setLocation(rs.getString("location"));
                shop.setOwnerId(rs.getInt("owner_id"));
                // LocalDate로 변환
                Date createdAt = rs.getDate("created_at");
                if (createdAt != null) {
                    shop.setCreatedAt(createdAt.toLocalDate());
                }

                list.add(shop);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
