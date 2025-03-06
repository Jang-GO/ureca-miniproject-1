package connection;

public abstract class ConnectionConst {
    public static final String URL =  "jdbc:mysql://localhost:3306/ureca_mini1";
    public static final String USERNAME = System.getenv("MYSQL_USERNAME");
    public static final String PASSWORD = System.getenv("MYSQL_PASSWORD");
}
