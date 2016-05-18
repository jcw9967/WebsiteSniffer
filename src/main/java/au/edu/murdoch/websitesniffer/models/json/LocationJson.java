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
package au.edu.murdoch.websitesniffer.models.json;

public class LocationJson
{
	private final String city;
	private final String country_name;
	private final double latitude;
	private final double longitude;

	public LocationJson( final String city, final String country_name, final double latitude, final double longitude )
	{
		this.city = city;
		this.country_name = country_name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCity()
	{
		return city;
	}

	public String getCountry()
	{
		return country_name;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}
}
