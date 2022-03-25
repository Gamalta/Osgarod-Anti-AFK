package fr.gamalta.osgarod.noafk.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.gamalta.osgarod.noafk.NoAFK;

public class NoAfkListener implements Listener {

	private NoAFK main;

	public NoAfkListener(NoAFK main) {

		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {

		Player player = event.getPlayer();
		main.afkTime.remove(player);
		main.afk.remove(player);
		main.afk.put(player, player.getLocation());

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {

		Player player = event.getPlayer();
		main.afkTime.remove(player);
		main.afk.remove(player);
		main.afk.put(player, player.getLocation());

	}
}
