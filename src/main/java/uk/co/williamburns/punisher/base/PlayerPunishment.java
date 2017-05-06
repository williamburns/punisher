package uk.co.williamburns.punisher.base;

import java.util.UUID;

import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentType;

/**
 * A basic implementation of a punishment for a player.
 * <p>
 * Any fields set on a punishment object are only locally cached, and must be updated in the
 * database for them to be saved permanently.
 */
public class PlayerPunishment implements Punishment
{
	private int id; // non-final so it can be set after db has auto-generated id

	private final PunishmentType type;
	private final UUID uuid;
	private final String punishedName;
	private final UUID staffUuid;
	private final String reason;
	private final long timePunished;
	private final long duration;

	private boolean removed;

	/**
	 * Class constructor.
	 *
	 * @param id The id of this punishment. <code>-1</code> if there is no auto-generated id
	 *           assigned yet.
	 * @param type The type of punishment.
	 * @param uuid The UUID of the target player.
	 * @param punishedName The name of the target player at time of punishment.
	 * @param staffUuid The UUID of the staff member.
	 * @param reason The reason for this punishment.
	 * @param timePunished The time this punishment was made.
	 * @param duration The duration of this punishment. <code>-1L</code> if permanent.
	 * @param removed Whether or not this punishment has been removed.
	 */
	public PlayerPunishment(int id, PunishmentType type, UUID uuid, String punishedName, UUID
			staffUuid, String reason, long timePunished, long duration, boolean removed)
	{
		this.id = id;
		this.type = type;
		this.uuid = uuid;
		this.punishedName = punishedName;
		this.staffUuid = staffUuid;
		this.reason = reason;
		this.timePunished = timePunished;
		this.duration = duration;
		this.removed = removed;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public PunishmentType getType()
	{
		return type;
	}

	@Override
	public UUID getUuid()
	{
		return uuid;
	}

	@Override
	public String getPunishedName()
	{
		return punishedName;
	}

	@Override
	public UUID getStaffUuid()
	{
		return staffUuid;
	}

	@Override
	public String getReason()
	{
		return reason;
	}

	@Override
	public long getTimePunished()
	{
		return timePunished;
	}

	@Override
	public long getDuration()
	{
		return duration;
	}

	@Override
	public boolean isRemoved()
	{
		return removed;
	}

	@Override
	public boolean isActive()
	{
		return !isRemoved() && System.currentTimeMillis() < (getTimePunished() + getDuration());
	}

	/**
	 * Sets the id of this punishment. This should only be used to assign an auto-generated id
	 * from the database.
	 *
	 * @param id The id of this punishment.
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Sets whether or not this punishment has been removed.
	 * <p>
	 * This punishment must be updated in the database for the change to be saved.
	 *
	 * @param removed Whether or not this punishment has been removed.
	 */
	public void setRemoved(boolean removed)
	{
		this.removed = removed;
	}
}
