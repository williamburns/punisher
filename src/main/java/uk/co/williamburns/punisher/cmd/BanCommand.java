package uk.co.williamburns.punisher.cmd;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang.math.NumberUtils;
import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.base.PlayerPunishment;
import uk.co.williamburns.punisher.util.MessageUtil;
import uk.co.williamburns.punisher.util.NameFetcher;
import uk.co.williamburns.punisher.util.UUIDFetcher;

/**
 * A command that a staff member can use to issue a ban punishment.
 */
public class BanCommand implements CommandExecutor
{
	private final PunishmentManager manager;

	/**
	 * Class constructor.
	 *
	 * @param manager The punishment manager instance.
	 */
	public BanCommand(PunishmentManager manager)
	{
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			// block console
			sender.sendMessage(MessageUtil.error("Only players can use this command."));
			return false;
		}

		Player staff = (Player) sender;

		if (!staff.hasPermission("punisher.command.ban"))
		{
			sender.sendMessage(MessageUtil.error("You do not have permission to use this command."));
		}

		if (args.length < 3)
		{
			staff.sendMessage(MessageUtil.error(
					"Incorrect Usage: " + ChatColor.WHITE + "/ban <player> <hours | -1 (permanent)> <reason>"
			));
			return false;
		}

		// find target player online or offline
		Player targetPlayer = Bukkit.getPlayerExact(args[0]);
		UUID targetUuid = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
				.getUniqueId() : UUIDFetcher.getUUIDOf(args[0]);
		if (targetUuid == null)
		{
			staff.sendMessage(MessageUtil.error("That player can't be found online or offline."));
			return false;
		}

		String targetName = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
				.getName() : NameFetcher.getNameOf(targetUuid);

		// time length of punishment
		if (!NumberUtils.isNumber(args[1]))
		{
			staff.sendMessage(MessageUtil.error("That is not a valid number of hours, or -1 for " +
					"permanent."));
			return false;
		}

		long duration;
		int num = Integer.parseInt(args[1]);
		if (num == -1)
		{
			duration = -1L;
		}
		else
		{
			duration = num * 60 * 60 * 1000;
		}

		// build reason
		StringBuilder sb = new StringBuilder();
		for (int i = 2; i < args.length; i++)
		{
			sb.append(args[i]).append(" ");
		}
		String reason = sb.toString().trim();

		// punish player
		manager.savePunishment(new PlayerPunishment(
				manager.getPunishmentType("BAN"),
				targetUuid,
				targetName,
				staff.getUniqueId(),
				reason,
				System.currentTimeMillis(),
				duration,
				false
		));

		return false;
	}
}
