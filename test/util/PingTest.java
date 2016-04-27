package util;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

public class PingTest
{
    public PingTest()
    {
    }

    @Test
    public void testIPv4()
    {
	System.out.println( "IPv4" );
	final String ipv4Address = DNSStuff.getIPv4Address( "google.com" );

	final Integer result = Ping.IPv4( ipv4Address );
	System.out.println( result.toString() );

	assertNotNull( result );
    }

    @Test
    public void testIPv6()
    {
	System.out.println( "IPv6" );
	final String ipv6Address = DNSStuff.getIPv6Address( "google.com" );

	final Integer result = Ping.IPv6( ipv6Address );
	System.out.println( result.toString() );

	assertNotNull( result );
    }

    @Test
    public void testConcurrentPingIPv4()
    {
	System.out.println( "Concurrent IPv4" );
	final String[] ipAddresses = new String[]
	{
	    DNSStuff.getIPv4Address( "www.google.com.au" ),
	    DNSStuff.getIPv4Address( "www.facebook.com" ),
	    DNSStuff.getIPv4Address( "www.yahoo.com" ),
	    DNSStuff.getIPv4Address( "www.tumblr.com" )
	};

	for( final String address : ipAddresses )
	{
	    if( address != null )
	    {
		final Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
			System.out.println( address + ": Ping..." );
			final Integer pingResult = Ping.IPv4( address );
			if( pingResult != null )
			{
			    System.out.println( address + ": " + pingResult.toString() + "ms" );
			}
		    }
		};

		new Thread( runnable ).start();
	    }
	}

	try
	{
	    Thread.sleep( 10000 );
	}
	catch( final InterruptedException ex )
	{
	    Logger.getLogger( PingTest.class.getName() ).log( Level.SEVERE, null, ex );
	}
    }

    @Test
    public void testConcurrentPingIPv6()
    {
	System.out.println( "Concurrent IPv6" );
	final String[] ipAddresses = new String[]
	{
	    DNSStuff.getIPv6Address( "www.google.com.au" ),
	    DNSStuff.getIPv6Address( "www.facebook.com" ),
	    DNSStuff.getIPv6Address( "www.yahoo.com" ),
	    DNSStuff.getIPv6Address( "www.tumblr.com" )
	};

	for( final String address : ipAddresses )
	{
	    if( address != null )
	    {
		final Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
			System.out.println( address + ": Ping..." );
			final Integer pingResult = Ping.IPv6( address );
			if( pingResult != null )
			{
			    System.out.println( address + ": " + pingResult.toString() + "ms" );
			}
		    }
		};

		new Thread( runnable ).start();
	    }
	}

	try
	{
	    Thread.sleep( 10000 );
	}
	catch( final InterruptedException ex )
	{
	    Logger.getLogger( PingTest.class.getName() ).log( Level.SEVERE, null, ex );
	}
    }
}
