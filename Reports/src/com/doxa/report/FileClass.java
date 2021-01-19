package com.doxa.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class FileClass {

	public void createFile() {
		try {
			File file = new File("plugins/Reports/reports.txt");
			//check if File exists
		    if (file.createNewFile()) {
		    	//Create new File
		    	System.out.println("File created: " + file.getName());
		    } else {
		    	//File exists so just scan it
		    	System.out.println("File scanned: reports.txt");
		    }
	    } catch (IOException e) {
	        System.out.println("An error occurred creating or reading reports.txt. contact Nick Doxa ASAP!");
	    }
	}
	
	public void writeReport(Player sender, Player reported, String reason, String prefix) {
		    try {
		    	File file = new File("plugins/Reports/reports.txt");
		        FileWriter myWriter = new FileWriter(file.getPath(), true);
		        myWriter.write(reported.getName() + " was reported for " + reason + " by " + 				
		        sender.getName() + "\n");
		        myWriter.close();
		        sender.sendMessage(prefix + ChatColor.GOLD + "Report Created!");
		    } catch (IOException e) {
		        sender.sendMessage(ChatColor.RED + "Error with reports.txt contact Administration immediately!");
		    }
	}
	
	public void clearFile() {
		File file = new File("plugins/Reports/reports.txt");
		file.delete();
		createFile();
	}
	
	public void scanFile(Player player) throws IOException {
		File file = new File("plugins/Reports/reports.txt");
		try {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(file);
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reports");
			player.sendMessage("");
			int number = 0;
			BufferedReader br = new BufferedReader(new FileReader(file.getPath())); 
			if (br.readLine() == null) {
				player.sendMessage(ChatColor.RED + "No active reports!");
			} else {
				while(scan.hasNextLine()) {
					number++;
					player.sendMessage(ChatColor.YELLOW + "" + number + ". " + scan.nextLine());
				}
			}
		} catch (FileNotFoundException e) {
			createFile();
			player.sendMessage("File not found... Creating Now!");
		}
	}
	
}
