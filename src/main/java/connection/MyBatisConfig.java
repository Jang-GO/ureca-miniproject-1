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



        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
