package de.ninju.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Events {

	/*public Events() {
		loadEvents();
	}

	static ArrayList<String> sChannels = new ArrayList<>();	//Support
	static HashMap<String, ArrayList<ChannelInfo>> channels = new HashMap<>();

	public static void loadEvents() {
		channels.put("fortnite", new ArrayList<ChannelInfo>());
		channels.put("minecraft", new ArrayList<ChannelInfo>());
		channels.put("counter strike", new ArrayList<ChannelInfo>());
		channels.put("overwatch", new ArrayList<ChannelInfo>());
		channels.put("gta", new ArrayList<ChannelInfo>());
		channels.put("user-talk", new ArrayList<ChannelInfo>());
		ServerBotMain.api.registerAllEvents();
		ServerBotMain.api.addTS3Listeners(new TS3Listener() {

			@Override
			public void onTextMessage(TextMessageEvent e) {
				Client c = ServerBotMain.api.getClientInfo(e.getInvokerId());
				ClientObject co = ServerBotMain.clients.get(c.getId());
				if(!c.isServerQueryClient()) {
					String msg = e.getMessage();
					String[] args = msg.split(" ");
					if(args[0].equalsIgnoreCase("!help")) {
						co.sendMessage("Diese Commands kannst du nutzen: \n!link <game> <ingame-name> | Verknüpft dich mit deinem Account \n!unlink <game> | Hebt eine Verknüpfung auf \n!"
								+"info <client> | Zeigt dir die Informationen zu einer Person an\n !togglebot | Ändert, ob du eine Join-Nachricht erhältst\n!nopoke / !nomsg | Ändert die entsprechenden Ruhegruppen"
								+"\nWenn du eine Beschwerde einreichen möchtest (weil jemand z.B. einen Stimmverzerrer benutzt) dann mach'"
								+" [B]Rechtsklick[/B] auf ihn und [B]Beschwerde einreichen[/B]");
					}
					else if(args[0].equalsIgnoreCase("!setnews") && c.isInServerGroup(35)) {
						if(args.length >= 2) {
							MySQL.updateNews(msg.replace("!setnews ", ""));
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
								MySQL.addGamegroup(co.getUuid(), args[1].toLowerCase(), args[2]);
								if(!c.isInServerGroup(getGroup(args[1].toLowerCase()))) {
									ServerBotMain.api.addClientToServerGroup(getGroup(args[1]), c.getDatabaseId());
								}else {
									co.sendMessage("Dein Name im Spiel " + args[1] + " wurde aktualisiert. Du heiß nun: " + args[2]);
								}
							}else {
								co.sendMessage("Bitte gib eines der folgenden Spiele an: Fortnite, Overwatch, Counter-Strike, Minecraft, GTA."); 
							}
						}
					}else if(args[0].equalsIgnoreCase("!unlink")) {
						if(args.length == 2) {
							if(MySQL.getGames(co.getUuid()).containsKey(args[1].toLowerCase())) {
								MySQL.removeGame(co.getUuid(), args[1].toLowerCase());
								if(c.isInServerGroup(getGroup(args[1].toLowerCase()))) {
									ServerBotMain.api.removeClientFromServerGroup(getGroup(args[1].toLowerCase()), c.getDatabaseId());
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
							for(Client client : ServerBotMain.api.getClients()) {
								if(client.getNickname().equalsIgnoreCase(name)) {
									cl = client;
								}
							}
							if(cl!=null) {
								MySQL.sendInfo(co, cl.getUniqueIdentifier(), cl.getNickname());
							}else {
								co.sendMessage("Der User [B]" +name + "[/B] ist nicht online. Überprüfe, ob du dich nicht vertippt hast.");
							}
						}else {
							co.sendMessage("Nutze [B]!info <name>[/B].");
						}
					}
					else if(args[0].equalsIgnoreCase("!togglebot")) {
						if(c.isInServerGroup(86)) {
							ServerBotMain.api.removeClientFromServerGroup(86, c.getDatabaseId());
						}else {
							ServerBotMain.api.addClientToServerGroup(86, c.getDatabaseId());
							co.sendMessage("Melde dich im Support, wenn du wieder Nachrichten vom Bot erhalten möchtest.");
						}
					}
					else if(args[0].equalsIgnoreCase("!nopoke")) {
						if(c.isInServerGroup(46)) {
							ServerBotMain.api.removeClientFromServerGroup(46, c.getDatabaseId());
						}else {
							ServerBotMain.api.addClientToServerGroup(46, c.getDatabaseId());
						}
					}
					else if(args[0].equalsIgnoreCase("!nomsg")) {
						if(c.isInServerGroup(45)) {
							ServerBotMain.api.removeClientFromServerGroup(45, c.getDatabaseId());
						}else {
							ServerBotMain.api.addClientToServerGroup(45, c.getDatabaseId());
						}
					}
					else if(args[0].equalsIgnoreCase("!channelhistory") && (c.isInServerGroup(35) || c.isInServerGroup(40) || c.isInServerGroup(89))) {
						if(args.length >= 2) {
							Client cl = null;
							for(Client client : ServerBotMain.api.getClients()) {
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
							if(isNumeric(args[1])) {
								int time = Integer.valueOf(args[1]);
								if(time > 0) {
									ServerBotMain.afkMinutes = time;
									MySQL.setAfkMinutes(time);
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
							for(Client cl : ServerBotMain.api.getClients()) {
								if(cl.getNickname().equalsIgnoreCase(name)){
									clientId = cl.getId();
								}
							}
							if(clientId!=0) {
								if(co.getChat() == null) {
									ClientObject co2 = ServerBotMain.clients.get(clientId);
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

			@Override
			public void onServerEdit(ServerEditedEvent arg0) {

			}

			@Override
			public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent arg0) {

			}

			@Override
			public void onClientMoved(ClientMovedEvent e) {
				Client c = ServerBotMain.api.getClientInfo(e.getClientId());
				ClientObject co = ServerBotMain.clients.get(c.getId());
				if(!c.isServerQueryClient()) {
					ChannelInfo info = ServerBotMain.api.getChannelInfo(e.getTargetChannelId());
					deleteChannel();
					if(info.getId() == 588) {
						support(c);
					}
					if(info.getId() == 138 || info.getId() == 142 || info.getId() == 144 || info.getId() == 580 || info.getId() == 139 || info.getId() == 146) {
						createChannel(c, info.getName());
					}
					if(e.getReasonId() != 1) {
						co.addChannel(info.getName());
						if(!c.isInServerGroup(35) && !c.isInServerGroup(40) && !c.isInServerGroup(89)) {
							if(co.isChannelhopping()) {
								co.banClient(300, "Autobann: Channelhopping");
							}
						}
					}
				}
			}

			@Override
			public void onClientLeave(ClientLeaveEvent e) {
				int id = e.getClientId();
				ClientObject co = ServerBotMain.clients.get(id);
				if(co.getChat() != null) {
					co.getChat().setChat(null);
				}
				MySQL.leave(co);
				ServerBotMain.clients.remove(id);
				deleteChannel();
			}

			@Override
			public void onClientJoin(ClientJoinEvent e) {
				Client c = ServerBotMain.api.getClientInfo(e.getClientId());
				ClientObject co = new ClientObject(c);
				if(!c.isServerQueryClient()) {
					ServerBotMain.clients.put(c.getId(), co);
					if(!c.isInServerGroup(86)) {
						co.sendMessage("Willkommen auf [B]EXITE[/B]\nFür Hilfe schreibe mich mit [B]!help[/B] an.\nWenn du diese Nachricht nicht mehr erhalten möchtest, schreibe [B]!togglebot[/B].");
					}
					if(ServerBotMain.api.getDatabaseClientByUId(c.getUniqueIdentifier()).getTotalConnections() >= 50 && !c.isInServerGroup(37)) {
						if(!c.isInServerGroup(35) && !c.isInServerGroup(40) && !c.isInServerGroup(57) && !c.isInServerGroup(38) && !c.isInServerGroup(51) && !c.isInServerGroup(89)) {
							ServerBotMain.api.addClientToServerGroup(37, c.getDatabaseId());
							co.sendMessage("Herzlichen Glückwunsch, du bist nun Stammuser.");
						}
					}
					MySQL.join(c);
				}
			}

			@Override
			public void onChannelPasswordChanged(ChannelPasswordChangedEvent arg0) {

			}

			@Override
			public void onChannelMoved(ChannelMovedEvent arg0) {

			}

			@Override
			public void onChannelEdit(ChannelEditedEvent arg0) {

			}

			@Override
			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent arg0) {

			}

			@Override
			public void onChannelDeleted(ChannelDeletedEvent arg0) {

			}

			@Override
			public void onChannelCreate(ChannelCreateEvent e) {
				ChannelInfo info = ServerBotMain.api.getChannelInfo(e.getChannelId());
				Client c = ServerBotMain.api.getClientInfo(e.getInvokerId());
				if(info.getName().startsWith("»") && (!c.isInServerGroup(35) && !c.isInServerGroup(85) && !c.isInServerGroup(89))) {
					ServerBotMain.api.deleteChannel(info.getId());
				}
			}
		});
	}

	static void createChannel(Client c, String name) {
		int channelorder = getChannelOrder(name);
		int channelid;
		if(channelorder == 1) {
			channelid = ServerBotMain.api.getChannelByNameExact(name, true).getId();
		}else {
			channelid= ServerBotMain.api.getChannelByNameExact(name+ " • №"+(channelorder-1), true).getId();
		}
		Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
		property.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
		property.put(ChannelProperty.CHANNEL_ORDER, String.valueOf(channelid));

		ChannelInfo ci = ServerBotMain.api.getChannelInfo(ServerBotMain.api.createChannel(name+ " • №"+channelorder, property));
		ServerBotMain.api.moveClient(c.getId(), ci.getId());
		ServerBotMain.api.setClientChannelGroup(25, ci.getId(), c.getDatabaseId());
		channels.get(name.replace("» ", "").toLowerCase()).add(ci);
	}

	static int getChannelOrder(String name) {
		int target = 1;
		ArrayList<Integer> pChannels = new ArrayList<>();
		for(ChannelInfo info : channels.get(name.replace("» ", "").toLowerCase())) {
			int current = Integer.valueOf(info.getName().split("№")[1]);
			pChannels.add(current);
			if(target == current) {
				target++;
				while(pChannels.contains(target)) {
					target++;
				}
			}
		}
		return target;
	}

	static void support(Client c) {
		ClientObject co = ServerBotMain.clients.get(c.getId());
		ArrayList<Client> sups = new ArrayList<>();
		for(Client sup : ServerBotMain.api.getClients()) {
			if(sup.isInServerGroup(84)) {
				sups.add(sup);
			}
		}
		if(sups.size() >=1) {
			co.sendMessage("Es wurden [B]"+ sups.size() +"[/B] Supporter benachrichtigt");
			boolean var = false;
			Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
			property.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
			property.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
			property.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
			property.put(ChannelProperty.CHANNEL_DESCRIPTION, co.getUuid());
			property.put(ChannelProperty.CHANNEL_ORDER, "588");
			Channel channel = ServerBotMain.api.getChannelByNameExact("» Support • "+c.getNickname(), true);
			if(channel != null) {
				ChannelInfo info = ServerBotMain.api.getChannelInfo(channel.getId());
				Map<ChannelProperty, String> p = new HashMap<ChannelProperty, String>();
				p.put(ChannelProperty.CHANNEL_NAME, "» Support • "+ServerBotMain.api.getClientByUId(info.getDescription()).getNickname());
				ServerBotMain.api.editChannel(info.getId(), p);
				sChannels.add(info.getName());
				var = true;
			}
			ChannelInfo ci = ServerBotMain.api.getChannelInfo(ServerBotMain.api.createChannel("» Support • "+c.getNickname(), property));
			ServerBotMain.api.addChannelPermission(ci.getId(), "i_channel_needed_join_power", 35);
			ServerBotMain.api.addChannelPermission(ci.getId(), "i_channel_needed_delete_power", 75);
			co.move(ci.getId());
			if(!var)
				sChannels.add(ci.getName());
			for(Client sup : sups) {
				String name= c.getNickname().replaceAll("|", "%7C");
				name = name.replaceAll(" ", "%20");
				ServerBotMain.api.sendPrivateMessage(sup.getId(), "[URL=client://" + c.getId() + "/" + co.getUuid() + "~"+name+"]" + c.getNickname() + "[/URL]"+" braucht Support!");
			}
		}else {
			co.sendMessage("Aktuell ist leider kein Supporter verfügbar.");
			co.kickFromChannel("Aktuell ist leider kein Supporter verfügbar.");
		}

	}

	static void deleteChannel() {
		String cName = null;
		ChannelInfo ci = null;
		for(String name : sChannels) {
			Channel ch = ServerBotMain.api.getChannelByNameExact(name, true);
			if(ch.getTotalClients() == 0) {
				ServerBotMain.api.deleteChannel(ch.getId());
				cName = name;
			}
		}
		for(String game : channels.keySet()) {
			for(ChannelInfo info : channels.get(game)) {
				String name = info.getName();
				Channel ch = ServerBotMain.api.getChannelByNameExact(name, true);
				if(ch.getTotalClients() == 0) {
					ServerBotMain.api.deleteChannel(ch.getId());
					ci = info;
				}
			}
		}
		if(cName != null) {
			sChannels.remove(cName);
		}if(ci != null) {
			String name = ci.getName().replace("» ", "").split(" • ")[0].toLowerCase();
			channels.get(name).remove(ci);
		}
	}

	static int getGroup(String game) {
		game = game.toLowerCase();
		switch (game) {
		case "fortnite":
			return 82;
		case "overwatch":
			return 79;
		case "counterstrike":
			return 81;
		case "minecraft":
			return 87;
		case "gta":
			return 88;
		default:
			return 0;
		}
	}

	static boolean isNumeric(String arg) {
		try {
			int number = Integer.parseInt(arg);
			return number>-1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
*/
}
