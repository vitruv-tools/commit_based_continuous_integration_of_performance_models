package cipm.consistency.cpr.javapcm.additional.validation;

/**
 * Represents a pair of an external call and its target.
 * 
 * @author Martin Armbruster
 */
public class ExternalCallCallTargetPair {
	private String componentName;
	private String seffName;
	private String externalCallEncoding;
	private String interfaceName;
	private String serviceName;
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String getSeffName() {
		return seffName;
	}
	
	public void setSeffName(String seffName) {
		this.seffName = seffName;
	}
	
	public String getExternalCallEncoding() {
		return externalCallEncoding;
	}
	
	public void setExternalCallEncoding(String externalCallEncoding) {
		this.externalCallEncoding = externalCallEncoding;
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
