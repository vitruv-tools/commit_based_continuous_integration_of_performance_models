package test;

import java.util.concurrent.TimeUnit;

import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.sampler.sigar.ISigarSamplerFactory;
import kieker.monitoring.sampler.sigar.SigarSamplerFactory;
import kieker.monitoring.sampler.sigar.samplers.CPUsDetailedPercSampler;
import testRepository.WebGUI.WebGUIImpl;
import testRepository.contracts.IWebGUI;
import tools.vitruv.applications.pcmjava.modelrefinement.parameters.monitoring.ThreadMonitoringController;

public class Test {

	final static ISigarSamplerFactory sigarFactory = SigarSamplerFactory.INSTANCE;
	final static CPUsDetailedPercSampler cpuSampler = sigarFactory.createSensorCPUsDetailedPerc();
	    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MonitoringController.getInstance().schedulePeriodicSampler(
                cpuSampler, 0, 1, TimeUnit.SECONDS);
		
		ThreadMonitoringController.setSessionId("session-1");
		
		IWebGUI test =  new WebGUIImpl();
		for(int i=0;i<10;i++) {
			test.httpDownload();
		}
	}

}
