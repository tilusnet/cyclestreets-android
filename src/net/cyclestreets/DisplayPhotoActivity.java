package net.cyclestreets;

import net.cyclestreets.util.ImageDownloader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayPhotoActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showphoto);
		
		final Intent i = getIntent();

		final ImageView iv = (ImageView)findViewById(R.id.photo);
		final WindowManager wm = getWindowManager();
		final int height = wm.getDefaultDisplay().getHeight() / 10 * 4;
		final int width = wm.getDefaultDisplay().getWidth();
		iv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		iv.startAnimation(AnimationUtils.loadAnimation(this, R.anim.spinner));

		final TextView text = (TextView)findViewById(R.id.photo_text);
		text.setText(i.getStringExtra("caption"));

		final Uri uri = i.getData();
		ImageDownloader.get(uri.toString(), iv);
	} // onCreate
	
	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_UP)
			finish();
		return false;
	} // onTouchEvent
} // DisplayPhotoActivity
