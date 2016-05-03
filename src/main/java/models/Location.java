package models;

public class Location
{
	private final String mCity;
	private final String mCountry;
	private final double mLatitude;
	private final double mLongitude;
	private final int mId;

	public Location( final int id, final String city, final String country, final double latitude, final double longitude )
	{
		mCity = city;
		mCountry = country;
		mLatitude = latitude;
		mLongitude = longitude;
		mId = id;
	}

	public String getCity()
	{
		return mCity;
	}

	public String getCountry()
	{
		return mCountry;
	}

	public double getLatitude()
	{
		return mLatitude;
	}

	public double getLongitude()
	{
		return mLongitude;
	}

	public Integer getId()
	{
		return mId;
	}
}
