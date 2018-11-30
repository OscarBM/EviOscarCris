package com.tecmi.oscar.calendardemo3;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import java.util.Calendar;
import android.icu.util.Calendar;

import java.util.TimeZone;

import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;


public class AddEventActivity extends AppCompatActivity implements View.OnClickListener {

    //Declarar EditTexts. Estos almacenaran los datos del evento
    private EditText eventName, eventLocation, startDate, endDate, startHour, endHour;
    private EditText description;
    private EditText search;

    //Declarar botones
    private Button btnAddEvent, btnCancelEvent;
    private Button btnList;
    private Button btnSearch;
    private Button btnHome3;
    private Button btnAlarmOn;
    private Button btnAlarmOff;
    private boolean alarmOn=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


            //asignar valores a los EditText. Aqui se almacenarán los datos del evento
            eventName = (EditText)findViewById(R.id.edtTitle);
            eventLocation = (EditText)findViewById(R.id.edtLocation);
            startDate = (EditText)findViewById(R.id.edtStartDate);
            startHour = (EditText)findViewById(R.id.edtStartHour);
            endDate = (EditText)findViewById(R.id.edtEndtDate);
            endHour = (EditText)findViewById(R.id.edtEndHour);
            description = (EditText)findViewById(R.id.edtDescription);

            search = (EditText)findViewById(R.id.edtSearch2);

            //No estoy seguro si estas lineas al final se ocupen
            Bundle bundle = getIntent().getExtras();
            int day = 0, month = 0, year = 0;
            bundle.getInt("day");
            bundle.get("month");
            bundle.get("year");


            //Asignar valores a los botones. Aqui se ligan las variables con sus respectivos botones en el archivo xml correspondiente a esta actividad
            btnAddEvent = (Button)findViewById(R.id.btnAddEvent);
            btnCancelEvent = (Button)findViewById(R.id.btnCancelEvent);
            btnList = (Button)findViewById(R.id.btnList);
            btnSearch = (Button)findViewById(R.id.btnSearch2);
            btnHome3 = (Button)findViewById(R.id.btnHome3);
            btnAlarmOn = (Button)findViewById(R.id.btnAlarmOn);
            btnAlarmOff = (Button)findViewById(R.id.btnAlarmOff);

