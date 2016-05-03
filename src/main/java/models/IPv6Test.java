package models;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.TextParseException;
import util.DNSStuff;
import util.LocationHelper;
import util.Ping;

public class IPv6Test
{
	private final Domain mDomain;
	private long mTimestamp;
	private String mAddress;
	private Integer mPing;
	private Location mAddressLocation;
	private Integer mHttpStatusCode;
	private String mMxAddress;
	private Location mMxAddressLocation;
	private boolean mHasWorkingSMTP;
	private Integer mId;

	public IPv6Test( final Domain domain )
	{
		mDomain = domain;
	}

	public void performTest()
	{
		mTimestamp = new Date().getTime();

		mAddress = DNSStuff.getIPv6Address( mDomain.getUrl() );
		if( mAddress != null )
		{
			mAddressLocation = LocationHelper.getLocationByIP(mAddress );

			mHttpStatusCode = null;    //TODO http

			try
			{
				mPing = Ping.IPv6(mAddress );
			}
			catch( final IOException e )
			{
				mPing = null;
				Logger.getLogger( IPv4Test.class.getName() ).log( Level.SEVERE, null, e );
			}
		}

		final String mxUrl = DNSStuff.getMXUrl( mDomain.getUrl() );
		mMxAddress = mxUrl == null ? null : DNSStuff.getIPv6Address( mxUrl );
		mMxAddressLocation = mMxAddress == null ? null : LocationHelper.getLocationByIP( mMxAddress );
		mHasWorkingSMTP = mxUrl == null ? false : false;   //TODO smtp
	}

	public Domain getDomain()
	{
		return mDomain;
	}

	public long getTimestamp()
	{
		return mTimestamp;
	}

	public String getIpv6Address()
	{
		return mAddress;
	}

	public Integer getIpv6Ping()
	{
		return mPing;
	}

	public Location getIpv6AddressLocation()
	{
		return mAddressLocation;
	}

	public Integer getHttpStatusCode()
	{
		return mHttpStatusCode;
	}

	public String getMxAddress()
	{
		return mMxAddress;
	}

	public Location getMxAddressLocation()
	{
		return mMxAddressLocation;
	}

	public boolean hasWorkingSMTP()
	{
		return mHasWorkingSMTP;
	}

	public Integer getId()
	{
		return mId;
	}
}
