package com.tilusnet.net.cyclestreets.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osmdroid.api.IGeoPoint;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import android.graphics.drawable.Drawable;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class POICategory
{
  private final String key_;
  private final String shortName_;
  private final String name_;
  private final Drawable icon_;
  
  public POICategory(final String key,
                     final String shortName,
                     final String name, 
                     final Drawable icon)
  {
    key_ = key;
    shortName_ = shortName;
    name_ = name;
    icon_ = icon;
  } // POICategory
  
  public String shortName() { return shortName_; }
  public String name() { return name_; }
  public Drawable icon() { return icon_; }
  
  public boolean equals(final Object rhs)
  {
    if(!(rhs instanceof POICategory))
      return false;
    
    return name_.equals(((POICategory)rhs).name_);
  } // equals

  public List<POI> pois(final IGeoPoint centre,
                        final int radius)
    throws Exception
  {
    try {
      final List<POI> pois = ApiClient.getPOIs(key_, 
                                               centre.getLongitudeE6() / 1E6,
                                               centre.getLatitudeE6() / 1E6,
                                               radius);
      for(final POI poi : pois)
        poi.setCategory(this);
      return pois;
    } // try
    catch(Exception e) {
      return Collections.emptyList();
    } // catch
  } // pois
  
  static public Factory<List<POI>> factory() { 
    return new POIFactory();
  } // factory
  
  static private class POIFactory extends Factory<List<POI>>
  {
    private List<POI> pois_;
    private int id_;
    private String name_;
    private String notes_;
    private String url_;
    private double lat_;
    private double lon_;
    
    @Override
    protected ContentHandler contentHandler()
    {
      pois_ = new ArrayList<POI>();
      
      final RootElement root = new RootElement("pois");
      final Element item = root.getChild("pois").getChild("poi");
      item.setStartElementListener(new StartElementListener() {
        @Override
        public void start(Attributes attributes)
        {
          id_ = 0;
          name_ = null;
          notes_ = null;
          url_ = null;
          lat_ = 0;
          lon_ = 0;
        } // start
      });
      item.setEndElementListener(new EndElementListener(){
          public void end() {
            pois_.add(new POI(id_, name_, notes_, url_, lat_, lon_));
          }
      });
      item.getChild("id").setEndTextElementListener(new EndTextElementListener() {
        public void end(String body) {
          id_ = Integer.parseInt(body);
        }
      });
      item.getChild("longitude").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            lon_ = Double.parseDouble(body);
          }
      });
      item.getChild("latitude").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            lat_ = Double.parseDouble(body);
          }
      });
      item.getChild("name").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            name_ = body;
          }
      });
      item.getChild("notes").setEndTextElementListener(new EndTextElementListener(){
        public void end(String body) {
          notes_ = body;
        }
      });
      item.getChild("website").setEndTextElementListener(new EndTextElementListener(){
        public void end(String body) {
          url_ = body;
        }
      });

      return root.getContentHandler();
    } // contentHandler

    @Override
    protected List<POI> get()
    {
      return pois_;
    } // get    
  } // class POIFactory
} // class POICategory
