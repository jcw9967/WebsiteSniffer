package util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DNSStuffTest
{
    public DNSStuffTest()
    {
    }

    @Test
    public void testGetIPv4Address()
    {
	System.out.println( "getIPv4Address" );

	final String url = "www.google.com";
	final String address = DNSStuff.getIPv4Address( url );
	System.out.println( address );

	assertNotNull( address );
    }

    @Test
    public void testGetIPv6Address()
    {
	System.out.println( "getIPv6Address" );

	final String url = "www.google.com";
	final String address = DNSStuff.getIPv6Address( url );
	System.out.println( address );

	assertNotNull( address );
    }

    @Test
    public void testGetMXAddress()
    {
	System.out.println( "getMXAddress" );

	final String url = "gmail.com";
	final String address = DNSStuff.getMXAddress( url );
	System.out.println( address );

	assertNotNull( address );
    }

}
