package models;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xbill.DNS.TextParseException;
import util.DNSLookup;

public class IPv6Test extends IPTest
{
	public IPv6Test( final Domain domain )
	{
		super( domain );
	}

	@Override
	public String getAddress()
	{
		if( !mHasTestedAddress )
		{
			mHasTestedAddress = true;
			
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
		if( !mHasTestedMxAddress )
		{
			mHasTestedMxAddress = true;
			
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
