/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.
 * Modifications copyright 2017 Ryu Izawa. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.verdi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class RecognitionScoreView extends View implements ResultsView {

  private final static String TAG = ClassifierActivity.class.getSimpleName();

  private static final float TEXT_SIZE_DIP = 24;
  private List<Classifier.Recognition> results;
  private final float textSizePx;
  private final Paint fgPaint;
  private final Paint bgPaint;
  private static Context activityContext;
  private static Activity activity;
  protected SQLiteDatabase dbw;
  protected String mCurrentPhotoPath;
  static final int REQUEST_TAKE_PHOTO = 1;

  public RecognitionScoreView(final Context context, final AttributeSet set) {
    super(context, set);

    textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    fgPaint = new Paint();
    fgPaint.setColor(0xccffffff);
    fgPaint.setTextSize(textSizePx);

    bgPaint = new Paint();
    bgPaint.setColor(0xcc00cc00);

    activityContext = context;
    activity = (Activity) context;
  }

  @Override
  public void setResults(final List<Classifier.Recognition> results) {
    this.results = results;

    if (results != null) {
        final Classifier.Recognition recog = results.get(0);
        if (recog.getConfidence() > 0.8f) {

            // Gets the data repository in write-mode.
            SQLiteHelper SQLHelper = new SQLiteHelper(getContext());
            SQLiteDatabase db = SQLHelper.getWritableDatabase();

            String[] phylogeny = recog.getTitle().split(" ");
            String phylogeny_Genus = phylogeny[0];
            String phylogeny_Species = phylogeny[1];

            String date = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date());
            String user = "tester";
            String latitude = String.valueOf(CameraActivity.lastLocation.getLatitude());
            String longitude = String.valueOf(CameraActivity.lastLocation.getLongitude());
            String species = phylogeny_Species;
            String genus = phylogeny_Genus;

            // Create a new map of values, where the column names are the keys.
            ContentValues values = new ContentValues();
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_0, date);
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_1, user);
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_2, latitude);
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_3, longitude);
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_4, species);
            values.put(SchemaContract.SchemaCollection.COLUMN_NAME_5, genus);

            // Insert the new row, returning the primary key value of the new row.
            long newRowId = db.insert(SchemaContract.SchemaCollection.TABLE_NAME, null, values);

            // Take a picture of anything over the confidence threshold
            dispatchTakePictureIntent(
                    genus,
                    species,
                    "0000-01-01");
        }
    }

    postInvalidate();
  }

  private File createImageFile(String genus, String species, String date) throws IOException {
      // Create an image file name
      String imageFileName = "verdi_" + genus + "_" + species + "_" + date;
      File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
      File image = File.createTempFile(
              imageFileName,    /*prefix*/
              ".jpg",           /*suffix*/
              storageDir        /*directory*/
      );

      Log.i(TAG, "* * * * * * * * * " + image.getName());
      mCurrentPhotoPath = image.getAbsolutePath();
      return image;
  }

  private void dispatchTakePictureIntent(String genus, String species, String date) {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Ensure that there is a camera activity to handle the intent.
      if (takePictureIntent.resolveActivity(activityContext.getPackageManager()) != null) {
          // Create a file where the photo should go
          File photoFile = null;
          try {
              photoFile = createImageFile(genus, species, date);
          } catch (IOException ex) {
              // If there's an error
          }

          // Continue only if the file was successfully created
          if (photoFile != null) {
              Uri photoURI = FileProvider.getUriForFile(activityContext, "org.tensorflow.verdi.imgfileprovider", photoFile);
              takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
              activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
          }

          Log.i(TAG, "* * * * * * * * * dispatchTakePictureIntent");
      }
  }

  @Override
  public void onDraw(final Canvas canvas) {
    final int x = canvas.getWidth() / 5;
    int y = (int) (canvas.getHeight() / 2);

    canvas.drawCircle(x, y, 100, bgPaint);

    if (results != null) {
      for (final Classifier.Recognition recog : results) {
        String[] ident = recog.getTitle().split(" ");

        if (recog.getConfidence() > 0.8f) {
            canvas.drawText(Math.round(recog.getConfidence() * 100) + "%", (x - fgPaint.getTextSize() * 0.8f), y + (fgPaint.getTextSize() * 0.4f), fgPaint);
            canvas.drawText(ident[0], (canvas.getWidth() / 3), y - (fgPaint.getTextSize() * 0.3f), fgPaint);
            canvas.drawText(ident[1], (canvas.getWidth() / 3), y + (fgPaint.getTextSize() * 0.9f), fgPaint);
        } else if (recog.getConfidence() > 0.5f) {
            canvas.drawText("?", (x - fgPaint.getTextSize() * 0.2f), y + (fgPaint.getTextSize() * 0.4f), fgPaint);
            canvas.drawText(ident[0], (canvas.getWidth() / 3), y - (fgPaint.getTextSize() * 0.3f), fgPaint);
            canvas.drawText(ident[1], (canvas.getWidth() / 3), y + (fgPaint.getTextSize() * 0.9f), fgPaint);
        } else {
            canvas.drawText("attempting", (canvas.getWidth() / 3), y - (fgPaint.getTextSize() * 0.4f), fgPaint);
            canvas.drawText("to classify...", (canvas.getWidth() / 3), y + (fgPaint.getTextSize() * 0.9f), fgPaint);
        }
      }
    }
  }
}



