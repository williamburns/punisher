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
 * A punishment type that stops the player from chatting.
 */
public class MuteType implements PunishmentType
{
	@Override
	public String getId()
	{
		return "MUTE";
	}

	@Override
	public void onPunish(Punishment punishment)
	{
		// player may not be online, as staff can punish offline players
		Player player = Bukkit.getPlayer(punishment.getUuid());
		String playerName = player != null && player.isOnline() ? player.getName() : NameFetcher.getNameOf(punishment.getUuid());

		boolean permanent = punishment.getTimePunished() < 0;
		long duration = punishment.getDuration();

		// notify staff member
		// e.g. SomePlayer was muted for 10 minutes with reason: fly hacking.
		Player staffPlayer = Bukkit.getPlayer(punishment.getStaffUuid());
		String staffPeriod = permanent ? ChatColor.YELLOW + "permanently" : "for " +
				ChatColor.YELLOW + PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
		staffPlayer.sendMessage(MessageUtil.format(
				ChatColor.YELLOW + playerName + ChatColor.GRAY + " was muted " + staffPeriod +
						ChatColor.GRAY + " with reason: " + ChatColor.WHITE + punishment.getReason() +
						ChatColor.GRAY + "."
		));

		if (player != null && player.isOnline())
		{
			long remaining = duration - (System.currentTimeMillis() - punishment.getTimePunished());
			player.sendMessage(MessageUtil.format(
					"You have been muted for " + ChatColor.YELLOW
					+ PeriodFormat.getDefault().print(new Duration(remaining).toPeriod())
					+ ChatColor.GRAY + " with reason: "
					+ ChatColor.WHITE + punishment.getReason()
					+ ChatColor.GRAY + "."
			));
		}
	}

	@Override
	public String onJoin(Punishment punishment)
	{
		// joins are not affected by kicks
		return null;
	}

	@Override
	public boolean onChat(Punishment punishment)
	{
		String time = punishment.getDuration() < 0 ? "Forever" : PeriodFormat.getDefault()
				.print(new Duration(punishment.getDuration() - (System.currentTimeMillis() -
						punishment.getTimePunished())).toPeriod());

		Bukkit.getPlayer(punishment.getUuid()).sendMessage(MessageUtil.format(
				"You are muted by " + ChatColor.YELLOW + NameFetcher.getNameOf(punishment.getStaffUuid())
				+ ChatColor.GRAY + " for " + ChatColor.YELLOW + time
				+ ChatColor.GRAY + " with reason: " + ChatColor.WHITE + punishment.getReason()
				+ ChatColor.GRAY + "."
		));

		return false;
	}
}
