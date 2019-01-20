package de.ninju.main;

import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;

import de.ninju.events.ChannelCreateListener;
import de.ninju.events.JoinListener;
import de.ninju.events.LeaveListener;
import de.ninju.events.MoveListener;
import de.ninju.events.TextMessageListener;

public class ServerBotMain {

	
	//TODO TextEvent CoolDown
	//TODO Supporter Group Remove
	//TODO Erstellen neuer Clientobjects bei Start
	//TODO Besseres Online Time logging
	//TODO Console Log
	//TODO ConsoleCommands
	
	
	private TS3Config config = new TS3Config();
	public TS3Api api;
	private TS3Query query;
	public Utils utils;
	public MySQL mySql;
	public int afkMinutes = 60;
	public HashMap<Integer, ClientObject> clients = new HashMap<>();

	public static void main(String[] args) {
		new ServerBotMain();
	}
	
	public ServerBotMain() {
		startQuery();
		//stopQuery();
	}
	
	private void loadEvents() {
		//Register eventtypes
		api.registerEvent(TS3EventType.TEXT_PRIVATE);
		api.registerEvent(TS3EventType.SERVER);
		api.registerEvent(TS3EventType.CHANNEL);
		//Register events
		new ChannelCreateListener(this);
		new JoinListener(this);
		new LeaveListener(this);
		new MoveListener(this);
		new TextMessageListener(this);
	}
	
	private void startQuery() { //Starts the Bot
		mySql = new MySQL(this); //Init MySQL
		utils = new Utils(this); //Init Utils
		new Schedulers(this); //Starts Schedulers
		config.setHost("52.57.169.64");
		config.setFloodRate(FloodRate.UNLIMITED);
		//config.setDebugLevel(java.util.logging.Level.ALL);
		query = new TS3Query(config);
		query.connect();
		api = query.getApi();
		api.login("query", "");
		api.selectVirtualServerByPort(9987);
		api.setNickname("ServerBot");
		utils.registerClients(); //Loads Own ClientObjects
		loadEvents(); //Loads Events
		afkMinutes = mySql.getAfkMinutes(); //Gets the max afk time
		System.out.println(utils.getTimeStamp() + "Bot ist gestartet!");
	}
	
	private void stopQuery() { //Stops the bot
		query.exit();
		System.out.println(utils.getTimeStamp() + "Bot ist gestoppt");
	}

	

}
