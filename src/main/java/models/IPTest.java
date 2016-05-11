package models;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import util.LocationHelper;
import util.Ping;

public abstract class IPTest
{
	private static final int MAX_REDIRECTS = 5;

	public enum Type
	{
		IPv4,
		IPv6
	}

	protected final Domain mDomain;
	protected boolean mHasTestedAddress;
	protected String mAddress;
	protected boolean mHasTestedPing;
	protected Integer mPing;
	protected boolean mHasTestedAddressLocation;
	protected Location mAddressLocation;
	protected boolean mHasTestedHttpStatusCode;
	protected Integer mHttpStatusCode;
	protected boolean mHasTestedMxAddress;
	protected String mMxAddress;
	protected boolean mHasTestedMxAddressLocation;
	protected Location mMxAddressLocation;
	protected boolean mHasTestedWorkingSMTP;
	protected boolean mHasWorkingSMTP = false;

	public IPTest( final Domain domain )
	{
		mDomain = domain;
	}

	public Location getAddressLocation()
	{
		if( mHasTestedAddress && !mHasTestedAddressLocation )
		{
			mHasTestedAddressLocation = true;

			if( mAddress != null )
			{
				mAddressLocation = LocationHelper.getLocationByIP( mAddress );
			}
		}

		return mAddressLocation;
	}

	public Integer getPing()
	{
		if( mHasTestedAddress && !mHasTestedPing )
		{
			mHasTestedPing = true;

			if( mAddress != null )
			{
				try
				{
					mPing = Ping.ping( mAddress );
				}
				catch( final IOException ex )
				{
					Logger.getLogger( IPv4Test.class.getName() ).log( Level.SEVERE, null, ex );
				}
			}
		}

		return mPing;
	}

	public Integer getHttpStatusCode()
	{
		if( mHasTestedAddress && !mHasTestedHttpStatusCode )
		{
			mHasTestedHttpStatusCode = true;

			if( mAddress != null )
			{
				try
				{
					final URL url = new URL( "http://" + mAddress );
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					mHttpStatusCode = connection.getResponseCode();

					int redirectCount = 0;
					while( mHttpStatusCode >= 300
							&& mHttpStatusCode <= 308
							&& redirectCount < MAX_REDIRECTS )
					{
						//Follow redirect
						++redirectCount;
						final String locationUrl = connection.getHeaderField( "Location" );
						final URL redirectUrl = new URL( locationUrl );
						connection = (HttpURLConnection) redirectUrl.openConnection();
						connection.connect();

						mHttpStatusCode = connection.getResponseCode();
					}
				}
				catch( final IOException ex )
				{
					Logger.getLogger( IPTest.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
				}
			}
		}

		return mHttpStatusCode;
	}

	public Location getMxAddressLocation()
	{
		if( mHasTestedMxAddress && !mHasTestedMxAddressLocation )
		{
			mHasTestedMxAddressLocation = true;

			if( mMxAddress != null )
			{
				mMxAddressLocation = LocationHelper.getLocationByIP( mMxAddress );
			}
		}

		return mMxAddressLocation;
	}

	public boolean hasWorkingSMTP()
	{
		if( mHasTestedMxAddress && !mHasTestedWorkingSMTP )
		{
			mHasTestedWorkingSMTP = true;

			if( mMxAddress != null )
			{
				try
				{
					final SMTPClient client = new SMTPClient();
					client.connect( mMxAddress );

					final int replyCode = client.getReplyCode();
					mHasWorkingSMTP = SMTPReply.isPositiveCompletion( replyCode );
				}
				catch( final IOException ex )
				{
					Logger.getLogger( IPTest.class.getName() ).log( Level.SEVERE, null, ex );
				}
			}
		}

		return mHasWorkingSMTP;
	}

	public abstract String getAddress();

	public abstract String getMxAddress();
}
