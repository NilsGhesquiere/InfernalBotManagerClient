package net.nilsghesquiere.runnables;

import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitWaitRunnable implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExitWaitRunnable.class);
	private volatile boolean exit = false;
	private volatile boolean launchHook = true;
	
	public ExitWaitRunnable() {
		super();
	}

	@Override
	public void run() {
		while(!exit){
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e2) {
				LOGGER.debug(e2.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		if (launchHook){
			Main.gracefullExitHook.start();
		}
	}

	public void exit(){
		exit = true;
	}

	public boolean getExit(){
		return exit;
	}
	
	public void dontLaunchHook(){
		launchHook = false;
	}

}
