package net.nilsghesquiere;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.Data;
import net.nilsghesquiere.entities.InfernalBotManagerClientSettings;
import net.nilsghesquiere.services.InfernalSettingsService;
import net.nilsghesquiere.services.LolAccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

@Data
public class InfernalBotManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalBotManagerClient.class);
	private InfernalBotManagerClientSettings clientSettings;
	
	public InfernalBotManagerClient(InfernalBotManagerClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}
	
	//Schedule Reboot
	public void scheduleReboot(){
		if (clientSettings.getReboot()){
			try {
				Process p = Runtime.getRuntime().exec("shutdown -r -t " + clientSettings.getRebootTime());
			} catch (IOException e) {
				LOGGER.error("Error scheduling reboot");
				LOGGER.debug(e.getMessage().toString());
			}
			LOGGER.info("Shutdown scheduled in " + clientSettings.getRebootTime() + " seconds");
		}
	}

	//Connection Check
	public boolean checkConnection(){
		//TODO try /catch
		//Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 8.8.8.8");
		if (clientSettings.getBypassDevChecks() == false){
			try{
				Process p1 = java.lang.Runtime.getRuntime().exec("ping -n 1 8.8.8.8");
				int returnVal = p1.waitFor();
				boolean reachable = (returnVal==0);
				if (reachable){
					LOGGER.info("Connected to network");
					//this way doesn't work, do it with REST
					Process p2 = java.lang.Runtime.getRuntime().exec("ping -n 1 " + clientSettings.getWebServer());
					int returnVal2 = p2.waitFor();
					boolean reachable2 = (returnVal2==0);
					if (reachable2){
						LOGGER.info("Connected to the InfernalBotManager server");
					} else {
						LOGGER.error("Failure connecting to the InfernalBotManager server");
						return false;
					}
				} else {
					LOGGER.error("Error connecting to network");
					return false;
				}
			}catch (IOException | InterruptedException e ){
				LOGGER.error("Failure establishing connection");
				LOGGER.debug(e.getMessage());
				return false;
			}
		}
		return true;
	}
	
	//LolAccount methods
	public boolean accountExchange(){
		try{
			LolAccountService lolAccountService = new LolAccountService(clientSettings);
			return lolAccountService.exchangeAccounts(clientSettings.getUserId(), clientSettings.getClientRegion(), clientSettings.getClientTag(), clientSettings.getAccountAmount());
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
				return false;
		} 
	}
	
	public boolean setAccountsAsReadyForUse(){
		try{
			LolAccountService lolAccountService = new LolAccountService(clientSettings);
			lolAccountService.setAccountsAsReadyForUse(clientSettings.getUserId());
			return true;
		} catch (ResourceAccessException e) {
			LOGGER.error("Failure retrieving the requested resource");
			LOGGER.debug(e.getMessage());
			return false;
		} 
	}

	//InfernalSettings methods
	public boolean setInfernalSettings(){
		if (clientSettings.getFetchSettings()){
			try{
				InfernalSettingsService infernalSettingsService = new InfernalSettingsService(clientSettings);
				infernalSettingsService.updateInfernalSettings(clientSettings.getUserId());
				//get the settings here and overwrite them if in ini has the values, this should probably be done in the service
				//--> boolean for overwrite and values in a map --> pass to the method
				return true;
			} catch (ResourceAccessException e) {
				LOGGER.error("Failure retrieving the requested resource");
				LOGGER.debug(e.getMessage());
				return false;
			}
		} else {
			LOGGER.info("Not requesting settings from the InfernalBotManager Server, using InfernalBots own settings.");
			return true;
		}
	}
	
	public boolean backUpInfernalDatabase(){
		if(checkDir()){
			LOGGER.info("Located Infernalbot");
			Path backupDir = Paths.get(clientSettings.getInfernalMap() + "InfernalManager") ;
			Path file = Paths.get(clientSettings.getInfernalMap() + "InfernalDatabase.sqlite") ;
			Path backupFile = Paths.get(clientSettings.getInfernalMap() + "InfernalManager/InfernalDatabase.bak") ;
			if(!Files.exists(backupDir)){
				try {
					Files.createDirectories(backupDir);
				} catch (IOException e1) {
					//Path exists, do nothing
				}
			}
			if (Files.exists(file)){
				try {
					Files.copy(file,backupFile, StandardCopyOption.REPLACE_EXISTING);
					LOGGER.info("Backed up Infernalbot database" );
				} catch (IOException e) {
					LOGGER.error("Failure backing up Infernal Database: " + e.getMessage());
					LOGGER.debug(e.getMessage());
					return false;
				}
			} else {
				LOGGER.error("Infernalbot database not found, check your path.");
				return false;
			}
		} else {
			LOGGER.error("Failure locating Infernalbot");
			return false;
		}
		return true;
	}
	
	private boolean checkDir(){
		Path infernalPath = Paths.get(clientSettings.getInfernalMap() + clientSettings.getInfernalProg());
		return Files.exists(infernalPath);
	}
	
}
