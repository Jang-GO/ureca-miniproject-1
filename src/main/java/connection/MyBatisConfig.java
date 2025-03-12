package connection;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MyBatisConfig {
    public SqlSessionFactory getSqlSessionFactory() {

        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/ureca_mini1");
        dataSource.setUsername(System.getenv("MYSQL_USERNAME"));
        dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
        Configuration configuration = new Configuration();
        configuration.setEnvironment(new org.apache.ibatis.mapping.Environment("development",
                new JdbcTransactionFactory(), dataSource));
        // 매퍼 클래스 등록
        configuration.addMapper(repository.mybatis.MBShopRepository.class);
        configuration.addMapper(repository.mybatis.MBShopPhoneRepository.class);
        configuration.addMapper(repository.mybatis.MBCommonCodeRepository.class);
        configuration.addMapper(repository.mybatis.MBCustomerRepository.class);
        configuration.addMapper(repository.mybatis.MBOwnerRepository.class);
        configuration.addMapper(repository.mybatis.MBPhoneRepository.class);
        configuration.addMapper(repository.mybatis.MBSaleRepository.class);


        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
