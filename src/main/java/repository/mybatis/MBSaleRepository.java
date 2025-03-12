package repository.mybatis;

import domain.Sale;
import domain.dto.SaleDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MBSaleRepository {
    // 1. 판매 기록 저장
    @Insert("INSERT INTO sale (quantity, total_price, sale_date, customer_id, shop_id, phone_id) " +
            "VALUES (#{quantity}, #{totalPrice}, #{saleDate}, #{customerId}, #{shopId}, #{phoneId})")
    boolean saveSale(Sale sale);

    @Select("SELECT * FROM sale WHERE shop_id = #{shopId}")
    List<Sale> findSalesByShopId(@Param("shopId") int shopId);

    // 3. 특정 가맹점의 특정 연도 판매 내역 조회
    @Select("SELECT * FROM sale WHERE shop_id = #{shopId} AND YEAR(sale_date) = #{year}")
    List<Sale> findSalesByShopIdAndYear(@Param("shopId") int shopId, @Param("year") int year);

    // 4. 가맹점 ID와 검색어(고객 이름 또는 휴대폰 모델명)로 판매 내역 조회
    @Select("""
            SELECT c.name, c.phone_number, p.model_name, s.quantity, s.total_price, s.sale_date 
            FROM sale s 
            JOIN customer c ON s.customer_id = c.customer_id 
            JOIN phone p ON s.phone_id = p.phone_id 
            WHERE s.shop_id = #{shopId} 
            AND (LOWER(c.name) LIKE CONCAT('%', #{searchText}, '%') 
            OR LOWER(p.model_name) LIKE CONCAT('%', #{searchText}, '%'))
            """)
    List<SaleDTO> findSalesByShopIdAndSearchText(@Param("shopId") int shopId, @Param("searchText") String searchText);
}
