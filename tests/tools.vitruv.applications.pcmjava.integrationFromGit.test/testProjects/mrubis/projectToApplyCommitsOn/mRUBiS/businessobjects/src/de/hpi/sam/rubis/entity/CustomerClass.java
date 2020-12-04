package de.hpi.sam.rubis.entity;

public enum CustomerClass {

	GOLD("GOLD"), SILVER("SILVER");

	private final String customerClass;

	private CustomerClass(final String customerClass) {
		this.customerClass = customerClass;
	}

	public static CustomerClass getCustomerClassByString(String customerClass) {
		if (customerClass == null) {
			return SILVER;
		} else {
			if (customerClass.equals(GOLD.toString())) {
				return GOLD;
			} else if (customerClass.equals(SILVER.toString())) {
				return SILVER;
			}
		}
		// default
		return SILVER;
	}

	@Override
	public String toString() {
		return this.customerClass;
	}

}
