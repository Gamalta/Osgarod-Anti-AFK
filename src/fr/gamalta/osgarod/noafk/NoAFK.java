package fr.gamalta.osgarod.noafk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.gamalta.lib.RedLib;
import fr.gamalta.lib.config.Configuration;
import fr.gamalta.lib.message.Message;
import fr.gamalta.osgarod.noafk.commands.NoAfkCmd;
import fr.gamalta.osgarod.noafk.listeners.NoAfkListener;
import net.md_5.bungee.api.chat.BaseComponent;

public class NoAFK extends JavaPlugin {

	public Configuration settingsCFG = new Configuration(this, "Anti-Afk", "Settings");
	public HashMap<Player, Location> afk = new HashMap<>();
	public HashMap<Player, Long> afkTime = new HashMap<>();
	public ArrayList<Player> kick = new ArrayList<>();
	private RedLib lib = new RedLib();

	@Override
	public void onEnable() {

		Bukkit.getPluginManager().registerEvents(new NoAfkListener(this), this);
		getCommand("noafk").setExecutor(new NoAfkCmd(this));
		init();

	}

	@Override
	public void onDisable() {

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");

		out.writeUTF("lobby");

		for (Player player : Bukkit.getOnlinePlayers()) {

			player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		}
	}

	private void init() {

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

			for (Player player : Bukkit.getOnlinePlayers()) {

				if (!player.hasPermission(settingsCFG.getString("BypassPermission"))) {

					if (afk.containsKey(player)) {

						if (settingsCFG.getInt("Radius") + 1 >= afk.get(player).distance(player.getLocation())) {

							long time = settingsCFG.getLong("Delay.Refresh");

							if (afkTime.containsKey(player)) {

								time = +afkTime.get(player);
								afkTime.remove(player);
							}

							afkTime.put(player, time);

						} else {

							afk.remove(player);
							afk.put(player, player.getLocation());
						}
					} else {

						afk.put(player, player.getLocation());
					}
				}
			}

			ArrayList<Player> alert = new ArrayList<>();

			for (Map.Entry<Player, Long> entry : afkTime.entrySet()) {

				Player player = entry.getKey();

				if (player != null) {
					if (entry.getValue() / 60 >= settingsCFG.getLong("Delay.Afk")) {

						alert.add(player);
					}
				} else {

					afk.remove(entry.getKey());
					afkTime.remove(entry.getKey());

				}
			}

			if (!alert.isEmpty()) {

				BaseComponent[] message = new Message(this, settingsCFG, "Alert.Message").create();

				lib.sendTitle(alert, settingsCFG, "Alert.Title");
				lib.playSound(alert, settingsCFG, "Alert.Sound");

				for (Player player : alert) {

					player.spigot().sendMessage(message);
					kick.add(player);
				}
			}

			Bukkit.getScheduler().runTaskLater(this, () -> {

				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF("lobby");
				String players = "";
				int i = 0;

				BaseComponent[] message = new Message(this, settingsCFG, "Kick.Message").create();

				for (Player player : kick) {

					players += player.getName() + ", ";
					player.spigot().sendMessage(message);
					player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
					i++;
				}

				String substring = players.substring(0, players.length() - 2);
				BaseComponent[] messageAll = new Message(this, settingsCFG, "Kick.ActionBar." + (i == 1 ? "Singular" : "Plural")).replace("%player%", substring).replace("%players%", substring).create();

				for (Player player : Bukkit.getOnlinePlayers()) {

					player.spigot().sendMessage(messageAll);
				}

			}, settingsCFG.getLong("Delay.Kick") * 20);

		}, 1, settingsCFG.getLong("Delay.Refresh") * 20);
	}
}
