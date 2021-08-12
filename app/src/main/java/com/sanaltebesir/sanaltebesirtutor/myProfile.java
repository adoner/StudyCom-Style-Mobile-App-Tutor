package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class myProfile extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/tutorprofile.php";
    private static String phpUrl2 = "http://www.sanaltebesir.com/android/tutor/profileEdit.php";
    private static String phpUrl3 = "http://www.sanaltebesir.com/android/tutor/setSwitchStatus.php";
    public String [] cities = {"Adana","Adıyaman","Afyonkarahisar","Ağrı","Amasya","Ankara","Antalya","Artvin","Aydın","Balıkesir","Bilecik","Bingöl","Bitlis","Bolu","Burdur","Bursa","Çanakkale",
            "Çankırı","Çorum","Denizli","Diyarbakır","Edirne","Elazığ","Erzincan","Erzurum","Eskişehir","Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Isparta","Mersin",
            "İstanbul","İzmir","Kars","Kastamonu","Kayseri","Kırklareli","Kırşehir","Kocaeli","Konya","Kütahya","Malatya","Manisa","Kahramanmaraş","Mardin","Muğla","Muş","Nevşehir",
            "Niğde","Ordu","Rize","Sakarya","Samsun","Siirt","Sinop","Sivas","Tekirdağ","Tokat","Trabzon","Tunceli","Şanlıurfa","Uşak","Van","Yozgat","Zonguldak","Aksaray","Bayburt",
            "Karaman","Kırıkkale","Batman","Şırnak","Bartın","Ardahan","Iğdır","Yalova","Karabük","Kilis","Osmaniye","Düzce"
    };
    private static final String KEY_EMPTY = "";
    private JSONObject json;
    private ProgressDialog pDialog;
    public String tutorid;
    public Spinner spCities;
    private SessionHandler session;
    public ArrayList<HashMap<String, String>> tutorList;
    JSONParser jsonParser = new JSONParser();
    public EditText etName;
    public EditText etEmail;
    public TextView etPhone;
    public Button btCalender;
    public EditText etBirthdate;
    public Spinner etCity;
    public String name;
    public String email;
    public String birthdate;
    public Button editProfile;
    public Switch switch1;
    public Switch switch2;
    public String switch1Status;
    public String switch2Status;
    public Spinner spCity;
    public int selectionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.profile_list_item, null );*/

        session = new SessionHandler(getApplicationContext());
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etCity = findViewById(R.id.spCity);
        etBirthdate = findViewById(R.id.birthdate);
        btCalender = findViewById(R.id.btCalender);
        editProfile = findViewById(R.id.updateButton);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        spCity = findViewById(R.id.spCity);
        tutorList = new ArrayList<>();
        tutorid = session.getUserDetails().userid;
        new GetTutorProfile().execute();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = etName.getText().toString();
                email = etEmail.getText().toString();
                if(validateInputs()) {
                    new editProfile().execute();
                }
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    //Do what you want
                    switch1Status = "1";
                }else{
                    switch1Status = "0";
                }

                new SetSwitchStatus().execute();
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    //Do what you want
                    switch2Status = "1";
                }else{
                    switch2Status = "0";
                }

                new SetSwitchStatus().execute();
            }
        });
        btCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
                int month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz

                DatePickerDialog datePicker;//Datepicker objemiz
                datePicker = new DatePickerDialog(myProfile.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        etBirthdate.setText( dayOfMonth + "-" + monthOfYear + "-" + year);//Ayarla butonu tıklandığında textview'a yazdırıyoruz

                    }
                },year,month,day);//başlarken set edilcek değerlerimizi atıyoruz

                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

                datePicker.show();

            }
        });
    }

    private void setSpinner () {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,cities);
        spCity.setAdapter(adapter);
        selectionPosition= adapter.getPosition(tutorList.get(0).get("city"));
        spCity.setSelection(selectionPosition);

    }

    private class GetTutorProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myProfile.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("tutorid", tutorid));
                json = jsonParser.makeHttpRequest(phpUrl,
                        "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            try {

                JSONArray arr = json.getJSONArray("tutorprofile");

                for(int i = 0; i<arr.length();i++){

                    String name = arr.getJSONObject(i).getString("name");
                    String email = arr.getJSONObject(i).getString("email");
                    String phone = arr.getJSONObject(i).getString("phone");
                    String city = arr.getJSONObject(i).getString("city");
                    String birthdate = arr.getJSONObject(i).getString("birthdate");
                    String notfQrequest = arr.getJSONObject(i).getString("notfQrequest");
                    String notfGeneral = arr.getJSONObject(i).getString("notfGeneral");

                    HashMap<String, String> qList = new HashMap<>();

                    // adding each child node to HashMap key => value
                    qList.put("name", name);
                    qList.put("email", email);
                    qList.put("phone", phone);
                    qList.put("city", city);
                    qList.put("birthdate", birthdate);
                    qList.put("notfQrequest", notfQrequest);
                    qList.put("notfGeneral", notfGeneral);

                    // adding contact to contact list
                    tutorList.add(qList);

                }

            }catch (JSONException e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                setSpinner ();
                etName.setText(tutorList.get(0).get("name"));
                etEmail.setText(tutorList.get(0).get("email"));
                etPhone.setText(tutorList.get(0).get("phone"));
                etBirthdate.setText(tutorList.get(0).get("birthdate"));
                spCities = findViewById(R.id.spCity);

                switch(tutorList.get(0).get("notfQrequest")){
                    case "1":
                        switch1.setChecked(true);
                        break;
                    case "0":
                        switch1.setChecked(false);
                        break;
                }

                switch(tutorList.get(0).get("notfGeneral")){
                    case "1":
                        switch2.setChecked(true);
                        break;
                    case "0":
                        switch2.setChecked(false);
                        break;
                }

            }catch(NullPointerException e){

                e.printStackTrace();

            }
        }
    }



    private class editProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myProfile.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... voids) {

            name = etName.getText().toString();
            email = etEmail.getText().toString();
            birthdate = etBirthdate.getText().toString();

            try{

                // Building Parameters
                List<NameValuePair> params2 = new ArrayList<>();
                params2.add(new BasicNameValuePair("tutorid", tutorid));
                params2.add(new BasicNameValuePair("name", name));
                params2.add(new BasicNameValuePair("email", email));
                params2.add(new BasicNameValuePair("birthdate", birthdate));
                params2.add(new BasicNameValuePair("city", spCity.getSelectedItem().toString()));
                json = jsonParser.makeHttpRequest(phpUrl2, "POST", params2);

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }

    class SetSwitchStatus extends AsyncTask<Void,Void,Void> {

        protected Void doInBackground(Void... voids){

            try{

                // Building Parameters
                List<NameValuePair> params3 = new ArrayList<>();
                params3.add(new BasicNameValuePair("tutorid", tutorid));
                params3.add(new BasicNameValuePair("switch1Status", switch1Status));
                params3.add(new BasicNameValuePair("switch2Status", switch2Status));
                json = jsonParser.makeHttpRequest(phpUrl3, "POST", params3);

            }catch(Exception e){

                e.printStackTrace();

            }
            return null;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            // e-posta formatı kontrol
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    private boolean validateInputs() {

        if (KEY_EMPTY.equals(name)) {
            etName.setError("Ad Soyad boş olamaz");
            etName.requestFocus();
            return false;
        }
        if (KEY_EMPTY.equals(email)) {
            etEmail.setError("Eposta boş olamaz");
            etEmail.requestFocus();
            return false;
        }
        if (isValidEmail(email)!=true) {
            etEmail.setError("Geçerli bir eposta adresi giriniz");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
