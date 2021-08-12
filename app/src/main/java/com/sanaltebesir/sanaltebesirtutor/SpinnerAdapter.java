package com.sanaltebesir.sanaltebesirtutor;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    String[] cities;
    Context context;
    //String[] colors = {"#13edea","#e20ecd","#15ea0d","#13edea","#e20ecd","#15ea0d","#13edea","#e20ecd","#15ea0d","#13edea","#e20ecd","#15ea0d","#13edea","#e20ecd","#15ea0d"};
    //String[] colorsback = new String[]{"#FF000000", "#FFF5F1EC", "#ea950d","#FF000000", "#FFF5F1EC", "#ea950d","#FF000000", "#FFF5F1EC", "#ea950d","#FF000000", "#FFF5F1EC", "#ea950d","#FF000000", "#FFF5F1EC", "#ea950d"};

    public SpinnerAdapter(Context context, String[] cities) {
        this.cities = cities;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cities.length;
    }

    @Override
    public Object getItem(int position) {
        return cities[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view =  View.inflate(context, R.layout.cities_main, null);
        TextView textView = (TextView) view.findViewById(R.id.main);
        textView.setText(cities[position]);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view;
        view =  View.inflate(context, R.layout.cities_dropdown, null);
        final TextView textView = (TextView) view.findViewById(R.id.dropdown);
        textView.setText(cities[position]);

        //textView.setTextColor(Color.parseColor(colors[position]));
        //textView.setBackgroundColor(Color.parseColor(colorsback[position]));


        return view;
    }
}
