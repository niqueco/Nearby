package ar.com.lichtmaier.nearby;

public enum Category
{
	EAT_AND_DRINK(R.string.cat_eat_and_drink), BANK(R.string.cat_bank),
	MARKET(R.string.cat_market), ENTERTAINMENT(R.string.cat_entertainment),
	OTHER(R.string.cat_other);

	Category(int descId)
	{
		this.descId = descId;
	}

	public final int descId;
}
