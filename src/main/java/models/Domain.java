package models;

public class Domain
{
	private final String mUrl;
	private final int mId;

	public Domain( final int id, final String url )
	{
		mUrl = url;
		mId = id;
	}

	public String getUrl()
	{
		return mUrl;
	}
	
	public int getId()
	{
		return mId;
	}
}
