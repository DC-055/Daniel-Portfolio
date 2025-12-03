package final_proj_08_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class FINAL_PROJ_08_11 {

	public static void main(String[] args) throws SQLException {

		StoreManage sm = StoreManage.getInstance();
		sm.menu();
		sm.printAllProducts();
	}

}
