# nerdUHC

A plugin to run single-instance UHC matches on Bukkit/Spigot servers.

## Features

1. **Two Game Modes** - currently supports both solo and team matches.
* During *Solo Mode* matches, players will automatically be added to the Alive team, whose name is customizable.
* During *Team Mode* matches, configuration supports multiple options including whether players can pick their own teams (vs. autoassign), custom team names, dynamic team size caps, and forcing even teams.
2. **Pregame GUI** - a user-friendly alternative to the `/join`, `/spectate` and `/teamlist` commands. Teams are represented by the wool color that most closely approximates the team color.
3. **Gamemaster GUI** - a user-friendly alternative to the `/uhc start` command. Also includes the ability to change the duration, the team friendly fire option, and the world difficulty of the next match.
4. **Full Scoreboard Handling** - The plugin handles all creation of scoreboards, teams, scores, and objectives, including display slots.
5. **Combat Logging Prevention** - don't worry about players being less-than-honest and logging out during combat. If a player attacks (or is attacked by) a mob or another player, CombatLogger will tag them for a set duration customizable in the configuration. If they log out while they are actively tagged, a *doppel* will spawn in their place. Any damage the doppel takes will be transferred to the player upon relog.
* The doppel's entity type is customizable but defaults fittingly to a chicken :chicken:.
* The doppel inherits the player's name, position, health, and inventory drops.
* Hostile mobs targeting the player upon logout will continue to attack the doppel.
6. **Spawn Barrier Builder** - having to create a pre-game lobby can be a bit of a headache, and allowing players to get a preview of the spawn/showdown area can have its perks. With the Spawn Barrier Builder, one simply sets the barrier type and radius in the configuration, and the plugin does the rest. The configuration defaults to a 6-block radius barrier made of the invisible BARRIER block. The builder will not overwrite any existing blocks in your spawn build.

## Commands

A :white_check_mark: indicates GUI support. Naturally, player commands are available to gamemasters as well.

### Gamemaster Commands

These commands are only usable by those with the `nerduhc.gamemaster` permission level (default for OP).

* `/barrier [on|off]` Draws (or deletes) the barrier using the settings specified in config.
* `/extendtime [sec]` Extends the current match timer by the given number of seconds.
* `/sb-all` Refreshes the scoreboard of every player in the match.
* `/togglepvp` Toggles whether PVP is enabled or disabled in the current match's world. Uses the gamerule.
* `/uhc [start|stop]` Starts or stops the current match.

### Player Commands

* `/fixme` Fixes a spectator bug which causes a player to not be able to fly. Specific to the Nerd Nu server.
* `/kit` Gives the player a lobby kit. Only works during pre-match.
* :white_check_mark: `join [team]` Joins the specified team. Not case-sensitive.
* `t [msg]` Team chat.
* :white_check_mark: `/teamlist` Displays a list of teams in the current match, as well as their size and capacity.
* `sb` Refreshes the player's scoreboard.

## Configuration

* `world-name` Name of the world which to use for the matches.
* `uhc-game-mode` The game mode of the matches.
* :white_check_mark: `match-duration` Duration of the math in minutes.

* `spawn-x` x-coordinate of the spawn location.
* `spawn-y` y-coordinate of the spawn location.
* `spawn-z` z-coordinate of the spawn location.

* `spawn-barrier-radius` Radius of the barrier around the spawn location.
* `spawn-barrier-block-name` Name of the material with which to build the barrier.

* `spread-distance-between-players` Minimum spread distance between players.
* `spread-distance-from-spawn` Maximum spread distance from spawn.
* `spread-respect-teams` Whether or not to respect teams during the spread.

* `let-players-pick-teams` Whether or not to allow players to pick teams.
* `force-even-teams` Whether or not teams should be forced as even as possible.
* `max-team-size` Maximum number of players per team.
* `alive-team-name` Name of the alive team (for solo mode only).
* `dead-team-name` Name of the dead team (for solo mode only).
* :white_check_mark: `allow-friendly-fire` Whether or not to allow friendly fire within teams.

* `player-combat-tag-time-in-sec` Number of seconds after attacking (or taking damage) for which the player should spawn a doppel upon logout.
* `combat-tag-doppel` Type of LivingEntity to use for doppels.

* `do-deathmatch` Whether or not to start a deathmatch after the timer runs out.
* `deathmatch-distance-between-players` Minimum spread distance between players for deathmatch.
* `deathmatch-distance-from-spawn` Maximum spread distance from spawn for deathmatch.

* `teams` A maplist of team properties. The default is shown below.
* `name` Name of the team.
* `color` Color of the team. Should be a ChatColor.

```
teams:
  - name: 'Sapphlings'
    color: 'RED'
  - name: 'Guardians'
    color: 'GOLD'
  - name: 'Blazes'
    color: 'YELLOW'
  - name: 'Creepers'
    color: 'GREEN'
  - name: 'Vexes'
    color: 'BLUE'
  - name: 'Endermen'
    color: 'DARK_PURPLE'
```

* `gamerules` A maplist of gamerules to set. The default is shown below.

```
gamerules:
  - doDaylightCycle: TRUE
  - NaturalRegeneration: FALSE
```