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

import au.edu.murdoch.websitesniffer.util.DNSLookup;
import au.edu.murdoch.websitesniffer.util.Ping;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.xbill.DNS.TextParseException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IPv6Test extends IPTest
{
	private static final Logger log = Logger.getLogger( IPv6Test.class.getName() );

	public IPv6Test( final Domain domain )
	{
		super( domain );
	}

	@Override
	public String getAddress()
	{
		if( !mHasTestedAddress )
		{
			mHasTestedAddress = true;

			try
			{
				mAddress = DNSLookup.getIPv6Address( mDomain.getUrl() );
			}
			catch( final UnknownHostException | TextParseException ignored )
			{
			}
		}

		return mAddress;
	}

	@Override
	public Integer getHttpStatusCode()
	{
		if( mHasTestedAddress && !mHasTestedHttpStatusCode )
		{
			mHasTestedHttpStatusCode = true;

			if( mAddress != null )
			{
				try
				{
					final Connection.Response response = Jsoup.connect( "http://[" + mAddress + ']' ).method( Connection.Method.HEAD ).execute();
					mHttpStatusCode = response.statusCode();
				}
				catch( final HttpStatusException e )
				{
					mHttpStatusCode = e.getStatusCode();
				}
				catch( final IOException ignored )
				{
				}
			}
		}

		return mHttpStatusCode;
	}

	@Override
	public String getMxAddress()
	{
		if( !mHasTestedMxAddress )
		{
			mHasTestedMxAddress = true;

			try
			{
				final String mxUrl = DNSLookup.getMXUrl( mDomain.getUrl() );
				mMxAddress = mxUrl == null ? null : DNSLookup.getIPv6Address( mxUrl );
			}
			catch( final UnknownHostException | TextParseException ignored )
			{
			}
		}

		return mMxAddress;
	}

	@Override
	public Integer getPing()
	{
		if( mHasTestedAddress && !mHasTestedPing )
		{
			mHasTestedPing = true;

			try
			{
				mPing = Ping.ping( mDomain.getUrl(), Type.IPv6 );
			}
			catch( final IOException | InterruptedException ex )
			{
				final String newURL = "www." + mDomain.getUrl();
				try
				{
					mPing = Ping.ping( newURL, Type.IPv6 );
				}
				catch( final IOException | InterruptedException ignored )
				{
				}
			}
		}

		return mPing;
	}
}
