package com.testify.ecfeed.adapter;

public interface ITypeAdapter {
	public boolean compatible(String type);
	// returns null if conversion is not possible
	public String convert(String value);
	public String defaultValue();
	public boolean isNullAllowed();
}
