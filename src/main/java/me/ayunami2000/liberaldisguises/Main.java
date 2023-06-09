package me.ayunami2000.liberaldisguises;

import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AreaEffectCloudWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.EnderDragonWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PhantomWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WitherWatcher;
import me.libraryaddict.disguise.events.DisguiseEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		JavaPlugin disgPlugin = (JavaPlugin) getServer().getPluginManager().getPlugin("LibsDisguises");
		if (disgPlugin != null) onEvent(new PluginEnableEvent(disgPlugin));
	}

	@Override
	public void onDisable() {
		JavaPlugin disgPlugin = (JavaPlugin) getServer().getPluginManager().getPlugin("LibsDisguises");
		if (disgPlugin != null && disgPlugin.isEnabled()) {
			for (String cmd : disgPlugin.getDescription().getCommands().keySet()) {
				PluginCommand pc = disgPlugin.getCommand(cmd);
				pc.setTabCompleter(null);
				pc.setExecutor(this);
			}
			getLogger().warning("All LibsDisguises commands have been disabled for safety. Please restart the server to re-enable LibsDisguises.");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.GRAY + "That command is blocked.");
		return true;
	}

	private static float safeYMod(float f) {
		return Math.max(-256f, Math.min(256f, f));
	}

	@EventHandler
	public void onEvent(PluginEnableEvent event) {
		if (!event.getPlugin().getName().equals("LibsDisguises")) return;
		DisguiseConfig.setAutoUpdate(false);
		DisguiseConfig.setNotifyUpdate(false);
		JavaPlugin disgPlugin = (JavaPlugin) event.getPlugin();
		for (String cmd : event.getPlugin().getDescription().getCommands().keySet()) {
			if (cmd.equals("disguise") || cmd.equals("undisguise") || cmd.equals("disguisemodify") || cmd.equals("copydisguise") || cmd.equals("disguisehelp") || cmd.equals("disguiseviewself"))
				continue;
			PluginCommand pc = disgPlugin.getCommand(cmd);
			pc.setTabCompleter(null);
			pc.setExecutor(this);
		}
	}

	@EventHandler
	public void onEvent(PluginDisableEvent event) {
		if (!event.getPlugin().getName().equals("LibsDisguises")) return;
		int[] xd = new int[1];
		xd[0] = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
			JavaPlugin disgPlugin = (JavaPlugin) getServer().getPluginManager().getPlugin("LibsDisguises");
			if (disgPlugin != null && disgPlugin.isEnabled()) {
				for (String cmd : disgPlugin.getDescription().getCommands().keySet()) {
					PluginCommand pc = disgPlugin.getCommand(cmd);
					pc.setTabCompleter(null);
					pc.setExecutor(this);
				}
				getLogger().warning("All LibsDisguises commands have been disabled due to a reload. Please restart the server to re-enable LibsDisguises. (Not my fault for the scuffed DisguiseEvent listener behavior -ayunami2000)");
				getServer().getScheduler().cancelTask(xd[0]);
			}
		}, 0, 1);
	}

	@EventHandler
	public void onEvent(DisguiseEvent event) {
		event.setCancelled(true);
		if (event.getDisguise().getType() == DisguiseType.FISHING_HOOK) {
			event.getCommandSender().sendMessage(ChatColor.RED + "You cannot use Fishing Hook disguises");
			return;
		}
		String name = event.getDisguise().getWatcher().getCustomName();
		if (name != null) {
			int noColorLen = ChatColor.stripColor(name).length();
			// each color code counts as one char rather than two, for flexibility
			if (((name.length() - noColorLen) / 2) + noColorLen > 32) {
				event.getCommandSender().sendMessage(ChatColor.RED + "Your disguise name is too long");
				return;
			}
		}
		if (event.getDisguise().getWatcher() instanceof EnderDragonWatcher watcher && watcher.getPhase() == 7) watcher.setPhase(6);
		if (event.getDisguise().getWatcher() instanceof WitherWatcher watcher && watcher.getInvulnerability() > 2048) watcher.setInvulnerability(2048);
		if (event.getDisguise().isPlayerDisguise()) {
			PlayerDisguise playerDisguise = (PlayerDisguise) event.getDisguise();
			String targetName = playerDisguise.getName();
			String origName = event.getDisguised().getName();
			playerDisguise.setName(origName);
			playerDisguise.setNameVisible(true);
			playerDisguise.getWatcher().setNameYModifier(0);
			playerDisguise.setSkin(targetName);
			playerDisguise.setDisplayedInTab(false);
			playerDisguise.setTablistName(origName);
		}
		if (event.getDisguise().isHidePlayer()) event.getDisguise().setHidePlayer(false);
		if (event.getDisguise().getWatcher() instanceof AreaEffectCloudWatcher watcher) {
			if (watcher.getRadius() > 5) {
				watcher.setRadius(5);
			} else if (watcher.getRadius() < 0) {
				watcher.setRadius(0);
			}
		}
		event.getDisguise().getWatcher().setNameYModifier(safeYMod(event.getDisguise().getWatcher().getNameYModifier()));
		event.getDisguise().getWatcher().setYModifier(safeYMod(event.getDisguise().getWatcher().getYModifier()));
		if (event.getDisguise().getWatcher() instanceof SlimeWatcher watcher && watcher.getSize() > 10)
			watcher.setSize(10);
		if (event.getDisguise().getWatcher() instanceof PhantomWatcher watcher) {
			if (watcher.getSize() > 20) {
				watcher.setSize(20);
			} else if (watcher.getSize() < -36) {
				watcher.setSize(-36);
			}
		}
		event.setCancelled(false);
	}
}