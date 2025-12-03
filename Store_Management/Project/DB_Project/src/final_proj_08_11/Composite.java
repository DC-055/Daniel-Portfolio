package final_proj_08_11;

import static final_proj_08_11.StoreManage.arr;
import static final_proj_08_11.StoreManage.scan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class Composite {

	public void addProduct() throws SQLException {
		char option = ' ';

		while (option < 'A' || option > 'C') {
			System.out.println("Choose a product type:\n\tPress A for Website Product,"
					+ "\n\tPress B for Store Product,\n\t" + "Press C for Wholesale Product.\nEnter A, B or C:\n");

			option = scan.nextLine().charAt(0);
		}
		int catalog = 0;
		System.out.println("Please enter the Product's name:");
		String name = scan.nextLine();
		System.out.println("Please enter the Product's cost price:");
		int c_price = Integer.parseInt(scan.nextLine());
		System.out.println("Please enter the Product's selling price:");
		int s_price = Integer.parseInt(scan.nextLine());
		System.out.println("Please enter the Product's current stock:");
		int stock = Integer.parseInt(scan.nextLine());
		// for DB
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			String selectSQL;
			ResultSet rs;
			PreparedStatement pstmt = null;
			addProductToDB(conn, name, c_price, s_price, stock);
			selectSQL = "SELECT * FROM Product_Table WHERE catalog_num = "
					+ "(SELECT MAX(catalog_num) FROM Product_Table)";

			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next())
				catalog = rs.getInt("catalog_num");
			rs.close();
			pstmt.close();

			switch (option) {
			case 'A':
				selectSQL = "SELECT * FROM (SELECT Product_Table.catalog_num, product_name "
						+ "FROM Product_Table NATURAL JOIN WebsiteProduct_Table) WHERE product_name = ?";

				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setString(1, name);
				rs = pstmt.executeQuery();
				if (rs.next())
					System.out.println(
							"Product wasn't added - the product name and product type you entered is already in use.");
				else {
					addProduct_CaseA(name, c_price, s_price, stock, catalog);

					System.out.println("Product was added successfully!");
				}
				rs.close();
				pstmt.close();
				break;
			case 'B':
				selectSQL = "SELECT * from (SELECT Product_Table.catalog_num, product_name "
						+ "FROM Product_Table NATURAL JOIN StoreProduct_Table) WHERE product_name = ?";

				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setString(1, name);
				rs = pstmt.executeQuery();
				if (rs.next())
					System.out.println(
							"Product wasn't added - the product name and product type you entered is already in use.");
				else {
					addProduct_CaseB_C(name, c_price, s_price, stock, catalog, 1);
					System.out.println("Product was added successfully!");
				}
				rs.close();
				pstmt.close();
				break;
			case 'C':
				selectSQL = "SELECT * FROM (SELECT Product_Table.catalog_num, product_name "
						+ "FROM Product_Table NATURAL JOIN WholesaleProduct_Table) WHERE product_name = ?";

				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setString(1, name);
				rs = pstmt.executeQuery();
				if (rs.next())
					System.out.println(
							"Product wasn't added - the product name and product type you entered is already in use.");
				else {
					addProduct_CaseB_C(name, c_price, s_price, stock, catalog, 2);
					System.out.println("Product was added successfully!");
				}
				rs.close();
				pstmt.close();
				break;
			default:
				System.out.println("A, B or C are the only available options, please try again.");
				break;
			}
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	public void removeProduct() throws SQLException {
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			String deleteSQL;
			int remove_CN = 0, rowsAffected = 0;
			PreparedStatement pstmt = null;

			toStringAllProducts(conn);
			while (true) {
				System.out.println("Choose a product to remove, please provide its Catalog Number:\n");
				// check for input validity
				if (scan.hasNextInt()) {
					remove_CN = scan.nextInt();
					scan.nextLine();
					break;
				} else {
					System.out.println("Invalid input! Please enter a valid integer.");
					scan.nextLine();
				}
			}

			// delete-orderTable
			deleteSQL = "DELETE FROM Order_Table USING OrderProduct_Table "
					+ "WHERE Order_Table.order_num = OrderProduct_Table.order_num "
					+ "AND OrderProduct_Table.catalog_num = ?";
			pstmt = conn.prepareStatement(deleteSQL);
			pstmt.setInt(1, remove_CN);
			rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Product was removed from the Order_Table");
			} else {
				System.out.println("OrderTable info not updated -->\n"
						+ "(1) invalid CN Number OR (2) no orders were linked to this product.");
			}

			// ProductTable
			deleteSQL = "DELETE FROM Product_Table WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(deleteSQL);
			pstmt.setInt(1, remove_CN);
			rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				Thread.sleep(1000); // so called loading
				System.out.println("\n*The product has been removed*");
				return; // Exit the loop after successful deletion
			} else {
				System.out.println("invalid CN Number.\n");
			}
			scan.nextLine();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	public void restockProduct() throws SQLException {
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			String selectSQL;
			int remove_CN, stock;
			PreparedStatement pstmt = null;
			selectSQL = "SELECT * FROM Product_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// prints all products
				this.toStringAllProducts(conn);
				while (true) {
					System.out.println("Choose a product to restock, please provide its Catalog Number:\n");
					// check for input validity
					if (scan.hasNextInt()) {
						remove_CN = scan.nextInt();
						scan.nextLine();
						System.out.println("Please provide the stock quantity:\n");
						// check for input validity
						if (scan.hasNextInt()) {
							stock = scan.nextInt();
							scan.nextLine();
							String updateSQL = "UPDATE Product_Table SET stock = ? WHERE catalog_num = ?";
							pstmt = conn.prepareStatement(updateSQL);
							pstmt.setInt(1, stock);
							pstmt.setInt(2, remove_CN);
							int rowsAffected = pstmt.executeUpdate();
							if (rowsAffected > 0) {
								System.out.println("The product's stock has been updated.\n");
								break; // Exit the loop after successful deletion
							} else {
								System.out.println("No product found with the given CN, please try again.\n");
							}
						} else {
							System.out.println("Invalid input! Please enter a valid integer.");
							scan.nextLine();
						}
					} else {
						System.out.println("Invalid input! Please enter a valid integer.");
						scan.nextLine();
					}
				}
			} else
				System.out.println("There are no products in the system - please enter some and come back!\n");
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

	public void addOrder() throws SQLException {
		char option = ' ';

		while (!((option >= 'A' && option <= 'C') || (option >= 'a' && option <= 'c'))) {
			System.out.println("Choose a order type:\n\tPress A for Website Order," + "\n\tPress B for Store Order,\n\t"
					+ "Press C for Wholesale Order.\nEnter A, B or C:\n");

			option = scan.nextLine().charAt(0);
		}
		switch (option) {
		case 'A':
		case 'a':
			addOrder_CaseA();
			scan.nextLine();
			break;
		case 'B':
		case 'b':
			addOrder_CaseB();
			break;
		case 'C':
		case 'c':
			addOrder_CaseC();
			break;
		default:
			System.out.println("A, B or C are the only available options, please try again.");
			break;
		}
	}

	public void printAllProducts() throws SQLException {
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			toStringAllProducts(conn);
			conn.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}

	}

	public int findProductByCN() {
		int CN = 0;
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			CN = 0;
			ResultSet rs = null;
			PreparedStatement pstmt = null;

			toStringAllProducts(conn);
			while (true) {
				System.out.println("Please provide the product's catalog number you want to appear:\n");
				// check for input validity
				if (scan.hasNextInt()) {
					CN = scan.nextInt();
					scan.nextLine();
					break;
				} else {
					System.out.println("Invalid input! Please enter a valid integer.");
					scan.nextLine();
				}
			}

			String selectSQL = "SELECT * FROM Product_Table WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, CN);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// Print column headers
				System.out.printf("%-12s %-21s %-16s %-15s %-10s%n", "Catalog_Num", "Product_Name", "Cost_Price",
						"Selling_Price", "Stock");
				System.out.println("-------------------------------------------------------------------------");
				int catalogNum = rs.getInt("catalog_num");
				String productName = rs.getString("product_name");
				double costPrice = rs.getDouble("cost_price");
				double sellingPrice = rs.getDouble("selling_price");
				int stock = rs.getInt("stock");

				// Print each row's data
				System.out.printf("| %-11d| %-20s| %-15f| %-14f| %-8d%n", catalogNum, productName, costPrice,
						sellingPrice, stock);

				System.out.println("-------------------------------------------------------------------------\n");
				// Close the ResultSet and PreparedStatement
				pstmt.close();
				rs.close();
			} else {
				System.out.printf("Invalid catalog number, please try again later.\n");
			}

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
		return CN;
	}

	/**
	 * This method finds a product by a user-given CN number, it prints all Orders
	 * linked to that certain product.
	 */
	public void printProductOrdersByCN() {
		// there might be a need for a shallower printing method or removal of the
		// following line.
		double totProf = 0;
		int CN = findProductByCN();

		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			String selectSQL = "SELECT * FROM WebsiteProduct_Table WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, CN);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rs.close();
				// printing all of website orders
				selectSQL = "SELECT o.order_num, o.amount, w.shipping_method_id, w.shipping_price,"
						+ " p.selling_price, p.cost_price " + "FROM Order_Table o "
						+ "JOIN OrderProduct_Table op ON o.order_num = op.order_num "
						+ "JOIN WebsiteProduct_Table wp ON op.catalog_num = wp.catalog_num "
						+ "JOIN WebsiteOrder_Table w ON o.order_num = w.order_num "
						+ "JOIN Product_Table p ON op.catalog_num = p.catalog_num " + "WHERE wp.catalog_num = ?";
				pstmt = conn.prepareStatement(selectSQL);
				pstmt.setInt(1, CN);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int orderNum = rs.getInt("order_num");
					int amount = rs.getInt("amount");
					int shippingMethodId = rs.getInt("shipping_method_id");
					double shippingPrice = rs.getDouble("shipping_price");
					double sellingPrice = rs.getDouble("selling_price");
					double costPrice = rs.getDouble("cost_price");
					totProf += amount * (sellingPrice - costPrice);
					System.out.printf(
							"Order Num: %d, Amount: %d, Shipping Method ID: %d, Shipping Price: %.2f, Selling Price: %.2f, Cost Price: %.2f%n",
							orderNum, amount, shippingMethodId, shippingPrice, sellingPrice, costPrice);
				}

			} else {
				selectSQL = "SELECT o.order_num, o.amount, w.shipping_method_id, w.shipping_price, "
						+ "p.selling_price, p.cost_price " + "FROM Order_Table o "
						+ "JOIN OrderProduct_Table op ON o.order_num = op.order_num "
						+ "JOIN StoreProduct_Table sp ON op.catalog_num = sp.catalog_num "
						+ "JOIN WebsiteOrder_Table w ON o.order_num = w.order_num "
						+ "JOIN Product_Table p ON op.catalog_num = p.catalog_num " + "WHERE sp.catalog_num = ?";
				pstmt.setInt(1, CN);

				rs = pstmt.executeQuery();
				while (rs.next()) {
					int orderNum = rs.getInt("order_num");
					int amount = rs.getInt("amount");
					int shippingMethodId = rs.getInt("shipping_method_id");
					double shippingPrice = rs.getDouble("shipping_price");
					double sellingPrice = rs.getDouble("selling_price");
					double costPrice = rs.getDouble("cost_price");
					totProf += amount * (sellingPrice - costPrice);
					System.out.printf(
							"Order Num: %d, Amount: %d, Shipping Method ID: %d, Shipping Price: %.2f, Selling Price: %.2f, Cost Price: %.2f%n",
							orderNum, amount, shippingMethodId, shippingPrice, sellingPrice, costPrice);
				}
			}
			rs.close();
			pstmt.close();
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
		System.out.println("\nThe Total Profit from the chosen product is: " + totProf + "₪\n");
	}

	private WebsiteProduct addProduct_CaseA(String name, int c_price, int s_price, int stock, int catalog)
			throws SQLException {
		String dest;
		double weight;
		System.out.println("Please enter the Product's destination country:");
		dest = scan.nextLine();
		System.out.println("Please enter the Product's weight:");
		weight = Double.parseDouble(scan.nextLine());
		WebsiteProduct wprod = Factory_Product.initWebsiteProduct(name, c_price, s_price, stock, catalog, weight, dest);
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			wprod.addWebsiteProductToDB(conn);
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
		return wprod;
	}

	private Product addProduct_CaseB_C(String name, int c_price, int s_price, int stock, int catalog, int _case)
			throws SQLException {
		return Factory_Product.initStoreorWholesaleProduct(name, c_price, s_price, stock, catalog, _case);
	}

	private void addOrder_CaseA() throws SQLException {
		int shipment = 0;
		WebsiteProduct ordered_product = null;
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			String selectSQL;
			int stock = 0;
			int CN = 0;
			PreparedStatement pstmt = null;
			selectSQL = "SELECT * FROM WebsiteProduct_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// prints all products
				this.toStringWebsiteProducts(conn);
				while (true) {
					System.out.println(
							"Choose a product you want to add an order to, please provide its Catalog Number:\n");
					// check for input validity
					if (scan.hasNextInt()) {
						CN = scan.nextInt();
						scan.nextLine();
						selectSQL = "SELECT Product_Table.catalog_num, Product_Table.product_name, Product_Table.cost_price,"
								+ "	Product_Table.selling_price, Product_Table.stock,"
								+ "	WebsiteProduct_Table.dest_country," + "	WebsiteProduct_Table.prod_weight"
								+ " FROM Product_Table JOIN WebsiteProduct_Table"
								+ "	ON Product_Table.catalog_num = WebsiteProduct_Table.catalog_num"
								+ "	WHERE Product_Table.catalog_num=?";
						pstmt = conn.prepareStatement(selectSQL);
						pstmt.setInt(1, CN);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							// name, c_price, s_price, stock, catalog, weight, dest
							CN = rs.getInt("catalog_num");
							String name = rs.getString("product_name");
							int cost_price = rs.getInt("cost_price");
							int selling_price = rs.getInt("selling_price");
							stock = rs.getInt("stock");
							String dest = rs.getString("dest_country");
							double weight = rs.getDouble("prod_weight");

							ordered_product = Factory_Product.initWebsiteProduct(name, cost_price, selling_price, stock,
									CN, weight, dest);

							System.out.println("The product has been found.\n");
							break; // Exit the loop after successful deletion
						} else {
							System.out.println("No website product found with the given CN, please try again.\n");
						}
					} else {
						System.out.println("Invalid input! Please enter a valid integer.");
						scan.nextLine();
					}
				}
			} else {
				System.out.println("There are no website products in the system - please enter some and come back!\n");
				return;
			}
			rs.close();
			pstmt.close();

			int cat_no = 0;

			System.out.println("Please enter the order's amount:");
			int amount = Integer.parseInt(scan.nextLine());
			if (stock < amount) {
				System.out.println("Order failed!, there aren't enough pcs in stock for the chosen amount!");
				return;
			}

			// customer
			Customer customer = null;
			customer = customerInputCheck(conn);
			if (customer == null) {
				return;
			}

			// shipping
			System.out.println("Please enter the order's shipment: ('1-for standard'\n2-'express')");
			while (true) {
				shipment = scan.nextInt();
				if (shipment == 1 || shipment == 2)
					break;
				System.out.println("Wrong input, please try again.");
			}

			// creates order and checks if can be added
			Factory_Order.initWebsiteOrder(cat_no, amount, ordered_product, customer, shipment, arr);
			String updateSQL = "UPDATE Product_Table SET stock = ? WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setInt(1, stock - amount);
			pstmt.setInt(2, CN);
			pstmt.executeUpdate();
			System.out.println("The product's stock has been updated.\n");

		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	private void addOrder_CaseB() throws SQLException {
		int stock = 0, cost_price = 0, selling_price = 0;
		StoreProduct ordered_product = null;
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			String selectSQL;
			int CN = 0;
			PreparedStatement pstmt = null;
			selectSQL = "SELECT * FROM StoreProduct_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// prints all products
				this.toStringStoreProducts(conn);
				while (true) {
					System.out.println(
							"Choose a product you want to add an order to, please provide its Catalog Number:\n");
					// check for input validity
					if (scan.hasNextInt()) {
						CN = scan.nextInt();
						scan.nextLine();
						selectSQL = "SELECT Product_Table.catalog_num, Product_Table.product_name, Product_Table.cost_price,"
								+ "	Product_Table.selling_price, Product_Table.stock"
								+ " FROM Product_Table JOIN StoreProduct_Table"
								+ "	ON Product_Table.catalog_num = StoreProduct_Table.catalog_num"
								+ "	WHERE Product_Table.catalog_num=?";
						pstmt = conn.prepareStatement(selectSQL);
						pstmt.setInt(1, CN);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							// name, c_price, s_price, stock, catalog
							CN = rs.getInt("catalog_num");
							String name = rs.getString("product_name");
							cost_price = rs.getInt("cost_price");
							selling_price = rs.getInt("selling_price");
							stock = rs.getInt("stock");
							ordered_product = new StoreProduct(name, cost_price, selling_price, stock, CN);
							System.out.println("The product has been found.\n");
							break; // Exit the loop after successful deletion
						} else {
							System.out.println("No sotre product found with the given CN, please try again.\n");
						}
					} else {
						System.out.println("Invalid input! Please enter a valid integer.");
						scan.nextLine();
					}
				}
			} else {
				System.out.println("There are no store products in the system - please enter some and come back!\n");
			}
			rs.close();

			int cat_no = CN;
			System.out.println("Please enter the order's amount:");
			int amount = Integer.parseInt(scan.nextLine());

			if (stock < amount) {
				System.out.println("Order failed!, there aren't enough pcs in stock for the chosen amount!");
				return;
			}

			// customer
			Customer customer = null;
			customer = customerInputCheck(conn);
			if (customer == null) {
				return;
			}
			// creates order and checks if can be added
			StoreProduct sprod = new StoreProduct(customer.getCustomerName(), cost_price, selling_price, stock, CN);
			Factory_Order.initOrder(cat_no, amount, ordered_product, customer);

			String updateSQL = "UPDATE Product_Table SET stock = ? WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setInt(1, stock - amount);
			pstmt.setInt(2, CN);
			pstmt.executeUpdate();
			System.out.println("The product's stock has been updated.\n");
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}

	}

	private void addOrder_CaseC() throws SQLException {
		int stock = 0, cost_price = 0, selling_price = 0;
		WholesaleProduct ordered_product = null;
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			String selectSQL;
			int CN = 0;
			PreparedStatement pstmt = null;
			selectSQL = "SELECT * FROM WholesaleProduct_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// prints all products
				this.toStringWholesaleProducts(conn);
				while (true) {
					System.out.println(
							"Choose a product you want to add an order to, please provide its Catalog Number:\n");
					// check for input validity
					if (scan.hasNextInt()) {
						CN = scan.nextInt();
						scan.nextLine();
						selectSQL = "SELECT Product_Table.catalog_num, Product_Table.product_name, Product_Table.cost_price,"
								+ "	Product_Table.selling_price, Product_Table.stock"
								+ " FROM Product_Table JOIN WholesaleProduct_Table"
								+ "	ON Product_Table.catalog_num = WholesaleProduct_Table.catalog_num"
								+ "	WHERE Product_Table.catalog_num=?";
						pstmt = conn.prepareStatement(selectSQL);
						pstmt.setInt(1, CN);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							// name, c_price, s_price, stock, catalog
							CN = rs.getInt("catalog_num");
							String name = rs.getString("product_name");
							cost_price = rs.getInt("cost_price");
							selling_price = rs.getInt("selling_price");
							stock = rs.getInt("stock");
							ordered_product = new WholesaleProduct(name, cost_price, selling_price, stock, CN);
							System.out.println("The product has been found.\n");
							break; // Exit the loop after successful deletion
						} else {
							System.out.println("No wholesale product found with the given CN, please try again.\n");
						}
					} else {
						System.out.println("Invalid input! Please enter a valid integer.");
						scan.nextLine();
					}
				}
			} else {
				System.out
						.println("There are no Wholesale products in the system - please enter some and come back!\n");
				return;
			}
			rs.close();

			int cat_no = CN;
			System.out.println("Please enter the order's amount:");
			int amount = Integer.parseInt(scan.nextLine());

			if (stock < amount) {
				System.out.println("Order failed!, there aren't enough pcs in stock for the chosen amount!");
				return;
			}

			// customer
			Customer customer = null;
			customer = customerInputCheck(conn);
			if (customer == null) {
				return;
			}

			// creates order and checks if can be added
			Factory_Order.initOrder(cat_no, amount, ordered_product, customer);

			String updateSQL = "UPDATE Product_Table SET stock = ? WHERE catalog_num = ?";
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setInt(1, stock - amount);
			pstmt.setInt(2, CN);
			pstmt.executeUpdate();
			System.out.println("The product's stock has been updated.\n");
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	public void deleteOrder() throws SQLException {
		try {
			String dbUrl = "jdbc:postgresql://localhost:5432/StoreManagement";
			Connection conn = DriverManager.getConnection(dbUrl, "postgres", "Aa123456");
			ResultSet rs;
			String selectSQL;
			int ON = 0, CN = 0, amount = 0;
			PreparedStatement pstmt = null;
			selectSQL = "SELECT * FROM OrderProduct_Table";
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				// prints all products
				this.toStringAllOrders(conn);
				while (true) {
					System.out.println(
							"\nChoose an order you want to delete, please provide its Order Number:\n");
					// check for input validity
					if (scan.hasNextInt()) {
						ON = scan.nextInt();
						scan.nextLine();
						selectSQL = "SELECT amount FROM Order_Table WHERE Order_Table.order_num=?";
						pstmt = conn.prepareStatement(selectSQL);
						pstmt.setInt(1, ON);
						rs = pstmt.executeQuery();
						if(rs.next()) {
							amount = rs.getInt("amount");
						}
						rs.close();
						pstmt.close();
						
						selectSQL = "SELECT catalog_num FROM OrderProduct_Table WHERE OrderProduct_Table.order_num=?";
						pstmt = conn.prepareStatement(selectSQL);
						pstmt.setInt(1, ON);
						rs = pstmt.executeQuery();
						if(rs.next()) {
							CN = rs.getInt("catalog_num");
						}
						rs.close();
						pstmt.close();
						
						String deleteSQL = "DELETE FROM Order_Table WHERE Order_Table.order_num=?";
						pstmt = conn.prepareStatement(deleteSQL);
						pstmt.setInt(1, ON);
						int rowsAffected = pstmt.executeUpdate();
						if (rowsAffected > 0) {
							System.out.println("The order has been deleted!");
							
							rs.close();
							pstmt.close();
							String updateSQL = "UPDATE Product_Table SET stock = stock + ? WHERE catalog_num= ?";
							pstmt = conn.prepareStatement(updateSQL);
							pstmt.setInt(1, amount);
							pstmt.setInt(2, CN);
							rowsAffected = pstmt.executeUpdate();
							if (rowsAffected > 0) {
								System.out.println("Product restocked.");
								break;
							}
						} else {
							System.out.println("No order found with the given order number, please try again.\n");
						}
					} else {
						System.out.println("Invalid input! Please enter a valid integer.");
						scan.nextLine();
					}
				}
			} else {
				System.out.println("There are no orders in the system - "
						+ "please enter some and come back!\n");
			}
			rs.close();
			pstmt.close();
		}catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
	}

	private Customer customerInputCheck(Connection conn) {
		Customer customer = null;
		try {
			System.out.println("Please enter the Customer's name:");
			String name = scan.nextLine();
			System.out.println("Please enter the Customer's Mobile Number:");
			String number = scan.nextLine();
			if (number.length() > 10) {
				System.out.println("Mobile number longer than 10 digits");
				return null;
			}

			String selectSQL = "SELECT customer_mobile FROM Customer_Table WHERE customer_mobile = ?";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, number);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("Order failed!, there customer mobile you entered is already exists!");
				return null;
			} else
				customer = new Customer(name, number);
		} catch (SQLException ex) {
			while (ex != null) {
				System.out.println("SQL Exception: " + ex.getMessage());
				ex = ex.getNextException();
			}
		} catch (Exception ex) {
			System.out.println("General Exception: " + ex.getMessage());
		}
		return customer;
	}

	public void toStringWebsiteProducts(Connection conn) throws SQLException {
		ResultSet rs = null;
		String selectSQL = "SELECT * FROM Product_Table INNER JOIN WebsiteProduct_Table ON Product_Table.catalog_num = WebsiteProduct_Table.catalog_num";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(selectSQL);
		rs = pstmt.executeQuery();
		// Print column headers
		System.out.printf("%-12s %-21s %-16s %-15s %-10s%n", "Catalog_Num", "Product_Name", "Cost_Price",
				"Selling_Price", "Stock");
		System.out.println("-------------------------------------------------------------------------");

		// Process the ResultSet and print each row
		while (rs.next()) {
			int catalogNum = rs.getInt("catalog_num");
			String productName = rs.getString("product_name");
			double costPrice = rs.getDouble("cost_price");
			double sellingPrice = rs.getDouble("selling_price");
			int stock = rs.getInt("stock");

			// Print each row's data
			System.out.printf("| %-11d| %-20s| %-15.2f| %-14.2f| %-8d%n", catalogNum, productName, costPrice,
					sellingPrice, stock);
		}
		System.out.println("-------------------------------------------------------------------------\n");
		// Close the ResultSet and PreparedStatement
		pstmt.close();
		rs.close();
	}

	public void toStringStoreProducts(Connection conn) throws SQLException {
		ResultSet rs = null;
		String selectSQL = "SELECT * FROM Product_Table INNER JOIN StoreProduct_Table ON Product_Table.catalog_num = StoreProduct_Table.catalog_num";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(selectSQL);
		rs = pstmt.executeQuery();
		// Print column headers
		System.out.printf("%-12s %-21s %-16s %-15s %-10s%n", "Catalog_Num", "Product_Name", "Cost_Price",
				"Selling_Price", "Stock");
		System.out.println("-------------------------------------------------------------------------");

		// Process the ResultSet and print each row
		while (rs.next()) {
			int catalogNum = rs.getInt("catalog_num");
			String productName = rs.getString("product_name");
			double costPrice = rs.getDouble("cost_price");
			double sellingPrice = rs.getDouble("selling_price");
			int stock = rs.getInt("stock");

			// Print each row's data
			System.out.printf("| %-11d| %-20s| %-15.2f| %-14.2f| %-8d%n", catalogNum, productName, costPrice,
					sellingPrice, stock);
		}
		System.out.println("-------------------------------------------------------------------------\n");
		// Close the ResultSet and PreparedStatement
		pstmt.close();
		rs.close();
	}

	public void toStringWholesaleProducts(Connection conn) throws SQLException {
		ResultSet rs = null;
		String selectSQL = "SELECT * FROM Product_Table INNER JOIN WholesaleProduct_Table ON Product_Table.catalog_num = WholesaleProduct_Table.catalog_num";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(selectSQL);
		rs = pstmt.executeQuery();
		// Print column headers
		System.out.printf("%-12s %-21s %-16s %-15s %-10s%n", "Catalog_Num", "Product_Name", "Cost_Price",
				"Selling_Price", "Stock");
		System.out.println("-------------------------------------------------------------------------");

		// Process the ResultSet and print each row
		while (rs.next()) {
			int catalogNum = rs.getInt("catalog_num");
			String productName = rs.getString("product_name");
			double costPrice = rs.getDouble("cost_price");
			double sellingPrice = rs.getDouble("selling_price");
			int stock = rs.getInt("stock");

			// Print each row's data
			System.out.printf("| %-11d| %-20s| %-15.2f| %-14.2f| %-8d%n", catalogNum, productName, costPrice,
					sellingPrice, stock);
		}
		System.out.println("-------------------------------------------------------------------------\n");
		// Close the ResultSet
		rs.close();
	}

	public void toStringAllOrders(Connection conn) {
		try {
			String deleteSQL;
			int remove_CN = 0, rowsAffected = 0;
			PreparedStatement pstmt = null;
			String selectSQL = "SELECT \r\n"
					+ "    OrderProduct_Table.order_num, \r\n"
					+ "    Product_Table.catalog_num,\r\n"
					+ "    Product_Table.product_name, \r\n"
					+ "    Product_Table.cost_price, \r\n"
					+ "    Product_Table.selling_price, \r\n"
					+ "    Order_Table.amount\r\n"
					+ "FROM \r\n"
					+ "    OrderProduct_Table\r\n"
					+ "INNER JOIN \r\n"
					+ "    Product_Table \r\n"
					+ "ON \r\n"
					+ "    OrderProduct_Table.catalog_num = Product_Table.catalog_num\r\n"
					+ "INNER JOIN \r\n"
					+ "    Order_Table \r\n"
					+ "ON \r\n"
					+ "    OrderProduct_Table.order_num = Order_Table.order_num;";
			pstmt = conn.prepareStatement(selectSQL);
			ResultSet rs = pstmt.executeQuery();
			System.out.printf("%-12s %-12s %-21s %-16s %-15s %-10s%n", "Order_Num", "Catalog_Num", "Product_Name",
					"Cost_Price", "Selling_Price", "Amount");
			System.out.println("----------------------------------------------------------------------------------------");

			// Process the ResultSet and print each row
			while (rs.next()) {
				int ON = rs.getInt("order_num");
				int CN = rs.getInt("catalog_num");
				String name = rs.getString("product_name");
				double cost_price = rs.getDouble("cost_price");
				double selling_price = rs.getDouble("selling_price");
				int amount = rs.getInt("amount");

				// Print each row's data
				System.out.printf("|%-11d| %-11d| %-20s| %-15.2f| %-14.2f| %-8d%n", ON, CN, name, cost_price, selling_price,
						amount);
			}
			System.out.println("----------------------------------------------------------------------------------------\n");
			// Close the ResultSet and PreparedStatement
			pstmt.close();
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

	public void toStringAllProducts(Connection conn) throws SQLException {
		try {
			ResultSet rs = null;
			String selectSQL = "SELECT * FROM Product_Table";
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			// Print column headers
			System.out.printf("%-12s %-21s %-16s %-15s %-10s%n", "Catalog_Num", "Product_Name", "Cost_Price",
					"Selling_Price", "Stock");
			System.out.println("-------------------------------------------------------------------------");

			// Process the ResultSet and print each row
			while (rs.next()) {
				int catalogNum = rs.getInt("catalog_num");
				String productName = rs.getString("product_name");
				double costPrice = rs.getDouble("cost_price");
				double sellingPrice = rs.getDouble("selling_price");
				int stock = rs.getInt("stock");

				// Print each row's data
				System.out.printf("| %-11d| %-20s| %-15.2f| %-14.2f| %-8d%n", catalogNum, productName, costPrice,
						sellingPrice, stock);
			}
			System.out.println("-------------------------------------------------------------------------\n");
			// Close the ResultSet and PreparedStatement
			pstmt.close();
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

	public void addProductToDB(Connection conn, String name, int c_price, int s_price, int stock) throws SQLException {
		try {
			// Prepared SQL statement to insert values
			String insertSQL = "INSERT INTO Product_Table (product_name, cost_price, selling_price, stock) "
					+ "VALUES (?, ?, ?, ?)";
			// Create the PreparedStatement object
			PreparedStatement pstmt = conn.prepareStatement(insertSQL);
			// Set the values for the place-holders (?)
			pstmt.setString(1, name);
			pstmt.setInt(2, c_price);
			pstmt.setInt(3, s_price);
			pstmt.setInt(4, stock);
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
}
