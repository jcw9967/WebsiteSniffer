package util;

import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSLookup
{
	public static String getIPv4Address( final String url ) throws TextParseException
	{
		String address = null;
		
		final Record[] records = new Lookup( url, Type.A ).run();
		if( records != null )
		{
			final ARecord record = (ARecord) records[0];
			address = record.getAddress().getHostAddress();
		}
		
		return address;
	}

	public static String getIPv6Address( final String url ) throws TextParseException
	{
		String address = null;
		
		final Record[] records = new Lookup( url, Type.AAAA ).run();
		if( records != null )
		{
			final AAAARecord record = (AAAARecord) records[0];
			address = record.getAddress().getHostAddress();
		}
		
		return address;
	}

	public static String getMXUrl( final String url ) throws TextParseException
	{
		String address = null;
		
		final Record[] records = new Lookup( url, Type.MX ).run();
		if( records != null )
		{
			final MXRecord lowestPreferenceMX = getLowestPreferenceMX( records );

			address = lowestPreferenceMX.getTarget().toString();
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
