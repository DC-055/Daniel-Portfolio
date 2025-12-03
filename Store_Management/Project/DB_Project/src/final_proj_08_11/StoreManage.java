
package final_proj_08_11;

import java.util.Set;
import java.util.Stack;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;

// SINGLETON CLASS
public class StoreManage {

    private static StoreManage instance;
    private Composite comp = null;
    protected static Set<DeliveryCompany> arr = null;
    public static Scanner scan = new Scanner(System.in);
    protected static MyButton inform_company = new MyButton();

    private StoreManage() {
        HandleCompanies();
        comp = new Composite();
    }
    
    private void HandleCompanies()
    {
        DeliveryCompany _dhl = new DHL("Hadas Lapid", "0548792577");
        DeliveryCompany _fedex = new FedEx("Itzhak Genuth", "0525683317");
        arr = new HashSet<>();
        arr.add(_fedex);
        arr.add(_dhl);
        inform_company.attach(_dhl);
        inform_company.attach(_fedex);
    }

    public static StoreManage getInstance() {
        if (instance == null) {
            return new StoreManage();
        }
        return instance;
    }

    public void menu() throws SQLException {

        String option = null;
        // MENU OPTION LOOP
        while (option != "E" || option != "e") {
            System.out.println("\nMENU OPTIONS:\n\t4.1 - Use previous hard-coded information."
                    + "\n\t4.2 - Add a product\n\t4.3 - Remove a product\n\t4.4 - Restock a product\n\t4.5 - Place an order"
                    + "\n\t4.6 - Find a product by CN\n\t4.7 - Show all exisiting products"
                    + "\n\t4.8 - Show all orders linked to a specific product\n\t4.9 - Delete order\n"
                    + " Press E/e to exit.");

            option = scan.nextLine();
            switch (option) {
                case "4.1": {
                    hardCodedInfo();
                    break;
                }
                case "4.2": {
                    addProduct();
                    break;
                }
                case "4.3": {
                    removeProduct();
                    break;
                }
                case "4.4": {
                    setPrdouctStock();
                    break;
                }
                case "4.5": {
                    addOrder();
                    break;
                }
                case "4.6": {
                    printProductByCN();
                    break;
                }
                case "4.7": {
                    printAllProducts();
                    break;
                }
                case "4.8": {
                    printOrdersByCN();
                    break;
                }
                case "4.9": {
                	deleteOrderFromOrders();
                    break;
                }
                
                case "E":
                case "e": {
                    System.out.println("Exiting...");
                    return;
                }
                default:
                    System.out.println("Wrong input, Please try again.\n");
            }
        }
    }
    
    public void deleteOrderFromOrders() throws SQLException {
    	comp.deleteOrder();
    }
    
    public void hardCodedInfo() {
    	Connection conn = null;
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			PreparedStatement pstmt;
			ResultSet rs;
			
			String selectSQL = "SELECT catalog_num FROM Product_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("There are already products in the DB, "
						+ "please delete them all before trying again");
				pstmt.close();
				rs.close();
			}
			else {
				//WebsiteProducts
				String insertSQL = "INSERT INTO Product_Table (product_name, cost_price, selling_price, stock) VALUES ('Galaxy S24',3100,4200,11) RETURNING catalog_num";
				pstmt = conn.prepareStatement(insertSQL);
				rs = pstmt.executeQuery();
				int catalogNum = 0;
				if (rs.next()) {
					catalogNum = rs.getInt("catalog_num"); // Retrieve the generated catalog_num
				}
				pstmt.close();

				insertSQL = "INSERT INTO WebsiteProduct_Table (catalog_num, dest_country, price_in_dollars, prod_weight, import_tax) VALUES (?,?,?,?,?)";
				pstmt = conn.prepareStatement(insertSQL);
				pstmt.setInt(1, catalogNum);
				pstmt.setString(2, "USA");
				pstmt.setDouble(3, 4200 / Product.DOLLAR_VAL);
				pstmt.setDouble(4, 0.18);
				pstmt.setDouble(5, 20.0);
				pstmt.executeUpdate();
				pstmt.close();
				
				insertSQL = "INSERT INTO Product_Table (product_name, cost_price, selling_price, stock) VALUES ('Iphone 15',3500,5000,17) RETURNING catalog_num";
				pstmt = conn.prepareStatement(insertSQL);
				rs = pstmt.executeQuery();
				catalogNum = 0;
				if (rs.next()) {
					catalogNum = rs.getInt("catalog_num"); // Retrieve the generated catalog_num
				}
				pstmt.close();

				insertSQL = "INSERT INTO WebsiteProduct_Table (catalog_num, dest_country, price_in_dollars, prod_weight, import_tax) VALUES (?,?,?,?,?)";
				pstmt = conn.prepareStatement(insertSQL);
				pstmt.setInt(1, catalogNum);
				pstmt.setString(2, "ISR");
				pstmt.setDouble(3, 5000 / Product.DOLLAR_VAL);
				pstmt.setDouble(4, 0.2);
				pstmt.setDouble(5, 20.0);
				pstmt.executeUpdate();
				pstmt.close();
				
				//StoreProducts
				insertSQL = "INSERT INTO Product_Table (product_name, cost_price, selling_price, stock) VALUES ('AsusLap15',4500,6000,7) RETURNING catalog_num";
				pstmt = conn.prepareStatement(insertSQL);
				rs = pstmt.executeQuery();
				catalogNum = 0;
				if (rs.next()) {
					catalogNum = rs.getInt("catalog_num"); // Retrieve the generated catalog_num
				}
				pstmt.close();

				insertSQL = "INSERT INTO StoreProduct_Table (catalog_num) VALUES (?)";
				pstmt = conn.prepareStatement(insertSQL);
				pstmt.setInt(1, catalogNum);
				pstmt.executeUpdate();
				pstmt.close();
				
				//WholesaleProducts
				insertSQL = "INSERT INTO Product_Table (product_name, cost_price, selling_price, stock) VALUES ('iRobot-j7',4100,5000,24) RETURNING catalog_num";
				pstmt = conn.prepareStatement(insertSQL);
				rs = pstmt.executeQuery();
				catalogNum = 0;
				if (rs.next()) {
					catalogNum = rs.getInt("catalog_num"); // Retrieve the generated catalog_num
				}
				pstmt.close();

				insertSQL = "INSERT INTO WholesaleProduct_Table (catalog_num) VALUES (?)";
				pstmt = conn.prepareStatement(insertSQL);
				pstmt.setInt(1, catalogNum);
				pstmt.executeUpdate();
				pstmt.close();
				
				//printAllProducts
				comp.toStringAllProducts(conn);
			}
		} catch (

		SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
    }

    public void addProduct() throws SQLException {
        comp.addProduct();
    }

    public void removeProduct() throws SQLException {
        comp.removeProduct();
    }

    public void setPrdouctStock() throws SQLException {
        comp.restockProduct();
    }

    public void addOrder() throws SQLException {
        this.comp.addOrder();
    }

    public void printProductByCN() {
        comp.findProductByCN();
    }

    public void printOrdersByCN() {
        comp.printProductOrdersByCN();
    }

    public void printAllProducts() throws SQLException {
        comp.printAllProducts();
    }
     
}

