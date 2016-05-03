package util;

import org.junit.Test;

public class PingTest
{
	public PingTest()
	{
	}

	@Test
	public void testIPv4() throws Exception
	{
		final String ipv4Address = DNSStuff.getIPv4Address( "google.com" );
		Ping.IPv4( ipv4Address );
	}

	@Test
	public void testIPv6() throws Exception
	{
		final String ipv6Address = DNSStuff.getIPv6Address( "google.com" );
		Ping.IPv6( ipv6Address );
	}
}
