package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

import java.util.Arrays;
import java.util.UUID;

public class BlockRefactoringUtil {
	
	/**
	 * @param object 
	 * @return
	 */
	public static boolean isPrimitiveType(Object object){
		if(object.getClass().equals(Float.class))
			return true;
		if(object.getClass().equals(Double.class))
			return true;
		if(object.getClass().equals(Integer.class))
			return true;
		if(object.getClass().equals(Long.class))
			return true;
		if(object.getClass().equals(Byte.class))
			return true;
		if(object.getClass().equals(Short.class))
			return true;
		if(object.getClass().equals(Character.class))
			return true;
		if(object.getClass().equals(Boolean.class))
			return true;
		else
			return false;
	}
	
	
	public static boolean isPrimitiveTypeFromString(String typeName){
		String[] javaPrimitiveTypes = {"boolean", "byte", "short", "int", "long", "char", "float","double"};
		if(Arrays.asList(javaPrimitiveTypes).contains(typeName)){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/**
	 * @return
	 */
	public static String getUUID(){
		 UUID uuid = UUID.randomUUID();
		 return uuid.toString();
	}
	
}
