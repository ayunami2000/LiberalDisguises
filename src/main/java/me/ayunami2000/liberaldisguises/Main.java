package me.ayunami2000.liberaldisguises;

import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AreaEffectCloudWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PhantomWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.events.DisguiseEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		onEvent(new PluginEnableEvent(getServer().getPluginManager().getPlugin("LibsDisguises")));
		DisguiseConfig.setAutoUpdate(false);
		DisguiseConfig.setNotifyUpdate(false);
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
		JavaPlugin disgPlugin = (JavaPlugin) event.getPlugin();
		for (String cmd : event.getPlugin().getDescription().getCommands().keySet()) {
			if (cmd.equals("disguise") || cmd.equals("undisguise") || cmd.equals("disguisemodify") || cmd.equals("copydisguise") || cmd.equals("disguisehelp")) continue;
			disgPlugin.getCommand(cmd).setTabCompleter(null);
			disgPlugin.getCommand(cmd).setExecutor(this);
		}
	}

	@EventHandler
	public void onEvent(DisguiseEvent event) {
		event.setCancelled(true);
		if (event.getDisguise().isPlayerDisguise()) {
			PlayerDisguise playerDisguise = (PlayerDisguise) event.getDisguise();
			String targetName = playerDisguise.getName();
			String origName = event.getDisguised().getName();
			playerDisguise.setName(origName);
			playerDisguise.setNameVisible(true);
			playerDisguise.getWatcher().setNameYModifier(0);
			playerDisguise.setSkin(targetName);
		}
		if (event.getDisguise().getType() == DisguiseType.FISHING_HOOK) {
			event.getCommandSender().sendMessage(ChatColor.RED + "You cannot use Fishing Hook disguises");
			return;
		}
		if (event.getDisguise().isHidePlayer()) event.getDisguise().setHidePlayer(false);
		if (event.getDisguise().getWatcher() instanceof AreaEffectCloudWatcher watcher) {
			if (watcher.getRadius() > 5) {
				watcher.setRadius(5);
			} else if (watcher.getRadius() < 0) {
				watcher.setRadius(0);
			}
		}
		String name = event.getDisguise().getDisguiseName();
		int len = name.length();
		int noColorLen = ChatColor.stripColor(name).length();
		// each color code counts as one char rather than two, for flexibility
		if (((len - noColorLen) / 2) + noColorLen > 16) {
			event.getCommandSender().sendMessage(ChatColor.RED + "Your disguise name is too long");
			return;
		}
		event.getDisguise().getWatcher().setNameYModifier(safeYMod(event.getDisguise().getWatcher().getNameYModifier()));
		event.getDisguise().getWatcher().setYModifier(safeYMod(event.getDisguise().getWatcher().getYModifier()));
		if (event.getDisguise().getWatcher() instanceof SlimeWatcher watcher && watcher.getSize() > 10) watcher.setSize(10);
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