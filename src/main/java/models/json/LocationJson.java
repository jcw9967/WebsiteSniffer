package models.json;

public class LocationJson
{
	private final String city;
	private final String country_name;
	private final double latitude;
	private final double longitude;

	public LocationJson( final String city, final String country_name, final double latitude, final double longitude )
	{
		this.city = city;
		this.country_name = country_name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCity()
	{
		return city;
	}

	public String getCountry()
	{
		return country_name;
	}

	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
}
