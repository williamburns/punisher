package uk.co.williamburns.punisher.util;

import org.bukkit.ChatColor;

/**
 * A set of utility methods for displaying messages.
 * <p>
 * As a general standard for each message, the body content should be gray, and any elements
 * highlighted in yellow or white.
 */
public class MessageUtil
{

	// prefix for all messages
	public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Punisher" +
			ChatColor.DARK_GRAY + "]";

	/**
	 * Formats a string to the standardised message style.
	 *
	 * @param content The content of the message.
	 * @return A formatted/stylised message as a string.
	 */
	public static final String format(String content)
	{
		return PREFIX + " " + ChatColor.GRAY + content;
	}

	/**
	 * Formats an error string to the standardised message style.
	 *
	 * @param content The content of the error message.
	 * @return A formatted/stylised error message as a string.
	 */
	public static final String error(String content)
	{
		return PREFIX + " " + ChatColor.RED + content;
	}

}
