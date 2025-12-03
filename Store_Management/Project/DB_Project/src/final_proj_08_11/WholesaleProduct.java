
package final_proj_08_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WholesaleProduct extends Product {
    
   public WholesaleProduct(String name, int c_price, int s_price, int stock, int catalog) throws SQLException
   {
       super(name, c_price, s_price, stock, catalog);
       this.pType = Product.productType.eWholesale;
   }
   
   public WholesaleProduct(Product prod)
   {
       super(prod);
   }
   
   public void addWholesaleProductToDB(Connection conn) throws SQLException {
		PreparedStatement pstmt;
		String insertSQL = "INSERT INTO WholesaleProduct_Table (catalog_num) "
				+ "VALUES (?)";

		try {
			// Class.forName("com.postgresql.cj.jdbc.Driver");
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");

			// Create the PreparedStatement object
			pstmt = conn.prepareStatement(insertSQL);
			// Set the values for the place-holders (?)
			pstmt.setInt(1, this.CN);

			// Execute the insert
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}
   
   public String toString() {
	   return super.toString() + "price: " + super.selling_price + "₪\n";
   }
   
}
