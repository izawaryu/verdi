package org.tensorflow.verdi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.izawaryu.verdi.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(mainToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maps_observe:
                makeObservation();
                return true;
            case R.id.maps_collection:
                viewCollection();
                return true;
            case R.id.maps_account:
                accountSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makeObservation() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void viewCollection() {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }

    public void accountSettings() {
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        markLocations();
    }

    private void markLocations() {
        // Gets the data repository in read-mode.
        SQLiteHelper SQLHelper = new SQLiteHelper(this);
        SQLiteDatabase db = SQLHelper.getReadableDatabase();

        // Reading from the local SQLite database
        // Define a projection: the columns queried
        String[] projection = {
                SchemaContract.SchemaCollection._ID,
                SchemaContract.SchemaCollection.COLUMN_NAME_2,
                SchemaContract.SchemaCollection.COLUMN_NAME_3,
                SchemaContract.SchemaCollection.COLUMN_NAME_4,
                SchemaContract.SchemaCollection.COLUMN_NAME_5
        };

        // DEMO: WHERE filter
        String selection = SchemaContract.SchemaCollection.COLUMN_NAME_1 + " = ?";
        String[] selectionArgs = { "some_criteria" };

        // DEMO: Sort clause
        String sortOrder =
                SchemaContract.SchemaCollection._ID + " DESC";

        Cursor cursor = db.query(
                SchemaContract.SchemaCollection.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {

            LatLng mystery = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
            mMap.addMarker(new MarkerOptions()
                    .position(mystery)
                    .title(cursor.getString(4) + " " + cursor.getString(3)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mystery));

        }
    }
}
