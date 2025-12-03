package final_proj_08_11;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Factory_Order {

    public static Order initOrder(int cat_no, int amount, Product ordered_product, Customer cus) throws SQLException {
        return new Order(cat_no, amount, ordered_product, cus);
    }

    public static Order initOrder(Order order) {
        return new Order(order);
    }

    public static WebsiteOrder initWebsiteOrder(int cat_no, int amount, Product ordered_product, Customer cus, int ship, Set<DeliveryCompany> arr) throws SQLException {
        return new WebsiteOrder(cat_no, amount, ordered_product, cus, ship, arr);
    }

    public static WebsiteOrder initWebsiteOrder(WebsiteOrder web) {
        return new WebsiteOrder(web);
    }
}
