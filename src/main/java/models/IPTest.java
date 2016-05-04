package models;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.LocationHelper;
import util.Ping;

public abstract class IPTest
{
	public enum Type {
		IPv4,
		IPv6
	}
		
	protected final Domain mDomain;
	protected long mTimestamp;
	protected String mAddress;
	protected Integer mPing;
	protected Location mAddressLocation;
	protected Integer mHttpStatusCode;
	protected String mMxAddress;
	protected Location mMxAddressLocation;
	protected Boolean mHasWorkingSMTP;

	public IPTest( final Domain domain )
	{
		mDomain = domain;
		mTimestamp = new Date().getTime();
	}

	public Domain getDomain()
	{
		return mDomain;
	}

	public long getTimestamp()
	{
		return mTimestamp;
	}

	public Location getAddressLocation()
	{
		if( mAddressLocation == null )
		{
			int count = 0;
			do
			{
				count++;
				mAddressLocation = LocationHelper.getLocationByIP( mAddress );
			}
			while( mAddressLocation == null && count < 3 );
		}

		return mAddressLocation;
	}
	
	public Integer getPing()
	{
		if( mPing == null )
		{
			try
			{
				mPing = Ping.ping( mAddress );
			}
			catch( final IOException ex )
			{
				Logger.getLogger( IPv4Test.class.getName() ).log( Level.SEVERE, null, ex );
			}
		}

		return mPing;
	}

	public Integer getHttpStatusCode()
	{
		if( mHttpStatusCode == null )
		{
			//TODO
		}

		return mHttpStatusCode;
	}

	public Location getMxAddressLocation()
	{
		if( mMxAddressLocation == null )
		{
			mMxAddressLocation = mMxAddress == null ? null : LocationHelper.getLocationByIP( mMxAddress );
		}

		return mMxAddressLocation;
	}

	public boolean hasWorkingSMTP()
	{
		if( mHasWorkingSMTP == null )
		{
			//TODO
			mHasWorkingSMTP = false;
		}

		return mHasWorkingSMTP;
	}

	public abstract String getAddress();

	public abstract String getMxAddress();
}
