
package final_proj_08_11;

public abstract class DeliveryCompany implements Observer {
    
	public static enum companyType {eDHL, eFedEx};
    protected String contact;
    protected String whatsapp;
    
    public DeliveryCompany(String contact, String number)
    {
        this.contact = contact;
        this.whatsapp = number;
    }
    
    public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}

	public String toString()
    {
    	return "Company's contact: " + this.contact + "\n" + "contact mobile number: " + this.whatsapp + "\n";
    }
        
        @Override
	public void update(MyButton b) {
		System.out.println("\nSENT MESSAGE TO: " + contact + " in company " + getCompanyType() + "\n" + b.getMessage() +"\n");		
	}
        
    abstract companyType getCompanyType();
    

}
