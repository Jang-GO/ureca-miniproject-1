package repository;

import connection.DBConnectionUtil;
import domain.Owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OwnerRepository {

    public Owner findByUUID(UUID ownerUuid) {
        String sql = "SELECT * FROM owner WHERE owner_uuid = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // UUID를 쿼리의 파라미터로 설정
            statement.setString(1, ownerUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Owner owner = new Owner();

                    owner.setOwnerId(resultSet.getInt("owner_id"));
                    owner.setName(resultSet.getString("name"));
                    owner.setPhoneNumber(resultSet.getString("phone_number"));
                    owner.setOwnerUuid(UUID.fromString(resultSet.getString("owner_uuid")));

                    return owner;
                } else {
                    return null; // 해당 UUID를 가진 owner가 없으면 null 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }
    }
}
