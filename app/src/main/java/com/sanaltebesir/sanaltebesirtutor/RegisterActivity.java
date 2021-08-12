package com.sanaltebesir.sanaltebesirtutor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import static com.sanaltebesir.sanaltebesirtutor.uploadAnswer.UPLOAD_KEY;

public class RegisterActivity extends AppCompatActivity {

    private String register_url = "http://www.sanaltebesir.com/android/tutor/register.php";
    public static final String UPLOAD_URL = "http://www.sanaltebesir.com/android/tutor/certificateUpload.php";
    public static final String UPLOAD_KEY = "image";
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_LECTURE = "lecture";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IMAGENAME = "imagename";
    private static final String KEY_EMPTY = "";
    public int PICK_IMAGE_REQUEST = 1;
    public static final int STORAGE_PERMISSION_CODE = 123;
    public Bitmap bitmap;
    public Uri filePath;
    private EditText etUsername;
    private EditText etPhone;
    private Spinner etLecture;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etFullName;
    private TextView tvSelectfile;
    private String username;
    private String phone;
    private String imagename;
    public String [] lectures = {"Matematik","Geometri","Fizik","Kimya","Biyoloji","Fen Bilgisi","Türkçe","Sosyal Bilgiler","Tarih","Coğrafya","Felsefe ve Din Bilgisi","İngilizce","Fransızca","Almanca"};
    private String password;
    private String confirmPassword;
    private String fullName;
    public ProgressDialog pDialog;
    public SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etLecture = findViewById(R.id.etLecture);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        tvSelectfile = findViewById(R.id.tvSelectfile);

        Button login = findViewById(R.id.btnRegisterLogin);
        Button register = findViewById(R.id.btnRegister);

        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), lectures);
        etLecture.setPrompt("Lütfen Branş Seçiniz...");
        etLecture.setAdapter(new NothingSelectedSpinnerAdapter(adapter, R.layout.register_nothing_selected, getApplicationContext()));
        //etLecture.setAdapter(adapter);

        //Launch Login screen when Login Button is clicked
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, signinActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //Retrieve the data entered in the edit texts
                    username = etUsername.getText().toString().toLowerCase().trim();
                    phone = etPhone.getText().toString().trim();
                    imagename = tvSelectfile.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    confirmPassword = etConfirmPassword.getText().toString().trim();
                    fullName = etFullName.getText().toString().trim();

                    if (validateInputs()) {

                        uploadImage();

                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        tvSelectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Select file provider
                showFileChooser();
            }
        });
        //Requesting storage permission
        requestStoragePermission();
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                String filename = getFileName(filePath);
                tvSelectfile.setText(filename);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(){

        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            RequestHandler rh = new RequestHandler();
            @Override
            protected void onPreExecute(){

                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(RegisterActivity.this);
                pDialog.setMessage("Lütfen bekleyiniz...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {

                bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("name", getFileName(filePath));

                String result = rh.postRequest(UPLOAD_URL,data);
                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (pDialog.isShowing())
                    pDialog.dismiss();
                registerUser();
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    //method to get imagename from URI
    public String getFileName(Uri uri) {
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

    public String getStringImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
/*
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
*/
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    /**
     * Display Progress bar while registering
     */
    private void displayLoader() {
        pDialog = new ProgressDialog(RegisterActivity.this);
        pDialog.setMessage("Kayıt yapılıyor.. Lütfen bekleyiniz...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    /**
     * Launch Dashboard Activity on Successful Sign Up
     */
   /* private void loadDashboard() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();

    }*/

    private void loadSignin() {
        Intent i = new Intent(getApplicationContext(), signinActivity.class);
        startActivity(i);
        finish();

    }

    private void registerUser() {

        displayLoader();
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, username);
            request.put(KEY_PASSWORD, password);
            request.put(KEY_FULL_NAME, fullName);
            request.put(KEY_PHONE, phone);
            request.put(KEY_IMAGENAME, getFileName(filePath));
            request.put(KEY_LECTURE, etLecture.getSelectedItem().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //Check if user got registered successfully
                            if (response.getInt(KEY_STATUS) == 0) {
                                //Set the user session
                                //session.loginUser(username,userid);
                                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                                loadSignin();

                            }else if(response.getInt(KEY_STATUS) == 1){
                                //Display error message if username is already existsing
                                etUsername.setError("Kullanıcıadı zaten alınmış!");
                                etUsername.requestFocus();

                            }else{
                                Toast.makeText(getApplicationContext(), response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            // e-posta formatı kontrol
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {

        if (KEY_EMPTY.equals(fullName)) {
            etFullName.setError("Ad Soyad boş olamaz");
            etFullName.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(phone)) {
            etPhone.setError("Telefon numarası boş olamaz");
            etPhone.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(imagename)) {
            tvSelectfile.setError("Dosya alanı boş olamaz");
            tvSelectfile.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(username)) {
            etUsername.setError("Kullanıcıadı boş olamaz");
            etUsername.requestFocus();
            return false;
        }
        if (isValidEmail(username)!=true) {
            etUsername.setError("Geçerli bir eposta adresi giriniz");
            etUsername.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(password)) {
            etPassword.setError("Şifre boş olamaz");
            etPassword.requestFocus();
            return false;
        }

        if (KEY_EMPTY.equals(confirmPassword)) {
            etConfirmPassword.setError("Şifre tekrar boş olamaz");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Şifre ve Şifre Tekrar uyuşmuyor");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}
