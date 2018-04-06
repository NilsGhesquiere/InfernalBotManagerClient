package net.nilsghesquiere.services;

import java.util.Map.Entry;

import net.nilsghesquiere.entities.ClientSettings;
import net.nilsghesquiere.entities.InfernalSettings;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalClient;
import net.nilsghesquiere.infernalclients.InfernalSettingsInfernalJDBCClient;
import net.nilsghesquiere.managerclients.InfernalSettingsManagerClient;
import net.nilsghesquiere.managerclients.InfernalSettingsManagerRESTClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfernalSettingsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfernalSettingsService.class);
	private final InfernalSettingsInfernalClient infernalClient;
	private final InfernalSettingsManagerClient managerClient;
	private final ClientSettings clientSettings;
	
	public InfernalSettingsService(ClientSettings clientSettings){
		this.infernalClient =  new InfernalSettingsInfernalJDBCClient(clientSettings.getInfernalMap());
		if(clientSettings.getPort().equals("")){
			this.managerClient = new InfernalSettingsManagerRESTClient(clientSettings.getWebServer(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		} else {
			this.managerClient = new InfernalSettingsManagerRESTClient(clientSettings.getWebServer() + ":" + clientSettings.getPort(), clientSettings.getUsername(), clientSettings.getPassword(), clientSettings.getDebugHTTP());
		}
		this.clientSettings = clientSettings;
	}
	
	public boolean checkPragmas(){
		return infernalClient.checkPragmas();
	}
	
	public boolean updateInfernalSettings(Long userid){
		InfernalSettings infernalSettingsFromREST = managerClient.getUserInfernalSettings(userid);
		//Disabled this for now: It is always the default set atm
		//InfernalSettings infernalSettingsFromJDBC = jdbcClient.getInfernalBotManagerInfernalSettings();
		InfernalSettings infernalSettingsFromJDBC = infernalClient.getDefaultInfernalSettings();
		if (infernalSettingsFromREST != null){
			if (infernalSettingsFromJDBC != null){
				//TODO update every field from the jdbc setting with the settings from rest and update
				InfernalSettings infernalSettingsForUpdate = prepareInfernalSettingsForJDBC(infernalSettingsFromJDBC, infernalSettingsFromREST);
				Long newId = infernalClient.updateInfernalSettings(infernalSettingsForUpdate);
				if (newId == infernalSettingsForUpdate.getId()){
					return true;
				} else{
					LOGGER.debug("ID of updated record not the same as ID of record to update");
					return false;
				}
			} else {
				InfernalSettings infernalSettingsDefaultFromJDBC = infernalClient.getDefaultInfernalSettings();
				if (infernalSettingsDefaultFromJDBC != null){
					//TODO update every field from the jdbc setting with the settings from rest and update, set ID to -1
					infernalSettingsDefaultFromJDBC.setId(-1L);
					InfernalSettings infernalSettingsForInsert = prepareInfernalSettingsForJDBC(infernalSettingsDefaultFromJDBC, infernalSettingsFromREST);
					Long newId = infernalClient.insertInfernalSettings(infernalSettingsForInsert);
					if(newId != -1L){
						//LOGGER.info("Successfully created InfernalBotManager setting in the InfernalBot database");
					} else {
						//LOGGER.info("Error: Failed to create InfernalBotManager settings in the InfernalBot database");
						return false;
					}
				} else {
					return false;
				}
			}
		} else {
			LOGGER.error("Failure retrieving Infernal settings from the InfernalBotManager server");
			return false;
		}
		return true;
	}

	//todo welke velden de API velden beheren en toevoegen
	private InfernalSettings prepareInfernalSettingsForJDBC(InfernalSettings infernalSettingsFromJDBC, InfernalSettings infernalSettingsFromREST) {
		//update all settings from JDBC with settings from REST
	//	infernalSettingsFromJDBC.setSets(infernalSettingsFromREST.getSets());
	//	Not able to select sets right now so just leave it at default
	//	infernalSettingsFromJDBC.setSets("InfernalBotManager");
		infernalSettingsFromJDBC.setSets("Default");
	//	infernalSettingsFromJDBC.setUsername(infernalSettingsFromREST.getUsername());
	//	infernalSettingsFromJDBC.setPassword(infernalSettingsFromREST.getPassword());
		infernalSettingsFromJDBC.setGroups(infernalSettingsFromREST.getGroups());
	//	infernalSettingsFromJDBC.setLevel(infernalSettingsFromREST.getLevel());
		infernalSettingsFromJDBC.setClientPath(infernalSettingsFromREST.getClientPath());
	//TODO: Check if bot checks the currentversion automatically at startup, if so we don't need this
	//	infernalSettingsFromJDBC.setCurrentVersion(infernalSettingsFromREST.getCurrentVersion());
		infernalSettingsFromJDBC.setWildcard(infernalSettingsFromREST.getWildcard());
		infernalSettingsFromJDBC.setMaxLevel(infernalSettingsFromREST.getMaxLevel());
		infernalSettingsFromJDBC.setSleepTime(infernalSettingsFromREST.getSleepTime());
		infernalSettingsFromJDBC.setPlayTime(infernalSettingsFromREST.getPlayTime());
		infernalSettingsFromJDBC.setPrio(infernalSettingsFromREST.getPrio());
	//	infernalSettingsFromJDBC.setGrSize(infernalSettingsFromREST.getGrSize());
	//	infernalSettingsFromJDBC.setClientUpdateSel(infernalSettingsFromREST.getClientUpdateSel());
		//autostart after login, temporarely set this to true manually 
		infernalSettingsFromJDBC.setClientUpdateSel(true);
		infernalSettingsFromJDBC.setReplaceConfig(infernalSettingsFromREST.getReplaceConfig());
		infernalSettingsFromJDBC.setLolHeight(infernalSettingsFromREST.getLolHeight());
		infernalSettingsFromJDBC.setLolWidth(infernalSettingsFromREST.getLolWidth());
		infernalSettingsFromJDBC.setMaxBe(infernalSettingsFromREST.getMaxBe());
		infernalSettingsFromJDBC.setAktive(infernalSettingsFromREST.getAktive());
		infernalSettingsFromJDBC.setClientHide(infernalSettingsFromREST.getClientHide());
		infernalSettingsFromJDBC.setConsoleHide(infernalSettingsFromREST.getConsoleHide());
		infernalSettingsFromJDBC.setRamManager(infernalSettingsFromREST.getRamManager());
		infernalSettingsFromJDBC.setRamMin(infernalSettingsFromREST.getRamMin());
		infernalSettingsFromJDBC.setRamMax(infernalSettingsFromREST.getRamMax());
		infernalSettingsFromJDBC.setLeaderHide(infernalSettingsFromREST.getLeaderHide());
		infernalSettingsFromJDBC.setSurrender(infernalSettingsFromREST.getSurrender());
		infernalSettingsFromJDBC.setRenderDisable(infernalSettingsFromREST.getRenderDisable());
		infernalSettingsFromJDBC.setLeaderRenderDisable(infernalSettingsFromREST.getLeaderRenderDisable());
		infernalSettingsFromJDBC.setCpuBoost(infernalSettingsFromREST.getCpuBoost());
		infernalSettingsFromJDBC.setLeaderCpuBoost(infernalSettingsFromREST.getLeaderCpuBoost());
		infernalSettingsFromJDBC.setLevelToBeginnerBot(infernalSettingsFromREST.getLevelToBeginnerBot());
		infernalSettingsFromJDBC.setTimeSpan(infernalSettingsFromREST.getTimeSpan());
		infernalSettingsFromJDBC.setSoftEndDefault(infernalSettingsFromREST.getSoftEndDefault());
		infernalSettingsFromJDBC.setSoftEndValue(infernalSettingsFromREST.getSoftEndValue());
		infernalSettingsFromJDBC.setQueuerAutoClose(infernalSettingsFromREST.getQueuerAutoClose());
		infernalSettingsFromJDBC.setQueueCloseValue(infernalSettingsFromREST.getQueueCloseValue());
		infernalSettingsFromJDBC.setWinReboot(infernalSettingsFromREST.getWinReboot());
		infernalSettingsFromJDBC.setWinShutdown(infernalSettingsFromREST.getWinShutdown());
		infernalSettingsFromJDBC.setTimeoutLogin(infernalSettingsFromREST.getTimeoutLogin());
		infernalSettingsFromJDBC.setTimeoutLobby(infernalSettingsFromREST.getTimeoutLobby());
		infernalSettingsFromJDBC.setTimeoutChamp(infernalSettingsFromREST.getTimeoutChamp());
		infernalSettingsFromJDBC.setTimeoutMastery(infernalSettingsFromREST.getTimeoutMastery());
		infernalSettingsFromJDBC.setTimeoutLoadGame(infernalSettingsFromREST.getTimeoutLoadGame());
		infernalSettingsFromJDBC.setTimeoutInGame(infernalSettingsFromREST.getTimeoutInGame());
		infernalSettingsFromJDBC.setTimeoutInGameFF(infernalSettingsFromREST.getTimeoutInGameFF());
		infernalSettingsFromJDBC.setTimeoutEndOfGame(infernalSettingsFromREST.getTimeoutEndOfGame());
		infernalSettingsFromJDBC.setTimeUntilCheck(infernalSettingsFromREST.getTimeUntilCheck());
		infernalSettingsFromJDBC.setTimeUntilReboot(infernalSettingsFromREST.getTimeUntilReboot());
	//	infernalSettingsFromJDBC.setServerCon(infernalSettingsFromREST.getServerCon());
	//	infernalSettingsFromJDBC.setServerPort(infernalSettingsFromREST.getServerPort());
		infernalSettingsFromJDBC.setOpenChest(infernalSettingsFromREST.getOpenChest());
		infernalSettingsFromJDBC.setOpenHexTech(infernalSettingsFromREST.getOpenHexTech());
		infernalSettingsFromJDBC.setDisChest(infernalSettingsFromREST.getDisChest());
	//	infernalSettingsFromJDBC.setApiClient(infernalSettingsFromREST.getApiClient());
	//	infernalSettingsFromJDBC.setMySQLServer(infernalSettingsFromREST.getMySQLServer());
	//	infernalSettingsFromJDBC.setMySQLDatabase(infernalSettingsFromREST.getMySQLDatabase());
	//	infernalSettingsFromJDBC.setMySQLUser(infernalSettingsFromREST.getMySQLUser());
	//	infernalSettingsFromJDBC.setMySQLPassword(infernalSettingsFromREST.getMySQLPassword());
	//	infernalSettingsFromJDBC.setMySQLQueueTable(infernalSettingsFromREST.getMySQLQueueTable());
	//	infernalSettingsFromJDBC.setMySQLAktivTable(infernalSettingsFromREST.getMySQLAktivTable());
		//new 29/03/2018
		infernalSettingsFromJDBC.setEnableAutoExport(infernalSettingsFromREST.getEnableAutoExport());
		infernalSettingsFromJDBC.setExportPath(infernalSettingsFromREST.getExportPath());
		infernalSettingsFromJDBC.setExportWildCard(infernalSettingsFromREST.getExportWildCard());
		infernalSettingsFromJDBC.setExportRegion(infernalSettingsFromREST.getExportRegion());
		infernalSettingsFromJDBC.setExportLevel(infernalSettingsFromREST.getExportLevel());
		infernalSettingsFromJDBC.setExportBE(infernalSettingsFromREST.getExportBE());
	//	Set Region from the settings in the ini
		infernalSettingsFromJDBC.setRegion(clientSettings.getClientRegion());
		if (clientSettings.getOverwriteSettings()){
			//	Overwrite settings with the settings from the ini file
			for (Entry <String,String >entry : clientSettings.getSettingsOverwriteMap().entrySet()){
				if(entry.getKey().equals("groups")){
					Integer groups = Integer.parseInt(entry.getValue());
					infernalSettingsFromJDBC.setGroups(groups);
				}
				if(entry.getKey().equals("clientpath")){
					String clientpath = entry.getValue();
					infernalSettingsFromJDBC.setClientPath(clientpath);
				}
			}
		}
		return infernalSettingsFromJDBC;
	}
	
	//TEST METHODS
	public void testPragmas(){
		String oldPragmas = infernalClient.getOldPragmas();
		String newPragmas = infernalClient.getNewPragmas();
		if (newPragmas.equals(oldPragmas)){
			LOGGER.info("Settings pragmas identitical");
			LOGGER.debug("Old settings pragmas: " + oldPragmas);
			LOGGER.debug("New settings pragmas: " + newPragmas);
		} else {
			LOGGER.info("Settings pragmas changed");
			LOGGER.info("Old settings pragmas: " + oldPragmas);
			LOGGER.info("New settings pragmas: " + newPragmas);
		}
	}
}