package uk.co.williamburns.punisher.util.data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * A callable that makes an update query to the database.
 */
public class UpdateQueryCallable implements Callable<Void>
{
	private final DataSource source;
	private final String query;
	private final Object[] args;
	private final Consumer<ResultSet> consumer;

	/**
	 * Class constructor.
	 *
	 * @param source The database source.
	 * @param query The select query that is run.
	 * @param args Any arguments applied to the prepared statement.
	 * @param consumer The consumer which takes the {@link ResultSet} object.
	 */
	public UpdateQueryCallable(DataSource source, String query, Object[] args, Consumer<ResultSet> consumer)
	{
		this.source = source;
		this.query = query;
		this.args = args;
		this.consumer = consumer;
	}

	@Override
	public Void call() throws Exception
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

			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < args.length; i++)
			{
				statement.setObject(i + 1, args[i]);
			}

			// pass to consumer and return
			results = statement.executeQuery();
			consumer.accept(results);
			return null;
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
