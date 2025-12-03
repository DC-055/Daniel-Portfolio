
package final_proj_08_11;

public class Customer {
    
    // 1. instance variables
    
    private String customer_name;
    private String mobile;
    
    public Customer(String name, String number)
    {
        this.customer_name = name;
        this.mobile = number;
    }
    
    public Customer(Customer other)
    {
        this.customer_name = other.getCustomerName();
        this.mobile = other.getMobile();
    }
    
    public String getCustomerName()
    {
        return this.customer_name;
    }
    
    public String getMobile()
    {
        return this.mobile;
    }
    
    public String toString()
    {
        return "Customer name: " + this.customer_name + "\nMobile: " + this.mobile;
    }
}
