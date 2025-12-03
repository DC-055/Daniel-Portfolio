
package final_proj_08_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StoreProduct extends Product {

	public StoreProduct(String name, int cost_price, int selling_price, int stock, int catalog) throws SQLException {
		super(name, cost_price, selling_price, stock, catalog);
		this.pType = Product.productType.eStore;
	}

	public StoreProduct(Product prod) {
		super(prod);
	}

	public void addStoreProductToDB(Connection conn) throws SQLException {

		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			PreparedStatement pstmt;
			String insertSQL = "INSERT INTO StoreProduct_Table (catalog_num) " + "VALUES (?)";
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
