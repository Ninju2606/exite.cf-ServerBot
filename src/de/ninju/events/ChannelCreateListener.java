package de.ninju.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.ninju.main.ServerBotMain;

public class ChannelCreateListener extends TS3EventAdapter {
	
	ServerBotMain main;
	public ChannelCreateListener(ServerBotMain main) {
		this.main = main;
		main.api.addTS3Listeners(this);
	}
	
	@Override
	public void onChannelCreate(ChannelCreateEvent e) {
		ChannelInfo info = main.api.getChannelInfo(e.getChannelId());
		Client c = main.api.getClientInfo(e.getInvokerId());
		if(info.getName().startsWith("»") && (!c.isInServerGroup(35) && !c.isInServerGroup(85) && !c.isInServerGroup(89))) {
			main.api.deleteChannel(info.getId());
		}
	}

}
