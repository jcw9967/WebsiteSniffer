package models.json;

public class LocationJson
{
	private final String city;
	private final CountryJson country;
	private final PositionJson location;

	public LocationJson( final String city, final CountryJson country, final PositionJson location )
	{
		this.city = city;
		this.country = country;
		this.location = location;
	}

	public String getCity()
	{
		return city;
	}

	public CountryJson getCountry()
	{
		return country;
	}

	public PositionJson getLocation()
	{
		return location;
	}
}
