package com.example.mycalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<Events> arrayList ;
    DBOpenHelper dbOpenHelper;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
    final Events events = arrayList.get(position);
        holder.Event.setText(events.getEvents());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(events.getEvents(),events.getDATE(),events.getTIME());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });



        if(isAlarmed(events.getDATE(),events.getEvents(),events.getTIME())){
            holder.SetAlarm.setImageResource(R.drawable.ic_action_notification_on);

        }else{
            holder.SetAlarm.setImageResource(R.drawable.ic_action_notification_off);

        }


        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(ConvertStringToDate(events.getDATE()));
        final int alarmYear = dateCalendar.get(Calendar.YEAR);
        final int alarmMont = dateCalendar.get(Calendar.MONTH);
        final int alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
        Calendar timeCalendar = Calendar.getInstance();
        dateCalendar.setTime(ConvertStringToTime(events.getTIME()));
        final int alarmHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        final int alarmMinute = timeCalendar.get(Calendar.MINUTE);



        holder.SetAlarm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(isAlarmed(events.getDATE(),events.getEvents(),events.getTIME())){
                   holder.SetAlarm.setImageResource(R.drawable.ic_action_notification_off);
                   cancelAlarm(getRequestCode(events.getDATE(),events.getEvents(),events.getTIME()));
                   updateEvent(events.getDATE(),events.getEvents(),events.getTIME(), "off");
                   notifyDataSetChanged();
               }else{

                   holder.SetAlarm.setImageResource(R.drawable.ic_action_notification_on);
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.set(alarmYear,alarmMont,alarmDay,alarmHour,alarmMinute);
                   setAlarm(alarmCalendar,events.getEvents(),events.getTIME(),getRequestCode(events.getDATE(),
                           events.getEvents(),events.getTIME()));
                   updateEvent(events.getDATE(),events.getEvents(),events.getTIME(),"on");
                    notifyDataSetChanged();
               }
           }
       }
        );

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public class  MyViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt , Event, Time;
        Button delete;
        ImageButton SetAlarm;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt = itemView.findViewById(R.id.eventdate);
            Event = itemView.findViewById(R.id.eventname);
            Time = itemView.findViewById(R.id.eventtime);
            delete = itemView.findViewById(R.id.delete);
            SetAlarm = itemView.findViewById(R.id.alarm_id);

        }
    }




    private void deleteEvent(String event, String date, String time){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event,date,time,database);
        dbOpenHelper.close();
    }


    private Date ConvertStringToTime(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.FRENCH);

        Date date = null ;

        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

        Date date = null ;

        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }



    private boolean isAlarmed(String date,String event,String time){
        boolean alarmed = false;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvent(date,event,time, database);
        while(cursor.moveToNext()){
          String notify = cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
          if(notify.equals("on")){
              alarmed = true;
          }else{
              alarmed = false;
          }

        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed;
    }



    private void setAlarm(Calendar calendar, String event, String time, int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event", event);
        intent.putExtra("time", time);
        intent.putExtra("id", RequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }





    private void cancelAlarm(int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    private int getRequestCode(String date, String event,String time){
        int code =0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvent(date,event,time, database);
        while(cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));

        }
        cursor.close();
        dbOpenHelper.close();
        return  code;

    }



    private void updateEvent(String date, String event,String time, String notify){
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        dbOpenHelper.updateEvent(date,event,time,notify,database);
        dbOpenHelper.close();

    }

}
