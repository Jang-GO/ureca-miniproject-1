package connection;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTest {
    public static void main(String[] args) {
        try(Connection con = DBConnectionUtil.getConnection()){
            if(con != null){
                System.out.println("DB 연결 성공");
            }else{
                System.out.println("DB 연결 실패");
            }
        }catch(SQLException e){
            System.out.println("DB 연결 중 예외 발생");
            e.printStackTrace();
        }
    }
}
