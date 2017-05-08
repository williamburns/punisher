package uk.co.williamburns.punisher.api;

import java.util.Set;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Manages punishments for players on the server.
 * <p>
 * Through the use of various commands, staff members can punish players by banning, muting,
 * kicking them etc.
 * <p>
 * Punishments are loaded for a player on login, where they are sorted and applied accordingly.
 * <p>
 * When a player is punished, the punishment is saved to the database immediately, as to try and
 * alleviate issues such as the database not being available on logout, losing the punishment.
 */
public interface PunishmentManager
{

	/**
	 * Loads a player's punishments from the database.
	 * <p>
	 * As long as the UUID is valid, offline player punishments can be loaded also.
	 * <p>
	 * The data is stored locally in case staff members want to look up the punishment history of
	 * a player. Another call to this method with the same UUID will refresh the locally cached
	 * data.
	 *
	 * @param player The UUID of the target player.
	 * @return A set of punishments that the player has, if any, as a future.
	 */
	ListenableFuture<Set<Punishment>> loadPunishments(UUID player);

	/**
	 * Retrieves the cached punishments for a player.
	 * <p>
	 * The punishments must have been cached locally for this to return results, either when the
	 * player logged in, or with a separate call to {@link #loadPunishments(UUID)} first.
	 *
	 * @param player The UUID of the target player.
	 * @return A set of punishments cached locally for the player, or <code>null</code> if none.
	 */
	Set<Punishment> getCachedPunishments(UUID player);

	/**
	 * Checks whether or not a player has locally cached punishments.
	 * <p>
	 * Also returns <code>false</code> if the player has no punishment record.
	 *
	 * @param player The UUID of the target player.
	 * @return Whether or not the player has cached punishments.
	 */
	boolean hasCachedPunishments(UUID player);

	/**
	 * Saves a punishment to the database.
	 * <p>
	 * If it is new, the id field of the supplied punishment is updated locally with the
	 * auto-generated id from the database.
	 * <p>
	 * Punishments that already exist in the database are updated to match the supplied
	 * punishment object.
	 *
	 * @param punishment The punishment that is saved/updated in the database.
	 * @return Whether or not the operation was successful, as a future.
	 */
	ListenableFuture<Boolean> savePunishment(Punishment punishment);

	/**
	 * Retrieves a punishment type by id.
	 *
	 * @param id The id string of the punishment type.
	 * @return The punishment type, if any. <code>null</code> if else.
	 */
	PunishmentType getPunishmentType(String id);

	/**
	 * Retrieves a locally cached punishment by id.
	 *
	 * @param id The id of the punishment.
	 * @return The punishment with the supplied id, or <code>null</code> if none.
	 */
	Punishment getPunishment(int id);

}
