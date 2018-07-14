package uk.co.williamburns.punisher.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import uk.co.williamburns.punisher.Punisher;
import uk.co.williamburns.punisher.api.Punishment;
import uk.co.williamburns.punisher.api.PunishmentManager;
import uk.co.williamburns.punisher.api.PunishmentType;
import uk.co.williamburns.punisher.data.PunishmentDatabase;
import uk.co.williamburns.punisher.type.BanType;
import uk.co.williamburns.punisher.type.KickType;
import uk.co.williamburns.punisher.type.MuteType;

/**
 * A basic implementation of {@link PunishmentManager} that loads and saves player punishments on
 * login and on punishment.
 */
public class PlayerPunishmentManager implements PunishmentManager, Listener
{
	private final Punisher plugin;
	private final PunishmentDatabase database;

	private final List<PunishmentType> types;

	private final Map<UUID, Set<Punishment>> punishments;

	/**
	 * Class constructor.
	 *
	 * @param plugin The Punisher plugin instance.
	 */
	public PlayerPunishmentManager(Punisher plugin)
	{
		this.plugin = plugin;
		this.database = new PunishmentDatabase(this, plugin);

		this.types = new ArrayList<>();
		this.types.add(new BanType());
		this.types.add(new KickType());
		this.types.add(new MuteType());

		this.punishments = new ConcurrentHashMap<>();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event)
	{
		ListenableFuture<Set<Punishment>> fut = loadPunishments(event.getPlayer().getUniqueId());
		fut.addListener(() ->
		{
			try
			{
				// load punishments; they are cached locally also
				Set<Punishment> puns = fut.get();

				// for each punishment, call the join event and disallow login if necessary
				puns.stream().filter(Punishment::isActive).forEach(p ->
				{
					String r = p.getType().onJoin(p);
					if (r != null)
					{
						event.disallow(PlayerLoginEvent.Result.KICK_OTHER, r);
					}
				});
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}, r -> plugin.getServer().getScheduler().runTask(plugin, r));
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!punishments.containsKey(event.getPlayer().getUniqueId()))
		{
			return;
		}

		punishments.get(event.getPlayer().getUniqueId()).stream().filter(Punishment::isActive)
				.forEach(p ->
				{
					// cancel the chat if punishment type chat even returns false
					event.setCancelled(event.isCancelled() || !p.getType().onChat(p));
				});
	}

	@Override
	public ListenableFuture<Set<Punishment>> loadPunishments(UUID player)
	{
		SettableFuture<Set<Punishment>> fut = SettableFuture.create();

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
		{
			// load from database; cache locally
			Set<Punishment> puns = database.loadPunishments(player);
			punishments.put(player, puns);

			// complete future
			fut.set(puns);
		});

		return fut;
	}

	@Override
	public Set<Punishment> getCachedPunishments(UUID player)
	{
		return punishments.get(player);
	}

	@Override
	public boolean hasCachedPunishments(UUID player)
	{
		return getCachedPunishments(player) != null;
	}

	@Override
	public ListenableFuture<Boolean> savePunishment(Punishment punishment)
	{
		SettableFuture<Boolean> fut = SettableFuture.create();

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
		{
			// save in database
			fut.set(database.savePunishment(punishment));

			// call punishment received event
			plugin.getServer().getScheduler().runTask(plugin, () ->
					punishment.getType().onPunish(punishment));
		});

		return fut;
	}

	@Override
	public PunishmentType getPunishmentType(String id)
	{
		return types.stream().filter(t -> t.getId().equals(id)).findAny().orElse(null);
	}

	@Override
	public Punishment getPunishment(int id)
	{
		if (id < 0)
		{
			// ignore non-auto-generated ids
			return null;
		}

		return punishments.values().stream().flatMap(Collection::stream).filter(p -> p.getId() == id)
				.findFirst().orElse(null);
	}
}
