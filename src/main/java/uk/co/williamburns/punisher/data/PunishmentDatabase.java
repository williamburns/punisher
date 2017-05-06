package uk.co.williamburns.punisher.data;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import uk.co.williamburns.punisher.Punisher;
import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.base.PlayerPunishment;
import uk.co.williamburns.punisher.util.DatabaseUtil;
import uk.co.williamburns.punisher.util.data.SelectQueryCallable;
import uk.co.williamburns.punisher.util.data.UpdateQueryCallable;

/**
 * Manages the loading and saving of punishments in the database.
 */
public class PunishmentDatabase
{
	private static final String LOAD_PUNISHMENTS = "SELECT * FROM punishments WHERE uuid = ?";

	private static final String SAVE_PUNISHMENT = "INSERT INTO punishments (uuid, " +
			"punishedName, type, staff, reason, timePunished, duration, removed) VALUES (?, ?, ?," +
			" ?, ?, ?, ?, ?)";

	private static final String UPDATE_PUNISHMENT = "UPDATE punishments SET removed = ? WHERE id " +
			"= ?";

	private final PunishmentManager manager;
	private final Punisher plugin;
	private final DataSource database;

	/**
	 * Class constructor.
	 *
	 * @param plugin The Punisher plugin instance.
	 */
	public PunishmentDatabase(PunishmentManager manager, Punisher plugin)
	{
		this.manager = manager;
		this.plugin = plugin;

		// generate data source and save
		this.database = DatabaseUtil.generateDataSource(plugin.getConfig());
	}

	/**
	 * Saves a punishment to the database.
	 * <p>
	 * If the id of the punishment is <code>-1</code> (it hasn't been saved before), then the
	 * supplied object is updated with the id auto-generated from the database.
	 * <p>
	 * This operation runs networking on hte calling thread, and therefore is not safe to run
	 * from any active gameplay threads.
	 *
	 * @param punishment The punishment that is saved.
	 * @return Whether or not the query was successful.
	 */
	public boolean savePunishment(Punishment punishment)
	{
		UpdateQueryCallable update;

		if (punishment.getId() == -1)
		{
			// new punishment
			update = new UpdateQueryCallable(
					database, SAVE_PUNISHMENT, new Object[]
					{
							punishment.getUuid().toString(),
							punishment.getPunishedName(),
							punishment.getType().getId(),
							punishment.getStaffUuid().toString(),
							punishment.getReason(),
							punishment.getTimePunished(),
							punishment.getDuration(),
							punishment.isRemoved()
					},
					results ->
					{
						try
						{
							// retrieve auto-generated id
							results.first();
							punishment.setId(results.getInt(1));
						}
						catch (SQLException e)
						{
							e.printStackTrace();
						}
					}
			);
		}
		else
		{
			// existing punishment
			update = new UpdateQueryCallable(
					database, UPDATE_PUNISHMENT, new Object[]
					{
							punishment.isRemoved(),
							punishment.getId()
					},
					results -> {}
			);
		}

		try
		{
			update.call();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Loads punishments for a player from the database.
	 * <p>
	 * This operation runs networking on the calling thread, and therefore is not safe to run
	 * from any active gameplay threads.
	 *
	 * @param uuid The UUID of the target player.
	 * @return A set of punishments for that player, if any. <code>null</code> if SQL fails.
	 */
	public Set<Punishment> loadPunishments(UUID uuid)
	{
		SelectQueryCallable<Set<Punishment>> select = new SelectQueryCallable<>(
				database,
				LOAD_PUNISHMENTS,
				new Object[]{ uuid.toString() },
				results ->
				{
					// handle result set of loaded punishments
					Set<Punishment> punishments = new HashSet<>();

					try
					{
						while (results.next())
						{
							Punishment p = new PlayerPunishment(
									results.getInt("id"),
									manager.getPunishmentType("type"),
									UUID.fromString(results.getString("uuid")),
									results.getString("punishedName"),
									UUID.fromString(results.getString("staff")),
									results.getString("reason"),
									results.getLong("timePunished"),
									results.getLong("duration"),
									results.getBoolean("removed")
							);

							punishments.add(p);
						}
					}
					catch (SQLException e)
					{
						e.printStackTrace();
						return null;
					}

					return punishments;
				}
		);

		try
		{
			return select.call();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
