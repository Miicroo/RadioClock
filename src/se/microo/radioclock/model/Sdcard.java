package se.microo.radioclock.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Sdcard {

	private Context ctx;
	
	public Sdcard(Context c) {
		if(c == null) {
			throw new IllegalArgumentException("Cannot find sdcard from: null");
		}
		
		this.ctx = c;
	}

	public List<MediaData> scanSdcard(){
		List<MediaData> data = new ArrayList<MediaData>();
	    String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
	    String[] projection = {
	            MediaStore.Audio.Media.TITLE,
	            MediaStore.Audio.Media.ARTIST,
	            MediaStore.Audio.Media.DATA,
	            MediaStore.Audio.Media.DISPLAY_NAME,
	            MediaStore.Audio.Media.DURATION
	    };
	    final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

	    Cursor cursor = null;
	    try {
	        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	        cursor = ctx.getContentResolver().query(uri, projection, selection, null, sortOrder);
	        if( cursor != null){
	            cursor.moveToFirst();
	            while( !cursor.isAfterLast() ){
	                String title = cursor.getString(0);
	                String artist = cursor.getString(1);
	                String path = cursor.getString(2);
	                String displayName  = cursor.getString(3);
	                String songDuration = cursor.getString(4);
	                
	                MediaData media = new MediaData(title, artist, path, displayName, songDuration);
	                data.add(media);
	                
	                cursor.moveToNext();
	            }

	        }

	    } catch (Exception e) {
	        Log.e("Error retrieving sdcard music", e.toString());
	    }finally{
	        if( cursor != null){
	            cursor.close();
	        }
	    }
	    
	    return data;
	}
}
