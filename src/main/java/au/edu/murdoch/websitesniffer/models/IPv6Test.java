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
import org.xbill.DNS.TextParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IPv6Test extends IPTest
{
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
			catch( final UnknownHostException | TextParseException ex )
			{
				Logger.getLogger( IPv6Test.class.getName() ).log( Level.SEVERE, ex.getMessage() );
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
					final URL url = new URL( "http://[" + mAddress + ']' );
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					mHttpStatusCode = connection.getResponseCode();

					int redirectCount = 0;
					while( mHttpStatusCode >= 300
							&& mHttpStatusCode <= 308
							&& redirectCount < MAX_REDIRECTS )
					{
						//Follow redirect
						++redirectCount;
						final String locationUrl = connection.getHeaderField( "Location" );
						final URL redirectUrl = new URL( locationUrl );
						connection = (HttpURLConnection) redirectUrl.openConnection();
						connection.connect();

						mHttpStatusCode = connection.getResponseCode();
					}
				}
				catch( final IOException ex )
				{
					Logger.getLogger( IPTest.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
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
			catch( final UnknownHostException | TextParseException ex )
			{
				Logger.getLogger( IPv6Test.class.getName() ).log( Level.SEVERE, ex.getMessage() );
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

			if( mAddress != null )
			{
				try
				{
					mPing = Ping.ping( mAddress, Type.IPv6 );
				}
				catch( final TimeoutException | IOException ex )
				{
					Logger.getLogger( IPv4Test.class.getName() ).log( Level.SEVERE, "Failed to retrieve ping for " + mDomain.getUrl() + " (" + mAddress + "):\n" + ex.getMessage() );
				}
			}
		}

		return mPing;
	}
}
