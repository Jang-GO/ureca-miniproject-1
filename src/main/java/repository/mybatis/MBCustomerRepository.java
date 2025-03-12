package repository.mybatis;

import domain.Customer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MBCustomerRepository {

    @Insert("INSERT INTO customer (name, phone_number) VALUES (#{name}, #{phoneNumber})")
    void save(Customer customer);

    @Select("SELECT * FROM customer WHERE customer_id = #{customerId}")
    Customer findById(@Param("customerId") int customerId);

    @Select("SELECT * FROM customer WHERE name = #{name} AND phone_number = #{phoneNumber}")
    Customer findCustomerByNameAndPhone(@Param("name") String name, @Param("phoneNumber") String phoneNumber);
}