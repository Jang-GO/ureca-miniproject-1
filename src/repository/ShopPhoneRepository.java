package repository;

import connection.DBConnectionUtil;
import domain.Phone;
import domain.ShopPhone;
import domain.ShopPhoneDTO;

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

    public void updateStock(int shopId, int phoneId, int newStock) {
        // ShopPhone 엔티티를 찾아서 재고를 업데이트하는 로직
        // 예시:
        String query = "UPDATE shop_phone SET stock = ? WHERE shop_id = ? AND phone_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, newStock);
            stmt.setInt(2, shopId);
            stmt.setInt(3, phoneId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("재고 업데이트 실패");
        }
    }

    public List<ShopPhoneDTO> findByShopIdAndSearchText(int shopId, String searchText) {
        List<ShopPhoneDTO> phones = new ArrayList<>();

        String sql = "SELECT p.model_name, p.brand, p.price, sp.stock " +
                "FROM shop_phone sp " +
                "JOIN phone p ON sp.phone_id = p.id " +
                "WHERE sp.shop_id = ? " +
                "AND (LOWER(p.model_name) LIKE LOWER(?) OR LOWER(p.brand) LIKE LOWER(?))";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, shopId);
            pstmt.setString(2, "%" + searchText + "%");
            pstmt.setString(3, "%" + searchText + "%");

            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    phones.add(new ShopPhoneDTO(
                            rs.getString("model_name"),
                            rs.getString("brand"),
                            rs.getInt("price"),
                            rs.getInt("stock")
                    ));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return phones;
    }
}
