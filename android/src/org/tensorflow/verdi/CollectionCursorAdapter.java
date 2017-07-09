package org.tensorflow.verdi;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.izawaryu.verdi.R;

import static java.lang.Math.abs;

/**
 * Created by rizawa on 6/28/2017.
 * https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter
 */

public class CollectionCursorAdapter extends CursorAdapter {

    public CollectionCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate and return a new view.
    // Data is not bound to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_collection, parent, false);
    }

    // The bindView method is used to bind data to the view,
    // such as setting text on a TextView.
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template.
        TextView text = (TextView) view.findViewById(R.id.item_text);

        Double Lat = cursor.getDouble(cursor.getColumnIndexOrThrow("Latitude"));
        Double Lon = cursor.getDouble(cursor.getColumnIndexOrThrow("Longitude"));

        String NS = new String();
        String EW = new String();

        if (Lat < 0) {
            NS = "S";
        } else {
            NS = "N";
        }


        if (Lon < 0) {
            EW = "W";
        } else {
            EW = "E";
        }

        String LatLon =
                Math.abs(Math.round(Lat*100000.0)/100000.0) + " " + NS + " " +
                Math.abs(Math.round(Lon*100000.0)/100000.0) + " " + EW ;

        // Extract properties from cursor
        String string = cursor.getString(cursor.getColumnIndexOrThrow("_id")) + ": " +
                cursor.getString(cursor.getColumnIndexOrThrow("OnDate")) + " \n" +
                cursor.getString(cursor.getColumnIndexOrThrow("Genus")) + " " +
                cursor.getString(cursor.getColumnIndexOrThrow("Species")) + " \n" +
                LatLon;

        // Populate fields with extracted data
        text.setText(string);
    }
}
