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
package au.edu.murdoch.websitesniffer.models;

public class Location
{
	private final String mCity;
	private final String mCountry;
	private final double mLatitude;
	private final double mLongitude;
	private final int mId;

	public Location( final int id, final String city, final String country, final double latitude, final double longitude )
	{
		mCity = city;
		mCountry = country;
		mLatitude = latitude;
		mLongitude = longitude;
		mId = id;
	}

	public String getCity()
	{
		return mCity;
	}

	public String getCountry()
	{
		return mCountry;
	}

	public double getLatitude()
	{
		return mLatitude;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public Integer getId()
	{
		return mId;
	}

	@Override
	public String toString()
	{
		final String string;

		if( mCity == null || mCity.equals( "" ) )
		{
			string = mCountry;
		}
		else
		{
			string = mCity + ", " + mCountry;
		}

		return string;
	}
}
