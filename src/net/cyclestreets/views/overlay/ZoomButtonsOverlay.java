package net.cyclestreets.views.overlay;

import net.cyclestreets.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class ZoomButtonsOverlay extends Overlay 
							    implements TapListener
{
	private final MapView mapView_;
	private final OverlayButton zoomIn_;
	private final OverlayButton zoomOut_;
	
	public ZoomButtonsOverlay(final Context context, 
							  final MapView mapView)
	{
		super(context);
		
		mapView_ = mapView;

		final int offset = OverlayHelper.offset(context);	
		final float radius = OverlayHelper.cornerRadius(context);
		
		final Resources res = context.getResources();
			zoomIn_ = new OverlayButton(res.getDrawable(R.drawable.btn_plus),
				offset,
				offset,
				radius);
		zoomIn_.rightAlign().bottomAlign();

		zoomOut_ = new OverlayButton(res.getDrawable(R.drawable.btn_minus),
				 zoomIn_.right() + offset,
				 offset,
				 radius);
		zoomOut_.rightAlign().bottomAlign();
	} // ZoomButtonsOverlay
	
	@Override
	protected void draw(final Canvas canvas, final MapView mapView, final boolean shadow) 
	{
	} // draw
	
	public void drawButtons(final Canvas canvas, final MapView mapView)
	{
		zoomIn_.enable(mapView.canZoomIn());
		zoomIn_.draw(canvas);
		zoomOut_.enable(mapView.canZoomOut());
		zoomOut_.draw(canvas);
	} // drawButtons
	
	//////////////////////////////////////////////
	@Override
    public boolean onSingleTap(final MotionEvent event) 
	{
    	return tapZoom(event);
    } // onSingleTapUp
	
	public boolean onDoubleTap(final MotionEvent event)
	{
		return zoomIn_.hit(event) || zoomOut_.hit(event);
	} // onDoubleTap

    private boolean tapZoom(final MotionEvent event)
	{
		if(zoomIn_.hit(event))
		{
			if(zoomIn_.enabled())
				mapView_.getController().zoomIn();
			return true;
		} // if ...
		if(zoomOut_.hit(event))
		{
			if(zoomOut_.enabled())
				mapView_.getController().zoomOut();
			return true;
		} // if ...
		
		return false;
	} // tapPrevNext
} // class ZoomButtonsOverlay
