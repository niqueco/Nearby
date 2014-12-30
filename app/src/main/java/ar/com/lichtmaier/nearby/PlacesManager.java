package ar.com.lichtmaier.nearby;

import android.content.Context;
import android.location.Location;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.com.lichtmaier.osm.*;

public class PlacesManager
{
	final static private Map<ElementSpec, Element> elementCache = new HashMap<>();

	private static String queryTemplate;
	private static Pattern pattern;

	private static Map<Category, Collection<Element>> lastResult;
	private static double lastLatitude = Double.NaN, lastLongitude = Double.NaN;

	private static synchronized String getQuery(Context ctx, int radius, double latitude, double longitude) throws IOException
	{
		if(queryTemplate == null)
		{
			InputStreamReader r = new InputStreamReader(ctx.getResources().openRawResource(R.raw.query), "UTF-8");
			StringWriter sw = new StringWriter();
			char[] buf = new char[8192];
			int n;
			while( ( n = r.read(buf) ) != -1)
				sw.write(buf, 0, n);
			queryTemplate = sw.toString();
			pattern = Pattern.compile("\\$([a-z]+)", Pattern.CASE_INSENSITIVE);
		}
		Matcher m = pattern.matcher(queryTemplate);
		StringBuffer sb = new StringBuffer();
		while(m.find())
		{
			switch(m.group(1))
			{
				case "latitude":
					m.appendReplacement(sb, Double.toString(latitude));
					break;
				case "longitude":
					m.appendReplacement(sb, Double.toString(longitude));
					break;
				case "radius":
					m.appendReplacement(sb, Integer.toString(radius));
					break;
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static Map<Category, Collection<Element>> around(Context ctx, final Location loc)
	{
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();

		if(latitude == lastLatitude && longitude == lastLongitude)
			return lastResult;

		InputStream s = null;
		try
		{
			List<Element> places = new ArrayList<>();
			XmlPullParserFactory pf = XmlPullParserFactory.newInstance();
			pf.setValidating(false);
			pf.setNamespaceAware(false);
			XmlPullParser parser = pf.newPullParser();

			URL url = new URL("http://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(getQuery(ctx, 1000, latitude, longitude), "UTF-8"));
			s = url.openStream();
			parser.setInput(s, null);

			Map<Long, Node> nodes = new HashMap<>();
			Map<Long, Way> ways = new HashMap<>();
			int t;
			long id;
			Element thing = null;
			while((t = parser.next()) != XmlPullParser.END_DOCUMENT)
			{
				String tagName = parser.getName();
				switch(t)
				{
					case XmlPullParser.START_TAG:
						switch(tagName)
						{
							case "node":
								id = Long.parseLong(parser.getAttributeValue(null, "id"));
								Node node = new Node(id, Float.parseFloat(parser.getAttributeValue(null, "lat")), Float.parseFloat(parser.getAttributeValue(null, "lon")));
								thing = node;
								nodes.put(id, node);
								break;
							case "way":
								id = Long.parseLong(parser.getAttributeValue(null, "id"));
								Way way = new Way(id);
								thing = way;
								ways.put(id, way);
								break;
							case "relation":
								id = Long.parseLong(parser.getAttributeValue(null, "id"));
								thing = new Relation(id);
								break;
							case "tag":
								if(thing != null)
									thing.setTag(parser.getAttributeValue(null, "k"), parser.getAttributeValue(null, "v"));
								break;
							case "nd":
								id = Long.parseLong(parser.getAttributeValue(null, "ref"));
								assert thing != null;
								((Way)thing).add(nodes.get(id));
								break;
							case "member":
								id = Long.parseLong(parser.getAttributeValue(null, "ref"));
								assert thing != null;
								String role = parser.getAttributeValue(null, "role");
								Map<Long, ? extends Element> l = null;
								switch(parser.getAttributeValue(null, "type"))
								{
									case "node":
										l = nodes;
										break;
									case "way":
										l = ways;
										break;
								}
								if(l != null)
									((Relation) thing).add(role, l.get(id));
								break;
						}
						break;
					case XmlPullParser.END_TAG:
						if(thing != null && (tagName.equals("node") || tagName.equals("way") || tagName.equals("relation")))
						{
							if(thing.hasTag("amenity") || thing.hasTag("shop") || thing.hasTag("leisure") || thing.hasTag("tourism"))
							{
								places.add(thing);
								elementCache.put(new ElementSpec(thing), thing);
							}
							thing = null;
						}
						break;
				}
			}
			Collections.sort(places, new Comparator<Element>()
			{
				@Override
				public int compare(Element lhs, Element rhs)
				{
					return Double.compare(lhs.distanceTo(loc), rhs.distanceTo(loc));
				}
			});
			Map<Category,Collection<Element>> pp = new HashMap<>();
			for(Element element : places)
			{
				for(PlaceType pt : PlaceType.values())
				{
					if(pt.belongs(element))
						element.type = pt;
				}
				Category category = getElementCategory(element);
				Collection<Element> l = pp.get(category);
				if(l == null)
				{
					l = new ArrayList<>();
					pp.put(category, l);
				}
				l.add(element);
			}
			lastResult = pp;
			lastLatitude = latitude;
			lastLongitude = longitude;
			return pp;
		} catch(XmlPullParserException|IOException e)
		{
			throw new RuntimeException(e);
		} finally
		{
			if(s != null) try { s.close(); } catch(IOException ignored) { }
		}
	}

	static Category getElementCategory(Element element)
	{
		if(element.type != null)
			return element.type.category;
		String v;
		v = element.getTag("shop");
		if (v != null) switch(v)
		{
			case "convenience":
				return Category.MARKET;
		}
		v = element.getTag("tourism");
		if (v != null) switch(v)
		{
			case "attraction":
			case "museum":
			case "gallery":
			case "zoo":
			case "theme_park":
				return Category.ENTERTAINMENT;
		}
		return Category.OTHER;
	}

	public static Element get(ElementSpec elementSpec)
	{
		return elementCache.get(elementSpec);
	}
}
