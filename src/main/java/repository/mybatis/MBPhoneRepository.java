package repository.mybatis;

import domain.Phone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MBPhoneRepository {

    @Select("""
        SELECT p.phone_id, p.model_name, c.code_name AS brand_name, p.price, p.created_at 
        FROM phone p
        JOIN common_code c ON p.brand_code = c.code_id
        WHERE p.phone_id = #{phoneId}
    """)
    Phone findById(@Param("phoneId") int phoneId);

    @Select("""
        SELECT p.phone_id, p.model_name, c.code_name AS brand_name, p.price, p.created_at 
        FROM phone p
        JOIN common_code c ON p.brand_code = c.code_id
        WHERE p.model_name = #{modelName}
    """)
    Phone findByModelName(@Param("modelName") String modelName);
}
