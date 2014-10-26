package ar.com.lichtmaier.osm;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Relation extends Element
{
	private final Map<String, List<Element>> members = new HashMap<>();

	public Relation(long id)
	{
		super(id);
	}

	public void add(String role, Element member)
	{
		List<Element> l = members.get(role);
		if(l == null)
		{
			l = new ArrayList<>();
			members.put(role, l);
		}
		l.add(member);
	}

	@Override
	public void getBoundingBox(Location[] bb)
	{
		double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
		Location[] x = new Location[2];
		List<Element> outer = members.get("outer");
		if(outer == null)
			throw new RuntimeException("rel " + id + " has no members");
		for(Element element : outer)
		{
			element.getBoundingBox(x);
			if(x[0].getLatitude() < minLat)
				minLat = x[0].getLatitude();
			if(x[0].getLongitude() < minLon)
				minLon = x[0].getLongitude();
			if(x[1].getLatitude() > maxLat)
				maxLat = x[1].getLatitude();
			if(x[1].getLongitude() > maxLon)
				maxLon = x[1].getLongitude();
		}
		bb[0] = new Location("?");
		bb[0].setLatitude(minLat);
		bb[0].setLongitude(minLon);
		bb[1] = new Location("?");
		bb[1].setLatitude(maxLat);
		bb[1].setLongitude(maxLon);
	}
}
