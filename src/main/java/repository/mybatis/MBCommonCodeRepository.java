package repository.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MBCommonCodeRepository {

    @Select("SELECT code_name FROM common_code WHERE parent_code = 'B01' AND use_yn = 'Y'")
    List<String> findBrandNames();
}