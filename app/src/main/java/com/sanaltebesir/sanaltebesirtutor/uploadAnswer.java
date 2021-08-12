package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class uploadAnswer extends AppCompatActivity {

    public static final String UPLOAD_URL = "http://www.sanaltebesir.com/android/tutor/uploadPhoto.php";
    public static final String UPLOAD_KEY = "image";
    private SessionHandler session;
    private Bitmap bitmap;
    public String userid;
    public Uri contentUri = null;
    public ImageView mImageView;
    public Button goon;
    public String mCurrentPhotoPath;
    public String questionid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_answer);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());
        Intent intent = getIntent();
        questionid = intent.getStringExtra("id");
        userid = session.getUserDetails().userid;
        mImageView = findViewById(R.id.bitmapImageview);
        goon = findViewById(R.id.button_goon);
        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);

        goon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do it
                uploadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            File photoFile = null;
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            try {
                contentUri = Uri.parse(result.getUri().toString());
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(result.getUri().toString()));
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (resultCode == RESULT_OK) {
                //(mImageView).setImageURI(result.getUri());
                mImageView.setImageBitmap(bitmap);
                //Toast.makeText(getApplicationContext(), result.getUri().toString(), Toast.LENGTH_LONG).show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadImage(){

        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                Intent intent = new Intent(getApplicationContext(), uploadToServer.class);
                intent.putExtra("questionid", questionid);
                intent.putExtra("imagename", getFileName(contentUri));
                startActivity(intent);
                //loading = ProgressDialog.show(qCamUpload.this, "Resim Yükleniyor", "Lütfen bekleyiniz...",true,false);

            }

            @Override
            protected String doInBackground(Bitmap... params) {

                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);

                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("name", getFileName(contentUri));
                data.put("id", questionid);

                String result = rh.postRequest(UPLOAD_URL,data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                File f = new File(mCurrentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                SharedPreferences preferences=getSharedPreferences("UploadProgress",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("progress", false);
                editor.putString("notification", s);
                editor.commit();
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    public String getStringImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
