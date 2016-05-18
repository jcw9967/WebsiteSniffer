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

import java.util.Date;

public class Test
{
	private final Domain mDomain;
	private final long mTimestamp;
	private final Location mUserLocation;
	private IPv4Test mIPv4Test;
	private IPv6Test mIPv6Test;

	public Test( final Domain domain, final Location location )
	{
		mDomain = domain;
		mTimestamp = new Date().getTime();
		mUserLocation = location;
	}

	public Domain getDomain()
	{
		return mDomain;
	}

	public long getTimestamp()
	{
		return mTimestamp;
	}

	public Location getUserLocation()
	{
		return mUserLocation;
	}

	public IPv4Test getIPv4Test()
	{
		return mIPv4Test;
	}

	public IPv6Test getIPv6Test()
	{
		return mIPv6Test;
	}

	public void setIPv4Test( final IPv4Test IPv4Test )
	{
		mIPv4Test = IPv4Test;
	}

	public void setIPv6Test( IPv6Test IPv6Test )
	{
		mIPv6Test = IPv6Test;
	}
}
