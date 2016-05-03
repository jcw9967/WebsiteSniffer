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
		String address = null;
		
		try
		{
			final Record[] records = new Lookup( url, Type.A ).run();
			if( records != null )
			{
				final ARecord record = (ARecord) records[0];
				address = record.getAddress().getHostAddress();
			}
		}
		catch( final TextParseException ex )
		{
			Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
		}
		
		return address;
	}

	public static String getIPv6Address( final String url )
	{
		String address = null;
		
		try
		{
			final Record[] records = new Lookup( url, Type.AAAA ).run();
			if( records != null )
			{
				final AAAARecord record = (AAAARecord) records[0];
				address = record.getAddress().getHostAddress();
			}
		}
		catch( TextParseException ex )
		{
			Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
		}
		
		return address;
	}

	public static String getMXUrl( final String url )
	{
		String address = null;
		
		try
		{
			final Record[] records = new Lookup( url, Type.MX ).run();
			if( records != null )
			{
				final MXRecord lowestPreferenceMX = getLowestPreferenceMX( records );
				
				address = lowestPreferenceMX.getTarget().toString();
			}
		}
		catch( TextParseException ex )
		{
			Logger.getLogger( DNSStuff.class.getName() ).log( Level.SEVERE, null, ex );
		}
		
		return address;
	}

	private static MXRecord getLowestPreferenceMX( final Record[] records )
	{
		MXRecord lowest = null;
		
		for( final Record record : records )
		{
			final MXRecord current = (MXRecord) record;

			if( lowest == null || current.getPriority() < lowest.getPriority() )
			{
				lowest = current;
			}
		}
		
		return lowest;
	}
}
