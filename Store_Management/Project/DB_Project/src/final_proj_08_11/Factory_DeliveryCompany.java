package final_proj_08_11;

public class Factory_DeliveryCompany {
	public static DeliveryCompany initCompany(String contact, String whatsapp, DeliveryCompany.companyType _type) {
		switch(_type) {
		case eDHL:
			return new DHL(contact, whatsapp);
		case eFedEx:
			return new FedEx(contact, whatsapp);
		}
		throw new IllegalArgumentException();
	}
}
