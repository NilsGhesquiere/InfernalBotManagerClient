package net.nilsghesquiere.util.enums;

//TODO clean this
//Split in one for the queuers page -> specific(infernal startup etc)
//And one for the clients page -> UNASSIGNED,CONNECTED,DISCONNECTED,?REBOOTING?,OFFLINE
public enum ClientDataStatus {
	INIT, // Monitor created, no client
	UPDATE, //Client is performing an update
	INFERNAL_STARTUP, //Infernalbot is starting up
	INFERNAL_RUNNING,  //Infernalbot is running
	CLOSE, // Closing client
	CLOSE_REBOOT, // Closing client with reboot
	ERROR, //Error
}
