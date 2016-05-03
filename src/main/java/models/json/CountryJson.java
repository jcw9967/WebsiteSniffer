package models.json;

public class CountryJson
{
	private final String name;
	private final String code;

	public CountryJson( final String name, final String code )
	{
		this.name = name;
		this.code = code;
	}

	public String getName()
	{
		return name;
	}

	public String getCode()
	{
		return code;
	}
}
