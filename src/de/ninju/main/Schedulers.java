package de.ninju.main;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.Complaint;

public class Schedulers {
	
	ServerBotMain main;
	public Schedulers(ServerBotMain main) {
		this.main = main;
		afkCheck(); //1M
		clientCheck(); //10S
	}
	
	private void afkCheck() { //Handle afk clients
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
			for(Client c : main.api.getClients()) {
				if(!c.isServerQueryClient()) {
					if(!c.isInServerGroup(35) && !c.isInServerGroup(40) && !c.isInServerGroup(85)) {
						if(c.getIdleTime() >=main.afkMinutes-5*60*1000 && c.getIdleTime() < main.afkMinutes-4*60*1000){
							main.api.sendPrivateMessage(c.getId(), "Du bist bereits seit "+(main.afkMinutes-5)+" Minuten AFK. Wenn du weiterhin AFK bist, wirst du in 5 Minuten gekickt.");
						}
						if(c.getIdleTime() >= main.afkMinutes*60*1000) {
							main.api.kickClientFromServer("Du warst zu lange AFK!", c);
						}
					}
				}
			}
		}, 0, 1L, TimeUnit.MINUTES);
	}

	private void clientCheck() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
			for(Complaint com : main.api.getComplaints()) {
				//Checks reports
				for(Client sup : main.api.getClients()) {
					if(!sup.isServerQueryClient()) {
						if(sup.isInServerGroup(84)) {
							Client report = main.api.getClientByNameExact(com.getSourceName(), true);
							if(!report.isServerQueryClient()) {
								String reporter= com.getSourceName().replaceAll("|", "%7C");
								reporter = reporter.replaceAll(" ", "%20");
								reporter = "[URL=client://" + report.getId() + "/" + report.getUniqueIdentifier() + "~"+reporter+"]" + com.getSourceName() + "[/URL]";
								Client target = main.api.getClientByNameExact(com.getTargetName(), true);
								if(!target.isServerQueryClient()) {
									String targeter= com.getSourceName().replaceAll("|", "%7C");
									targeter = targeter.replaceAll(" ", "%20");
									targeter = "[URL=client://" + target.getId() + "/" + target.getUniqueIdentifier() + "~"+targeter+"]" + com.getTargetName() + "[/URL]";
									main.api.sendPrivateMessage(sup.getId(), reporter +" reportet "+ targeter+ " wegen: [B]"+com.getMessage()+"[/B].");
								}
							}
						}
					}
				}
				main.api.deleteComplaint(com.getTargetClientDatabaseId(), com.getSourceClientDatabaseId());
			}
			for(Client c : main.api.getClients()) {
				//Checks client name
				if(!c.isServerQueryClient()) {
					if(!c.isInServerGroup(35) && !c.isInServerGroup(40) && !c.isInServerGroup(85)) {
						if(c.getNickname().contains("[") || c.getNickname().contains("]")) {
							main.api.kickClientFromServer("Dein Name darf keine Sonderzeichen enthalten", c.getId());
						}
					}
				}
			}
		}, 0, 10L, TimeUnit.SECONDS);
	}

}
