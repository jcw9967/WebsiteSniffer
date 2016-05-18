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

import au.edu.murdoch.websitesniffer.models.Domain;
import au.edu.murdoch.websitesniffer.models.IPv4Test;
import au.edu.murdoch.websitesniffer.models.IPv6Test;
import au.edu.murdoch.websitesniffer.models.Location;
import au.edu.murdoch.websitesniffer.models.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHelper
{
	private static final String DATABASE_NAME = "sniffer.db";
	private static Connection connection;

	static
	{
		try
		{
			connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			createDatabase();
		}
		catch( final SQLException ex )
		{
			Logger.getLogger( DatabaseHelper.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public static List<Domain> getAllDomains() throws SQLException
	{
		final List<Domain> domains = new ArrayList<>();

		try( final Statement statement = connection.createStatement();
			 final ResultSet resultSet = statement.executeQuery( "SELECT * FROM " + Domains.TABLE_NAME ) )
		{
			while( resultSet.next() )
			{
				final int id = resultSet.getInt( 1 );
				final String url = resultSet.getString( 2 );

				final Domain domain = new Domain( id, url );
				domains.add( domain );
			}
		}

		return domains;
	}

	public static void insertDomains( final List<String> domains ) throws SQLException
	{
		try( final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Domains.TABLE_NAME + "("
				+ Domains.FIELD_URL
				+ ") VALUES ( ? )"
		) )
		{
			for( final String domain : domains )
			{
				statement.setString( 1, domain );
				statement.addBatch();
			}

			connection.setAutoCommit( false );
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
	}

	public static Location getLocation( final String city, final String country ) throws SQLException
	{
		Location location = null;

		try( final PreparedStatement statement = connection.prepareStatement( "SELECT * FROM " + Locations.TABLE_NAME + " WHERE "
				+ Locations.FIELD_CITY + "=? AND "
				+ Locations.FIELD_COUNTRY + "=? LIMIT 1"
		) )
		{
			statement.setString( 1, city );
			statement.setString( 2, country );

			try( final ResultSet resultSet = statement.executeQuery() )
			{
				if( resultSet.next() )
				{
					final int rId = resultSet.getInt( 1 );
					final String rCity = resultSet.getString( 2 );
					final String rCountry = resultSet.getString( 3 );
					final double rLatitude = resultSet.getDouble( 4 );
					final double rLongitude = resultSet.getDouble( 5 );

					location = new Location( rId, rCity, rCountry, rLatitude, rLongitude );
				}
			}
		}

		return location;
	}

	public static void insertLocation( final String city, final String country, final double latitude, final double longitude ) throws SQLException
	{

		try( final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Locations.TABLE_NAME + "("
				+ Locations.FIELD_CITY + ","
				+ Locations.FIELD_COUNTRY + ","
				+ Locations.FIELD_LATITUDE + ","
				+ Locations.FIELD_LONGITUDE
				+ ") VALUES ( ?, ?, ?, ? )"
		) )
		{
			statement.setString( 1, city );
			statement.setString( 2, country );
			statement.setDouble( 3, latitude );
			statement.setDouble( 4, longitude );
			statement.executeUpdate();
		}
	}

	private static int getNextTestNumber( final int domainID ) throws SQLException
	{
		int nextTestNumber;

		try( final PreparedStatement statement = connection.prepareStatement( "SELECT COALESCE( MAX( " + Tests.FIELD_TEST_NUMBER + " ), 0 ) + 1 FROM " + Tests.TABLE_NAME + " WHERE "
				+ Tests.FIELD_FK_DOMAIN_ID + "=?"
		) )
		{
			statement.setInt( 1, domainID );

			try( final ResultSet resultSet = statement.executeQuery() )
			{
				resultSet.next();
				nextTestNumber = resultSet.getInt( 1 );
			}
		}

		return nextTestNumber;
	}

	public static void insertTest( final Test test ) throws SQLException
	{
		try( final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Tests.TABLE_NAME + "("
				+ Tests.FIELD_FK_DOMAIN_ID + ","
				+ Tests.FIELD_TEST_NUMBER + ","
				+ Tests.FIELD_TIMESTAMP + ","
				+ Tests.FIELD_FK_LOCATION_ID + ","
				+ Tests.FIELD_FK_IPV4_TEST_ID + ","
				+ Tests.FIELD_FK_IPV6_TEST_ID
				+ ") VALUES ( ?, ?, ?, ?, ?, ? )"
		) )
		{
			statement.setObject( 1, test.getDomain().getId() );
			statement.setObject( 2, getNextTestNumber( test.getDomain().getId() ) );
			statement.setObject( 3, test.getTimestamp() );
			statement.setObject( 4, test.getUserLocation().getId() );

			final Integer ipv4TestPK = insertIPv4Test( test.getIPv4Test() );
			statement.setObject( 5, ipv4TestPK );

			final Integer ipv6TestPK = insertIPv6Test( test.getIPv6Test() );
			statement.setObject( 6, ipv6TestPK );

			statement.executeUpdate();
		}
	}

	private static Integer insertIPv4Test( final IPv4Test ipv4Test ) throws SQLException
	{
		Integer PK = null;
		
		if( ipv4Test != null )
		{
			try( final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + IPv4Tests.TABLE_NAME + "("
					+ IPv4Tests.FIELD_ADDRESS + ","
					+ IPv4Tests.FIELD_ADDRESS_PING + ","
					+ IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + ","
					+ IPv4Tests.FIELD_HTTP_STATUS_CODE + ","
					+ IPv4Tests.FIELD_MX_ADDRESS + ","
					+ IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ","
					+ IPv4Tests.FIELD_HAS_WORKING_SMTP
					+ ") VALUES ( ?, ?, ?, ?, ?, ?, ? )"
			) )
			{
				statement.setString( 1, ipv4Test.getAddress() );
				statement.setObject( 2, ipv4Test.getPing() );

				final Location ipv4AddressLocation = ipv4Test.getAddressLocation();
				statement.setObject( 3, ipv4AddressLocation == null ? null : ipv4AddressLocation.getId() );

				statement.setObject( 4, ipv4Test.getHttpStatusCode() );
				statement.setString( 5, ipv4Test.getMxAddress() );

				final Location mxAddressLocation = ipv4Test.getMxAddressLocation();
				statement.setObject( 6, mxAddressLocation == null ? null : mxAddressLocation.getId() );

				statement.setBoolean( 7, ipv4Test.hasWorkingSMTP() );
				statement.executeUpdate();

				try( final Statement pkStatement = connection.createStatement();
						final ResultSet resultSet = pkStatement.executeQuery( "SELECT last_insert_rowid();" ))
				{
					resultSet.next();
					PK = resultSet.getInt( 1 );
				}
			}
		}

		return PK;
	}

	private static Integer insertIPv6Test( final IPv6Test ipv6Test ) throws SQLException
	{
		Integer PK = null;

		if( ipv6Test != null )
		{
			try( final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + IPv6Tests.TABLE_NAME + "("
					+ IPv6Tests.FIELD_ADDRESS + ","
					+ IPv6Tests.FIELD_ADDRESS_PING + ","
					+ IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + ","
					+ IPv6Tests.FIELD_HTTP_STATUS_CODE + ","
					+ IPv6Tests.FIELD_MX_ADDRESS + ","
					+ IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ","
					+ IPv6Tests.FIELD_HAS_WORKING_SMTP
					+ ") VALUES ( ?, ?, ?, ?, ?, ?, ? )"
			) )
			{
				statement.setString( 1, ipv6Test.getAddress() );
				statement.setObject( 2, ipv6Test.getPing() );

				final Location ipv6AddressLocation = ipv6Test.getAddressLocation();
				statement.setObject( 3, ipv6AddressLocation == null ? null : ipv6AddressLocation.getId() );

				statement.setObject( 4, ipv6Test.getHttpStatusCode() );
				statement.setString( 5, ipv6Test.getMxAddress() );

				final Location mxAddressLocation = ipv6Test.getMxAddressLocation();
				statement.setObject( 6, mxAddressLocation == null ? null : mxAddressLocation.getId() );

				statement.setBoolean( 7, ipv6Test.hasWorkingSMTP() );
				statement.executeUpdate();

				try( final Statement pkStatement = connection.createStatement();
						final ResultSet resultSet = pkStatement.executeQuery( "SELECT last_insert_rowid();" ) )
				{
					resultSet.next();
					PK = resultSet.getInt( 1 );
				}
			}
		}

		return PK;
	}

	private static void createDatabase()
	{
		try( final Statement statement = connection.createStatement() )
		{
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + Domains.TABLE_NAME + "("
					+ Domains.FIELD_ID + " INTEGER,"
					+ Domains.FIELD_URL + " TEXT NOT NULL,"
					+ "PRIMARY KEY(" + Domains.FIELD_ID + "),"
					+ "UNIQUE(" + Domains.FIELD_URL + ") ON CONFLICT IGNORE"
					+ ")"
			);
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + Locations.TABLE_NAME + "("
					+ Locations.FIELD_ID + " INTEGER,"
					+ Locations.FIELD_CITY + " TEXT NOT NULL,"
					+ Locations.FIELD_COUNTRY + " TEXT NOT NULL,"
					+ Locations.FIELD_LATITUDE + " REAL NOT NULL,"
					+ Locations.FIELD_LONGITUDE + " REAL NOT NULL,"
					+ "PRIMARY KEY(" + Locations.FIELD_ID + "),"
					+ "UNIQUE(" + Locations.FIELD_CITY + "," + Locations.FIELD_COUNTRY + ") ON CONFLICT IGNORE"
					+ ")"
			);
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + IPv4Tests.TABLE_NAME + "("
					+ IPv4Tests.FIELD_ID + " INTEGER,"
					+ IPv4Tests.FIELD_ADDRESS + " TEXT,"
					+ IPv4Tests.FIELD_ADDRESS_PING + " INTEGER,"
					+ IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + " INTEGER,"
					+ IPv4Tests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
					+ IPv4Tests.FIELD_MX_ADDRESS + " TEXT,"
					+ IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + " INTEGER,"
					+ IPv4Tests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
					+ "FOREIGN KEY(" + IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "PRIMARY KEY(" + IPv4Tests.FIELD_ID + ")"
					+ ")"
			);
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + IPv6Tests.TABLE_NAME + "("
					+ IPv6Tests.FIELD_ID + " INTEGER,"
					+ IPv6Tests.FIELD_ADDRESS + " TEXT,"
					+ IPv6Tests.FIELD_ADDRESS_PING + " INTEGER,"
					+ IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + " INTEGER,"
					+ IPv6Tests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
					+ IPv6Tests.FIELD_MX_ADDRESS + " TEXT,"
					+ IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + " INTEGER,"
					+ IPv6Tests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
					+ "FOREIGN KEY(" + IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "PRIMARY KEY(" + IPv6Tests.FIELD_ID + " )"
					+ ")"
			);
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + Tests.TABLE_NAME + "("
					+ Tests.FIELD_FK_DOMAIN_ID + " INTEGER,"
					+ Tests.FIELD_TEST_NUMBER + " INTEGER,"
					+ Tests.FIELD_TIMESTAMP + " INTEGER NOT NULL,"
					+ Tests.FIELD_FK_LOCATION_ID + " INTEGER NOT NULL,"
					+ Tests.FIELD_FK_IPV4_TEST_ID + " INTEGER,"
					+ Tests.FIELD_FK_IPV6_TEST_ID + " INTEGER,"
					+ "FOREIGN KEY(" + Tests.FIELD_FK_DOMAIN_ID + ") REFERENCES " + Domains.TABLE_NAME + "(" + Domains.FIELD_ID + "),"
					+ "FOREIGN KEY(" + Tests.FIELD_FK_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "FOREIGN KEY(" + Tests.FIELD_FK_IPV4_TEST_ID + ") REFERENCES " + IPv4Tests.TABLE_NAME + "(" + IPv4Tests.FIELD_ID + "),"
					+ "FOREIGN KEY(" + Tests.FIELD_FK_IPV6_TEST_ID + ") REFERENCES " + IPv6Tests.TABLE_NAME + "(" + IPv6Tests.FIELD_ID + "),"
					+ "PRIMARY KEY(" + Tests.FIELD_FK_DOMAIN_ID + "," + Tests.FIELD_TEST_NUMBER + ")"
					+ ")"
			);

			connection.setAutoCommit( false );
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
		catch( final SQLException ex )
		{
			Logger.getLogger( DatabaseHelper.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			connection.close();
		}
		finally
		{
			super.finalize();
		}
	}

	public static enum Domains
	{
		TABLE_NAME( "Domains" ),
		FIELD_ID( "DomainID" ),
		FIELD_URL( "URL" );

		private final String mField;

		private Domains( final String field )
		{
			mField = field;
		}

		@Override
		public String toString()
		{
			return mField;
		}
	}

	public static enum Locations
	{
		TABLE_NAME( "Locations" ),
		FIELD_ID( "LocationID" ),
		FIELD_CITY( "City" ),
		FIELD_COUNTRY( "Country" ),
		FIELD_LATITUDE( "Latitude" ),
		FIELD_LONGITUDE( "Longitude" );

		private final String mField;

		private Locations( final String field )
		{
			mField = field;
		}

		@Override
		public String toString()
		{
			return mField;
		}
	}

	public static enum Tests
	{
		TABLE_NAME( "Tests" ),
		FIELD_FK_DOMAIN_ID( "FK_DomainID" ),
		FIELD_TEST_NUMBER( "TestNumber" ),
		FIELD_TIMESTAMP( "Timestamp" ),
		FIELD_FK_LOCATION_ID( "FK_LocationID" ),
		FIELD_FK_IPV4_TEST_ID( "FK_IPv4TestID" ),
		FIELD_FK_IPV6_TEST_ID( "FK_IPv6TestID" );

		private final String mField;

		private Tests( final String field )
		{
			mField = field;
		}

		@Override
		public String toString()
		{
			return mField;
		}
	}

	public static enum IPv4Tests
	{
		TABLE_NAME( "IPv4Tests" ),
		FIELD_ID( "IPv4TestID" ),
		FIELD_ADDRESS( "IPv4Address" ),
		FIELD_ADDRESS_PING( "IPv4AddressPing" ),
		FIELD_FK_ADDRESS_LOCATION_ID( "FK_IPv4AddressLocationID" ),
		FIELD_HTTP_STATUS_CODE( "IPv4HttpStatusCode" ),
		FIELD_MX_ADDRESS( "IPv4MXAddress" ),
		FIELD_FK_MX_ADDRESS_LOCATION_ID( "FK_IPv4MXAddressLocationID" ),
		FIELD_HAS_WORKING_SMTP( "IPv4HasWorkingSMTP" );

		private final String mField;

		private IPv4Tests( final String field )
		{
			mField = field;
		}

		@Override
		public String toString()
		{
			return mField;
		}
	}

	public static enum IPv6Tests
	{
		TABLE_NAME( "IPv6Tests" ),
		FIELD_ID( "IPv6TestID" ),
		FIELD_ADDRESS( "IPv6Address" ),
		FIELD_ADDRESS_PING( "IPv6AddressPing" ),
		FIELD_FK_ADDRESS_LOCATION_ID( "FK_IPv6AddressLocationID" ),
		FIELD_HTTP_STATUS_CODE( "IPv6HttpStatusCode" ),
		FIELD_MX_ADDRESS( "IPv6MXAddress" ),
		FIELD_FK_MX_ADDRESS_LOCATION_ID( "FK_IPv6MXAddressLocationID" ),
		FIELD_HAS_WORKING_SMTP( "IPv6HasWorkingSMTP" );

		private final String mField;

		private IPv6Tests( final String field )
		{
			mField = field;
		}

		@Override
		public String toString()
		{
			return mField;
		}
	}
}
