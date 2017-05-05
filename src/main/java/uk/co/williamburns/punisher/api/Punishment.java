package uk.co.williamburns.punisher.api;

import java.util.UUID;

/**
 * A punishment that can be applied to players.
 */
public interface Punishment
{

	/**
	 * @return The unique id number of the punishment. <code>-1L</code> if the id is not set yet,
	 *         for example if the punishment is new and hasn't had an id generated in the database.
	 */
	int getId();

	/**
	 * @return The type of the punishment.
	 */
	PunishmentType getType();

	/**
	 * @return The Minecraft UUID of the punished player.
	 */
	UUID getUuid();

	/**
	 * @return The name of the player at the time of punishment, regardless as to whether they
	 *         have changed it now.
	 */
	String getPunishedName();

	/**
	 * @return The Minecraft UUID of the staff member that punished the player.
	 */
	UUID getStaffUuid();

	/**
	 * @return The reason for the punishment supplied by the staff member.
	 */
	String getReason();

	/**
	 * @return The time at which the punishment was carried out as an epoch timestamp.
	 */
	long getTimePunished();

	/**
	 * @return The duration of the punishment. <code>-1L</code> if it is permanent/unnecessary.
	 */
	long getDuration();

	/**
	 * @return Whether or not the punishment has been removed.
	 */
	boolean isRemoved();

	/**
	 * @return Whether or not this punishment is still active (not expired and not removed).
	 */
	boolean isActive();

}
