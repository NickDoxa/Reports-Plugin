package com.doxa.report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class SQL {
	
	Main plugin;
	public SQL(Main main) {
		this.plugin = main;
	}

	//CONNECTION SHIT
    private String host, port, database, username, password;
    static Connection connection;

    public void connect() throws SQLException {
	    host = plugin.getConfig().getString("host");
	    port = plugin.getConfig().getString("port");
	    database = plugin.getConfig().getString("database");
	    username = plugin.getConfig().getString("username");
	    password = plugin.getConfig().getString("password");
	    connection = DriverManager.getConnection("jdbc:mysql://" +
	    	     host + ":" + port + "/" + database + "?useSSL=false",
	    	     username, password);
	    System.out.print("Connected successfully to MySQL Database!\n");
    }
    
    public void disconnect() {
    	if (isConnected()) {
    		try {
    			connection.close();
    			System.out.print("Disconnected successfully from MySQL Database!\n");
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static Connection getConnection() {
    	return connection;
    }
    
    public boolean isConnected() {
    	return (connection == null ? false : true);
    }
    
    //COMMANDS FOR DATBASE
    
    //TABLE NAME IS rpt
    
    public void createTable() {
    	PreparedStatement ps;
    	try {
    		ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS rpt"
    				+ " (NAME VARCHAR(100),REASON VARCHAR(100),SENDER VARCHAR(100),PRIMARY KEY (NAME))");
    		ps.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    public void createPlayerReport(Player player, Player sender, String reason, String prefix) {
    	try {
    		PreparedStatement ps2 = getConnection().prepareStatement("INSERT IGNORE INTO rpt" + 
    				" (NAME,REASON,SENDER) VALUES (?,?,?)");
    		ps2.setString(1, player.getName());
    		ps2.setString(2, reason);
    		ps2.setString(3, sender.getName());
    		ps2.executeUpdate();
    		sender.sendMessage(plugin.prefix + ChatColor.GOLD + "Report Created!");
    		return;
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    //READ TABLE rpt
    public void readTable(Player player) {
    	try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM rpt");
			ResultSet rs = ps.executeQuery();
			List<String> msg = new ArrayList<>();
			int rowid = 0;
			while (rs.next()) {
				rowid++;
				String name = rs.getString("NAME");
				String reason = rs.getString("REASON");
				String sender = rs.getString("SENDER");
				msg.add(ChatColor.YELLOW + "" + rowid + ". " + name + 
						" was reported for " + reason + " by " +  sender + "\n");
			}
			String test = msg.toString().substring(1, msg.toString().length() - 1);
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reports");
			player.sendMessage("");
			if (test.length() > 0) {
				player.sendMessage(test);
			} else {
				player.sendMessage(ChatColor.RED + "No active reports!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void clearTable() {
    	try {
			PreparedStatement ps = getConnection().prepareStatement("DELETE FROM rpt");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
