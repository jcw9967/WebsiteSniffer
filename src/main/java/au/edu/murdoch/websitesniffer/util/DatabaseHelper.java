/* 
 * Copyright (C) 2016 Jordan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.edu.murdoch.websitesniffer.util;

import au.edu.murdoch.websitesniffer.core.Main;
import au.edu.murdoch.websitesniffer.models.Database.Domains;
import au.edu.murdoch.websitesniffer.models.Database.Locations;
import au.edu.murdoch.websitesniffer.models.*;

import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static au.edu.murdoch.websitesniffer.util.SQLStatements.*;

public class DatabaseHelper
{
	private final Properties properties = new Properties();
	private static DatabaseHelper mInstance;

	private DatabaseHelper() throws SQLException
	{
		createDatabase();

		properties.put( "busy_timeout", "33000" );
	}

	public static DatabaseHelper getInstance() throws SQLException
	{
		if( mInstance == null )
		{
			mInstance = new DatabaseHelper();
		}

		return mInstance;
	}

	public List<Domain> getAllDomains() throws SQLException
	{
		final List<Domain> domains = new ArrayList<>();

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final Statement statement = connection.createStatement();
			 final ResultSet resultSet = statement.executeQuery( GET_ALL_DOMAINS ) )
		{
			while( resultSet.next() )
			{
				domains.add( new Domain( resultSet.getInt( Domains.FIELD_ID ), resultSet.getString( Domains.FIELD_URL ) ) );
			}
		}

		return domains;
	}

	public void insertDomains( final List<String> domains ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( INSERT_DOMAIN ) )
		{
			for( final String domain : domains )
			{
				setValues( statement,
						domain
				);

				statement.addBatch();
			}

			connection.setAutoCommit( false );
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
	}

	public Location getLocation( final String city, final String country ) throws SQLException
	{
		Location location = null;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( GET_LOCATION_FROM_COMPONENTS ) )
		{
			setValues( statement,
					city,
					country
			);

			try( final ResultSet resultSet = statement.executeQuery() )
			{
				if( resultSet.next() )
				{
					final int rId = resultSet.getInt( Locations.FIELD_ID );
					final String rCity = resultSet.getString( Locations.FIELD_CITY );
					final String rCountry = resultSet.getString( Locations.FIELD_COUNTRY );

					location = new Location( rId, rCity, rCountry );
				}
			}
		}

		return location;
	}

	public void insertLocation( final String city, final String country, final double latitude, final double longitude ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( INSERT_LOCATION ) )
		{
			setValues( statement,
					city,
					country,
					latitude,
					longitude
			);

			statement.executeUpdate();
		}
	}

	public void insertTest( final Test test ) throws SQLException, NullPointerException
	{
		final Domain domain = test.getDomain();
		if( domain == null )
		{
			throw new NullPointerException( "Domain in test is null!" );
		}

		final int domainId = domain.getId();
		final long timestamp = test.getTimestamp();
		final Integer userLocationId = test.getUserLocation().getId();
		final Integer ipv4TestId = insertIPv4Test( test.getIPv4Test() );
		final Integer ipv6TestId = insertIPv6Test( test.getIPv6Test() );

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( INSERT_TEST ) )
		{
			setValues( statement,
					domainId,
					timestamp,
					userLocationId,
					ipv4TestId,
					ipv6TestId
			);

			statement.executeUpdate();
		}
	}

	private Integer insertIPv4Test( final IPv4Test ipv4Test ) throws SQLException
	{
		Integer PK = null;

		if( ipv4Test != null )
		{
			final InetAddress address = ipv4Test.getAddress();
			final Integer ping = ipv4Test.getPing();
			final Location addressLocation = ipv4Test.getAddressLocation();
			final Integer httpStatusCode = ipv4Test.getHttpStatusCode();
			final InetAddress mxAddress = ipv4Test.getMxAddress();
			final Location mxAddressLocation = ipv4Test.getMxAddressLocation();
			final boolean hasWorkingSMTP = ipv4Test.hasWorkingSMTP();

			try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
				 final PreparedStatement statement = connection.prepareStatement( INSERT_IPV4TEST, Statement.RETURN_GENERATED_KEYS ) )
			{
				setValues( statement,
						address == null ? null : address.getHostAddress(),
						ping,
						addressLocation == null ? null : addressLocation.getId(),
						httpStatusCode,
						mxAddress == null ? null : mxAddress.getHostAddress(),
						mxAddressLocation == null ? null : mxAddressLocation.getId(),
						hasWorkingSMTP
				);

				statement.executeUpdate();

				try( final ResultSet resultSet = statement.getGeneratedKeys() )
				{
					if( resultSet.next() )
					{
						PK = resultSet.getInt( 1 );
					}
				}
			}
		}

		return PK;
	}

	private Integer insertIPv6Test( final IPv6Test ipv6Test ) throws SQLException
	{
		Integer PK = null;

		if( ipv6Test != null )
		{
			final InetAddress address = ipv6Test.getAddress();
			final Integer ping = ipv6Test.getPing();
			final Location addressLocation = ipv6Test.getAddressLocation();
			final Integer httpStatusCode = ipv6Test.getHttpStatusCode();
			final InetAddress mxAddress = ipv6Test.getMxAddress();
			final Location mxAddressLocation = ipv6Test.getMxAddressLocation();
			final boolean hasWorkingSMTP = ipv6Test.hasWorkingSMTP();

			try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
				 final PreparedStatement statement = connection.prepareStatement( INSERT_IPV6TEST, Statement.RETURN_GENERATED_KEYS ) )
			{
				setValues( statement,
						address == null ? null : address.getHostAddress(),
						ping,
						addressLocation == null ? null : addressLocation.getId(),
						httpStatusCode,
						mxAddress == null ? null : mxAddress.getHostAddress(),
						mxAddressLocation == null ? null : mxAddressLocation.getId(),
						hasWorkingSMTP
				);

				statement.executeUpdate();

				try( final ResultSet resultSet = statement.getGeneratedKeys() )
				{
					if( resultSet.next() )
					{
						PK = resultSet.getInt( 1 );
					}
				}
			}
		}

		return PK;
	}

	private void createDatabase() throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final Statement statement = connection.createStatement() )
		{
			statement.addBatch( CREATE_TABLE_DOMAINS );
			statement.addBatch( CREATE_TABLE_LOCATIONS );
			statement.addBatch( CREATE_TABLE_IPV4TESTS );
			statement.addBatch( CREATE_TABLE_IPV6TESTS );
			statement.addBatch( CREATE_TABLE_TESTS );

			connection.setAutoCommit( false );
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
	}

	private static void setValues( final PreparedStatement preparedStatement, final Object... values ) throws SQLException
	{
		for( int i = 0; i < values.length; i++ )
		{
			preparedStatement.setObject( i + 1, values[i] );
		}
	}
}
