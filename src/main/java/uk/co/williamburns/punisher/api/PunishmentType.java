package uk.co.williamburns.punisher.api;

/**
 * A type of punishment.
 */
public interface PunishmentType
{

	/**
	 * @return A unique identifier for this punishment type, which is stored in the database.
	 */
	String getId();

	/**
	 * Called when a staff member punishes a player with this punishment type.
	 *
	 * @param punishment The punishment that has this punishment type instance.
	 */
	void onPunish(Punishment punishment);

	/**
	 * Called when a target player attempts to rejoin after being punished with this punishment
	 * type.
	 * <p>
	 * Does not occur if the supplied punishment is not active.
	 *
	 * @param punishment The punishment that has this punishment type instance.
	 * @return The message displayed to the player when they are disallowed from joining. If
	 *         <code>null</code> they are allowed to join normally.
	 */
	String onJoin(Punishment punishment);

	/**
	 * Called when a target player attempts to chat after being punished with this punishment type.
	 * <p>
	 * Does not occur if the supplied punishment is not active.
	 *
	 * @param punishment The punishment that has this punishment type instance.
	 * @return If <code>false</code>, the target player's message will be blocked from public
	 *         chat.
	 */
	boolean onChat(Punishment punishment);

}
