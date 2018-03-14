package net.nilsghesquiere.runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.InfernalBotManagerClient;
import net.nilsghesquiere.entities.ClientSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDataManagerRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataManagerRunnable.class);
	private final InfernalBotManagerClient client;
	private volatile boolean stop = false;
	private volatile boolean rebootFromClientDataManagerClient = false;
	
	public ClientDataManagerRunnable(InfernalBotManagerClient client) {
		super();
		this.client = client;
	}

	@Override
	public void run() {
		LOGGER.info("Starting ClientData Updater in 5 minutes");
		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (InterruptedException e2) {
			LOGGER.error("Failure during sleep");
			LOGGER.debug(e2.getMessage());
		}
		LOGGER.info("Starting InfernalBot ClientData Updater");
		while (!stop){
			client.getClientDataService().sendData();
			if(!client.getClientDataService().hasActiveQueuer()){
				if(client.getClientSettings().getRebootFromManager()){
					LOGGER.info("No active queuers found");
					rebootFromClientDataManagerClient = true;
					System.exit(0);
				} else {
					LOGGER.info("No active queuers found, closing InfernalBot process");
					try {
						Runtime.getRuntime().exec("taskkill /F /IM " + client.getClientSettings().getInfernalProgramName());
					} catch (IOException e){
						LOGGER.error("Failure trying to kill InfernalBot Process");
						LOGGER.debug(e.getMessage());
					}
				}
			}
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e1) {
				LOGGER.error("Failure during sleep");
				LOGGER.debug(e1.getMessage());
			}
		}
		LOGGER.info("Successfully closed ClientData Updater thread");
	}

	public void stop(){
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}
	
	public boolean isRebootFromManager() {
		return rebootFromClientDataManagerClient;
	}
}
