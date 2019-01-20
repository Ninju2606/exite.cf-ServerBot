package de.ninju.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Utils {

	private ArrayList<String> sChannels = new ArrayList<>();	//Support
	private HashMap<String, ArrayList<ChannelInfo>> channels = new HashMap<>();
	
	ServerBotMain main;
	
	
	public void registerClients() {
		for(Client c : main.api.getClients()) {
			if(!c.isServerQueryClient()) {
				main.clients.put(c.getId(), new ClientObject(c, main));
			}
		}
	}
	
	public Utils(ServerBotMain main) {
		this.main = main;
		channels.put("fortnite", new ArrayList<ChannelInfo>());
		channels.put("minecraft", new ArrayList<ChannelInfo>());
		channels.put("counter strike", new ArrayList<ChannelInfo>());
		channels.put("overwatch", new ArrayList<ChannelInfo>());
		channels.put("gta", new ArrayList<ChannelInfo>());
		channels.put("user-talk", new ArrayList<ChannelInfo>());
	}
	
	
	public void createChannel(Client c, String name) {
		int channelorder = getChannelOrder(name);
		int channelid;
		if(channelorder == 1) {
			channelid = main.api.getChannelByNameExact(name, true).getId();
		}else {
			channelid= main.api.getChannelByNameExact(name+ " • №"+(channelorder-1), true).getId();
		}
		Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
		property.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
		property.put(ChannelProperty.CHANNEL_ORDER, String.valueOf(channelid));

		ChannelInfo ci = main.api.getChannelInfo(main.api.createChannel(name+ " • №"+channelorder, property));
		main.api.moveClient(c.getId(), ci.getId());
		main.api.setClientChannelGroup(25, ci.getId(), c.getDatabaseId());
		channels.get(name.replace("» ", "").toLowerCase()).add(ci);
	}

	public int getChannelOrder(String name) {
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

	public void support(Client c) {
		ClientObject co = main.clients.get(c.getId());
		ArrayList<Client> sups = new ArrayList<>();
		for(Client sup : main.api.getClients()) {
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
			Channel channel = main.api.getChannelByNameExact("» Support • "+c.getNickname(), true);
			if(channel != null) {
				ChannelInfo info = main.api.getChannelInfo(channel.getId());
				Map<ChannelProperty, String> p = new HashMap<ChannelProperty, String>();
				p.put(ChannelProperty.CHANNEL_NAME, "» Support • "+main.api.getClientByUId(info.getDescription()).getNickname());
				main.api.editChannel(info.getId(), p);
				sChannels.add(info.getName());
				var = true;
			}
			ChannelInfo ci = main.api.getChannelInfo(main.api.createChannel("» Support • "+c.getNickname(), property));
			main.api.addChannelPermission(ci.getId(), "i_channel_needed_join_power", 35);
			main.api.addChannelPermission(ci.getId(), "i_channel_needed_delete_power", 75);
			co.move(ci.getId());
			if(!var)
				sChannels.add(ci.getName());
			for(Client sup : sups) {
				String name= c.getNickname().replaceAll("|", "%7C");
				name = name.replaceAll(" ", "%20");
				main.api.sendPrivateMessage(sup.getId(), "[URL=client://" + c.getId() + "/" + co.getUuid() + "~"+name+"]" + c.getNickname() + "[/URL]"+" braucht Support!");
			}
		}else {
			co.sendMessage("Aktuell ist leider kein Supporter verfügbar.");
			co.kickFromChannel("Aktuell ist leider kein Supporter verfügbar.");
		}

	}

	public void deleteChannel() {
		String cName = null;
		ChannelInfo ci = null;
		for(String name : sChannels) {
			Channel ch = main.api.getChannelByNameExact(name, true);
			if(ch.getTotalClients() == 0) {
				main.api.deleteChannel(ch.getId());
				cName = name;
			}
		}
		for(String game : channels.keySet()) {
			for(ChannelInfo info : channels.get(game)) {
				String name = info.getName();
				Channel ch = main.api.getChannelByNameExact(name, true);
				if(ch.getTotalClients() == 0) {
					main.api.deleteChannel(ch.getId());
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

	public int getGroup(String game) {
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

	public boolean isNumeric(String arg) {
		try {
			int number = Integer.parseInt(arg);
			return number>-1;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public String getTimeStamp() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
		return "[" + sdf.format(cal.getTime()) + "] ";
	}

}
