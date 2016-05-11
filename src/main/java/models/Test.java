package models;

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

	public void setIPv6Test(  IPv6Test IPv6Test )
	{
		mIPv6Test = IPv6Test;
	}
	
	
}
