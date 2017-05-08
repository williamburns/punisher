package uk.co.williamburns.punisher.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentType;
import uk.co.williamburns.punisher.util.MessageUtil;

/**
 * A punishment that kicks the player from the server upon being received.
 */
public class KickType implements PunishmentType
{
	@Override
	public String getId()
	{
		return "KICK";
	}

	@Override
	public void onPunish(Punishment punishment)
	{
		// player will be online, as it's a kick
		Player player = Bukkit.getPlayer(punishment.getUuid());

		// notify staff member
		Player staff = Bukkit.getPlayer(punishment.getStaffUuid());
		staff.sendMessage(MessageUtil.format(
				ChatColor.YELLOW + player.getName()
				+ ChatColor.GRAY + " was kicked with reason: "
				+ ChatColor.WHITE + punishment.getReason()
				+ ChatColor.GRAY + "."
		));

		// kick
		player.kickPlayer(
				MessageUtil.PREFIX + "\n"
				+ ChatColor.GRAY + "You have been kicked from the server.\n\n"
				+ "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
				+ ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + staff.getName()
		);
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
		// chat is not affected by kicks
		return true;
	}
}
