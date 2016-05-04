package util;

import org.junit.Test;
import util.Ping.PingMethod;

public class PingTest
{
	public PingTest()
	{
	}
	
	@Test
	public void testURLUsingIPv4() throws Exception
	{
		Ping.ping( "google.com", PingMethod.IPv4 );
	}
	
	@Test
	public void testURLUsingIPv6() throws Exception
	{
		Ping.ping( "google.com", PingMethod.IPv6 );
	}

	@Test
	public void testIPv4() throws Exception
	{
		final String ipv4Address = DNSLookup.getIPv4Address( "google.com" );
		Ping.ping( ipv4Address );
	}

	@Test
	public void testIPv6() throws Exception
	{
		final String ipv6Address = DNSLookup.getIPv6Address( "google.com" );
		Ping.ping( ipv6Address );
	}
}
