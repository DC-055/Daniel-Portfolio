
package final_proj_08_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Product implements Comparable<Product> {

    // 1.a final 
    public final static double DOLLAR_VAL = 4.0;
    public static enum productType {eStore, eWebsite, eWholesale};

    // 1.b class variables
    protected String product_name;
    protected int cost_price;
    protected int selling_price;
    protected int stock;
    protected int CN; // Catalog number
    protected Set<Order> orders;
    protected productType pType;
     
    
    /* protected modifier for constructor so no 
    abstract objects will be created in the main prog
     */
    protected Product(String name, int c_price, int s_price, int stock, int cn) throws SQLException {
        this.product_name = name;
        this.cost_price = c_price;
        this.selling_price = s_price;
        this.stock = stock;
        this.CN = cn;
        orders = new LinkedHashSet<>();
    }
    
    protected Product(Product other)
    {
    	if (other != null) {
            this.product_name = other.product_name;
            this.cost_price = other.cost_price;
            this.selling_price = other.selling_price;
            this.stock = other.stock;
            this.CN = other.CN;
            this.pType = other.pType;
            this.orders = new LinkedHashSet<>();
            if (!other.orders.isEmpty()) {
                for (Order order : other.orders) {
                	Order o = new Order(order);
                	o.setProduct(other);
                    this.orders.add(o); // Deep copy of each Order object
                    
                }
            }
        }
    }

    public String getProductName() {
        return this.product_name;
    }

    public void setProductName(String h) {
        this.product_name = h;
    }

    public int getCostPrice() {
        return this.cost_price;
    }

    public int getStock() {
        return this.stock;
    }
    
    public int getCatalog()
    {
        return this.CN;
    }
    
    public productType getType() {
    	return this.pType;
    }

    public void setCostPrice(int c) {
        this.cost_price = c;
    }

    public int getSellingPrice() {
        return this.selling_price;
    }
    
    
    public LinkedHashSet<Order> getAllOrdersOfProduct() {
        return (LinkedHashSet)this.orders;
    }
      
    public void setSellingPrice(int c) {
        this.selling_price = c;
    }

    public void setStock(int s) {
        this.stock = s;
    }

    public int calcProfit() {
        return this.selling_price - this.cost_price;
    }
    
    public String toStringProductType()
    {
        if(this.pType == Product.productType.eStore)
            return "Store Product";
        else if(this.pType == Product.productType.eWebsite)
            return "Website Product";
        else return "Wholesale Product";
    }
    
    public String toString()
    { 
        return "\nProduct Type: " + this.toStringProductType() + "\nProduct info - CN: " + this.CN + "\nName: " + this.product_name + "\ncurrent stock: " + this.stock + " unit(s).\n";  
    }
    
    public String toStringAllOrdersOfProduct() 
    {
    	StringBuffer sb = new StringBuffer();
    	for(Order o : this.orders)
    	{
    		sb.append(o.toString() + "\n");
    	}
    	String str = sb.toString();
    	return str;
    }
    
    public boolean placeAnOrder(Order order) {
        
        //Product p_order = order.getProduct();
        if (this.getStock() < order.getAmount()) {
            System.out.println("Order cannot be completed; low stock!");
            return false;
            
        } else if (this.orders.contains(order)) {
            System.out.println("Order cannot be added - cat.no. already exists!");
            return false;
        } else { // order was successful, updating the stocks
            this.setStock(this.getStock() - order.getAmount());
            order.setProduct(Order.getProductType(this));
            //order.setProduct(this);
            this.orders.add(order);
            return true;
        }

    }
    
    @Override
    public int compareTo(Product p) {
    	if(this.CN == p.CN)
    		return 1;
        return 0;
    }
    

}
