package uk.co.williamburns.punisher;

import org.bukkit.plugin.java.JavaPlugin;

import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.base.PlayerPunishmentManager;
import uk.co.williamburns.punisher.cmd.KickCommand;

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
	}

}
