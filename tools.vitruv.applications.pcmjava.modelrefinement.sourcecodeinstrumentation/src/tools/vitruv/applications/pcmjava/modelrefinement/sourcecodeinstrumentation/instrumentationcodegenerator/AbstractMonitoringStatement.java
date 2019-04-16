package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator;

public abstract class AbstractMonitoringStatement {

	private String beforeExecution;
	private String afterExecution;
	
	public String getAfterExecution() {
		return afterExecution;
	}
	public void setAfterExecution(String afterExecution) {
		this.afterExecution = afterExecution;
	}
	public String getBeforeExecution() {
		return beforeExecution;
	}
	public void setBeforeExecution(String beforeExecution) {
		this.beforeExecution = beforeExecution;
	}
	public AbstractMonitoringStatement() {
		super();
		// TODO Auto-generated constructor stub
	}
		
	
}
