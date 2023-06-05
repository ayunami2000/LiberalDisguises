package me.ayunami2000.liberaldisguises;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PhantomWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.events.DisguiseEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
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
			/*
			if (event.getCommandSender() instanceof Player player) {
				player.sendMessage(ChatColor.BLUE + "You have thrown a rock, but you have also summoned a meteor!");
				ItemStack rock = new ItemStack(Material.STONE);
				ItemMeta meta = rock.getItemMeta();
				meta.setDisplayName(ChatColor.RESET + "" + ChatColor.BLUE + "Rock");
				rock.setItemMeta(meta);
				player.getInventory().addItem(rock);
			}
			*/
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