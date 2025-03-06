package repository;

import connection.DBConnectionUtil;
import domain.Sale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // 특정 가맹점의 판매 내역을 조회하는 메서드
    public List<Sale> findSalesByShopId(int shopId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sale WHERE shop_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, shopId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Sale sale = new Sale();
                sale.setSale_id(resultSet.getInt("sale_id"));
                sale.setQuantity(resultSet.getInt("quantity"));
                sale.setTotalPrice(resultSet.getInt("total_price"));
                sale.setSaleDate(resultSet.getTimestamp("sale_date").toLocalDateTime());
                sale.setCustomerId(resultSet.getInt("customer_id"));
                sale.setShopId(resultSet.getInt("shop_id"));
                sale.setPhoneId(resultSet.getInt("phone_id"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sales;
    }
}
