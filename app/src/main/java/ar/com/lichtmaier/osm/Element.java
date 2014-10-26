package ar.com.lichtmaier.osm;

import android.location.Location;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.HashMap;
import java.util.Map;

abstract public class Element
{
	final public long id;

	private Map<String, String> tags;

	public Element(long id)
	{
		this.id = id;
	}

	public void setTag(String tag, String value)
	{
		if(tags == null)
			tags = new HashMap<>();
		tags.put(tag, value);
	}

	public String getTag(String tag)
	{
		return tags == null ? null : tags.get(tag);
	}

	public boolean hasTag(String tag)
	{
		return tags != null && tags.containsKey(tag);
	}

	public abstract void getBoundingBox(Location[] bb);

	public Location[] getBoundingBox()
	{
		Location[] bb = new Location[2];
		getBoundingBox(bb);
		return bb;
	}

	public Location getCenter()
	{
		Location[] bb = getBoundingBox();
		Location l = new Location("?");
		l.setLatitude((bb[0].getLatitude() + bb[1].getLatitude()) / 2.0);
		l.setLongitude((bb[0].getLongitude() + bb[1].getLongitude()) / 2.0);
		return l;
	}

	@Override
	public String toString()
	{
		String name = getTag("name");
		if(name != null)
			return name;
		return '<' + getClass().getSimpleName() + ' ' + id + '>';
	}

	private transient double lastLon = Double.MAX_VALUE, lastLat = Double.MAX_VALUE, dist;
	public synchronized double distanceTo(Location loc)
	{
		Location center = getCenter();
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		if(latitude != lastLat || longitude != lastLon)
		{
			GlobalCoordinates coords = new GlobalCoordinates(latitude, longitude);
			dist = GeodeticCalculator.calculateGeodeticCurve(Ellipsoid.WGS84, new GlobalCoordinates(center.getLatitude(), center.getLongitude()), coords).getEllipsoidalDistance();
			lastLat = latitude;
			lastLon = longitude;
		}
		return dist;
	}
}
