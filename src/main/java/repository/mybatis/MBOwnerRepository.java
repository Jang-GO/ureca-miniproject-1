package repository.mybatis;

import domain.Owner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface MBOwnerRepository {

    @Select("SELECT * FROM owner WHERE owner_uuid = #{ownerUuid}")
    Owner findByUUID(@Param("ownerUuid") UUID ownerUuid);
}
