package repository.mybatis;

import domain.ShopPhone;
import domain.dto.ShopPhoneDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MBShopPhoneRepository {

    // 1. 가맹점 ID로 해당 가게의 모든 휴대폰 재고 조회
    @Select("SELECT * FROM shop_phone WHERE shop_id = #{shopId}")
    List<ShopPhone> findPhonesByShopId(int shopId);

    // 2. 재고 감소 (재고가 부족할 경우 업데이트가 수행되지 않음)
    @Update("""
            UPDATE shop_phone 
            SET stock = stock - #{quantity} 
            WHERE shop_id = #{shopId} 
            AND phone_id = #{phoneId} 
            AND stock >= #{quantity}
            """)
    void decreaseStock(@Param("shopId") int shopId, @Param("phoneId") int phoneId, @Param("quantity") int quantity);

    // 3. 특정 가게의 특정 휴대폰 재고를 설정값으로 업데이트
    @Update("""
            UPDATE shop_phone 
            SET stock = #{newStock} 
            WHERE shop_id = #{shopId} 
            AND phone_id = #{phoneId}
            """)
    void updateStock(@Param("shopId") int shopId, @Param("phoneId") int phoneId, @Param("newStock") int newStock);

    // 4. 가맹점 ID와 검색어로 휴대폰 정보 조회
    @Select("""
            SELECT p.model_name, p.brand, p.price, sp.stock 
            FROM shop_phone sp
            JOIN phone p ON sp.phone_id = p.phone_id
            WHERE sp.shop_id = #{shopId} 
            AND (LOWER(p.model_name) LIKE CONCAT('%', #{searchText}, '%') 
            OR LOWER(p.brand) LIKE CONCAT('%', #{searchText}, '%'))
            """)
    List<ShopPhoneDTO> findByShopIdAndSearchText(@Param("shopId") int shopId, @Param("searchText") String searchText);

    // 5. 특정 가게의 특정 휴대폰 정보를 삭제
    @Delete("DELETE FROM shop_phone WHERE shop_id = #{shopId} AND phone_id = #{phoneId}")
    void deleteByShopIdAndPhoneId(@Param("shopId") int shopId, @Param("phoneId") int phoneId);
}
