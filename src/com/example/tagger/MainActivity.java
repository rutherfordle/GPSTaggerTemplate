package com.example.tagger;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
//import android.view.View.OnTouchListener;

import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import com.example.tagger.GPSTracker;
import com.example.tagger.PreferenceConnector;
import com.example.tagger.R;
import com.example.tagger.CompassItem;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnClickListener, LocationListener  {
	
//Declarations
	static private final String LOG_TAG = "MainActivity";
	static private final String SERVER_URL = "http://crowdlab.soe.ucsc.edu/tagstore/default/";
	static private final int MAX_SETUP_DOWNLOAD_TRIES = 2;
	boolean flag=false;
	private LocationManager locationManager;
	private String provider;
	private Handler counterHandler = new Handler();
	Button tag1, tag2, tag3;
	GPSTracker gps;
	private double mLat;
	private double mLon;
	private float currLat = (float) 37.000899;
	private float currLng = (float) -122.062989 ;
	
  
  private ListView mainListView ;
  private ArrayAdapter<String> listAdapter ;

 
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
	tag1 = (Button) findViewById(R.id.tag1);
	tag2 = (Button) findViewById(R.id.tag2);
	tag3 = (Button) findViewById(R.id.tag3);
	tag1.setOnClickListener(yourListener);
	tag2.setOnClickListener(yourListener);
	tag3.setOnClickListener(yourListener);
    mainListView = (ListView) findViewById( R.id.listView1 );



	
	
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	// Define the criteria how to select the locatioin provider -> use
	// default
	Criteria criteria = new Criteria();
	provider = locationManager.getBestProvider(criteria, false);
 	Location location = locationManager.getLastKnownLocation(provider);
	// Initialize the location fields
	if (location != null) {
		System.out.println("Provider " + provider + " has been selected.");
		onLocationChanged(location);
	} 

    ArrayList<String> planetList = new ArrayList<String>();
    
    // Create ArrayAdapter
    listAdapter = new ArrayAdapter<String>(this, R.layout.item_row, planetList);
    
    // Set the ArrayAdapter as the ListView's adapter.
    mainListView.setAdapter( listAdapter );      


    getTag();
    LetsCount();
    mainListView.setOnItemClickListener(new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
      	  
      	  // selected item 
      	  String product = ((TextView) view).getText().toString();
      	String[] separated = product.split(":");
      	
      	 String bearing =separated[1].trim(); 
      	  // Launching new Activity on selecting single List Item
      	  Intent i = new Intent(getApplicationContext(), CompassItem.class);
      	  // sending data to new activity
      	  i.putExtra("product", product);
      	  i.putExtra("Dest", bearing);
      	  startActivity(i);
      	
        }
      });
    
  }
  
  View.OnClickListener yourListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
    	  String buttonText;
    	  Button b;
    	  switch(v.getId()) {
  		case R.id.tag1:
	        b = (Button)v;
	       buttonText = b.getText().toString();
	        
	        sendTag(buttonText);
  			break;

  		case R.id.tag2:
  			b = (Button)v;
	        buttonText = b.getText().toString();
	        //Toast.makeText(getApplicationContext(), buttonText, Toast.LENGTH_SHORT).show();

	        sendTag(buttonText);

  			break;
  		case R.id.tag3:
  			b = (Button)v;
	        buttonText = b.getText().toString();
	        //Toast.makeText(getApplicationContext(), buttonText, Toast.LENGTH_SHORT).show();
	        sendTag(buttonText);
  			break;
    	  }
    	  
      }
   };

   
   public String findLocal(double lat, double lng, float loc){

//apply values to locations
	      if(loc > 0 && loc < 90){   

	          	  return "NE";
	            
	         }

	         else if(loc>90 && loc<180){
	      	   return "SE";
	   
	         }
	         else if(loc>180 && loc<270){
	      		   return "SW";
	      	
	         }
	         else if(loc>270 && loc<360){
	      	   
	  		   return "NW";
	  	   
	     }
	         else if(loc==90){
	      	   return "E";
	         }
	         else if(loc==270){
	      	   return "W";

	         }
	         else if(loc==0 || loc==360){
	   
	      		   return "N";
	         }
	         else if(loc==180){

	      		   return "S";

	         }
	   

       return "Error";
   
   }
   
   private void sendTag(String Usertag){
	        checkGPS();
	        String lat=Float.toString(currLat);
	        String lng=Float.toString(currLng);

	        
	        if(currLat==0.0 || currLng==0.0){
	           lat="37.2";
	           lng="120.2";
	           //Toast.makeText(getApplicationContext(), "emp", Toast.LENGTH_SHORT).show();
	        
	        }
	        lat = String.format("%.2f", currLat);
	        lng = String.format("%.2f", currLng);
//set location range
	        
			lat = Float.toString(currLat);
			
			lng = Float.toString(currLng);
		     lat = String.valueOf(mLat);
		     lng = String.valueOf(mLon);
			
			// Let us build the parameters.
			ServerCallParams serverParams = new ServerCallParams();
			serverParams.url = "add_tagging.json";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", "CMPS121_yehaa"));
			params.add(new BasicNameValuePair("user", "luca"));
			//params.add(new BasicNameValuePair("nick", "Roma"));
			params.add(new BasicNameValuePair("lat", lat));
			params.add(new BasicNameValuePair("lng", lng));
			params.add(new BasicNameValuePair("tag", Usertag));
			serverParams.params = params;
			serverParams.continuation = new ContinuationAddTag();
			ContactServer contacter = new ContactServer();
			contacter.execute(serverParams);
   
   }
  
	private void LetsCount(){
		counterHandler.postDelayed(TextViewChanger, 10000);
	}
	private Runnable TextViewChanger = new Runnable(){
	    	public void run() {
	    		getTag();
	    		LetsCount();
	    	}
	    };
 
  

  
  public void uploadTag(View v) throws UnsupportedEncodingException {
	  check(v);
  String lats = String.format("%.2f", currLat);
      String lngs = String.format("%.2f", currLng);
      lats = lats.replace(" ", "");
      lats = lats.trim();
      
      lngs = lngs.replace(" ", "");
      lngs = lngs.trim();

      
     String k = lats.toString();
     String m = lngs.toString();
     k = String.valueOf(mLat);
     m = String.valueOf(mLon);
	  
		// Let us build the parameters.
		ServerCallParams serverParams = new ServerCallParams();
		serverParams.url = "add_tagging.json";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", "CMPS121_yehaa"));
		params.add(new BasicNameValuePair("user", "Mr_Sleepy"));
		params.add(new BasicNameValuePair("lat", k));
		params.add(new BasicNameValuePair("lng", m ));

		params.add(new BasicNameValuePair("tag", "hello"));
		serverParams.params = params;
		serverParams.continuation = new ContinuationAddTag();
		ContactServer contacter = new ContactServer();
		contacter.execute(serverParams);
	}
  
  public void check(View v){
      gps = new GPSTracker(MainActivity.this);

		// check if GPS enabled		
      if(gps.canGetLocation()){
      	
      	mLat = gps.getLatitude();
      	mLon = gps.getLongitude();
      	
      }else{
//error, can't get location here
      	gps.showSettingsAlert();
      }  
  
  }
  public void checkGPS(){
      gps = new GPSTracker(MainActivity.this);

		// check if GPS enabled		
      if(gps.canGetLocation()){
      	
      	mLat = gps.getLatitude();
      	mLon = gps.getLongitude();
      	
      	// \n is for new line
      	//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
      }else{
      	// can't get location
      	// GPS or Network is not enabled
      	// Ask user to enable GPS/network in settings
      	gps.showSettingsAlert();
      }  
	  
  }
  
  public void menu(View v){
		Intent i = new Intent("com.example.tagger.Menu");

		startActivity(i);  
		

  }
	
	class ContinuationAddTag implements Continuation {
		ContinuationAddTag() {
			
		}
		
		public void useString(String s) {
			if (s == null) {
				Log.d(LOG_TAG, "Returned an empty string.");
			} else {
				Log.d(LOG_TAG, "Returned: " + s);
			}
		}
	}

	public void getTags(View v) {
		// Let us build the parameters.
		
		String lat = Float.toString(currLat);
		lat = String.valueOf(mLat);
		String latM = Float.toString((float) (mLat + 10.3));
		//String latM = Float.toString((float) (currLat + 3.3));
		
		String lng = Float.toString(currLng);
		lng = String.valueOf(mLon);
		
		String lngM = Float.toString((float) (mLon + 10.3));
		//String lngM = Float.toString((float) (currLng + 3.3));
		
		ServerCallParams serverParams = new ServerCallParams();
		serverParams.url = "get_tags.json";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", "CMPS121_yehaa"));
		params.add(new BasicNameValuePair("user", "Mr_Sleepy"));
		params.add(new BasicNameValuePair("lat_min", lat));
		params.add(new BasicNameValuePair("lng_min", lng));
		params.add(new BasicNameValuePair("lat_max", latM));
		params.add(new BasicNameValuePair("lng_max", lngM));
		params.add(new BasicNameValuePair("n_taggings", "20"));
		serverParams.params = params;
		serverParams.continuation = new ContinuationGetTagList();
		ContactServer contacter = new ContactServer();
		contacter.execute(serverParams);
	}
	public void getTag() {
		checkGPS();
		// Let us build the parameters.

		String lat = Float.toString(currLat);
		lat = String.valueOf(mLat);
		String latM = Float.toString((float) (mLat + 3.3));
		
		String lng = Float.toString(currLng);
		lng = String.valueOf(mLon);
		
		String lngM = Float.toString((float) (mLon + 3.3));

		
		ServerCallParams serverParams = new ServerCallParams();
		serverParams.url = "get_tags.json";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("token", "CMPS121_yehaa"));
		params.add(new BasicNameValuePair("user", "Mr_Sleepy"));
		params.add(new BasicNameValuePair("lat_min", lat));
		params.add(new BasicNameValuePair("lng_min", lng));
		params.add(new BasicNameValuePair("lat_max", latM));
		params.add(new BasicNameValuePair("lng_max", lngM));
		params.add(new BasicNameValuePair("n_taggings", "20"));
		serverParams.params = params;
		serverParams.continuation = new ContinuationGetTagList();
		ContactServer contacter = new ContactServer();
		contacter.execute(serverParams);
	}
	
	class ContinuationGetTagList implements Continuation {
		public ContinuationGetTagList() {}

		public void useString(String s) {
			// Dejasonize the string.
			if (s == null) {
				Log.d(LOG_TAG, "Returned an empty string.");
			} else {
				Log.d(LOG_TAG, "Returned: " + s);
			    NearbyTags newTags = decodeNearbyTags(s);
			    if (newTags != null) {
			    	// We would have to replace the list in the array adaptor.
			    	Log.d(LOG_TAG, "The dejsonizing succeeded");
			    	Log.d(LOG_TAG, "N. of tags:" + newTags.tags);
			    	
			  
		             mainListView.setAdapter(null);
	                 listAdapter.clear();
	                
			    	for(int index =0; index!=newTags.tags.length;index++){
			    		Location locationA = new Location ("Current Location");
			    		locationA.setLatitude(mLat);
			    		locationA.setLongitude(mLon);
			    		Location locationB = new Location("point B");
			    		locationB.setLatitude(newTags.tags[index].lat);
			    		locationB.setLongitude(newTags.tags[index].lng);
			    		
			    		float distance = locationA.distanceTo(locationB);
			    		float foot =(float) (distance*3.28084);
			    		//String dist = Float.toString(distance);
			    		String dist = String.valueOf(foot);
			    		String tmp = findLocal(newTags.tags[index].lat,newTags.tags[index].lng, locationA.bearingTo(locationB));
			    		listAdapter.add(String.valueOf(newTags.tags[index].nick)+ "/ "+ String.valueOf(newTags.tags[index].tag)+ "("+ dist +" ft. "+ tmp +" ) " +": "+String.valueOf(locationA.bearingTo(locationB))+":deg");
			 
	  
			    	}
			    	mainListView.setAdapter( listAdapter );
			    	
			    	
			
			    }
				
			}
		}
	}
	
	
	
	interface Continuation {
		void useString(String s);
	}
	
	class ServerCallParams {
		public String url; // for example: get_tags.json
		public List<NameValuePair> params;
		public Continuation continuation;
	}
	
	class FinishInfo {
		public Continuation continuation;
		public String value;
	}

	// This class executed an http call to the server. 
	// You need to pass to it the ServerCallParams, containing the method to call,
	// a list of parameters for the call, and what to do afterwards (the continuation).
  private class ContactServer extends AsyncTask<ServerCallParams, String, FinishInfo> {

  	protected FinishInfo doInBackground(ServerCallParams... callParams) {
  		Log.d(LOG_TAG, "Starting the download.");
  		String downloadedString = null;
  		int numTries = 0;
  		ServerCallParams callInfo = callParams[0];
  		List<NameValuePair> params = callInfo.params;
			FinishInfo info = new FinishInfo();
			info.continuation = callInfo.continuation;
  		while (downloadedString == null && numTries < MAX_SETUP_DOWNLOAD_TRIES && !isCancelled()) {
  			numTries++;
  			HttpClient httpclient = new DefaultHttpClient();
  		    HttpPost httppost = new HttpPost(SERVER_URL + callInfo.url);
  	        HttpResponse response = null; 
  			try {
      	        httppost.setEntity(new UrlEncodedFormEntity(params));
      	        // Execute HTTP Post Request
  				response = httpclient.execute(httppost);
  			} catch (ClientProtocolException ex) {
  				Log.e(LOG_TAG, ex.toString());
  			} catch (IOException ex) {
  				Log.w(LOG_TAG, ex.toString());
  			}
  			if (response != null) {
  				// Checks the status code.
  				int statusCode = response.getStatusLine().getStatusCode();
  				Log.d(LOG_TAG, "Status code: " + statusCode);

  				if (statusCode == HttpURLConnection.HTTP_OK) {
  					// Correct response. Reads the real result.
  					// Extracts the string content of the response.
  					HttpEntity entity = response.getEntity();
  					InputStream iStream = null;
  					try {
  						iStream = entity.getContent();
  					} catch (IOException ex) {
  						Log.e(LOG_TAG, ex.toString());
  					}
  					if (iStream != null) {
  						downloadedString = ConvertStreamToString(iStream);
  						Log.d(LOG_TAG, "Received string: " + downloadedString);
  						// Passes the string, along with the continuation, to onPostExecute.
  						info.value = downloadedString;
  				    	return info;
  					}
  				}
  			}
  		}
  		// Returns null to indicate failure.
  		info.value = null;
  		return info;
  	}
  	
  	protected void onProgressUpdate(String... s) {}
  	
  	protected void onPostExecute(FinishInfo info) {
  		// Do something with what you get. 
  		if (info != null) {
  			info.continuation.useString(info.value);
  		} else {
  			// This is just an example: we can pass back null to the continuation
  			// to indicate that no string was in fact received.
  			info.continuation.useString(null);
  		}
  	}
  }
  
  // Here is an example of how to decode a JSON string.  We will decode the taglist.
  // First, we declare a class for the info on one tag.
  // Note that if you want to have this accessible from multiple activities, as it might be
  // a good idea to do, it might be better to define this as a public class in its own file,
  // rather than here. 
  class TagInfo {
  	public double lat;
  	public double lng;
  	public String nick;
  	public String tag;
  }
  // Now we create a class for the overall message.
  class NearbyTags {
  	public TagInfo[] tags;
  }
  
  // Decoding a received tag string is then simple.
  private NearbyTags decodeNearbyTags(String s) {
  	if (s == null) {
  		// Your choice of what to do; returning null may be a simple way to 
  		// propagate the fact that the call to the server failed.
  		return null;
  	}
  	// Gets a gson object for decoding a string.
  	Gson gson = new Gson();
  	NearbyTags tags = null;
  	try {
  		tags = gson.fromJson(s, NearbyTags.class);
  	} catch (JsonSyntaxException ex) {
  		Log.w(LOG_TAG, "Error decoding json: " + s + " exception: " + ex.toString());
  	}
  	return tags;
  }
  
  
  @Override
  public void onStop() {
  	// Cancel what you have to cancel.
  	super.onStop();
  }
  
  public static String ConvertStreamToString(InputStream is) {
  	
  	if (is == null) {
  		return null;
  	}
  	
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append((line + "\n"));
	        }
	    } catch (IOException e) {
	        Log.d(LOG_TAG, e.toString());
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            Log.d(LOG_TAG, e.toString());
	        }
	    }
	    return sb.toString();
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 10000, 0, this);
		
		Button Button1 = (Button) findViewById(R.id.tag1);
		Button Button2 = (Button) findViewById(R.id.tag2);
		Button Button3 = (Button) findViewById(R.id.tag3);
        whichName(Button1,PreferenceConnector.readString(this,PreferenceConnector.tag1, null));
        whichName(Button2,PreferenceConnector.readString(this,PreferenceConnector.tag2, null));
        whichName(Button3,PreferenceConnector.readString(this,PreferenceConnector.tag3, null));
		//locationManager.requestLocationUpdates(provider, 400, 1, this);
        

        
	}
	public void whichName(Button btn, String pref){
		if(pref != null && pref.length()!=0){
			btn.setText(pref);
		}
		else{
			if(btn.getText().toString().trim() == ""){
			btn.setText("TAG");
			}
		}
		
	
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		//37.000899, -122.062989
		currLat = (float) 37.000899;
		currLng = (float) -122.062989 ;
		if(location.getLatitude()==0.0){
			currLat = (float) 37.000899;
		}else{
        currLat = (float) location.getLatitude();
		}
		if(location.getLongitude()==0.0){
         currLng = (float) -122.062989 ;
		}
		currLat = (float) 37.000899;
		currLng = (float) -122.062989 ;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}