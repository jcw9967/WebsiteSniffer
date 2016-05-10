package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Domain;
import models.IPTest;
import models.IPTest.Type;
import models.Location;

public class DatabaseHelper
{
	private static final String DATABASE_NAME = "sniffer.db";

	private static DatabaseHelper databaseHelper;

	private DatabaseHelper()
	{
	}

	public static DatabaseHelper getInstance() throws SQLException
	{
		if( databaseHelper == null )
		{
			databaseHelper = new DatabaseHelper();
			createDatabase();
		}

		return databaseHelper;
	}

	public int count( final String tableName, final String whereStatement ) throws SQLException
	{
		int count = 0;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final Statement statement = connection.createStatement() )
		{
			final String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + whereStatement;

			final ResultSet resultSet = statement.executeQuery( query );
			if( resultSet.next() );
			{
				count = resultSet.getInt( 1 );
			}
		}

		return count;
	}

	public void customUpdate( final String update ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final Statement statement = connection.createStatement() )
		{
			statement.executeUpdate( update );
		}
	}

	public List<Domain> getAllDomains() throws SQLException
	{
		final List<Domain> domains;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final Statement statement = connection.createStatement();
			 final ResultSet resultSet = statement.executeQuery( "SELECT * FROM " + Domains.TABLE_NAME ) )
		{
			domains = new ArrayList<>();
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

	public void insertDomains( final List<String> domains ) throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Domains.TABLE_NAME + "("
					 + Domains.FIELD_URL
					 + ") VALUES ( ? )"
			 ) )
		{
			connection.setAutoCommit( false );
			for( final String domain : domains )
			{
				statement.setString( 1, domain );
				statement.addBatch();
			}
			statement.executeBatch();
			connection.setAutoCommit( true );
		}
	}

	public Location getLocation( final String city, final String country ) throws SQLException
	{
		Location location = null;

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final PreparedStatement statement = connection.prepareStatement( "SELECT * FROM " + Locations.TABLE_NAME + " WHERE "
					 + Locations.FIELD_CITY + "=? AND "
					 + Locations.FIELD_COUNTRY + "=? LIMIT 1"
			 ) )
		{
			statement.setString( 1, city );
			statement.setString( 2, country );

			final ResultSet resultSet = statement.executeQuery();
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

		return location;
	}

	public void insertLocation( final String city, final String country, final double latitude, final double longitude ) throws SQLException
	{

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + Locations.TABLE_NAME + "("
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

	public void insertTest( final IPTest ipTest, final Type type ) throws SQLException
	{
		final String table = type == Type.IPv4 ? IPTests.TABLE_NAME_IPV4.toString() : IPTests.TABLE_NAME_IPV6.toString();

		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final PreparedStatement statement = connection.prepareStatement( "INSERT INTO " + table + "("
					 + IPTests.FIELD_DOMAIN_ID + ","
					 + IPTests.FIELD_TIMESTAMP + ","
					 + IPTests.FIELD_ADDRESS + ","
					 + IPTests.FIELD_ADDRESS_PING + ","
					 + IPTests.FIELD_ADDRESS_LOCATION + ","
					 + IPTests.FIELD_HTTP_STATUS_CODE + ","
					 + IPTests.FIELD_MX_ADDRESS + ","
					 + IPTests.FIELD_MX_ADDRESS_LOCATION + ","
					 + IPTests.FIELD_HAS_WORKING_SMTP
					 + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )"
			 ) )
		{
			final Domain domain = ipTest.getDomain();
			statement.setObject( 1, domain == null ? null : domain.getId() );

			statement.setLong( 2, ipTest.getTimestamp() );
			statement.setString( 3, ipTest.getAddress() );
			statement.setObject( 4, ipTest.getPing() );

			final Location ipv6AddressLocation = ipTest.getAddressLocation();
			statement.setObject( 5, ipv6AddressLocation == null ? null : ipv6AddressLocation.getId() );

			statement.setObject( 6, ipTest.getHttpStatusCode() );
			statement.setString( 7, ipTest.getMxAddress() );

			final Location mxAddressLocation = ipTest.getMxAddressLocation();
			statement.setObject( 8, mxAddressLocation == null ? null : mxAddressLocation.getId() );

			statement.setBoolean( 9, ipTest.hasWorkingSMTP() );
			statement.executeUpdate();
		}
	}

	private static void createDatabase() throws SQLException
	{
		try( final Connection connection = DriverManager.getConnection( "jdbc:sqlite:" + DATABASE_NAME );
			 final Statement statement = connection.createStatement() )
		{
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS " + Domains.TABLE_NAME + "("
					+ Domains.FIELD_ID + " INTEGER,"
					+ Domains.FIELD_URL + " TEXT NOT NULL,"
					+ "PRIMARY KEY(" + Domains.FIELD_ID + "),"
					+ "UNIQUE(" + Domains.FIELD_URL + ") ON CONFLICT IGNORE"
					+ ")"
			);
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS " + Locations.TABLE_NAME + "("
					+ Locations.FIELD_ID + " INTEGER,"
					+ Locations.FIELD_CITY + " TEXT NOT NULL,"
					+ Locations.FIELD_COUNTRY + " TEXT NOT NULL,"
					+ Locations.FIELD_LATITUDE + " REAL NOT NULL,"
					+ Locations.FIELD_LONGITUDE + " REAL NOT NULL,"
					+ "PRIMARY KEY(" + Locations.FIELD_ID + "),"
					+ "UNIQUE(" + Locations.FIELD_CITY + "," + Locations.FIELD_COUNTRY + ") ON CONFLICT REPLACE"
					+ ")"
			);
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS " + IPTests.TABLE_NAME_IPV4 + "("
					+ IPTests.FIELD_ID + " INTEGER,"
					+ IPTests.FIELD_DOMAIN_ID + " INTEGER NOT NULL,"
					+ IPTests.FIELD_TIMESTAMP + " INTEGER NOT NULL,"
					+ IPTests.FIELD_ADDRESS + " TEXT,"
					+ IPTests.FIELD_ADDRESS_PING + " INTEGER,"
					+ IPTests.FIELD_ADDRESS_LOCATION + " INTEGER,"
					+ IPTests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
					+ IPTests.FIELD_MX_ADDRESS + " TEXT,"
					+ IPTests.FIELD_MX_ADDRESS_LOCATION + " INTEGER,"
					+ IPTests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
					+ "FOREIGN KEY(" + IPTests.FIELD_DOMAIN_ID + ") REFERENCES " + Domains.TABLE_NAME + "(" + Domains.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPTests.FIELD_ADDRESS_LOCATION + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPTests.FIELD_MX_ADDRESS_LOCATION + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "PRIMARY KEY(" + IPTests.FIELD_ID + ")"
					+ ")"
			);
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS " + IPTests.TABLE_NAME_IPV6 + "("
					+ IPTests.FIELD_ID + " INTEGER,"
					+ IPTests.FIELD_DOMAIN_ID + " INTEGER NOT NULL,"
					+ IPTests.FIELD_TIMESTAMP + " INTEGER NOT NULL,"
					+ IPTests.FIELD_ADDRESS + " TEXT,"
					+ IPTests.FIELD_ADDRESS_PING + " INTEGER,"
					+ IPTests.FIELD_ADDRESS_LOCATION + " INTEGER,"
					+ IPTests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
					+ IPTests.FIELD_MX_ADDRESS + " TEXT,"
					+ IPTests.FIELD_MX_ADDRESS_LOCATION + " INTEGER,"
					+ IPTests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
					+ "FOREIGN KEY(" + IPTests.FIELD_DOMAIN_ID + ") REFERENCES " + Domains.TABLE_NAME + "(" + Domains.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPTests.FIELD_ADDRESS_LOCATION + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "FOREIGN KEY(" + IPTests.FIELD_MX_ADDRESS_LOCATION + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
					+ "PRIMARY KEY(" + IPTests.FIELD_ID + " )"
					+ ")"
			);
		}
	}

	public enum Domains
	{
		TABLE_NAME( "domains" ),
		FIELD_ID( "id" ),
		FIELD_URL( "url" );

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

	public enum Locations
	{
		TABLE_NAME( "locations" ),
		FIELD_ID( "id" ),
		FIELD_CITY( "city" ),
		FIELD_COUNTRY( "country" ),
		FIELD_LATITUDE( "latitude" ),
		FIELD_LONGITUDE( "longitude" );

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

	public enum IPTests
	{
		TABLE_NAME_IPV4( "ipv4_tests" ),
		TABLE_NAME_IPV6( "ipv6_tests" ),
		FIELD_ID( "id" ),
		FIELD_DOMAIN_ID( "domain_id" ),
		FIELD_TIMESTAMP( "timestamp" ),
		FIELD_ADDRESS( "address" ),
		FIELD_ADDRESS_PING( "address_ping" ),
		FIELD_ADDRESS_LOCATION( "address_location" ),
		FIELD_HTTP_STATUS_CODE( "http_status_code" ),
		FIELD_MX_ADDRESS( "mx_address" ),
		FIELD_MX_ADDRESS_LOCATION( "mx_address_location" ),
		FIELD_HAS_WORKING_SMTP( "has_working_smtp" );

		private final String mField;

		private IPTests( final String field )
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
