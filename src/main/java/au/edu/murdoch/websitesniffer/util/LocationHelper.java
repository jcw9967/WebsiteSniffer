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
import au.edu.murdoch.websitesniffer.models.json.LocationJson;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationHelper
{
	public static Location getLocationForHost()
	{
		return getLocationByIP( "" );
	}

	public static Location getLocationByIP( final String ip )
	{
		Location location = null;

		try
		{
			final NetworkService networkService = new Retrofit.Builder()
					.baseUrl( NetworkService.GEOIP_BASE_URL )
					.addConverterFactory( GsonConverterFactory.create() )
					.build()
					.create( NetworkService.class );

			final Call<LocationJson> call = networkService.getLocationByIP( ip );
			final Response<LocationJson> response = call.execute();
			if( response.isSuccessful() )
			{
				final LocationJson locationJson = response.body();
				final String city = locationJson.getCity();
				final String country = locationJson.getCountry();
				final double latitude = locationJson.getLatitude();
				final double longitude = locationJson.getLongitude();

				//If the location is already in the database, get its id
				location = DatabaseHelper.getLocation( city, country );
				if( location == null )
				{
					//Location not found; insert it
					DatabaseHelper.insertLocation( city, country, latitude, longitude );
					location = DatabaseHelper.getLocation( city, country );
				}
			}
			else
			{
				try( final ResponseBody errorBody = response.errorBody() )
				{
					Logger.getLogger( LocationHelper.class.getName() ).log( Level.SEVERE, errorBody.string() );
				}
			}
		}
		catch( final IOException | SQLException ex )
		{
			Logger.getLogger( LocationHelper.class.getName() ).log( Level.SEVERE, null, ex );
		}

		return location;
	}
}
