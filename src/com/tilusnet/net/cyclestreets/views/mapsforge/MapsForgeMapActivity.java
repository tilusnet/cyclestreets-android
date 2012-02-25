package com.tilusnet.net.cyclestreets.views.mapsforge;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;

import android.os.Bundle;

public class MapsForgeMapActivity extends MapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		MapView mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
// 		mapView.setMapFile("/sdcard/external_sd/mapping/data/mapsforge/london.osm.map");
		mapView.setMapFile("/sdcard/external_sd/mapping/data/mapsforge/bremen-0.2.4.map");
		setContentView(mapView);
	}
	
	

}
