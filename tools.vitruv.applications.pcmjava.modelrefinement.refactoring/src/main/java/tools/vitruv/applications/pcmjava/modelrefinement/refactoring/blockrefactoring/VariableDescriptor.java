package tools.vitruv.applications.pcmjava.modelrefinement.refactoring.blockrefactoring;

public class VariableDescriptor {

	private String typeName;
	private String name;
	private int id;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public VariableDescriptor(String typeName, String name, int id) {
		super();
		this.typeName = typeName;
		this.name = name;
		this.id = id;
	}
	
	
	
	
	
}
