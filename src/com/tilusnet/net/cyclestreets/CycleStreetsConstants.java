package com.tilusnet.net.cyclestreets;

public class CycleStreetsConstants {
  // API Key
  public static final String API_KEY = "120175c44303728f";

  // Intent constants
  public static final int ACTIVITY_GET_ENDPOINTS = 1;
	public static final String EXTRA_PHOTO = "net.cyclestreets.extra.PHOTO";
	public static final String EXTRA_ROUTE_TYPE = "net.cyclestreets.extra.ROUTE_TYPE";
	public static final String EXTRA_ROUTE_SPEED = "net.cyclestreets.extra.ROUTE_SPEED";
	public static final String EXTRA_ROUTE_NUMBER = "net.cyclestreets.extra.ROUTE";
	
	public static final String ROUTE_ID = "net.cyclestreets.extra.ROUTE_ID";

	// Route types 
	public final static String PLAN_BALANCED = "balanced";
	public final static String PLAN_FASTEST = "fastest";
	public final static String PLAN_QUIETEST = "quietest";
	public final static String PLAN_SHORTEST = "shortest";       
};