package de.ninju.main;

import java.util.ArrayList;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class ClientObject {
	
	ServerBotMain main;
	
	String uuid;
	long joinTime;
	int id;
	ClientObject chat = null;
	ArrayList<String> channels = new ArrayList<>();
	ArrayList<Long> times = new ArrayList<>();
	
	public ClientObject(Client c, ServerBotMain main) {
		this.main = main;
		uuid = c.getUniqueIdentifier();
		joinTime = System.currentTimeMillis();
		id = c.getId();
		channels.add(main.api.getChannelInfo(c.getChannelId()).getName());
		times.add(System.currentTimeMillis());
		
	}
	
	public void sendMessage(String message) {
		main.api.sendPrivateMessage(id, message);
	}
	public void move(int channelId) {
		main.api.moveClient(id, channelId);
	}
	public void poke(String message) {
		main.api.pokeClient(id, message);
	}
	public void kickFromServer(String message) {
		main.api.kickClientFromServer(message, id);
	}
	public void kickFromServer() {
		main.api.kickClientFromServer(id);
	}
	public void kickFromChannel(String message) {
		main.api.kickClientFromChannel(message, id);
	}
	public void kickFromChannel() {
		main.api.kickClientFromChannel(id);
	}
	public void banClient(long duration) {
		main.api.banClient(id, duration);
	}
	public void banClient(long duration, String message) {
		main.api.banClient(id, duration, message);
	}
	
	public void addChannel(String name) {
		channels.add(name);
		times.add(System.currentTimeMillis());
	}
	public ArrayList<String> getChannels(){
		return channels;
	}
	public ArrayList<Long> getTimes(){
		return times;
	}
	
	public boolean isChannelhopping() {
		if(times.size() > 10) {
			if(times.get(times.size()-9) >= System.currentTimeMillis()-1000*60)
				return true;
		}
		return false;
	}

	public String getUuid() {
		return uuid;
	}

	public long getJoinTime() {
		return joinTime;
	}

	public int getId() {
		return id;
	}

	public ClientObject getChat() {
		return chat;
	}
	public void setChat(ClientObject chat) {
		this.chat = chat;
	}
	
}
