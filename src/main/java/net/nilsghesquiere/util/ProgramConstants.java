package net.nilsghesquiere.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProgramConstants {
	public static final String CLIENT_VERSION = "0.9.2.1";
	public static final String SERVER_VERSION = "0.9.2";
	public static final String INI_NAME = "settings.ini";
	public static final String INFERNAL_PROG_NAME = "Infernal-Start.exe" ;
	public static final String LEGACY_LAUNCHER_NAME = "Infernal Launcher.exe";
	public static final String WEBSERVER = "https://infernalbotmanager.com" ;
	public static final String PORT = "";
	public static final Boolean useSwingGUI = true;
	public static final String UPDATER_NAME = "InfernalBotManagerUpdater.exe";
	public static final Boolean enableOshiCPUCheck = true;
	public static final List<String> infernalProcList = 
		    Collections.unmodifiableList(Arrays.asList("notepad.exe", "calc.exe"));
	//TODO: add League of legends exe, infernal launcher and infernal queuer here.

}
