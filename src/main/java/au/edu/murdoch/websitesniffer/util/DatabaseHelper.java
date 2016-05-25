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
import au.edu.murdoch.websitesniffer.models.*;

import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseHelper
{
	private static final Logger log = Logger.getLogger( DatabaseHelper.class.getName() );
	private static final Properties properties = new Properties();
	private static DatabaseHelper mInstance;

	private DatabaseHelper() throws SQLException
	{
		createDatabase();

		properties.put( "busy_timeout", "66000" );
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
			 final ResultSet resultSet = statement.executeQuery( "SELECT "
					 + Domains.FIELD_ID + ','
					 + Domains.FIELD_URL
					 + " FROM "
					 + Domains.TABLE_NAME ) )
		{
			while( resultSet.next() )
			{
				domains.add( new Domain( resultSet.getInt( 1 ), resultSet.getString( 2 ) ) );
			}
		}

		return domains;
	}

	public void insertDomains( final List<String> domains ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Domains.TABLE_NAME + "("
					 + Domains.FIELD_URL
					 + ") VALUES (?)"
			 ) )
		{
			for( final String domain : domains )
			{
				statement.setObject( 1, domain );
				statement.addBatch();
			}

			connection.setAutoCommit( false );
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
	}

	Location getLocation( final String city, final String country ) throws SQLException
	{
		Location location = null;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( "SELECT "
					 + Locations.FIELD_ID + ','
					 + Locations.FIELD_CITY + ','
					 + Locations.FIELD_COUNTRY
					 + " FROM "
					 + Locations.TABLE_NAME
					 + " WHERE "
					 + Locations.FIELD_CITY + "=? AND "
					 + Locations.FIELD_COUNTRY + "=?"
					 + " LIMIT 1"
			 ) )
		{
			statement.setObject( 1, city );
			statement.setObject( 2, country );

			try( final ResultSet resultSet = statement.executeQuery() )
			{
				if( resultSet.next() )
				{
					final int rId = resultSet.getInt( 1 );
					final String rCity = resultSet.getString( 2 );
					final String rCountry = resultSet.getString( 3 );

					location = new Location( rId, rCity, rCountry );
				}
			}
		}

		return location;
	}

	void insertLocation( final String city, final String country, final double latitude, final double longitude ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Locations.TABLE_NAME + "("
					 + Locations.FIELD_CITY + ','
					 + Locations.FIELD_COUNTRY + ','
					 + Locations.FIELD_LATITUDE + ','
					 + Locations.FIELD_LONGITUDE
					 + ") VALUES (?,?,?,?)"
			 ) )
		{
			statement.setObject( 1, city );
			statement.setObject( 2, country );
			statement.setObject( 3, latitude );
			statement.setObject( 4, longitude );
			statement.executeUpdate();
		}
	}

	private int getNextTestNumber( final int domainID ) throws SQLException
	{
		int nextTestNumber = 1;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( "SELECT COALESCE(MAX(" + Tests.FIELD_TEST_NUMBER + "),0)+1"
					 + " FROM "
					 + Tests.TABLE_NAME
					 + " WHERE "
					 + Tests.FIELD_FK_DOMAIN_ID + "=?"
					 + " LIMIT 1"
			 ) )
		{
			statement.setObject( 1, domainID );

			try( final ResultSet resultSet = statement.executeQuery() )
			{
				if( resultSet.next() )
				{
					nextTestNumber = resultSet.getInt( 1 );
				}
			}
		}

		return nextTestNumber;
	}

	public void insertTest( final Test test ) throws SQLException, NullPointerException
	{
		final Domain domain = test.getDomain();
		if( domain == null )
		{
			throw new NullPointerException( "Domain in test is null!" );
		}

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Tests.TABLE_NAME + "("
					 + Tests.FIELD_FK_DOMAIN_ID + ','
					 + Tests.FIELD_TEST_NUMBER + ','
					 + Tests.FIELD_TIMESTAMP + ','
					 + Tests.FIELD_FK_LOCATION_ID + ','
					 + Tests.FIELD_FK_IPV4_TEST_ID + ','
					 + Tests.FIELD_FK_IPV6_TEST_ID
					 + ") VALUES (?,?,?,?,?,?)"
			 ) )
		{
			statement.setObject( 1, domain.getId(), Types.INTEGER );
			statement.setObject( 2, getNextTestNumber( domain.getId() ) );
			statement.setObject( 3, test.getTimestamp() );
			statement.setObject( 4, test.getUserLocation().getId() );

			connection.setAutoCommit( false );
			final Integer ipv4TestPK = insertIPv4Test( test.getIPv4Test() );
			statement.setObject( 6, ipv4TestPK );

			final Integer ipv6TestPK = insertIPv6Test( test.getIPv6Test() );
			statement.setObject( 6, ipv6TestPK );

			statement.executeUpdate();
			connection.setAutoCommit( true );
		}
	}

	private Integer insertIPv4Test( final IPv4Test ipv4Test ) throws SQLException
	{
		Integer PK = null;

		if( ipv4Test != null )
		{
			try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
				 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + IPv4Tests.TABLE_NAME + "("
						 + IPv4Tests.FIELD_ADDRESS + ','
						 + IPv4Tests.FIELD_ADDRESS_PING + ','
						 + IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + ','
						 + IPv4Tests.FIELD_HTTP_STATUS_CODE + ','
						 + IPv4Tests.FIELD_MX_ADDRESS + ','
						 + IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ','
						 + IPv4Tests.FIELD_HAS_WORKING_SMTP
						 + ") VALUES (?,?,?,?,?,?,?)"
				 ) )
			{
				final InetAddress address = ipv4Test.getAddress();
				statement.setObject( 1, address == null ? null : address.getHostAddress() );

				statement.setObject( 2, ipv4Test.getPing() );

				final Location ipv4AddressLocation = ipv4Test.getAddressLocation();
				statement.setObject( 3, ipv4AddressLocation == null ? null : ipv4AddressLocation.getId() );
				statement.setObject( 4, ipv4Test.getHttpStatusCode() );

				final InetAddress mxAddress = ipv4Test.getMxAddress();
				statement.setObject( 5, mxAddress == null ? null : mxAddress.getHostAddress() );

				final Location mxAddressLocation = ipv4Test.getMxAddressLocation();
				statement.setObject( 6, mxAddressLocation == null ? null : mxAddressLocation.getId() );

				statement.setObject( 7, ipv4Test.hasWorkingSMTP() );

				statement.executeUpdate();

				try( final Statement pkStatement = connection.createStatement();
					 final ResultSet resultSet = pkStatement.executeQuery( "SELECT last_insert_rowid();" ) )
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
			try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + Main.getOutputFilename(), properties );
				 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + IPv6Tests.TABLE_NAME + "("
						 + IPv6Tests.FIELD_ADDRESS + ','
						 + IPv6Tests.FIELD_ADDRESS_PING + ','
						 + IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + ','
						 + IPv6Tests.FIELD_HTTP_STATUS_CODE + ','
						 + IPv6Tests.FIELD_MX_ADDRESS + ','
						 + IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ','
						 + IPv6Tests.FIELD_HAS_WORKING_SMTP
						 + ") VALUES (?,?,?,?,?,?,?)"
				 ) )
			{
				final InetAddress address = ipv6Test.getAddress();
				statement.setObject( 1, address == null ? null : address.getHostAddress() );

				statement.setObject( 2, ipv6Test.getPing() );

				final Location ipv4AddressLocation = ipv6Test.getAddressLocation();
				statement.setObject( 3, ipv4AddressLocation == null ? null : ipv4AddressLocation.getId() );

				statement.setObject( 4, ipv6Test.getHttpStatusCode() );

				final InetAddress mxAddress = ipv6Test.getMxAddress();
				statement.setObject( 5, mxAddress == null ? null : mxAddress.getHostAddress() );

				final Location mxAddressLocation = ipv6Test.getMxAddressLocation();
				statement.setObject( 6, mxAddressLocation == null ? null : mxAddressLocation.getId() );

				statement.setObject( 7, ipv6Test.hasWorkingSMTP() );

				statement.executeUpdate();

				try( final Statement pkStatement = connection.createStatement();
					 final ResultSet resultSet = pkStatement.executeQuery( "SELECT last_insert_rowid()" ) )
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
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + Domains.TABLE_NAME + "("
					+ Domains.FIELD_ID + " INTEGER,"
					+ Domains.FIELD_URL + " TEXT NOT NULL,"
					+ "PRIMARY KEY(" + Domains.FIELD_ID + "),"
					+ "UNIQUE(" + Domains.FIELD_URL + ") ON CONFLICT IGNORE"
					+ ")"
			);
			statement.addBatch( "CREATE TABLE IF NOT EXISTS " + Locations.TABLE_NAME + "("
					+ Locations.FIELD_ID + " INTEGER,"
					+ Locations.FIELD_CITY + " TEXT,"
					+ Locations.FIELD_COUNTRY + " TEXT,"
					+ Locations.FIELD_LATITUDE + " REAL,"
					+ Locations.FIELD_LONGITUDE + " REAL,"
					+ "PRIMARY KEY(" + Locations.FIELD_ID + "),"
					+ "UNIQUE(" + Locations.FIELD_CITY + ',' + Locations.FIELD_COUNTRY + ") ON CONFLICT IGNORE"
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
					+ "PRIMARY KEY(" + Tests.FIELD_FK_DOMAIN_ID + ',' + Tests.FIELD_TEST_NUMBER + ")"
					+ ")"
			);

			statement.executeBatch();
		}
	}

	private static final class Domains
	{
		private static final String TABLE_NAME = "Domains";
		private static final String FIELD_ID = "DomainID";
		private static final String FIELD_URL = "URL";
	}

	private static final class Locations
	{
		private static final String TABLE_NAME = "Locations";
		private static final String FIELD_ID = "LocationID";
		private static final String FIELD_CITY = "City";
		private static final String FIELD_COUNTRY = "Country";
		private static final String FIELD_LATITUDE = "Latitude";
		private static final String FIELD_LONGITUDE = "Longitude";
	}

	private static final class Tests
	{
		private static final String TABLE_NAME = "Tests";
		private static final String FIELD_FK_DOMAIN_ID = "FK_DomainID";
		private static final String FIELD_TEST_NUMBER = "TestNumber";
		private static final String FIELD_TIMESTAMP = "Timestamp";
		private static final String FIELD_FK_LOCATION_ID = "FK_LocationID";
		private static final String FIELD_FK_IPV4_TEST_ID = "FK_IPv4TestID";
		private static final String FIELD_FK_IPV6_TEST_ID = "FK_IPv6TestID";
	}

	private static final class IPv4Tests
	{
		private static final String TABLE_NAME = "IPv4Tests";
		private static final String FIELD_ID = "IPv4TestID";
		private static final String FIELD_ADDRESS = "IPv4Address";
		private static final String FIELD_ADDRESS_PING = "IPv4AddressPing";
		private static final String FIELD_FK_ADDRESS_LOCATION_ID = "FK_IPv4AddressLocationID";
		private static final String FIELD_HTTP_STATUS_CODE = "IPv4HttpStatusCode";
		private static final String FIELD_MX_ADDRESS = "IPv4MXAddress";
		private static final String FIELD_FK_MX_ADDRESS_LOCATION_ID = "FK_IPv4MXAddressLocationID";
		private static final String FIELD_HAS_WORKING_SMTP = "IPv4HasWorkingSMTP";
	}

	private static final class IPv6Tests
	{
		private static final String TABLE_NAME = "IPv6Tests";
		private static final String FIELD_ID = "IPv6TestID";
		private static final String FIELD_ADDRESS = "IPv6Address";
		private static final String FIELD_ADDRESS_PING = "IPv6AddressPing";
		private static final String FIELD_FK_ADDRESS_LOCATION_ID = "FK_IPv6AddressLocationID";
		private static final String FIELD_HTTP_STATUS_CODE = "IPv6HttpStatusCode";
		private static final String FIELD_MX_ADDRESS = "IPv6MXAddress";
		private static final String FIELD_FK_MX_ADDRESS_LOCATION_ID = "FK_IPv6MXAddressLocationID";
		private static final String FIELD_HAS_WORKING_SMTP = "IPv6HasWorkingSMTP";
	}
}
