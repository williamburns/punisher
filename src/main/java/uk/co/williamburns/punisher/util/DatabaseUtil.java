package uk.co.williamburns.punisher.util;

import javax.sql.DataSource;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * A set of utility methods for database handling.
 */
public class DatabaseUtil
{

	/**
	 * Generates a {@link DataSource} using configuration details from the config.yml file in
	 * this plugin's data folder.
	 *
	 * @param config The config from the plugin.
	 * @return A DBCP DataSource generated with Apache's utility.
	 */
	public static DataSource generateDataSource(FileConfiguration config)
	{
		ConfigurationSection cs = config.getConfigurationSection("database");
		BasicDataSource bds = new BasicDataSource();

		bds.setDriverClassName("com.mysql.jdbc.Driver");
		bds.setUrl(
				"jdbc:mysql://" + cs.getString("host") + ":" + cs.getInt("port") + "/" + cs.getString("database")
		);
		bds.setUsername(cs.getString("username"));
		bds.setPassword(cs.getString("password"));

		return bds;
	}

}
