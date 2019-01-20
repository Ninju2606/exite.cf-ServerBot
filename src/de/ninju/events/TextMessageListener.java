package de.ninju.events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.ninju.main.ClientObject;
import de.ninju.main.ServerBotMain;

public class TextMessageListener extends TS3EventAdapter {
	
	ServerBotMain main;
	public TextMessageListener(ServerBotMain main) {
		this.main = main;
		main.api.addTS3Listeners(this);
	}
	
	@Override
	public void onTextMessage(TextMessageEvent e) {
		Client c = main.api.getClientInfo(e.getInvokerId());
		ClientObject co = main.clients.get(c.getId());
		if(!c.isServerQueryClient()) {
			String msg = e.getMessage();
			String[] args = msg.split(" ");
			if(args[0].equalsIgnoreCase("!help")) {
				co.sendMessage("Diese Commands kannst du nutzen: \n!link <game> <ingame-name> | Verknüpft dich mit deinem Account \n!unlink <game> | Hebt eine Verknüpfung auf \n!"
						+"info <client> | Zeigt dir die Informationen zu einer Person an\n !togglebot | Ändert, ob du eine Join-Nachricht erhältst\n!nopoke / !nomsg | Ändert die entsprechenden Ruhegruppen"
						+"\nWenn du eine Beschwerde einreichen möchtest (weil jemand z.B. einen Stimmverzerrer benutzt) dann mach'"
						+" [B]Rechtsklick[/B] auf ihn und [B]Beschwerde einreichen[/B]");
			}
			else if(args[0].equalsIgnoreCase("!yes")) {
				if(c.isInServerGroup(43)) {
					main.api.removeClientFromServerGroup(43, c.getDatabaseId());
				}
			}
			else if(args[0].equalsIgnoreCase("!setnews") && c.isInServerGroup(35)) {
				if(args.length >= 2) {
					main.mySql.updateNews(msg.replace("!setnews ", ""));
				}else co.sendMessage("Bitte füge einen Text an.");
			}
			else if(args[0].equalsIgnoreCase("!link")) {
				if(args.length == 1) {
					co.sendMessage("Diese Spiele kannst du angeben: Fortnite, Overwatch, CounterStrike, Minecraft, GTA.");
				}if(args.length == 2) {
					co.sendMessage("Bitte gib deinen Ingame-Namen an.");
				}if(args.length == 3) {
					List<String> games = Arrays.asList("fortnite", "overwatch", "counterstrike", "minecraft", "gta");
					if(games.contains(args[1].toLowerCase())) {
						main.mySql.addGamegroup(co.getUuid(), args[1].toLowerCase(), args[2]);
						if(!c.isInServerGroup(main.utils.getGroup(args[1].toLowerCase()))) {
							main.api.addClientToServerGroup(main.utils.getGroup(args[1]), c.getDatabaseId());
						}else {
							co.sendMessage("Dein Name im Spiel " + args[1] + " wurde aktualisiert. Du heiß nun: " + args[2]);
						}
					}else {
						co.sendMessage("Bitte gib eines der folgenden Spiele an: Fortnite, Overwatch, Counter-Strike, Minecraft, GTA."); 
					}
				}
			}else if(args[0].equalsIgnoreCase("!unlink")) {
				if(args.length == 2) {
					if(main.mySql.getGames(co.getUuid()).containsKey(args[1].toLowerCase())) {
						main.mySql.removeGame(co.getUuid(), args[1].toLowerCase());
						if(c.isInServerGroup(main.utils.getGroup(args[1].toLowerCase()))) {
							main.api.removeClientFromServerGroup(main.utils.getGroup(args[1].toLowerCase()), c.getDatabaseId());
						}
					}else {
						co.sendMessage("Du bist nicht mit diesem Spiel verknüpft.");
					}
				}else {
					co.sendMessage("Nutze [B]!unlink <game>[/B].");
				}
			}
			else if(args[0].equalsIgnoreCase("!info")) {
				if(args.length >= 2) {
					String name = msg.replace("!info ", "");
					Client cl = null;
					for(Client client : main.api.getClients()) {
						if(client.getNickname().equalsIgnoreCase(name)) {
							cl = client;
						}
					}
					if(cl!=null) {
						main.mySql.sendInfo(co, cl.getUniqueIdentifier(), cl.getNickname());
					}else {
						co.sendMessage("Der User [B]" +name + "[/B] ist nicht online. Überprüfe, ob du dich nicht vertippt hast.");
					}
				}else {
					co.sendMessage("Nutze [B]!info <name>[/B].");
				}
			}
			else if(args[0].equalsIgnoreCase("!togglebot")) {
				if(c.isInServerGroup(86)) {
					main.api.removeClientFromServerGroup(86, c.getDatabaseId());
				}else {
					main.api.addClientToServerGroup(86, c.getDatabaseId());
					co.sendMessage("Melde dich im Support, wenn du wieder Nachrichten vom Bot erhalten möchtest.");
				}
			}
			else if(args[0].equalsIgnoreCase("!nopoke")) {
				if(c.isInServerGroup(46)) {
					main.api.removeClientFromServerGroup(46, c.getDatabaseId());
				}else {
					main.api.addClientToServerGroup(46, c.getDatabaseId());
				}
			}
			else if(args[0].equalsIgnoreCase("!nomsg")) {
				if(c.isInServerGroup(45)) {
					main.api.removeClientFromServerGroup(45, c.getDatabaseId());
				}else {
					main.api.addClientToServerGroup(45, c.getDatabaseId());
				}
			}
			else if(args[0].equalsIgnoreCase("!channelhistory") && (c.isInServerGroup(35) || c.isInServerGroup(40) || c.isInServerGroup(89))) {
				if(args.length >= 2) {
					Client cl = null;
					for(Client client : main.api.getClients()) {
						if(client.getNickname().equalsIgnoreCase(msg.toLowerCase().replace("!channelhistory ", ""))) {
							cl = client;
						}
					}
					if(cl != null) {
						if(!cl.isServerQueryClient()) {
							String s = "Der User "+cl.getNickname()+" war zuletzt in folgenden Channeln:";
							ArrayList<String> channels = co.getChannels();
							ArrayList<Long> times = co.getTimes();
							if(channels.size() >= 10) {
								for(int i = channels.size()-10; i < channels.size(); i++) {
									DateFormat df = new SimpleDateFormat("HH-mm-ss");
									s += "\n"+channels.get(i)
									+ " | "+ df.format(times.get(i));
								}
							}else {
								for(int i = 0; i < channels.size(); i++) {
									DateFormat df = new SimpleDateFormat("HH-mm-ss");
									s += "\n"+channels.get(i)
									+ " | "+ df.format(times.get(i));
								}
							}
							co.sendMessage(s);
						}
					}else {
						co.sendMessage("Dieser Client wurde nicht gefunden");
					}
				}else {
					co.sendMessage("Bitte gib einen Namen an");
				}
			}
			else if(args[0].equalsIgnoreCase("!setafkminutes") && (c.isInServerGroup(35))) {
				if(args.length == 2) {
					if(main.utils.isNumeric(args[1])) {
						int time = Integer.valueOf(args[1]);
						if(time > 0) {
							main.afkMinutes = time;
							main.mySql.setAfkMinutes(time);
						}
					}
				}
			}
			else if(args[0].equalsIgnoreCase("!chat") && (c.isInServerGroup(35) || c.isInServerGroup(89))) {
				if(co.getChat() != null) {
					co.getChat().setChat(null);;
					co.setChat(null);
					co.sendMessage("Der Chat wurde beendet.");
				}
				else if(args.length >= 2) {
					String name = msg.replace("!chat ", "");
					int clientId = 0;
					for(Client cl : main.api.getClients()) {
						if(cl.getNickname().equalsIgnoreCase(name)){
							clientId = cl.getId();
						}
					}
					if(clientId!=0) {
						if(co.getChat() == null) {
							ClientObject co2 = main.clients.get(clientId);
							if(co2.getChat() == null) {
								co2.setChat(co);
								co.setChat(co2);
								co.sendMessage("Der Chat hat begonnen.");
							}else {
								co.sendMessage("Dieser Client ist bereits in einem Chat.");
							}
						}
					}else {
						co.sendMessage("Es wurde kein Client gefunden.");
					}
				}else {
					co.sendMessage("Bitte gib einen Namen an.");
				}
			}
			
			
			
			else if(co.getChat() != null) {
				co.getChat().sendMessage(msg);
			}
		}
	}

}
