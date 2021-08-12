package com.sanaltebesir.sanaltebesirtutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class myAccount extends AppCompatActivity {

    private String[] hesapMenu =
            {"Profil Bilgileri", "İstatistikler", "Cüzdanım","Çıkış"};

    private String[] hesapMenu2 =
            {"Kullanım Şartları", "Gizlilik İlkeleri", "Sıkça Sorulan Sorular", "Destek"};
    private SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionHandler(getApplicationContext());

        //(A) adımı
        final ListView listemiz=(ListView) findViewById(R.id.list1);
        ListView listemiz2=(ListView) findViewById(R.id.list2);

        //(B) adımı
        ArrayAdapter<String> veriAdaptoru=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, hesapMenu);
        ArrayAdapter<String> veriAdaptoru2=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, hesapMenu2);

        // Add a header to the ListView
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header,listemiz2,false);
        listemiz2.addHeaderView(header);

        //(C) adımı
        listemiz.setAdapter(veriAdaptoru);
        listemiz2.setAdapter(veriAdaptoru2);

        listemiz.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (position == 0) {
                    Intent myIntent = new Intent(view.getContext(), myProfile.class);
                    startActivity(myIntent);
                }
                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), myStatistics.class);
                    startActivity(myIntent);
                }
                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), myWallet.class);
                    startActivity(myIntent);
                }
                if (position == 3) {
                    //logout codes will be added here
                    session.logoutUser();
                    Intent i = new Intent(myAccount.this, signinActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        listemiz2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), userTerms.class);
                    startActivity(myIntent);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), privacyTerms.class);
                    startActivity(myIntent);
                }
                if (position == 3) {
                    Intent myIntent = new Intent(view.getContext(), faqPage.class);
                    startActivity(myIntent);
                }
                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), supportPage.class);
                    startActivity(myIntent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
