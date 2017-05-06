package uk.co.williamburns.punisher;

import org.bukkit.plugin.java.JavaPlugin;

import uk.co.williamburns.punisher.base.PlayerPunishmentManager;

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
		new PlayerPunishmentManager(this);
	}

}
