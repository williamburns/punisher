package uk.co.williamburns.punisher.util.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * A callable that makes a select query to the database.
 *
 * @param <V> The type of the result value.
 */
public class SelectQueryCallable<V> implements Callable<V>
{
	private final DataSource source;
	private final String query;
	private final Object[] args;
	private final Function<ResultSet, V> function;

	/**
	 * Class constructor.
	 *
	 * @param source The database source.
	 * @param query The select query that is run.
	 * @param args Any arguments applied to the prepared statement.
	 * @param function The function to which the {@link ResultSet} is applied.
	 */
	public SelectQueryCallable(DataSource source, String query, Object[] args, Function<ResultSet, V> function)
	{
		this.source = source;
		this.query = query;
		this.args = args;
		this.function = function;
	}

	@Override
	public V call() throws Exception
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		try
		{
			// instantiate connection and form statement
			connection = source.getConnection();
			if (connection == null)
			{
				throw new SQLException("Could not connect to database.");
			}

			statement = connection.prepareStatement(query);
			for (int i = 0; i < args.length; i++)
			{
				statement.setObject(i + 1, args[i]);
			}

			// apply results to function and return value
			results = statement.executeQuery();
			return function.apply(results);
		}
		catch (Exception e)
		{
			throw new SQLException(e);
		}
		finally
		{
			// cleanup connection etc.
			if (connection != null)
			{
				connection.close();
			}

			if (statement != null)
			{
				statement.close();
			}

			if (results != null)
			{
				results.close();
			}
		}
	}
}
