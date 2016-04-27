package util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSStuff
{
    public static String getIPv4Address( final String url )
    {
	try
	{
	    final Record[] records = new Lookup( url, Type.A ).run();
	    if( records != null )
	    {
		final ARecord record = (ARecord) records[0];
		return record.getAddress().getHostAddress();
	    }
	}
	catch( final TextParseException ex )
	{
	    Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
	}

	return null;
    }

    public static String getIPv6Address( final String url )
    {
	try
	{
	    final Record[] records = new Lookup( url, Type.AAAA ).run();
	    if( records != null )
	    {
		final AAAARecord record = (AAAARecord) records[0];
		return record.getAddress().getHostAddress();
	    }
	}
	catch( final TextParseException ex )
	{
	    Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
	}

	return null;
    }

    public static String getMXAddress( final String url )
    {
	try
	{
	    final Record[] records = new Lookup( url, Type.MX ).run();
	    if( records != null )
	    {
		final MXRecord highestPrefMX = getHighestPriorityMX( records );

		return highestPrefMX.getTarget().toString();
	    }
	}
	catch( final TextParseException ex )
	{
	    Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
	}

	return null;
    }

    private static MXRecord getHighestPriorityMX( final Record[] records )
    {
	MXRecord highest = null;
	for( final Record record : records )
	{
	    final MXRecord current = (MXRecord) record;

	    if( highest == null || current.getPriority() > highest.getPriority() )
	    {
		highest = current;
	    }
	}
	return highest;
    }
}
