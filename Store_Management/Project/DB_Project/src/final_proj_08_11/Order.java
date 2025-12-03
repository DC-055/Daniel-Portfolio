
package final_proj_08_11;

import java.sql.*;
import java.util.Objects;

public class Order {

	private final double TAX = 0.17;

	protected int cat_no;
	protected int amount;
	protected Product ordered_product;
	protected Customer customer;

	public Order(int cat_no, int amount, Product ordered_product, Customer cus) throws SQLException {
		this.cat_no = cat_no;
		this.amount = amount;
		this.ordered_product = ordered_product;
		this.customer = new Customer(cus);

		try {
		Connection conn = null;
		addOrderToDB(conn);
		}catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	public Order(Order other) {
		this.cat_no = other.cat_no;
		this.amount = other.amount;
		this.ordered_product = getProductType(ordered_product);
		this.customer = new Customer(other.customer);
	}

	public void addOrderToDB(Connection conn)
			throws SQLException {
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			PreparedStatement pstmt;
			
			String insertSQL = "INSERT INTO Order_Table (amount) VALUES (?)";
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setInt(1, this.amount);
			pstmt.executeUpdate();
			
			String selectSQL = "SELECT order_num FROM Order_Table WHERE order_num = (SELECT MAX(order_num) FROM Order_Table)";		
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				this.cat_no = rs.getInt("order_num");
			}
			//updating order_num into customer
			insertSQL = "INSERT INTO Customer_Table (customer_mobile, customer_name, order_num) VALUES (?,?,?)";
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, this.customer.getMobile());
			pstmt.setString(2, this.customer.getCustomerName());
			pstmt.setInt(3, this.cat_no);
			pstmt.executeUpdate();
			
			insertSQL = "INSERT INTO OrderProduct_Table (order_num,catalog_num) VALUES (?,?)";
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setInt(1, this.cat_no);
			pstmt.setInt(2, this.ordered_product.CN);
			pstmt.executeUpdate();
			
			selectSQL = "SELECT * FROM Order_Table WHERE order_num = "
					+ "(SELECT MAX(order_num) FROM Order_Table)";
			
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next())
				this.cat_no = rs.getInt("order_num");
			rs.close();

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	public int getCatNo() {
		return this.cat_no;
	}

	public int getAmount() {
		return this.amount;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public Product getProduct() {
		return this.ordered_product;
	}

	public void setCatNo(int c) {
		this.cat_no = c;
	}

	public void setAmount(int a) {
		this.amount = a;
	}

	public void setProduct(Product p) {
		this.ordered_product = p;
	}

	public static Product getProductType(Product p) {
		if (p instanceof WebsiteProduct)
			return Factory_Product.initWebsiteProduct(p, ((WebsiteProduct) p).getProductWeight(),
					((WebsiteProduct) p).getDestCountry());
		else if (p instanceof StoreProduct)
			return Factory_Product.initStoreorWholesaleProduct(p, 1);
		else
			return Factory_Product.initStoreorWholesaleProduct(p, 2);
	}

	public int calcOrderTotal() {
		return this.ordered_product.selling_price * amount;
	}

	public double calcTaxTotal() {
		double num = this.calcOrderTotal() * TAX;
		// Formatting the number to have two digits after the decimal point
		String formattedNum = String.format("%.2f", num);
		// Parsing the formatted string back to double (optional, depends on your use
		// case)
		double formattedNumDouble = Double.parseDouble(formattedNum);
		return formattedNumDouble;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("## ORDER CN: " + this.cat_no + " ##\n");
		sb.append(this.customer.toString() + "\n");
		sb.append("Product name: " + this.ordered_product.product_name + "\n");
		sb.append("Product type: " + this.ordered_product.toStringProductType() + "\n");
		sb.append("Qunatity ordered: " + this.amount + " unit(s).");

		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		// Cast the object to the appropriate type
		Order other = (Order) obj;
		// Compare the relevant fields for equality
		return this.cat_no == other.cat_no;
	}

}
