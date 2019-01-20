package de.ninju.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.ninju.main.ClientObject;
import de.ninju.main.ServerBotMain;

public class MoveListener extends TS3EventAdapter {
	
	ServerBotMain main;
	public MoveListener(ServerBotMain main) {
		this.main = main;
		main.api.addTS3Listeners(this);
	}
	
	@Override
	public void onClientMoved(ClientMovedEvent e) {
		Client c = main.api.getClientInfo(e.getClientId());
		ClientObject co = main.clients.get(c.getId());
		if(!c.isServerQueryClient()) {
			ChannelInfo info = main.api.getChannelInfo(e.getTargetChannelId());
			main.utils.deleteChannel();
			if(info.getId() == 588) {
				main.utils.support(c);
			}
			if(info.getId() == 138 || info.getId() == 142 || info.getId() == 144 || info.getId() == 580 || info.getId() == 139 || info.getId() == 146) {
				main.utils.createChannel(c, info.getName());
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

}
