package tools.vitruv.application.pcmjava.modelrefinement.monitoring.primitiveattributeextraction;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveAttributeExtracterImp implements PrimitiveAttributeExtracter{

	Map<String, Object> listPrimitiveAttributes;
	
	
	public PrimitiveAttributeExtracterImp() {
		listPrimitiveAttributes = new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> getObjectPrimitiveAttributeValues(Object[] listObjects, int depth) {
		for(Object object: listObjects) {
			this.findPrimitiveAttributes(object);
		}
		return this.listPrimitiveAttributes;
	}
	
	
	private void findPrimitiveAttributes(Object object){
		 Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
       	 field.setAccessible(true);
       	 Object fieldValue = null;
       	 Class<?> classObject = field.getType();
       	 if(classObject.isArray() || classObject.isPrimitive()) {
       		 try {
	        		 if(classObject.isArray()) {
	        			 fieldValue = Array.getLength(field.get(object));
	        		 }
	        		 else if(classObject.isPrimitive()) {
	        			 fieldValue = field.get(object); 
	        		 }
	        		 
	        		 this.listPrimitiveAttributes.put(field.getName(), fieldValue);
       		 }catch (IllegalArgumentException | IllegalAccessException e) {

					}
       	 }
       	 else{
       		 // call recursively
       		 try {
					this.findPrimitiveAttributes(field.get(object));
				} catch (IllegalArgumentException | IllegalAccessException e) {

				}
       	 }
            
        }
	}

}
