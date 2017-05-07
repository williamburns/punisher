package uk.co.williamburns.punisher;

import org.bukkit.plugin.java.JavaPlugin;

import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.base.PlayerPunishmentManager;
import uk.co.williamburns.punisher.cmd.BanCommand;
import uk.co.williamburns.punisher.cmd.KickCommand;
import uk.co.williamburns.punisher.cmd.MuteCommand;
import uk.co.williamburns.punisher.cmd.PunishmentHistoryCommand;

/**
 * The plugin bootstrap class for Punisher.
 */
public class Punisher extends JavaPlugin
{

	@Override
	public void onEnable()
	{
		// save default config file to data folder
		saveDefaultConfig();

		// instantiate punishment manager
		PunishmentManager manager = new PlayerPunishmentManager(this);

		// commands
		getCommand("kick").setExecutor(new KickCommand(manager));
		getCommand("ban").setExecutor(new BanCommand(manager));
		getCommand("ban").setExecutor(new MuteCommand(manager));
		getCommand("phistory").setExecutor(new PunishmentHistoryCommand(manager));
	}

}
