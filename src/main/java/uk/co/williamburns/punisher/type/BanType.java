package uk.co.williamburns.punisher.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentType;
import uk.co.williamburns.punisher.util.MessageUtil;
import uk.co.williamburns.punisher.util.NameFetcher;

/**
 * A punishment type that will kick a player upon receiving the punishment, and disallow them from
 * joining until the duration of the punishment is completed.
 */
public class BanType implements PunishmentType
{

	@Override
	public void onPunish(Punishment punishment)
	{
		// player may not be online, as staff can punish offline players
		Player player = Bukkit.getPlayer(punishment.getUuid());
		String playerName = player != null && player.isOnline() ? player.getName() : NameFetcher.getNameOf(punishment.getUuid());

		boolean permanent = punishment.getTimePunished() < 0;
		long duration = punishment.getDuration();

		// notify staff member
		// e.g. SomePlayer was banned for 10 minutes with reason: fly hacking.
		Player staffPlayer = Bukkit.getPlayer(punishment.getStaffUuid());
		String staffPeriod = permanent ? ChatColor.YELLOW + "permanently" : "for " +
				ChatColor.YELLOW + PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
		staffPlayer.sendMessage(MessageUtil.format(
				ChatColor.YELLOW + playerName + ChatColor.GRAY + " was banned " + staffPeriod +
						ChatColor.GRAY + " with reason: " + ChatColor.WHITE + punishment.getReason() +
						ChatColor.GRAY + "."
		));

		if (player != null && player.isOnline())
		{
			long remaining = duration - (System.currentTimeMillis() - punishment.getTimePunished());
			player.kickPlayer(
					MessageUtil.PREFIX + "\n"
					+ ChatColor.GRAY + "You have been banned from the server.\n\n"
					+ "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
					+ ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + staffPlayer.getName() + "\n"
					+ ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + (permanent ?
							"Forever" : PeriodFormat.getDefault().print(new Duration(remaining).toPeriod()))
			);
		}
	}

	@Override
	public String onJoin(Punishment punishment)
	{
		String time = punishment.getDuration() < 0 ? "Forever" : PeriodFormat.getDefault()
				.print(new Duration(punishment.getDuration() - (System.currentTimeMillis() -
						punishment.getTimePunished())).toPeriod());

		return MessageUtil.PREFIX + "\n"
				+ ChatColor.GRAY + "You are banned from the server.\n\n"
				+ "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
				+ ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + NameFetcher.getNameOf(punishment.getStaffUuid()) + "\n"
				+ ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + time;
	}

	@Override
	public boolean onChat(Punishment punishment)
	{
		// chat is not affected by bans
		return true;
	}

}
