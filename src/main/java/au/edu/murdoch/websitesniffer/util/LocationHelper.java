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

import au.edu.murdoch.websitesniffer.models.Location;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;

public class LocationHelper
{
	private static DatabaseReader reader;

	static
	{
		try
		{
			reader = new DatabaseReader.Builder( new File( "GeoLite2-City.mmdb" ) ).withCache( new CHMCache() ).build();
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}
	}

	public static Location getLocationForHost()
	{
		String ip = null;

		try
		{
			final URL url = new URL( "http://checkip.amazonaws.com" );
			try( final InputStream inputStream = url.openStream();
				 final InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
				 final BufferedReader bufferedReader = new BufferedReader( inputStreamReader ) )
			{
				ip = bufferedReader.readLine();
			}
		}
		catch( final IOException e )
		{
			e.printStackTrace();
		}

		return getLocationByIP( ip );
	}

	public static Location getLocationByIP( final String ip )
	{
		Location location = null;

		if( ip != null )
		{
			try
			{
				final InetAddress address = InetAddress.getByName( ip );
				final CityResponse response = reader.city( address );

				final String city = response.getCity().getName();
				final String country = response.getCountry().getName();
				final double latitude = response.getLocation().getLatitude();
				final double longitude = response.getLocation().getLongitude();

				//If the location is already in the database, get its id
				location = DatabaseHelper.getLocation( city, country );
				if( location == null )
				{
					//Location not found; insert it
					DatabaseHelper.insertLocation( city, country, latitude, longitude );
					location = DatabaseHelper.getLocation( city, country );
				}
			}
			catch( final IOException | SQLException | GeoIp2Exception e )
			{
				e.printStackTrace();
			}
		}

		return location;
	}
}
