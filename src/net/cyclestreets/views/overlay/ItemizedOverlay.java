package net.cyclestreets.views.overlay;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Derived from osmdroid code by Marc Kurtz, Nicolas Gramlich, Theodore Hong, Fred Eisele
 * @param <Item>
 */
public class ItemizedOverlay<Item extends OverlayItem> extends Overlay 
{
    public static interface OnItemGestureListener<Item>
    {
        public boolean onItemSingleTapUp(final int index, final Item item);
        public boolean onItemLongPress(final int index, final Item item);
    } // interface OnItemGestureListener<Item>

	private final List<Item> items_;
	private final OnItemGestureListener<Item> itemListener_;
	private final Rect mRect = new Rect();
	private final Point mCurScreenCoords = new Point();
	private final Point mTouchScreenPoint = new Point();
	private final Point mItemPoint = new Point();

	public ItemizedOverlay(final Context context, 
						   final List<Item> items, 
						   final OnItemGestureListener<Item> listener) 
	{
		super(new DefaultResourceProxyImpl(context));
		items_ = items;
		itemListener_ = listener;
	} // ItemizedOverlay

	@Override
	public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) 
	{
		if(shadow)
			return;

		final Projection pj = mapView.getProjection();

		for (int i = items_.size() -1; i >= 0; i--) 
		{
			final Item item = items_.get(i);
			pj.toMapPixels(item.mGeoPoint, mCurScreenCoords);
			onDrawItem(canvas, item, mCurScreenCoords);
		} // for ...
	} // draw

	protected void onDrawItem(final Canvas canvas, final Item item, final Point curScreenCoords) 
	{
		final HotspotPlace hotspot = item.getMarkerHotspot();
		final Drawable marker = item.getMarker(0);
		boundToHotspot(marker, hotspot);

		// draw it
		Overlay.drawAt(canvas, 
					   marker, 
					   curScreenCoords.x, 
					   curScreenCoords.y, 
					   false);
	} // onDrawItem

	/**
	 * See if a given hit point is within the bounds of an item's marker. Override to modify the way
	 * an item is hit tested. The hit point is relative to the marker's bounds. The default
	 * implementation just checks to see if the hit point is within the touchable bounds of the
	 * marker.
	 * 
	 * @param item
	 *            the item to hit test
	 * @param marker
	 *            the item's marker
	 * @param hitX
	 *            x coordinate of point to check
	 * @param hitY
	 *            y coordinate of point to check
	 * @return true if the hit point is within the marker
	 */
	protected boolean hitTest(Item item, android.graphics.drawable.Drawable marker, int hitX,
			int hitY) {
		return marker.getBounds().contains(hitX, hitY);
	}

	protected synchronized Drawable boundToHotspot(Drawable marker, HotspotPlace hotspot) {
		int markerWidth = (int) (marker.getIntrinsicWidth() * mScale);
		int markerHeight = (int) (marker.getIntrinsicHeight() * mScale);

		mRect.set(0, 0, 0 + markerWidth, 0 + markerHeight);

		if (hotspot == null)
			hotspot = HotspotPlace.BOTTOM_CENTER;

		switch (hotspot) {
		default:
		case NONE:
			break;
		case CENTER:
			mRect.offset(-markerWidth / 2, -markerHeight / 2);
			break;
		case BOTTOM_CENTER:
			mRect.offset(-markerWidth / 2, -markerHeight);
			break;
		case TOP_CENTER:
			mRect.offset(-markerWidth / 2, 0);
			break;
		case RIGHT_CENTER:
			mRect.offset(-markerWidth, -markerHeight / 2);
			break;
		case LEFT_CENTER:
			mRect.offset(0, -markerHeight / 2);
			break;
		case UPPER_RIGHT_CORNER:
			mRect.offset(-markerWidth, 0);
			break;
		case LOWER_RIGHT_CORNER:
			mRect.offset(-markerWidth, -markerHeight);
			break;
		case UPPER_LEFT_CORNER:
			mRect.offset(0, 0);
			break;
		case LOWER_LEFT_CORNER:
			mRect.offset(0, markerHeight);
			break;
		}
		marker.setBounds(mRect);
		return marker;
	}
	
	@Override
	public boolean onSingleTapUp(final MotionEvent event, final MapView mapView) {
		return (activateSelectedItems(event, mapView, new ActiveItem() {
			@Override
			public boolean run(final int index) {
				final ItemizedOverlay<Item> that = ItemizedOverlay.this;
				return onSingleTapUpHelper(index, that.items_.get(index), mapView);
			}
		})) ? true : super.onSingleTapUp(event, mapView);
	}

	protected boolean onSingleTapUpHelper(final int index, final Item item, final MapView mapView) 
	{
		return itemListener_.onItemSingleTapUp(index, item);
	} // onSingleTapUpHelper

	@Override
	public boolean onLongPress(final MotionEvent event, final MapView mapView) 
	{
		return (activateSelectedItems(event, mapView, new ActiveItem() {
			@Override
			public boolean run(final int index) {
				final ItemizedOverlay<Item> that = ItemizedOverlay.this;
				return onLongPressHelper(index, that.items_.get(index));
			}
		})) ? true : super.onLongPress(event, mapView);
	}

	protected boolean onLongPressHelper(final int index, final Item item) 
	{
		return this.itemListener_.onItemLongPress(index, item);
	} 

	/**
	 * When a content sensitive action is performed the content item needs to be identified. This
	 * method does that and then performs the assigned task on that item.
	 * 
	 * @param event
	 * @param mapView
	 * @param task
	 * @return true if event is handled false otherwise
	 */
	private boolean activateSelectedItems(final MotionEvent event, final MapView mapView,
			final ActiveItem task) {
		final Projection pj = mapView.getProjection();
		final int eventX = (int) event.getX();
		final int eventY = (int) event.getY();

		/* These objects are created to avoid construct new ones every cycle. */
		pj.fromMapPixels(eventX, eventY, mTouchScreenPoint);

		for (int i = 0; i < items_.size(); ++i) {
			final Item item = items_.get(i);
			final Drawable marker = item.getMarker(0);

			pj.toPixels(item.getPoint(), mItemPoint);

			if (hitTest(item, marker, mTouchScreenPoint.x - mItemPoint.x, mTouchScreenPoint.y
					- mItemPoint.y)) {
				if (task.run(i)) {
					return true;
				}
			}
		}
		return false;
	}

	public static interface ActiveItem {
		public boolean run(final int aIndex);
	}

} // class ItemizedOverlay
