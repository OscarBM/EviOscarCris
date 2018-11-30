package com.tecmi.oscar.calendardemo3;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.TimeZone;

//import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener {

    //Declarar EditTexts. Estos almacenaran los datos del evento
    private EditText eventName, eventLocation, startDate, endDate, startHour, endHour;
    private EditText description;
    private EditText search;


    private long eventID;

    //Declarar botones
    private Button btnEditEvent1, btnCancelEvent;
    private Button btnList;
    private Button btnSearch;
    private Button btnHome3;

    private Button btnAlarmOn;
    private Button btnAlarmOff;

    private boolean alarmOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventID = (int)getIntent().getExtras().get("id");

       // try{
        //asignar valores a los EditText. Aqui se almacenarán los datos del evento
        eventName = (EditText)findViewById(R.id.edtTitle);
        eventLocation = (EditText)findViewById(R.id.edtLocation);
        startDate = (EditText)findViewById(R.id.edtStartDate);
        startHour = (EditText)findViewById(R.id.edtStartHour);
        endDate = (EditText)findViewById(R.id.edtEndtDate);
        endHour = (EditText)findViewById(R.id.edtEndHour);
        description = (EditText)findViewById(R.id.edtDescription);

        search = (EditText)findViewById(R.id.edtSearch2);

        eventName.setText((String)getIntent().getExtras().get("title"));
        eventLocation.setText((String)getIntent().getExtras().get("place"));
        startDate.setText((String)getIntent().getExtras().get("beginTime"));
        startHour.setText((String)getIntent().getExtras().get("beginHour"));
        endDate.setText((String)getIntent().getExtras().get("endTime"));
        endHour.setText((String)getIntent().getExtras().get("endHour"));
        description.setText((String)getIntent().getExtras().get("description"));





        //Asignar valores a los botones. Aqui se ligan las variables con sus respectivos botones en el archivo xml correspondiente a esta actividad
        btnEditEvent1 = (Button)findViewById(R.id.btnAddEvent);
        btnCancelEvent = (Button)findViewById(R.id.btnCancelEvent);
        btnList = (Button)findViewById(R.id.btnList);
        btnSearch = (Button)findViewById(R.id.btnSearch2);
        btnHome3 = (Button)findViewById(R.id.btnHome3);

        btnAlarmOn = (Button)findViewById(R.id.btnAlarmOn);
        btnAlarmOff = (Button)findViewById(R.id.btnAlarmOff);

        //Fijar los listener de los botones. Esto permite que los botones puedan realizar acciones al ser presionados.
        btnEditEvent1.setOnClickListener(this);
        btnCancelEvent.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnHome3.setOnClickListener(this);

        btnAlarmOff.setOnClickListener(this);
        btnAlarmOn.setOnClickListener(this);

        /*
        } catch (Exception e){
            Log.d("edit11", e.getMessage());
        }*/

    }

    @Override
    public void onClick(View v) {

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        //Como hay más de un botón, el metodo OnCLick priemro revisara el id del botón que llamó a este metodo para realizar las acciones correspondientes
        if(v.getId() == btnEditEvent1.getId()){//Este if se ejecuta cuando el botón presionado fue el de agregar el evento a la agenda






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








            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            try {
                /*Intent intent = new Intent(Intent.ACTION_EDIT)
                        .setData(uri)
                        .putExtra(CalendarContract.Events.TITLE, eventName.getText().toString());
                startActivity(intent);*/


                Uri updateUri = null;
// The new title for the event
                values.put(CalendarContract.Events.TITLE, eventName.getText().toString());
                values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
                values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation.getText().toString());
                values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                //values.put(CalendarContract.Events.OWNER_ACCOUNT, MainActivity.activeAccount);
                values.put(CalendarContract.Events.CALENDAR_ID, MainActivity.calID1);

                try{
                    if (alarmOn==true) {values.put(CalendarContract.Events.HAS_ALARM, true);}
                    //else if(alarmOn == false){values.put(CalendarContract.Events.HAS_ALARM, false);}
                } catch (Exception e){Log.d("failAlarm", e.getMessage());}

                //Se pone una zona horario porque es obligatorio ponerla, caso contrario la app no servira
                values.put(CalendarContract.Events.EVENT_TIMEZONE,TimeZone.getDefault().getID());

                updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                int rows = getContentResolver().update(updateUri, values, null, null);

                Log.d("reminder1", alarmOn+" es su valor de alarmOn");


                if (alarmOn == true)
                {
                    ContentValues reminders = new ContentValues();
                    reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    reminders.put(CalendarContract.Reminders.MINUTES, 5);
                    Log.d("reminder1", eventID+" con reminder");

                    //Uri uri3 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                    Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);
                }

                if (alarmOn == false){

                    long reminderId=0;

                    String[] projection = new String[] {
                            CalendarContract.Reminders._ID,
                            CalendarContract.Reminders.METHOD,
                            CalendarContract.Reminders.MINUTES
                    };

                    Cursor cursor = CalendarContract.Reminders.query(
                            getContentResolver(), eventID, projection);
                    while (cursor.moveToNext()) {
                        reminderId = cursor.getLong(0);
                        int method = cursor.getInt(1);
                        int minutes = cursor.getInt(2);

                        // etc.

                    }
                    cursor.close();


                    Uri reminderUri = ContentUris.withAppendedId(
                            CalendarContract.Reminders.CONTENT_URI, reminderId);
                    rows = getContentResolver().delete(reminderUri, null, null);

                }


                Log.i("funciona!!", "Rows updated hoy: " + rows);
            } catch (Exception e){
                Log.d("funciona!!", "fallo en esto: "+e.getMessage());
            }
            /*
            //Una vez que el evento fue agregado a la agenda, se procedera a limpiar los textEdit.
            eventName.setText("");
            eventLocation.setText("");
            startDate.setText("");
            startHour.setText("");
            endDate.setText("");
            endHour.setText("");
            description.setText("");*/
            //Aqui se crea un mensaje para avisar que ya se guardo la cuenta nueva
            Toast t = Toast.makeText(this, "Se guardaron los cambios",
                    Toast.LENGTH_SHORT);
            //Aqui ya se despliega el mensaje
            t.show();
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
            btnAlarmOn.setBackgroundColor(Color.GREEN);
            btnAlarmOff.setBackgroundColor(Color.CYAN);
            Log.d("reminder1", alarmOn+" es su valor de alarmOn con el boton");
        } else if(v.getId() == btnAlarmOff.getId()){
            alarmOn = false;
            btnAlarmOn.setBackgroundColor(Color.CYAN);
            btnAlarmOff.setBackgroundColor(Color.GREEN);
        }

        else {
            //Este else if es para cancelar guardar el evento.
            this.finish();
            return;
        }
    }
}
