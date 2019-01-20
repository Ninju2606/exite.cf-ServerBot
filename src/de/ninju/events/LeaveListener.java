package de.ninju.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;

import de.ninju.main.ClientObject;
import de.ninju.main.ServerBotMain;

public class LeaveListener extends TS3EventAdapter{
	
	ServerBotMain main;
	public LeaveListener(ServerBotMain main) {
		this.main = main;
		main.api.addTS3Listeners(this);
	}
	
	@Override
	public void onClientLeave(ClientLeaveEvent e) {
		int id = e.getClientId();
		ClientObject co = main.clients.get(id);
		if(co.getChat() != null) {
			co.getChat().setChat(null);
		}
		main.mySql.leave(co);
		main.clients.remove(id);
		main.utils.deleteChannel();
	}

}
