package uk.co.williamburns.punisher.cmd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.ListenableFuture;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.util.MessageUtil;
import uk.co.williamburns.punisher.util.NameFetcher;
import uk.co.williamburns.punisher.util.UUIDFetcher;

/**
 * A command that lets a staff member view the punishment history of a target player.
 */
public class PunishmentHistoryCommand implements CommandExecutor
{
	private final PunishmentManager manager;

	/**
	 * Class constructor.
	 *
	 * @param manager The punishment manager instance.
	 */
	public PunishmentHistoryCommand(PunishmentManager manager)
	{
		this.manager = manager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!sender.hasPermission("punisher.command.history"))
		{
			sender.sendMessage(MessageUtil.error("You do not have permission to use this command."));
		}

		if (args.length != 1)
		{
			sender.sendMessage(MessageUtil.error(
					"Incorrect Usage: " + ChatColor.WHITE + "/phistory <player>"
			));
			return false;
		}

		Player targetPlayer = Bukkit.getPlayerExact(args[0]);
		UUID targetUuid = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
				.getUniqueId() : UUIDFetcher.getUUIDOf(args[0]);
		if (targetUuid == null)
		{
			sender.sendMessage(MessageUtil.error("That player can't be found online or offline."));
			return false;
		}

		String targetName = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
				.getName() : NameFetcher.getNameOf(targetUuid);

		sender.sendMessage(MessageUtil.format(
				"Loading " + ChatColor.YELLOW + targetName + ChatColor.GRAY + "'s history..."
		));

		// load punishment history from database ignoring local cache
		ListenableFuture<Set<Punishment>> future = manager.loadPunishments(targetUuid);
		future.addListener(() ->
		{
			try
			{
				Set<Punishment> puns = future.get();
				if (puns.isEmpty())
				{
					// no punishments to show
					sender.sendMessage(MessageUtil.error("That player has no punishments."));
					return;
				}

				puns.forEach(p ->
				{
					String duration = p.getDuration() == -1 ? "Permanent" : PeriodFormat
							.getDefault().print(new Duration(p.getDuration()).toPeriod());
					String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
							.format(new Date(p.getTimePunished()));

					sender.sendMessage(
							ChatColor.DARK_GRAY + "- #" + p.getId() + " " + ChatColor.YELLOW
							+ p.getType().getId() + ChatColor.GRAY + "(" + duration + ") by "
							+ ChatColor.YELLOW + NameFetcher.getNameOf(p.getStaffUuid())
							+ ChatColor.GRAY + " at " + ChatColor.YELLOW + date + ChatColor.GRAY
							+ " with reason: " + ChatColor.WHITE + p.getReason()
					);
				});
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
				sender.sendMessage(MessageUtil.error(
						"An error occurred whilst attempting to load that player's history."
				));
			}
		}, Runnable::run);

		return false;
	}
}
