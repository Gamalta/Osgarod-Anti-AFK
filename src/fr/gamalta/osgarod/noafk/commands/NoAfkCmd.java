package fr.gamalta.osgarod.noafk.commands;

import fr.gamalta.lib.message.Message;
import fr.gamalta.osgarod.noafk.NoAFK;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NoAfkCmd implements CommandExecutor {

	private NoAFK main;

	public NoAfkCmd(NoAFK main) {

		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (main.kick.contains(player)) {

				main.kick.remove(player);

				player.spigot().sendMessage(new Message(main, main.settingsCFG, "RemoveAFK").create());

			} else {

				player.spigot().sendMessage(new Message(main, main.settingsCFG, "NoAFK").create());
			}

			main.afkTime.remove(player);
			main.afk.remove(player);
			main.afk.put(player, player.getLocation());

		} else {

			sender.sendMessage("Console can't be AFK!");

		}

		return false;
	}

}
