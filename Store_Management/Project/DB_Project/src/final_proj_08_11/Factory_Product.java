
package final_proj_08_11;

import java.sql.Connection;
import java.sql.SQLException;

// Factory class for all products

public class Factory_Product {
    
    public static WebsiteProduct initWebsiteProduct(String name, int c_price, int s_price, int stock, int catalog, double weight, String dest) throws SQLException
    {
        return new WebsiteProduct(name, c_price, s_price, stock, catalog, weight, dest);
    }
    
    public static WebsiteProduct initWebsiteProduct(Product prod, double weight, String dest)
    {
        return new WebsiteProduct(prod, weight, dest);
    }
    public static Product initStoreorWholesaleProduct(String name, int c_price, int s_price, int stock, int catalog, int _case) throws SQLException
    {
    	Connection conn = null;
        switch (_case) {
            case 1:
            	StoreProduct sprod = new StoreProduct(name, c_price, s_price, stock, catalog);
        		sprod.addStoreProductToDB(conn);
        		return sprod;
            case 2:
            	WholesaleProduct wprod = new WholesaleProduct(name, c_price, s_price, stock, catalog);
        		wprod.addWholesaleProductToDB(conn);
        		return wprod;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public static Product initStoreorWholesaleProduct(Product prod, int _case)
    {
        switch (_case) {
            case 1:
                return new StoreProduct(prod);
            case 2:
                return new WholesaleProduct(prod);
            default:
                throw new IllegalArgumentException();
        }
    }
    
    
    
}
