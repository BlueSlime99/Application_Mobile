package com.example.mycalendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentDate;
    List<Events> events;
    LayoutInflater inflater;



    public MyGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Events> events)
     {
        super(context, R.layout.single_cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.events = events;
        inflater  = LayoutInflater.from(context);

    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
     Date monthDate = dates.get(position);
     Calendar dateCalendar = Calendar.getInstance();
     dateCalendar.setTime(monthDate);
     int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
     int displayMont = dateCalendar.get(Calendar.MONTH)+1;
     int displayYear = dateCalendar.get(Calendar.YEAR);
     int currentMonth = currentDate.get(Calendar.MONTH) +1;
     int currentYear = currentDate.get(Calendar.YEAR);

     View view = convertView;
     if(view == null){
         view = inflater.inflate(R.layout.single_cell_layout,parent, false);
     }

     if(displayMont == currentMonth && displayYear == currentYear){
         view.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));

     }
     else{
         view.setBackgroundColor(Color.parseColor("#cccccc"));
     }

     TextView Day_Number = view.findViewById(R.id.calendar_day);
      TextView EventNumber = view.findViewById(R.id.events_id);
      Day_Number.setText(String.valueOf(DayNo));
      Calendar eventCalendar = Calendar.getInstance();
      ArrayList<String> arrayList = new ArrayList<>();
      for(int i = 0 ; i< events.size();i++){
          eventCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
           if(DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMont == eventCalendar.get(Calendar.MONTH) +1 && displayYear == eventCalendar.get(Calendar.YEAR)){
                    arrayList.add(events.get(i).getEvents());
                    EventNumber.setText( "Events");
               System.out.println(events.get(i).getDATE() + "EVENTLISTE " + i);

           }
        }

        return view;
    }


    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

        Date date = null ;

        try {
            date = format.parse(eventDate);
            System.out.println("TheparsingDate" + date);
        } catch (ParseException e) {
            System.out.println("NOparsingDate" + date);
            e.printStackTrace();
        }

        return date;

    }

    @Override
    public int getCount() {

        return dates.size();
    }


    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }


    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }


}
