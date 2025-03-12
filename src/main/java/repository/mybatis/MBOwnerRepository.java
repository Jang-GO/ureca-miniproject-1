package repository.mybatis;

import domain.Owner;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface MBOwnerRepository {

    @Select("SELECT * FROM owner WHERE owner_uuid = #{ownerUuid}")
    Owner findByUUID(@Param("ownerUuid") String ownerUuid);
}
