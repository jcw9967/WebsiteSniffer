package util;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class DNSLookupTest
{
	public DNSLookupTest()
	{
	}

	@Test
	public void testGetIPv4Address() throws Exception
	{
		final String url = "google.com";
		final String ipv4Address = DNSLookup.getIPv4Address( url );

		System.out.println( "IPv4 Address for `google.com`: " + ipv4Address );
		assertNotNull( ipv4Address );
	}

	@Test
	public void testGetIPv6Address() throws Exception
	{
		final String url = "google.com";
		final String ipv6Address = DNSLookup.getIPv6Address( url );

		System.out.println( "IPv6 Address for `google.com`: " + ipv6Address );
		assertNotNull( ipv6Address );
	}

	@Test
	public void testGetMXAddress() throws Exception
	{
		final String url = "gmail.com";
		final String address = DNSLookup.getMXUrl( url );

		System.out.println( "MX Address for `gmail.com`: " + address );
		assertNotNull( address );
	}
	
	@Test
	public void testGetIPv4ForMX() throws Exception
	{
		final String url = "gmail.com";
		final String mxAddress = DNSLookup.getMXUrl( url );
		final String mxIPv4Address = DNSLookup.getIPv4Address( mxAddress );
		
		System.out.println( "IPv4 Address for `" + mxAddress + "`: " + mxIPv4Address );
		assertNotNull( mxIPv4Address );
	}
	
	@Test
	public void testGetIPv6ForMX() throws Exception
	{
		final String url = "gmail.com";
		final String mxAddress = DNSLookup.getMXUrl( url );
		final String mxIPv6Address = DNSLookup.getIPv6Address( mxAddress );
		
		System.out.println( "IPv6 Address for `" + mxAddress + "`: " + mxIPv6Address );
		assertNotNull( mxIPv6Address );
	}
}
