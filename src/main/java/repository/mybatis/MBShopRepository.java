package repository.mybatis;

import domain.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MBShopRepository {
    @Select("""
                    SELECT s.shop_id, s.name, c.code_name AS location_name, s.owner_id, s.created_at 
                    FROM shop s
                    JOIN common_code c ON s.location_code = c.code_id
                    WHERE s.owner_id = #{ownerId};
                """)
    List<Shop> findByOwnerId(int ownerId);

}
