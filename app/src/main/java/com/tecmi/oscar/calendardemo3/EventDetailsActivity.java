package com.tecmi.oscar.calendardemo3;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //Se declarán como variables a los TextView que almacenarán los datos del evento
    private int eventID;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvHour;
    private TextView tvPlace;
    private TextView tvDescription;

    private Button btnEdit;
    private Button btnSearch3;
    private Button btnNewEvent;
    private Button btnList1;
    private Button btnHome;

    private EditText edtSearch1;

    ArrayList<String> myStringArray1 =  new ArrayList<String>();//
    public static ArrayAdapter<String> adapter;

    public String eHour1;
    public String sHour1;
    public String eDate1;
    public String sDate1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //Se asocia cada textView que contendran los datos dele vento con su respectiva variable
        tvTitle = (TextView)findViewById(R.id.tvTitle);//El Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference se solucionadeclarando las variables com oaqui
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvHour = (TextView)findViewById(R.id.tvHour);
        tvPlace = (TextView)findViewById(R.id.tvPlace);
        tvDescription = (TextView)findViewById(R.id.tvDescription);

        btnEdit = (Button)findViewById(R.id.btnEdit);
        btnSearch3 = (Button)findViewById(R.id.btnSearch2);
        btnNewEvent = (Button)findViewById(R.id.btnAddEvent);
        btnList1 = (Button)findViewById(R.id.btnList);
        btnHome = (Button)findViewById(R.id.btnHome3);

        edtSearch1 = (EditText)findViewById(R.id.edtSearch2);


        btnEdit.setOnClickListener(this);
        btnSearch3.setOnClickListener(this);
        btnNewEvent.setOnClickListener(this);
        btnList1.setOnClickListener(this);
        btnHome.setOnClickListener(this);



            eventID = (int)getIntent().getExtras().get("id");
            Log.d("idRecibido", "el que toma:"+eventID);

            //Este array de Strings permitira hacer consultas de eventos
            String[] mProjection =
                    {
                            "_id",
                            CalendarContract.Events.TITLE,
                            CalendarContract.Events.EVENT_LOCATION,
                            CalendarContract.Events.DTSTART,
                            CalendarContract.Events.DTEND,
                            CalendarContract.Events.DESCRIPTION
                    };

            // Ejecutar consulta de eventos para el listView lstvEvents
            Cursor cur = null;//El cursor almacenara la lista de eventos
            ContentResolver cr = getContentResolver();//El content Resolver correra la consulta y obtendra la lista de eventos
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = "(" + CalendarContract.Events.OWNER_ACCOUNT +" = ?)";

            //ESTA LINEA DE ABAJO ESTA PENDIENTE
            //String[] selectionArgs = new String[] {"usersample@gmail.com"};//Aqui se fija la cuenta de la cual se quieren revisar eventos.
            String[] selectionArgs = new String[] {MainActivity.activeAccount};//Aqui se fija la cuenta de la cual se quieren revisar eventos.


            // El cursor almacenara la consulta hecha por el contentResolver
            //selecctionArgs vendrian siendo los filtros. Aqui se encuentra el correo del usuario
            cur = cr.query(uri, mProjection, selection, selectionArgs, null);

            Calendar cal = Calendar.getInstance();//El Calendar nos permitira extraer el dia, mes y año de la fecha de inicio

            // El cursor comienza a recorrer los eventos de la consulta de uno en uno para obtener sus datos
            while (cur.moveToNext()) {

                //Se extraen los datos del evento en turno
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));//Se extrae el titulo del evento
                String description = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));//Se extrae la descripción del evento
                String eDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));//Se extrae la fecha de inicio del evento.
                String sDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));//Se extrae la fecha de inicio del evento.
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                int id = cur.getInt(cur.getColumnIndex(CalendarContract.Events._ID));//IDS
                //sDate sale en un tipo de String llamado timeStamp, el cual ocupa traducirse a una variable de tipo Date

                //AQUI TRADUCIR MES A LETRAS

                //El contenido de cada item del listView lstvEvents serán Strings que contengan la fecha de inicio, titulo y descripción del evento.
                if(id == eventID){
                    tvTitle.setText(title);
                    tvPlace.setText(location);
                    tvDescription.setText(description);
                    Log.d("fechaCorrecta", title+" "+location);

                    //Se convierte el sDate a una variable de tipo fecha (Date)
                    //Revisar si metodo stringToDate realmente se ocupa o si esta de mas
                    Date time=new Date((long) Long.parseLong(sDate));
                    //Extraer el dia, mes y año de la fecha de inicio
                    cal.setTime(time);
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH)+1;//NOTA: Los meses comienzan en 0 (y no en 1), por ello se le suma 1 al resultado.
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    String dayWeek = translateDayWeek(cal.get(Calendar.DAY_OF_WEEK));

                    String eventDates = dayWeek+", "+day+" "+translateMonth(month)+" "+year;
                    sDate1 = day+"/"+(month)+"/"+year;

                    //Extraer hora de inicio
                    int hour = cal.get(Calendar.HOUR);
                    if(Calendar.PM == 1){
                        hour += 12;
                    }
                    int minutes = cal.get(Calendar.MINUTE);

                    String eventHours = hour+":"+minutes;
                    sHour1 = (hour)+":"+minutes;


                    //Se pasa a obtener la fecha y hora de fin del evento
                    time=new Date((long) Long.parseLong(eDate));
                    //Extraer el dia, mes y año de la fecha de fin
                    cal.setTime(time);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH)+1;//NOTA: Los meses comienzan en 0 (y no en 1), por ello se le suma 1 al resultado.
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    dayWeek = translateDayWeek(cal.get(Calendar.DAY_OF_WEEK));
                    eDate1 = day+"/"+(month)+"/"+year;

                    if(eventDates == (dayWeek+", "+day+" "+translateMonth(month)+" "+year)){
                        tvDate.setText(eventDates);
                    } else {
                        tvDate.setText(eventDates + " - " + dayWeek + ", " + day + " " + translateMonth(month) + " " + year);
                    }

                    //Extraer hora de fin
                    hour = cal.get(Calendar.HOUR);
                    if(Calendar.PM == 1){
                        hour += 12;
                    }
                    minutes = cal.get(Calendar.MINUTE);
                    eHour1 = (hour)+":"+minutes;

                    if (eventHours == (hour+":"+minutes)){
                        tvHour.setText(eventDates);
                    } else {
                        tvHour.setText(eventHours + " - " + hour+":"+minutes);
                    }



                    break;
                }
            }





    }

    public String translateDayWeek (int day){
       // Log.d("DiaSemana", day+"");
        if(day == 2){return "Lun";}
        else if (day == 3){return "Mar";}
        else if (day == 4){return "Mie";}
        else if (day == 5){return "Jue";}
        else if (day == 6){return "Vie";}
        else if (day == 7){return "Sab";}
        else {return "Dom";}
    }

    public String translateMonth (int month){
        if (month == 1){return "Ene";}
        else if (month == 2){return "Feb";}
        else if (month == 3){return "Mar";}
        else if (month == 4){return "Abr";}
        else if (month == 5){return "May";}
        else if (month == 6){return "Jun";}
        else if (month == 7){return "Jul";}
        else if (month == 8){return "Ago";}
        else if (month == 9){return "Sep";}
        else if (month == 10){return "Oct";}
        else if (month == 11){return "Nov";}
        else {return  "Dic";}
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnEdit.getId()){

                Intent intent = new Intent(getApplication(), EditEventActivity.class);//Se crea la intent para poder cambiar de pantalla
            intent.putExtra("id", eventID)
                    .putExtra("title", tvTitle.getText().toString())
                    .putExtra("beginTime", sDate1)
                    .putExtra("endTime", eDate1)
                    .putExtra("beginHour", sHour1)
                    .putExtra("endHour", eHour1)
                    .putExtra("place", tvPlace.getText().toString())
                    .putExtra("description", tvDescription.getText().toString());

            startActivity(intent);//Se ejecuta la intent para pasar a la pantalla de Lista de eventos (ListActivity.java)

        } else if (v.getId() == btnSearch3.getId()){

            try{
                Intent intent = new Intent(getApplication(),SearchActivity.class);//Se crea la intent para poder cambiar de pantalla
                MainActivity.searchWord = edtSearch1.getText().toString();//Se obtiene la palabra que esta en la barra de busqueda. Si no hya nada no hay filtro de palabras
                startActivity(intent);
            } catch(Exception e){
                Log.d("SALIOMAL3", e.getMessage());
                btnEdit.setBackgroundColor(Color.CYAN);
                btnEdit.setText(e.getMessage());
            }

        } else if(v.getId() == btnNewEvent.getId()){
            //Esto es para cambiar a la actividad "AddEnventActivity.java"
            Intent intent = new Intent(getApplication(), AddEventActivity.class);//Se crea un intent para poder pasar a la siguiente pantalla
            Bundle bundle = new Bundle();//Probablemente esta linea sea innecesaria

            intent.putExtras(bundle);//Probablemente esta linea sea innecesaria
            startActivity(intent);//Se inicia el intent para pasar a la pantalla de Agregar evento nuevo (AddEventActivity.java)
        } else if (v.getId() == btnList1.getId()){
            Intent intent = new Intent(getApplication(), ListActivity.class);//Se crea la intent para poder cambiar de pantalla
            startActivity(intent);//Se ejecuta la intent para pasar a la pantalla de Lista de eventos (ListActivity.java)
        } else if (v.getId() == btnHome.getId()){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        } else {finish();}
    }
}
