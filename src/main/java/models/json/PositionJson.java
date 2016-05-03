package models.json;

public class PositionJson
{
	private final double latitude;
	private final double longitude;

	public PositionJson( final double latitude, final double longitude )
	{
		this.latitude = latitude;
		this.longitude = longitude;
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
