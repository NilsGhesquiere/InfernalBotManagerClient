package net.nilsghesquiere.hooks;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.nilsghesquiere.Main;
import net.nilsghesquiere.runnables.AccountlistUpdaterRunnable;
import net.nilsghesquiere.runnables.ClientActionCheckerRunnable;
import net.nilsghesquiere.runnables.ClientDataRunnable;
import net.nilsghesquiere.runnables.InfernalBotCheckerRunnable;
import net.nilsghesquiere.runnables.ThreadCheckerRunnable;
import net.nilsghesquiere.runnables.UpdateCheckerRunnable;
import net.nilsghesquiere.util.ProgramUtil;
import net.nilsghesquiere.util.enums.ClientDataStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GracefulExitHook extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(GracefulExitHook.class);
	public boolean rebootWindows = false;
	
	@Override
	public void run(){
		LOGGER.info("Shutting down all threads");
		boolean fail = false;
		for(Entry<Thread,Runnable> entry: Main.threadMap.entrySet()){
			
			if (entry.getValue() instanceof AccountlistUpdaterRunnable){
				AccountlistUpdaterRunnable accountlistUpdaterRunnable =(AccountlistUpdaterRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Accountlist Updater thread");
				accountlistUpdaterRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing Accountlist Updater thread",e);
				}
			}
			
			if (entry.getValue() instanceof UpdateCheckerRunnable){
				UpdateCheckerRunnable updateCheckerRunnable =(UpdateCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Update Checker thread");
				updateCheckerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing Update Checker thread",e);
				}
			}
			
			if (entry.getValue() instanceof ClientActionCheckerRunnable){
				ClientActionCheckerRunnable clientActionCheckerRunnable =(ClientActionCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Client Action Checker thread");
				clientActionCheckerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing Client Action Checker thread",e);
				}
			}
			
			if (entry.getValue() instanceof InfernalBotCheckerRunnable){
				InfernalBotCheckerRunnable infernalBotManagerRunnable =(InfernalBotCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down InfernalBotChecker thread");
				if(infernalBotManagerRunnable.isRebootFromManager()){
					this.rebootWindows = true;
					if(Main.managerMonitorRunnable.getClientDataStatus() != ClientDataStatus.UPDATE){
						Main.managerMonitorRunnable.setClientDataStatus(ClientDataStatus.CLOSE_REBOOT);
					}
				} else {
					if(Main.managerMonitorRunnable.getClientDataStatus() != ClientDataStatus.UPDATE){
						Main.managerMonitorRunnable.setClientDataStatus(ClientDataStatus.CLOSE);
					}
				}
				infernalBotManagerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing InfernalBotChecker thread",e);
					LOGGER.debug(e.getMessage());
				}
			}
			
			if (entry.getValue() instanceof ThreadCheckerRunnable){
				ThreadCheckerRunnable threadCheckerRunnable =(ThreadCheckerRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Thread Checker");
				threadCheckerRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing Thread Checker thread",e);
				}
			}

			if (entry.getValue() instanceof ClientDataRunnable){
				ClientDataRunnable managerMonitorRunnable =(ClientDataRunnable) entry.getValue();
				LOGGER.debug("Gracefully shutting down Client Data Monitor");
				managerMonitorRunnable.stop();
				entry.getKey().interrupt();
				try {
					entry.getKey().join();
				} catch (InterruptedException e) {
					fail = true;
					LOGGER.debug("Failure closing Client Data Monitor thread",e);
				}
			}
		}
		
		//check if we need to force a reboot
		if (Main.exitWaitRunnable.getForceReboot()){
			this.rebootWindows = true;
		}
		
		//Stop the runnable without launching hook
		if (Main.exitWaitThread.isAlive()){
			Main.exitWaitRunnable.dontLaunchHook();
			Main.exitWaitRunnable.exit();
			Main.exitWaitThread.interrupt();
			try {
				Main.exitWaitThread.join();
			} catch (InterruptedException e) {
				fail = true;
				LOGGER.debug("Failure closing Exit Wait thread",e);
			}
		}
		if(!fail){
			LOGGER.info("Closed all threads");
			if (this.rebootWindows){
				//Reboot windows
				LOGGER.info("Rebooting windows");
				ProgramUtil.unscheduleReboot();
				ProgramUtil.scheduleReboot(20);
			}
		}
		LOGGER.info("Closing InfernalBotManager Client");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage());
		}
		System.exit(0);
	}
}
