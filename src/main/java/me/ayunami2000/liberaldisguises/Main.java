package me.ayunami2000.liberaldisguises;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PhantomWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.events.DisguiseEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		JavaPlugin disgPlugin = (JavaPlugin) getServer().getPluginManager().getPlugin("LibsDisguises");
		for (String cmd : disgPlugin.getDescription().getCommands().keySet()) {
			if (cmd.equals("disguise") || cmd.equals("undisguise") || cmd.equals("disguisemodify") || cmd.equals("copydisguise") || cmd.equals("disguisehelp")) continue;
			disgPlugin.getCommand(cmd).setTabCompleter(null);
			disgPlugin.getCommand(cmd).setExecutor(this);
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