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

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSLookup
{
	private static SimpleResolver LOOKUP_RESOLVER;

	static
	{
		try
		{
			LOOKUP_RESOLVER = new SimpleResolver( "8.8.8.8" );
		}
		catch( final UnknownHostException ex )
		{
			Logger.getLogger( DNSLookup.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public static String getIPv4Address( final String url ) throws TextParseException, UnknownHostException
	{
		String address = null;

		final Lookup lookup = new Lookup( url, Type.A );
		lookup.setResolver( LOOKUP_RESOLVER );

		final Record[] records = lookup.run();
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

		final Lookup lookup = new Lookup( url, Type.AAAA );
		lookup.setResolver( LOOKUP_RESOLVER );

		final Record[] records = lookup.run();
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

		final Lookup lookup = new Lookup( url, Type.MX );
		lookup.setResolver( LOOKUP_RESOLVER );

		final Record[] records = lookup.run();
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
