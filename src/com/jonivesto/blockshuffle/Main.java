package com.jonivesto.blockshuffle;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	
	BukkitScheduler scheduler;
	
	boolean gameActive = false;
	
	@Override
    public void onEnable() {
		// Run checks every 10 ticks
		scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                runChecks();
            }
        }, 0L, 10L);
    }
	
    protected void runChecks() {
		if(!gameActive) return;
		
		// TODO: checks
	}

	@Override
    public void onDisable() {
		scheduler.cancelTasks(this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { 	
    	// Stop command
        if (command.getName().equalsIgnoreCase("stopblockshuffle")) {
            sender.sendMessage("§a§lYou stopped the game.");
            stopGame();
            return true;
        }       
        // Play command
        else if (command.getName().equalsIgnoreCase("playblockshuffle")) {
            sender.sendMessage("§a§lYou started the game.");
            newGame();
            return true;
        }
        return false;
    }

	private void stopGame() {
		gameActive = false;
		
	}

	private void newGame() {	
		shuffle();	
		gameActive = true;
	}

	private void shuffle() {
		// This object shuffles the blocks:
		Random random = new Random();
		// Assign new block for each player:
		for(Player player : getServer().getOnlinePlayers()) {
			// Get the limit for the range used for shuffle:
			int length = Material.values().length-1;
			// Shuffle until the result is a block:
			Material material = Material.values()[random.nextInt(length)];
			while(material.isBlock()==false) {
				material = Material.values()[random.nextInt(length)];
			}
			// Tell player their new block:
			player.sendMessage("§a§lYour new block is §6§l" + material.toString());
		}
		
	}
}
