package de.hpi.sam.rubis.client.main;

public class LoadGenerator {

	public LoadGenerator() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int numberOfClients = 20;
		ClientRunnable[] clients = new ClientRunnable[numberOfClients];
		Thread[] threads = new Thread[numberOfClients];

		for (int i = 0; i < clients.length; i++) {
			clients[i] = new ClientRunnable(i);
			threads[i] = new Thread(clients[i]);
		}
		
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
