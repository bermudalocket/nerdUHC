# nerdUHC

A plugin to run single-instance UHC matches on Bukkit/Spigot servers.

## Limitations

The plugin runs as a singleton limiting the number of concurrent matches to one. Expanding functionality to support multiple instances is currently not within the development scope of this project.

## Features

1. **Two Game Modes** - currently supports both solo and team matches.
  i. During *Solo Mode* matches, players will automatically be added to the Alive team, whose name is customizable.
  ii. During *Team Mode* matches, configuration supports multiple options including whether players can pick their own teams (vs. autoassign), custom team names, dynamic team size caps, and forcing even teams.
2. **Full Scoreboard Handling** - all you have to do is customize your desired Objectives in the configuration file and you're good to go. The plugin handles all creation of scoreboards, teams, scores, and objectives, including display slots.
3. **Combat Logging Prevention** - don't worry about players being less-than-honest and logging out during combat. If a player attacks (or is attacked by) a mob, CombatLogger will tag them for a set duration customizable in the configuration. If they log out while they are actively tagged, a *doppel* will spawn in their place. Any damage the doppel takes will be transferred to the player upon relog.
  i. The doppel's entity type is customizable but defaults fittingly to a chicken.
  ii. The doppel inherits the player's name, position, health, and inventory drops.
  iii. ~~Hostile mobs near the doppel will continue to attack the doppel.~~ (WIP)
4. **Spawn Barrier Builder** - having to create a pre-game lobby can be a bit of a headache, and allowing players to get a preview of the spawn/showdown area can have its perks. With the Spawn Barrier Builder, one simply sets the barrier type and radius in the configuration, and the plugin does the rest. The configuration defaults to a 6-block radius barrier made of the invisible BARRIER block. The builder will not overwrite any existing blocks in your spawn build.
