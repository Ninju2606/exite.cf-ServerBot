package de.ninju.main;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class MySQL {

	private ServerBotMain main;

	public MySQL(ServerBotMain main) {
		this.main = main;
	}

	private final String username = "ts3bot";
	private final String password = "";
	private Connection con;

	public int getAfkMinutes() {
		connect();
		int min = (int) get("1", "entry", "afkMinutes", "serverData");
		disconnect();
		return min;
	}

	public void setAfkMinutes(int m) {
		connect();
		update("UPDATE serverData SET afkMinutes = '"+m+"' WHERE entry = '1'");
		disconnect();
	}
	public void addGamegroup(String uid, String game, String name) {
		connect();
		update("UPDATE userData SET " + game + " = '" + name + "' WHERE uniqueIdentifier = '" + uid+ "'");
		disconnect();
	}
	public HashMap<String, String> getGames(String uuid){
		HashMap<String, String> games = new HashMap<>();
		connect();
		if(!((String) get(uuid, "uniqueIdentifier", "counterstrike", "userData")).equals("null")) {
			games.put("counterstrike", (String) get(uuid, "uniqueIdentifier", "counterstrike", "userData"));
		}
		if(!((String) get(uuid, "uniqueIdentifier", "fortnite", "userData")).equals("null")) {
			games.put("fortnite", (String) get(uuid, "uniqueIdentifier", "fortnite", "userData"));
		}
		if(!((String) get(uuid, "uniqueIdentifier", "gta", "userData")).equals("null")) {
			games.put("gta", (String) get(uuid, "uniqueIdentifier", "gta", "userData"));
		}
		if(!((String) get(uuid, "uniqueIdentifier", "minecraft", "userData")).equals("null")) {
			games.put("minecraft", (String) get(uuid, "uniqueIdentifier", "minecraft", "userData"));
		}
		if(!((String) get(uuid, "uniqueIdentifier", "overwatch", "userData")).equals("null")) {
			games.put("overwatch", (String) get(uuid, "uniqueIdentifier", "overwatch", "userData"));
		}
		disconnect();
		return games;
	}

	public void sendInfo(ClientObject co, String uuid, String name) {
		connect();
		String message = "Der User " +name+ " spielt folgende Spiele:";
		if(getGames(uuid).isEmpty()) {
			message = "Der User " +name+ " hat sich noch nicht verküpft.";
		}else {
			for(String game : getGames(uuid).keySet()) {
				message += "\n- " +game + ": "+getGames(uuid).get(game);
			}
		}
		co.sendMessage(message);
		disconnect();
	}
	public void removeGame(String uid, String game) {
		addGamegroup(uid, game, "null");
	}

	public boolean isRegistered(String uid) {
		try {
			ResultSet rs = getResult("SELECT * FROM `userData` WHERE `uniqueIdentifier` = '"+uid+"'");
			while(rs.next()) {
				return rs.getString("uniqueIdentifier") != null;
			}
			rs.close();
			return false;
		}catch(SQLException e) {}
		return false;
	}

	public void join(Client c) {
		connect();
		if(!isRegistered(c.getUniqueIdentifier())) {
			update("INSERT INTO `userData` (`uniqueIdentifier`, `lastName`, `onlineTime`, `lastAction`, `rank`, `country`, `news`) VALUES"
					+" ('"+c.getUniqueIdentifier()+"', '"+c.getNickname()+"', '0', '"+System.currentTimeMillis()+"', 'User', '"+c.getCountry()+"', '0');");
		}else {
			update("UPDATE userData SET lastAction = '"+System.currentTimeMillis()+"' WHERE uniqueIdentifier= '"+c.getUniqueIdentifier()+"'");
			update("UPDATE userData SET online = 'true' WHERE uniqueIdentifier= '"+c.getUniqueIdentifier()+"'");
			String rank = "User";
			if(c.isInServerGroup(37)) rank = "Stammuser";
			if(c.isInServerGroup(51)) rank = "YouTuber";
			if(c.isInServerGroup(57)) rank = "Freund";
			if(c.isInServerGroup(40)) rank = "Mod";
			if(c.isInServerGroup(35)) rank = "Admin";
			if(!rank.equals(String.valueOf(get(c.getUniqueIdentifier(), "uniqueIdentifier", "Rank", "userData")))) {
				update("UPDATE userData SET rank = '"+rank+"' WHERE uniqueIdentifier = '"+ c.getUniqueIdentifier()+"'");
			}
		}
		sendNews(c);
		disconnect();
	}

	public void leave(ClientObject c) {
		connect();
		update("UPDATE userData SET lastAction = '"+System.currentTimeMillis()+"' WHERE uniqueIdentifier= '"+c.getUuid()+"'");
		long onlineTime = (long) get(c.getUuid(), "uniqueIdentifier", "onlineTime", "userData") + (System.currentTimeMillis()-c.getJoinTime());
		update("UPDATE userData SET onlineTime = '"+onlineTime+"' WHERE uniqueIdentifier= '"+c.getUuid()+"'");
		disconnect();
	}

	public void sendNews(Client c) {
		int userLvl = (int) get(c.getUniqueIdentifier(), "uniqueIdentifier", "news", "userData");
		int newsLvl = getNewsLvl();
		if(userLvl < newsLvl) {
			for (int i = userLvl; i<newsLvl; i++) {
				main.api.sendPrivateMessage(c.getId(), getNews(i+1));
			}
			update("UPDATE userData SET news = '"+newsLvl+"' WHERE uniqueIdentifier = '"+c.getUniqueIdentifier()+"'");
			main.api.pokeClient(c.getId(), "Es gibt News, die du akzeptieren musst!");
			main.api.sendPrivateMessage(c.getId(), "Bitte akzeptiere die News mit [B]!yes[/B].");
			main.api.addClientToServerGroup(43, c.getDatabaseId());
		}else if(c.isInServerGroup(43)) {
			update("UPDATE userData SET news = '"+0+"' WHERE uniqueIdentifier = '"+c.getUniqueIdentifier()+"'");
			sendNews(c);
		}
	}
	public void updateNews(String news) {
		connect();
		int number = getNewsLvl() + 1;
		String date = new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis());
		update("INSERT INTO `news`(`number`, `date`, `news`) VALUES ('"+number+"', '"+date+"', '"+news+"');");
		disconnect();
	}
	public String getNews(int id) {
		String news = "[B]";
		news += get(String.valueOf(id), "number", "date", "news")+"[/B]: "+get(String.valueOf(id), "number", "news", "news");
		return news;
	}
	public int getNewsLvl() {
		try {
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM news");
			int zeilen = 0;
			while(rs.next()) {
				zeilen++;
			}
			return zeilen;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void connect() {
		if (!isConnected()) {
			try
			{
				con =  (Connection) DriverManager.getConnection("jdbc:mysql://18.185.25.212:3306/ts3bot", username, password);
			}
			catch (SQLException e)
			{
				System.out.println("[MySQL] Connection failed");
				e.printStackTrace();
			}
		}
	}
	private void disconnect()
	{
		if (isConnected()) {
			try
			{
				con.close();
				con = null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	private boolean isConnected()
	{
		return con != null;
	}

	private void update(String qry) {
		if (isConnected()) {
			try {
				con.createStatement().executeUpdate(qry);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private ResultSet getResult(String qry) {
		if (isConnected()) {
			try {
				return con.createStatement().executeQuery(qry);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private Object get(String whereresult, String where, String select, String database) {

		ResultSet rs = getResult("SELECT " + select + " FROM " + database + " WHERE " + where + "='" + whereresult + "'");
		try {
			if(rs.next()) {
				Object v = rs.getObject(select);
				return v;
			}
		} catch (SQLException e) {
			return "ERROR";
		}

		return "ERROR";
	}

}
