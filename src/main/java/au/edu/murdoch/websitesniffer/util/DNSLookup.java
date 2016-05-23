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

import org.xbill.DNS.*;

import java.net.UnknownHostException;

public class DNSLookup
{
	public static String getIPv4Address( final String url ) throws TextParseException, UnknownHostException
	{
		String address = null;

		final Record[] records = new Lookup( url, Type.A ).run();
		if( records != null )
		{
			final ARecord record = (ARecord) records[0];
			address = record.getAddress().getHostAddress();
		}

		return address;
	}

	public static String getIPv6Address( final String url ) throws TextParseException, UnknownHostException
	{
		String address = null;

		final Record[] records = new Lookup( url, Type.AAAA ).run();
		if( records != null )
		{
			final AAAARecord record = (AAAARecord) records[0];
			address = record.getAddress().getHostAddress();
		}

		return address;
	}

	public static String getMXUrl( final String url ) throws TextParseException, UnknownHostException
	{
		String address = null;

		final Record[] records = new Lookup( url, Type.MX ).run();
		if( records != null )
		{
			final MXRecord lowestPreferenceMX = getLowestPreferenceMX( records );
			address = lowestPreferenceMX.getTarget().toString();
		}

		return address;
	}

	private static MXRecord getLowestPreferenceMX( final Record[] records )
	{
		MXRecord lowest = null;

		for( final Record record : records )
		{
			final MXRecord current = (MXRecord) record;

			if( lowest == null || current.getPriority() < lowest.getPriority() )
			{
				lowest = current;
			}
		}

		return lowest;
	}
}
