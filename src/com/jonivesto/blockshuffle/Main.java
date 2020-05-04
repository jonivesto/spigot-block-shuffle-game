package com.jonivesto.blockshuffle;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin implements Listener {

	// Defines if there is a running game in progress
	boolean gameActive = false;
	boolean blockFound = true;
	
	// Timer
	Date lastShuffle;
	final long DEFAULT_SHUFFLE_INTERVAL = 300000; // (5 minutes)
	final long MIN_SHUFFLE_INTERVAL = 10000; // (10 seconds)
	long shuffleInterval = DEFAULT_SHUFFLE_INTERVAL; 
	
	// Random used to pick random block IDs
	Random random = new Random();
	
	// List of players in active game.
	// The String key is the player's UUID
	Map<String, Contestant> contestants = new HashMap<>();
	
	@Override
    public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		// Time left counter
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	if(gameActive==false||blockFound==true||lastShuffle==null) return;
		    	long elapsedMillis = new Date().getTime() - lastShuffle.getTime();
		    	if(elapsedMillis>=shuffleInterval) {		    			
	    			blockFound = true;
	    			for(Player player : getServer().getOnlinePlayers()) {	
	    				player.sendMessage("It's a draw! No one found their block in time.");	
	    			}
	    			shuffle();
	    		}
		    	else {
			    	for(Player player : getServer().getOnlinePlayers()) {	    		
			    		player.spigot().sendMessage(
			    				ChatMessageType.ACTION_BAR, 
			    				new TextComponent(ChatColor.GOLD+"Time left: "+((shuffleInterval/1000)-(elapsedMillis/1000))+"s"
			    		));	    		
			    	}
		    	}
		    }
		}, 0L, 20L);
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { 	
    	// Stop command
        if (command.getName().equalsIgnoreCase("stopblockshuffle")) {
        	// Return if there is no game to abort
        	if(!gameActive) return true;
        	// Stop game
            stopGame();
            // Inform all players who stopped the game:
            for(Player player : getServer().getOnlinePlayers()) {
            	player.sendMessage(sender.getName() + ChatColor.RED + " Aborted the game!");
            }
            return true;
        }       
        // Play command
        else if (command.getName().equalsIgnoreCase("playblockshuffle")) {
        	// Return if game already active
        	if(gameActive) return true;
        	// set refreshInterval if it's given
        	if(args.length>0) {
        		try {
        			shuffleInterval = Long.parseLong(args[0]);
        		}catch(Exception e) {
        			shuffleInterval = DEFAULT_SHUFFLE_INTERVAL;
        		}      		
        	}
        	// Don't allow too short interval
        	if(shuffleInterval<MIN_SHUFFLE_INTERVAL) shuffleInterval = MIN_SHUFFLE_INTERVAL;
            newGame();
            return true;
        }
        return false;
    }

	private void stopGame() {
		gameActive = false;
		blockFound = true;
		lastShuffle = null;
	}
	
	private void newGame() {
		contestants.clear();
		// Only players who are online when start command runs are added to the game
		for(Player player : getServer().getOnlinePlayers()) {
			contestants.put(player.getUniqueId().toString(), new Contestant());
			// Set survival gamemode if not current:
			if(player.getGameMode()!=GameMode.SURVIVAL) {
				player.setGameMode(GameMode.SURVIVAL);
			}
			// Inform player that game has started:
			player.sendMessage(ChatColor.DARK_PURPLE +"You were added to a new game of block shuffle!");
			
		}
		// Set game active so no one can join it anymore
		gameActive = true;
		// Start game flow by giving the blocks
		shuffle();
	}
	
	// Randomly picks blocks for the shuffle() method:
    private Material getRandomMaterial() {
        // Get the limit for the range used for shuffle:
        int length = Material.values().length-1;
        // Shuffle until the result is a block:
        Material material = Material.values()[random.nextInt(length)];
        while(material.name().contains("END_")
            ||material.name().contains("COMMAND")
            ||material.name().contains("BARRIER")
            ||material.name().contains("PURPUR")
            ||material.name().contains("STRUCTURE")
            ||material.name().contains("CHORUS")
            ||material.name().contains("BEACON")
            ||material.name().contains("HEAD")
            ||material.name().contains("SHULKER")
            ||material.name().contains("ENDER_")
            ||material.name().contains("EGG")
            ||material.name().contains("WALL")
            ||material.name().matches(".*\\d.*")
            ||material.isBlock() == false
            ||material.isSolid() == false) {
            material = Material.values()[random.nextInt(length)];
        }
        return material;
	}
	
	private void shuffle() {	
		// Assign new block for each player in contestants list
		for(Contestant contestant : contestants.values()) {
			
			contestant.previousTarget = contestant.currentTarget;
			contestant.currentTarget = getRandomMaterial();
		}	
		// Inform the players about their new blocks.
		// Only sent to players that are online.
		for(Player player : getServer().getOnlinePlayers()) {
			if(contestants.containsKey(player.getUniqueId().toString())) {
				Material material = contestants.get(player.getUniqueId().toString()).currentTarget;
				player.sendMessage("§a§lYour new block is §6§l" + material.name());
			}	
		}
		
		updateScoreboard();
		blockFound = false;
		lastShuffle = new Date();
	}
	
	@EventHandler
    public void join(PlayerJoinEvent e){
		// Set spectator mode if game is active and player is not in contestants list
		if(gameActive) {
			Player player = e.getPlayer();
			if(!contestants.containsKey(player.getUniqueId().toString())) {
				player.setGameMode(GameMode.SPECTATOR);
			}
		}
		else {
			Player player = e.getPlayer();
			player.setGameMode(GameMode.SURVIVAL);
		}	
    }
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		// Return if block is found
		if(blockFound) return;
		// Return if player did not move
	    if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) return;   
	    // See who the player is
	    Player player = (Player) e.getPlayer();
	    Contestant contestant = contestants.get(player.getUniqueId().toString());    
	    // Return if player is not contestant
	    if(contestants.containsKey(player.getUniqueId().toString())==false) {
	    	return;
	    }
	    // Return if  there is no target block    
	    if(contestant.currentTarget==null) {
	    	return;
	    }
	    // Check if block player is standing on is their target block
	    if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == contestant.currentTarget) {
	    	blockFound(player.getName(), contestant);   	
	    }
	}

	private void blockFound(String contestantName, Contestant contestant) {
		blockFound = true;
		contestant.points++;
		// Inform others:
		for(Player player : getServer().getOnlinePlayers()) {
			player.sendMessage(contestantName + " found their block " + contestant.currentTarget.name() + "!");			
		}
		// Shuffle new blocks:
		shuffle();
	}
	
	private void updateScoreboard() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
	    Scoreboard scoreboard = manager.getNewScoreboard();	
	    @SuppressWarnings("deprecation")
		Objective objective = scoreboard.registerNewObjective("Blocks found", "");        
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD + "Blocks found");
        
        
        for(Player player : getServer().getOnlinePlayers()) {
			if(contestants.containsKey(player.getUniqueId().toString())) {
				Score score = objective.getScore(player.getName());
		        score.setScore(contestants.get(player.getUniqueId().toString()).points);
			}
			
			player.setScoreboard(scoreboard);
		}
	}
	
}
