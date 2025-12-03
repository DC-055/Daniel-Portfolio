
package final_proj_08_11;

public class DHL extends DeliveryCompany implements expressShipping, standardShipping {
    
    private final double DHL_EXPRESS_BASE_PAYMENT = 100.0;
    private final double STANDARD_SHIP_PAYMENT = 10.0;
   
    public DHL(String contact, String number)
    {
        super(contact, number);
    }
    
      @Override
    public double fee_ExpressShipping(WebsiteProduct w_product) {
        return DHL_EXPRESS_BASE_PAYMENT + w_product.getDestImportTax();
    }

    @Override
    public double fee_StandardShipping(WebsiteProduct w_product) {
        if(w_product.getPriceInDollars() <= 100)
        {
            return w_product.getPriceInDollars() * 0.1;
        }
        
        return STANDARD_SHIP_PAYMENT;
    }
    
    @Override
    public String toString() {
    	return "Company name: " + this.getClass().getSimpleName() + "\n" + super.toString();
    }
    
    public companyType getCompanyType(){
        return companyType.eDHL;
    }
    
    
}
