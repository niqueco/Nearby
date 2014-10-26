package ar.com.lichtmaier.osm;

import android.os.Parcel;
import android.os.Parcelable;

/** A lightweight and parcelable reference to a OSM element.
 */
public class ElementSpec implements Parcelable
{
	public enum ElementType { NODE, WAY, RELATION }

	final ElementType type;
	final long id;

	public ElementSpec(Element element)
	{
		if(element instanceof Node)
		{
			type = ElementType.NODE;
		} else if(element instanceof Way)
		{
			type = ElementType.WAY;
		} else if(element instanceof Relation)
		{
			type = ElementType.RELATION;
		} else
			throw new RuntimeException(String.valueOf(element));
		id = element.id;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(!(o instanceof  ElementSpec))
			return false;
		ElementSpec that = (ElementSpec)o;
		return id == that.id && type == that.type;

	}

	@Override
	public int hashCode()
	{
		return 31 * type.hashCode() + (int) (id ^ (id >>> 32));
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(type.ordinal());
		dest.writeLong(id);
	}

	public static final Parcelable.Creator<ElementSpec> CREATOR = new Parcelable.Creator<ElementSpec>() {

		@Override
		public ElementSpec createFromParcel(Parcel source)
		{
			return new ElementSpec(source);
		}

		@Override
		public ElementSpec[] newArray(int size)
		{
			return new ElementSpec[size];
		}
	};

	private ElementSpec(Parcel in)
	{
		type = ElementType.values()[in.readInt()];
		id = in.readLong();
	}
}
