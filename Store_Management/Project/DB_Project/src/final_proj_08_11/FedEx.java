
package final_proj_08_11;

public class FedEx extends DeliveryCompany implements expressShipping, standardShipping {
    
    private final double BASE_EXPRESS_PAYMENT = 50.0;
    private final double BASE_STANDARD_PAYMENT = 10.0;
    
    public FedEx(String contact, String number)
    {
        super(contact, number);
    }

    @Override
    public double fee_ExpressShipping(WebsiteProduct web_p) {
        
        double pay_level = web_p.getProductWeight() / 10;
        
        if(web_p.getProductWeight() <= 10)
            return BASE_EXPRESS_PAYMENT + web_p.getDestImportTax();  
        
        else if(pay_level % 10 == 0)
            return (int)(pay_level) * BASE_EXPRESS_PAYMENT + web_p.getDestImportTax();
        
        return (int)(pay_level + 1) * BASE_EXPRESS_PAYMENT + web_p.getDestImportTax();
        
    }

    @Override
    public double fee_StandardShipping(WebsiteProduct web_p) {
        double pay_level = web_p.getProductWeight() / 10;
        
        if(web_p.getProductWeight() <= 10)
            return BASE_STANDARD_PAYMENT;  
        
        else if(pay_level % 10 == 0)
            return (int)(pay_level) * BASE_STANDARD_PAYMENT;
        
        return (int)(pay_level + 1) * BASE_STANDARD_PAYMENT;
        
    }
    
      public companyType getCompanyType(){
        return companyType.eFedEx;
    }
}
