package de.ninju.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.ninju.main.ClientObject;
import de.ninju.main.ServerBotMain;

public class JoinListener extends TS3EventAdapter{
	
	ServerBotMain main;
	public JoinListener(ServerBotMain main) {
		this.main = main;
		main.api.addTS3Listeners(this);
	}
	
	@Override
	public void onClientJoin(ClientJoinEvent e) {
		Client c = main.api.getClientInfo(e.getClientId());
		ClientObject co = new ClientObject(c, main);
		if(!c.isServerQueryClient()) {
			main.clients.put(c.getId(), co);
			if(!c.isInServerGroup(86)) {
				co.sendMessage("Willkommen auf [B]EXITE[/B]\nFür Hilfe schreibe mich mit [B]!help[/B] an.\nWenn du diese Nachricht nicht mehr erhalten möchtest, schreibe [B]!togglebot[/B].");
			}
			if(main.api.getDatabaseClientByUId(c.getUniqueIdentifier()).getTotalConnections() >= 50 && !c.isInServerGroup(37)) {
				if(!c.isInServerGroup(35) && !c.isInServerGroup(40) && !c.isInServerGroup(57) && !c.isInServerGroup(38) && !c.isInServerGroup(51) && !c.isInServerGroup(89)) {
					main.api.addClientToServerGroup(37, c.getDatabaseId());
					co.sendMessage("Herzlichen Glückwunsch, du bist nun Stammuser.");
				}
			}
			main.mySql.join(c);
		}
	}

}
