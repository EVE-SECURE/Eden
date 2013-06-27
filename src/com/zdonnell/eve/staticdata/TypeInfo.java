package com.zdonnell.eve.staticdata;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "typeInfo")
public class TypeInfo {

	@DatabaseField(id = true)
	public int typeID;

	@DatabaseField
	public int groupID;

	@DatabaseField
	public double volume;

	@DatabaseField
	public int marketGroupID = -1;

	@DatabaseField
	public String typeName;

	@DatabaseField
	public String description;
}
