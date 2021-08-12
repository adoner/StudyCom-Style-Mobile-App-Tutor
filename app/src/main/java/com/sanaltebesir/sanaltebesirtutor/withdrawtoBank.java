package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class withdrawtoBank extends AppCompatActivity {

    private static String phpUrl = "http://www.sanaltebesir.com/android/tutor/sendTransaction.php";
    private static final String KEY_EMPTY = "";
    public JSONObject json;
    public ProgressDialog pDialog;
    private SessionHandler session;
    JSONParser jsonParser = new JSONParser();
    public String userid;
    public EditText etIban;
    public EditText etName;
    public EditText etBirthdate;
    public EditText etAmount;
    public Button btnSubmit;
    public String iban;
    public String name;
    public String birthdate;
    public String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawto_bank);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(withdrawtoBank.this);
        userid = session.getUserDetails().userid;
        etIban = findViewById(R.id.etIban);
        etName = findViewById(R.id.etName);
        etBirthdate = findViewById(R.id.etBirthdate);
        etAmount = findViewById(R.id.etAmount);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iban = etIban.getText().toString().trim();
                name = etName.getText().toString().trim();
                birthdate = etBirthdate.getText().toString().trim();
                amount = etAmount.getText().toString().trim();

                if(validateInputs()) {
                    new sendTransaction().execute();
                }
            }
        });
    }

    private class sendTransaction extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(withdrawtoBank.this);
            pDialog.setMessage("Lütfen bekleyiniz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            try{

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userid", userid));
                params.add(new BasicNameValuePair("iban", iban));
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("birthdate", birthdate));
                params.add(new BasicNameValuePair("amount", amount));
                json = jsonParser.makeHttpRequest(phpUrl, "POST", params);

            }catch(Exception e){

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            finish();
            Intent intent = new Intent(withdrawtoBank.this, myWallet.class);
            startActivity(intent);

        }
    }

    /**
     * Validates inputs and shows error if any
     * @return
     */
    private boolean validateInputs() {

        if (KEY_EMPTY.equals(iban)) {
            etIban.setError("IBAN no boş olamaz");
            etIban.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(name)) {
            etName.setError("Adı soyadı boş olamaz");
            etName.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(birthdate)) {
            etBirthdate.setError("Doğum tarihi boş olamaz");
            etBirthdate.requestFocus();
            return false;

        }
        if (KEY_EMPTY.equals(amount)) {
            etAmount.setError("Miktar boş olamaz");
            etAmount.requestFocus();
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
