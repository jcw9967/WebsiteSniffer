package models;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.TextParseException;
import util.DNSLookup;
import util.Ping;

public class IPv6Test extends IPTest
{
	public IPv6Test( final Domain domain )
	{
		super( domain );
	}

	@Override
	public String getAddress()
	{
		if( mAddress == null )
		{
			try
			{
				mAddress = DNSLookup.getIPv6Address( mDomain.getUrl() );
			}
			catch( final TextParseException ex )
			{
				Logger.getLogger( IPTest.class.getName() ).log( Level.SEVERE, null, ex );
			}
		}

		return mAddress;
	}

	@Override
	public String getMxAddress()
	{
		if( mMxAddress == null )
		{
			try
			{
				final String mxUrl = DNSLookup.getMXUrl( mDomain.getUrl() );
				mMxAddress = mxUrl == null ? null : DNSLookup.getIPv6Address( mxUrl );
			}
			catch( TextParseException ex )
			{
				Logger.getLogger( IPTest.class.getName() ).log( Level.SEVERE, null, ex );
			}
		}

		return mMxAddress;
	}
}
