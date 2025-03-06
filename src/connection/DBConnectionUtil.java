package connection;

import java.sql.*;

import static connection.ConnectionConst.*;

public class DBConnectionUtil {

    public static Connection getConnection(){
        try{
            Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return con;
        }catch(SQLException e){
            throw new IllegalStateException("커넥션 에러", e);
        }
    }
}
