package repository;

import connection.DBConnectionUtil;
import domain.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SaleRepository {
    public boolean saveSale(Sale sale) {
        String query = "INSERT INTO sale (quantity, total_price, sale_date, customer_id, shop_id, phone_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, sale.getQuantity());
            pstmt.setInt(2, sale.getTotalPrice());
            pstmt.setTimestamp(3, Timestamp.valueOf(sale.getSaleDate())); // LocalDateTime -> Timestamp 변환
            pstmt.setInt(4, sale.getCustomerId());
            pstmt.setInt(5, sale.getShopId());
            pstmt.setInt(6, sale.getPhoneId());

            return pstmt.executeUpdate() > 0; // 성공하면 true 반환

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
