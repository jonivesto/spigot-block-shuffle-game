# Minecraft Block Shuffle game (plugin for Spigot)
Inspired by Dream's video on YouTube. Built and tested in 1.15.2. However, I don't see why it would not work on other versions as well.

You can have as many players as you want. The game assigns each player a block they must find and jump on before time runs out. If someone finds their block, everyone is given a new block. The game goes on forever, but it can be restarted using commands. If someone joins the server while others are playing, they will be in spectator gamemode until new game is started.

# Install
1. Download the jar file from https://github.com/jonivesto/spigot-block-shuffle-game/tree/master/target
2. Put it to your server's `plugins` directory and start your server.

# In-game commands:
- Start the game: `/playblockshuffle`
- Start the game with custom shuffle interval: `/playblockshuffle <shuffle interval in milliseconds>`
- Abort the game: `/stopblockshuffle`

