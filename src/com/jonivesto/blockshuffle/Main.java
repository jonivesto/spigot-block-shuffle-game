package com.jonivesto.blockshuffle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
    public void onEnable() {
		
    }
	
    @Override
    public void onDisable() {
    	
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { 	
    	// Stop command
        if (command.getName().equalsIgnoreCase("stopblockshuffle")) {
            sender.sendMessage("You ran /stopblockshuffle!");
            return true;
        }       
        // Play command
        else if (command.getName().equalsIgnoreCase("playblockshuffle")) {
            sender.sendMessage("You ran /playblockshuffle!");
            return true;
        }
        return false;
    }
}
