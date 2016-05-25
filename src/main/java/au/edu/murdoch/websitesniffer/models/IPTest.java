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

import au.edu.murdoch.websitesniffer.util.LocationHelper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class IPTest
{
	private static final Logger log = Logger.getLogger( IPTest.class.getName() );
	static final int MAX_REDIRECTS = 5;

	public enum Type
	{
		IPv4,
		IPv6
	}

	private boolean mHasTestedAddressLocation;
	private Location mAddressLocation;
	private boolean mHasTestedMxAddressLocation;
	private Location mMxAddressLocation;
	private boolean mHasTestedWorkingSMTP;
	private boolean mHasWorkingSMTP = false;

	final Domain mDomain;
	boolean mHasTestedAddress;
	InetAddress mAddress;
	boolean mHasTestedPing;
	Integer mPing;
	boolean mHasTestedHttpStatusCode;
	Integer mHttpStatusCode;
	boolean mHasTestedMxAddress;
	InetAddress mMxAddress;


	IPTest( final Domain domain )
	{
		mDomain = domain;
	}

	public Location getAddressLocation()
	{
		if( mHasTestedAddress && !mHasTestedAddressLocation )
		{
			mHasTestedAddressLocation = true;

			if( mAddress != null )
			{
				try
				{
					mAddressLocation = LocationHelper.getInstance().getLocationByIP( mAddress );
				}
				catch( final NullPointerException | IOException | GeoIp2Exception | SQLException ignored )
				{
				}
			}
		}

		return mAddressLocation;
	}

	public abstract Integer getHttpStatusCode();

	public Location getMxAddressLocation()
	{
		if( mHasTestedMxAddress && !mHasTestedMxAddressLocation )
		{
			mHasTestedMxAddressLocation = true;

			if( mMxAddress != null )
			{
				try
				{
					mMxAddressLocation = LocationHelper.getInstance().getLocationByIP( mMxAddress );
				}
				catch( final NullPointerException | IOException | GeoIp2Exception | SQLException ignored )
				{
				}
			}
		}

		return mMxAddressLocation;
	}

	public boolean hasWorkingSMTP()
	{
		if( mHasTestedMxAddress && !mHasTestedWorkingSMTP )
		{
			mHasTestedWorkingSMTP = true;

			if( mMxAddress != null )
			{
				try
				{
					final SMTPClient client = new SMTPClient();
					client.setConnectTimeout( 4000 );
					client.connect( mMxAddress );

					mHasWorkingSMTP = SMTPReply.isPositiveCompletion( client.getReplyCode() );
					client.disconnect();
				}
				catch( final IOException ignored )
				{
				}
			}
		}

		return mHasWorkingSMTP;
	}

	public abstract InetAddress getAddress();

	public abstract InetAddress getMxAddress();

	public abstract Integer getPing();
}
