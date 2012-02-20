package com.tilusnet.net.cyclestreets.util;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloader 
{
	static public void get(final String url, 
						   final ImageView imageView) 
	{
		final BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute(url);
	} // get

	//////////////////////////
	static class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> 
	{
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(final ImageView imageView) 
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		} // BitmapDownloaderTask

		@Override
		protected Bitmap doInBackground(String... params) 
		{
			return downloadBitmap(params[0]);
		} // doInBackground

		@Override
		protected void onPostExecute(final Bitmap bitmap) 
		{
			if(isCancelled()) 
				return;
		  
			if(imageViewReference == null)
				return;
			
			final ImageView imageView = imageViewReference.get();
			if (imageView == null) 
				return;

			imageView.setAnimation(null);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setBackgroundColor(Color.BLACK);
			imageView.setPadding(0, 8, 0, 0);
			imageView.setImageBitmap(bitmap);
		} // onPostExecute

		private Bitmap downloadBitmap(final String url) 
		{
			final HttpClient client = new DefaultHttpClient();
			final HttpGet getRequest = new HttpGet(url);
			getRequest.setHeader("User-Agent", "CycleStreets Android/1.0");

			try 
			{
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) 
					return null;

				final HttpEntity entity = response.getEntity();
				if (entity == null) 
					return null;
				
				try 
				{
					final InputStream inputStream = entity.getContent();
					return Bitmaps.loadStream(inputStream);
				} // try
				finally 
				{
					entity.consumeContent();
				} // finally
			} // try 
			catch (Exception e) 
			{
				getRequest.abort();
			} // catch
			return null;
		} // downloadBitmap
	} // BitmapDownloaderTask 
} // ImageDownloader
