package net.cyclestreets;

import java.util.List;
import java.util.ArrayList;

import org.osmdroid.events.DelayedMapListener;
import net.cyclestreets.views.overlay.ItemizedOverlay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PhotomapActivity extends CycleMapActivity
							implements ItemizedOverlay.OnItemGestureListener<PhotoItem> 	
{
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);

        final List<PhotoItem> photoList = new ArrayList<PhotoItem>();
        
        final ItemizedOverlay<PhotoItem> markers = 
        			new ItemizedOverlay<PhotoItem>(this,
        										   photoList,
       										   	   this);
        overlayPushBottom(markers);
        
        mapView().setMapListener(new DelayedMapListener(new PhotomapListener(this, mapView(), photoList)));
    } // onCreate

	//////////////////////////////////////////////
	public boolean onItemLongPress(int i, final PhotoItem item) 
	{
		showPhoto(item);
		return true;
	} // onItemLongPress
		
	public boolean onItemSingleTapUp(int i, final PhotoItem item) 
	{
		showPhoto(item);
		return true;
	} // onItemSingleTapUp
	
	private void showPhoto(final PhotoItem item)
	{
		final Intent intent = new Intent(PhotomapActivity.this, DisplayPhotoActivity.class);
		intent.setData(Uri.parse(item.photo.thumbnailUrl));
		intent.putExtra("caption", item.photo.caption);
		startActivity(intent);
	} // showPhoto
} // PhotomapActivity
