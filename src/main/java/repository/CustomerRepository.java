package repository;

import connection.DBConnectionUtil;
import domain.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRepository {


    // 고객 정보를 저장하는 메서드
    public void save(Customer customer) {
        String sql = "INSERT INTO customer (name, phone_number) VALUES (?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("고객 저장 실패", e);
        }
    }

    public Customer findById(int customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ? ;";
        Customer customer = null;

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, customerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 고객 데이터가 존재하면 Customer 객체로 반환
                customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setPhoneNumber(rs.getString("phone_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("고객 조회 실패", e);
        }

        return customer;
    }

    public Customer findCustomerByNameAndPhone(String customerName, String customerPhone) {
        String sql = "SELECT * FROM customer WHERE name = ? AND phone_number = ?";
        Customer customer = null;

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customerName);
            stmt.setString(2, customerPhone);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 고객 데이터가 존재하면 Customer 객체로 반환
                customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setPhoneNumber(rs.getString("phone_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("고객 조회 실패", e);
        }

        return customer;
    }
}
