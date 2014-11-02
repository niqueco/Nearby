package ar.com.lichtmaier.nearby;

import java.util.regex.Pattern;

import ar.com.lichtmaier.osm.Element;

public enum PlaceType
{
	BANK("amenity", "bank", R.string.type_bank, Category.BANK),

	CASINO("amenity", "casino", R.string.type_casino, Category.ENTERTAINMENT),
	CINEMA("amenity", "cinema", R.string.type_cinema, Category.ENTERTAINMENT),
	THEATRE("amenity", "theatre", R.string.type_theatre, Category.ENTERTAINMENT),

	SUPERMARKET("shop", "supermarket", R.string.type_supermarket, Category.MARKET),

	FAST_FOOD("amenity", "fast_food", R.string.type_fast_food, Category.EAT_AND_DRINK),
	ICE_CREAM("amenity", "ice_cream", R.string.type_ice_cream, Category.EAT_AND_DRINK) {
		@Override
		public boolean belongs(Element element)
		{
			return "cafe".equals(element.getTag("amenity")) && "ice_cream".equals(element.getTag("cuisine")) || super.belongs(element);
		}
	},
	CAFE("amenity", "cafe", R.string.type_cafe, Category.EAT_AND_DRINK),
	PUB("amenity", "pub", R.string.type_pub, Category.EAT_AND_DRINK),
	RESTAURANT("amenity", "restaurant", R.string.type_restaurant, Category.EAT_AND_DRINK);

	final String key;
	int description;
	final Pattern valuePattern;
	final Category category;

	PlaceType(String key, String valuePattern, int description, Category category)
	{
		this.key = key;
		this.description = description;
		this.valuePattern = Pattern.compile(valuePattern);
		this.category = category;
	}

	public boolean belongs(Element element)
	{
		String v = element.getTag(key);
		return v != null && valuePattern.matcher(v).matches();
	}
}
