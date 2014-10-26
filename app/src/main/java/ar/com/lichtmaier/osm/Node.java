package ar.com.lichtmaier.osm;

import android.location.Location;

public class Node extends Element
{
	final public float lat, lon;

	public Node(long id, float lat, float lon)
	{
		super(id);
		this.lat = lat;
		this.lon = lon;
	}

	@Override
	public Location getCenter()
	{
		Location l = new Location("?");
		l.setLatitude(lat);
		l.setLongitude(lon);
		return l;
	}

	@Override
	public void getBoundingBox(Location[] bb)
	{
		for(int i = 0; i < 2; i++)
		{
			bb[i] = new Location("?");
			bb[i].setLatitude(lat);
			bb[i].setLongitude(lon);
		}
	}
}
