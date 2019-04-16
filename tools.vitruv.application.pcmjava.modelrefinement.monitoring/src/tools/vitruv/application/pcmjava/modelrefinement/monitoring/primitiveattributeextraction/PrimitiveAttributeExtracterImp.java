package tools.vitruv.application.pcmjava.modelrefinement.monitoring.primitiveattributeextraction;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveAttributeExtracterImp implements PrimitiveAttributeExtracter{

	/*
	 * Map between service parameter name and parameter value
	 */
	Map<String, Object> listPrimitiveAttributes;
	
	public PrimitiveAttributeExtracterImp() {
		listPrimitiveAttributes = new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> getObjectPrimitiveAttributeValues(Object[] listObjects, String[] listObjectsNames) {
		for(int i = 0; i < listObjects.length; i++) {
			if(this.isArray(listObjects[i])) {
				this.handleArrays(listObjects[i], listObjectsNames[i] + "." + ServiceParametersProperties.NUMBER_OF_ELEMENTS);
			}
			else if(this.isCollection(listObjects[i])) {
				this.handleCollections(listObjects[i], listObjectsNames[i] + "." + ServiceParametersProperties.NUMBER_OF_ELEMENTS);
			}
			else if(this.isMap(listObjects[i])) {
				this.handleMap(listObjects[i], listObjectsNames[i] + "." + ServiceParametersProperties.NUMBER_OF_ELEMENTS);
			}
			else if(this.isPrimitive(listObjects[i])) {
				this.handlePrimitives(listObjects[i], listObjectsNames[i] + "." + ServiceParametersProperties.VALUE);
			}
			else if(this.isDataType(listObjects[i])) {
				this.handleDataType(listObjects[i]);
			}
		}
		return this.listPrimitiveAttributes;
	}
	
	private void handlePrimitives(Object object, String objectName) {
		this.listPrimitiveAttributes.put(objectName, object);
	}
	
	private void handleArrays(Object object, String objectName) {
		this.listPrimitiveAttributes.put(objectName, Array.getLength(object));
	}
	
	private void handleCollections(Object object, String objectName) {
		Collection<?> collection = (Collection<?>) object;
		this.listPrimitiveAttributes.put(objectName, collection.size());
	}
	
	private void handleMap(Object object, String objectName) {
		Map<?, ?> map = (Map<?, ?>) object;
		this.listPrimitiveAttributes.put(objectName, map.size());
	}
	
	private void handleDataType(Object object) {
		// use reflections
	}
	
	private boolean isPrimitive(Object object) {
		if(object instanceof Integer || object instanceof Double || 
				object instanceof Long || object instanceof Float) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean isArray(Object object) {
		if(object != null) {
			if(object.getClass().isArray()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isCollection(Object object) {
		if(object instanceof Collection<?>) {
			return true;
		}
		return false;
	}
	
	private boolean isMap(Object object) {
		if(object instanceof Map<?, ?>) {
			return true;
		}
		return false;
	}
	
	private boolean isDataType(Object object) {
		return false;
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
