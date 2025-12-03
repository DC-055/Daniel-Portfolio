
package final_proj_08_11;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

// includes the shipping option

public class WebsiteOrder extends Order {

	private int shipping_method;
	private double shipping_price;
	private DeliveryCompany shipping_company;

	public WebsiteOrder(int cat_no, int amount, Product prod, Customer cus, int ship, Set<DeliveryCompany> arr)
			throws SQLException {
		super(cat_no, amount, prod, cus);
		pickCompany(ship, arr);
		Connection conn = null;
		addWebsiteOrderToDB(conn, ship, arr);
	}

	public WebsiteOrder(WebsiteOrder web) {
		super((Order) web);
		this.shipping_company = web.shipping_company;
		this.shipping_method = web.shipping_method;
		this.shipping_company = Factory_DeliveryCompany.initCompany(web.getCompany().contact, web.getCompany().whatsapp,
				web.getCompany().getCompanyType());
	}

	public void addWebsiteOrderToDB(Connection conn, int ship, Set<DeliveryCompany> arr) {
		PreparedStatement pstmt;
		String insertSQL = "INSERT INTO WebsiteOrder_Table (order_num,shipping_method_id,shipping_price) "
				+ "VALUES (?, ?, ?)";

		try {
			// Class.forName("com.postgresql.cj.jdbc.Driver");
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");

			// Create the PreparedStatement object
			pstmt = conn.prepareStatement(insertSQL);
			// Set the values for the place-holders (?)
			pstmt.setInt(1, this.cat_no);
			pstmt.setInt(2, this.shipping_method);
			pstmt.setDouble(3, this.shipping_price);

			// Execute the insert
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("The order has been added!");
				addCompanyToDB(conn);
			}	
			else
				System.out.println("The order hasn't been added!");

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}
	
	public void addCompanyToDB(Connection conn) {
		try {
			PreparedStatement pstmt;
			String insertSQL = "INSERT INTO DeliveryOrder_Table (company_name,whatsapp_number,order_num)"
					+ "VALUES (?, ?, ?)";

			// Create the PreparedStatement object
			pstmt = conn.prepareStatement(insertSQL);
			// Set the values for the place-holders (?)
			pstmt.setString(1, this.shipping_company.getClass().getSimpleName());
			pstmt.setString(2, this.shipping_company.whatsapp);
			pstmt.setInt(3, this.cat_no);

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

	public int getShippingMethod() {
		return this.shipping_method;
	}

	public double getShippingPrice() {
		return this.shipping_price;
	}

	public DeliveryCompany getCompany() {
		return this.shipping_company;
	}

	public void setShippingPrice(double s) {
		this.shipping_price = s;
	}

	public void setShippingMethod(int s) {
		this.shipping_method = s;
	}

	public void setDeliveryCompany(DeliveryCompany d) {
		this.shipping_company = d;
	}

	private void pickCompany(int ship, Set<DeliveryCompany> arr) {
		DeliveryCompany chosenCompany = null;
		String compName = null;
		double lowestShippingPrice = 0, currPrice = 0;
		boolean fl = false;
		if (ship == 2) {
			// inform companies on chosen shipping method
			StoreManage.inform_company.setMsg(
					"Customer making Order no. " + this.cat_no + " has chosen " + "Express" + " shipping method");
			StoreManage.inform_company.click();
			for (DeliveryCompany comp : arr) {
				if (expressShipping.class.isAssignableFrom(comp.getClass())) {
					currPrice = ((expressShipping) comp).fee_ExpressShipping((WebsiteProduct) this.ordered_product);
					if (!fl) {
						lowestShippingPrice = currPrice;
						chosenCompany = comp;
						fl = true;
					}
					if (lowestShippingPrice > currPrice) {
						lowestShippingPrice = currPrice;
						chosenCompany = comp;
					}
				}
			}
		} else if (ship == 1) {
			// inform companies on chosen shipping method
			StoreManage.inform_company.setMsg(
					"Customer making Order no. " + this.cat_no + " has chosen " + "Standard" + " shipping method");
			StoreManage.inform_company.click();
			for (DeliveryCompany comp : arr) {
				if (standardShipping.class.isAssignableFrom(comp.getClass())) {
					currPrice = ((standardShipping) comp).fee_StandardShipping((WebsiteProduct) this.ordered_product);
					if (!fl) {
						lowestShippingPrice = currPrice;
						chosenCompany = comp;
						fl = true;
					}
					if (lowestShippingPrice > currPrice) {
						lowestShippingPrice = currPrice;
						chosenCompany = comp;
					}
				}
			}
		} else {
			System.out.println("Illegal shipping type!");
		}

		// set class variables

		this.setShippingPrice(lowestShippingPrice);
		this.setShippingMethod(ship);

		compName = chosenCompany.getClass().getSimpleName();
		if (compName.equals("DHL"))
			chosenCompany = Factory_DeliveryCompany.initCompany(chosenCompany.getContact(), chosenCompany.getWhatsapp(),
					DeliveryCompany.companyType.eDHL);
		else if (compName.equals("FedEx"))
			chosenCompany = Factory_DeliveryCompany.initCompany(chosenCompany.getContact(), chosenCompany.getWhatsapp(),
					DeliveryCompany.companyType.eFedEx);
		this.setDeliveryCompany(chosenCompany);

		System.out.println("Product & Shipping information:\n" + this);

	}

	public String toString() {
		// super.toString();
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append("\nShipping Method: " + this.shipping_method + "\n");
		sb.append("Shipping Price: " + this.shipping_price + "\n");
		sb.append("Shipping Company: " + this.shipping_company.getClass().getSimpleName());

		String str = sb.toString();
		return str;
	}

}
