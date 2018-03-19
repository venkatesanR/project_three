package com.addval.udfutils;

import java.util.Map;

public interface UdfSource {

	/* Get and Set EntityDefn */
	public EntityDefn getEntityDefn();

	public void setEntityDefn(EntityDefn defn);


	/* Get and Set UdfValues */
	public Map<String, AttributeValue> getUdfValues();

	public void setUdfValues(Map<String, AttributeValue> udfValues);
}
