
package final_proj_08_11;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WebsiteProduct extends Product {

	// 1.a final variables
	private static final double BASE_IMPORT_TAX = 20.0;

	// 1.b instance variables
	private String dest_country;
	private double country_import_tax;
	private double product_weight;
	private double price_in_dollar;

	public WebsiteProduct(String name, int c_price, int s_price, int stock, int catalog, double weight,
			String dest_country) throws SQLException {
		super(name, c_price, s_price, stock, catalog);
		this.price_in_dollar = s_price / Product.DOLLAR_VAL;
		this.dest_country = dest_country;
		this.product_weight = weight;
		this.pType = Product.productType.eWebsite;
	}

	public WebsiteProduct(Product prod, double weight, String dest_country) {
		super(prod);
		this.price_in_dollar = prod.getSellingPrice() / Product.DOLLAR_VAL;
		this.dest_country = dest_country;
		this.product_weight = weight;
	}
	
	
	public void addWebsiteProductToDB(Connection conn) throws SQLException {
		try {
			PreparedStatement pstmt;
			String insertSQL = "INSERT INTO WebsiteProduct_Table (catalog_num,dest_country, price_in_dollars, prod_weight, import_tax) "
					+ "VALUES (?, ?, ?, ?, ?)";
			// Create the PreparedStatement object
			pstmt = conn.prepareStatement(insertSQL);
			// Set the values for the place-holders (?)
			pstmt.setInt(1, this.CN);
			pstmt.setString(2, this.dest_country);
			pstmt.setDouble(3, this.price_in_dollar);
			pstmt.setDouble(4, this.product_weight);
			pstmt.setDouble(5, BASE_IMPORT_TAX);

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

	public String getDestCountry() {
		return dest_country;
	}

	public double getDestImportTax() {
		return BASE_IMPORT_TAX;
	}

	public double getProductWeight() {
		return this.product_weight;
	}

	public int getCostPrice() {
		return this.cost_price;
	}

	public int getSellingPrice() {
		// convert the price from NIS to Dollar
		return this.selling_price;
	}

	public double getPriceInDollars() {
		return this.price_in_dollar;
	}

	@Override
	public String toString() {
		return super.toString() + "price: " + this.price_in_dollar + "$\n";
	}

}
