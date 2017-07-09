package org.tensorflow.verdi;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.izawaryu.verdi.R;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private List<String> listValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        listValues = new ArrayList<String>();

        // Gets the data repository in read-mode.
        SQLiteHelper SQLHelper = new SQLiteHelper(this);
        SQLiteDatabase db = SQLHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM tbl_Collection", null);

        ListView listView = (ListView) findViewById(R.id.collection);
        CollectionCursorAdapter adapter = new CollectionCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }
}