            //Fijar los listener de los botones. Esto permite que los botones puedan realizar acciones al ser presionados.
            btnAddEvent.setOnClickListener(this);
            btnCancelEvent.setOnClickListener(this);
            btnList.setOnClickListener(this);
            btnSearch.setOnClickListener(this);
            btnHome3.setOnClickListener(this);
            btnAlarmOn.setOnClickListener(this);
            btnAlarmOff.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        //Como hay más de un botón, el metodo OnCLick priemro revisara el id del botón que llamó a este metodo para realizar las acciones correspondientes
        if(v.getId() == btnAddEvent.getId()){//Este if se ejecuta cuando el botón presionado fue el de agregar el evento a la agenda

            try{
                Calendar beginTime = Calendar.getInstance();
                String startDate1[] = startDate.getText().toString().split("/");//Aqui se hace un array de strings donde se almacenarán el dia, mes y año de la fecha de inicio del evento
                String startHour1[] = startHour.getText().toString().split(":");//Aqui se hace un array de strings donde se almacenarán la hora y los minutos de la fecha de inicio del evento
                beginTime.set(Integer.parseInt(startDate1[2]),//Aqui se fija el valor del año
                        Integer.parseInt(startDate1[1]) - 1,//Aqui se fija el valor del mes
                        Integer.parseInt(startDate1[0]),//Aqui se fija el valor del dia
                        Integer.parseInt(startHour1[0]),//Aqui se fija el valor de la hora
                        Integer.parseInt(startHour1[1]));//Aqui se fija el valor de los minutos

                Calendar endTime = Calendar.getInstance();
                String endDate1[] = endDate.getText().toString().split("/");//Aqui se hace un array de strings donde se almacenarán el dia, mes y año de la fecha de termino del evento
                String endHour1[] = endHour.getText().toString().split(":");//Aqui se hace un array de strings donde se almacenarán la hora y los minutos de la fecha de termino del evento
                endTime.set(Integer.parseInt(endDate1[2]),//Aqui se fija el valor del año
                        Integer.parseInt(endDate1[1]) - 1,//Aqui se fija el valor del mes
                        Integer.parseInt(endDate1[0]),//Aqui se fija el valor del dia
                        Integer.parseInt(endHour1[0]),//Aqui se fija el valor de la hora
                        Integer.parseInt(endHour1[1]));//Aqui se fija el valor de los minutos


                values.put(CalendarContract.Events.TITLE, eventName.getText().toString());
                values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
                values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());
                values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                //values.put(CalendarContract.Events.OWNER_ACCOUNT, MainActivity.activeAccount);
                values.put(CalendarContract.Events.CALENDAR_ID, MainActivity.calID1);
                //values.put(CalendarContract.Events.);

                //values.put(CalendarColumns.OWNER_ACCOUNT, MainActivity.activeAccount);
                //Log.d("mainAccount", CalendarContract.CalendarColumns.OWNER_ACCOUNT);

            /*
            //Esta intent es para añadir el evento nuevo a la agenda
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    //.putExtra(CalendarContract.Events.ACCOUNT_NAME, "oscarismaelbm98@gmail.com")
                    //.putExtra(CalendarContract.CalendarColumns.OWNER_ACCOUNT, MainActivity.activeAccount)
                    .putExtra(EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())//Aqui se fija la fecha (con hora y minutos) de inicio del evento
                    //.putExtra(CalendarContract.Events.HAS_ALARM, alarmOn)
                    .putExtra(EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())//Aqui se fija la fecha (con hora y minutos) de termino del evento
                    .putExtra(CalendarContract.Events.TITLE, eventName.getText().toString())//Aqui se fija el titulo del evento
                    .putExtra(CalendarContract.Events.DESCRIPTION, description.getText().toString())//Aqui se fija la descripción del evento
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString())//Aqui se fija la ubicación del evento
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            //.putExtra(CalendarContract.Events.CALENDAR_ID, 1);
            //.putExtra(CalendarContract.Calendars.CALENDAR_COLOR, Color.GREEN); //Aqui se intento poder modificar el color del evento. PENDIENTE
            startActivity(intent);//Aqui se ejecuta el intent para añadir el evento a la agenda
            */

                try{ if (alarmOn==true) {values.put(CalendarContract.Events.HAS_ALARM, true); }
                } catch (Exception e){Log.d("failAlarm", e.getMessage());}

                if (alarmOn == false){values.put(CalendarContract.Events.HAS_ALARM, 0);}
                values.put(CalendarContract.Events.EVENT_TIMEZONE,TimeZone.getDefault().getID());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                long eventID = Long.parseLong(uri.getLastPathSegment());

                if (alarmOn == true)
                {

                    ContentValues reminders = new ContentValues();
                    reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    reminders.put(CalendarContract.Reminders.MINUTES, 5);

                    Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                }

                if (alarmOn == false){
                    ContentValues reminders = new ContentValues();
                    reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    reminders.put(CalendarContract.Reminders.MINUTES, 0);

                    Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                }


            } catch (Exception e){
                Log.d("adicionFallo", e.getMessage());
            }

            //Aqui se crea un mensaje para avisar que ya se guardo la cuenta nueva
            Toast t = Toast.makeText(this, "Se añadio su evento nuevo", Toast.LENGTH_SHORT);
            //Aqui ya se despliega el mensaje
            t.show();

            //Quizas este try-catch no sea necesario

                //Una vez que el evento fue agregado a la agenda, se procedera a limpiar los textEdit.
               /* eventName.setText("");
                eventLocation.setText("");
                startDate.setText("");
                startHour.setText("");
                endDate.setText("");
                endHour.setText("");
                description.setText("");*/

            this.finish();


        } else if(v.getId() == btnList.getId()){
            Intent intent = new Intent(getApplication(), ListActivity.class);//Se crea la intent para poder cambiar de pantalla
            startActivity(intent);//Se ejecuta la intent para pasar a la pantalla de Lista de eventos (ListActivity.java)
        } else if(v.getId() == btnSearch.getId()){
            try{
                Intent intent = new Intent(getApplication(),SearchActivity.class);//Se crea la intent para poder cambiar de pantalla
                MainActivity.searchWord = search.getText().toString();//Se obtiene la palabra que esta en la barra de busqueda. Si no hya nada no hay filtro de palabras
                startActivity(intent);
            } catch(Exception e){
                Log.d("SALIOMAL31", e.getMessage());
            }
        } else if (v.getId() == btnHome3.getId()){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        } else if(v.getId() == btnAlarmOn.getId()){
            alarmOn = true;
        } else if(v.getId() == btnAlarmOff.getId()){
            alarmOn = false;
        }

        else {
            //Este else if es para cancelar guardar el evento.
            this.finish();
            return;
        }
    }
}
