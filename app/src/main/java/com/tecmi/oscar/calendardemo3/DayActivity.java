package com.tecmi.oscar.calendardemo3;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DayActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener /*implements View.OnLongClickListener*/{

    ArrayList<String> myStringArray1 =  new ArrayList<String>();//
    public static ArrayAdapter<String> adapter;
    private Button btnNewEvent1;
    private Button btnSearch3;
    private Button btnHome4;
    private Button btnList5;
    private EditText edtSearch3;
    private TextView tvSelectedDate;
    ArrayList<Integer> eventIDs = new ArrayList<Integer>();//Esta variable no se mostrará en la app, pero servira para almacenar los IDs de cada evento.

    private int day1, month1, year1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);



        //try{
            //Se declaran variables
        TextView tvToday = (TextView)findViewById(R.id.tvToday);//El textView que contendra la fecha actual. CAMBIOS PENDIENTES
        ListView lstvEvents = (ListView)findViewById(R.id.lstvEvents);//El listView que contendra la lista de eventos
        btnNewEvent1 = (Button)findViewById(R.id.btnNewEvent1);
        btnSearch3 = (Button)findViewById(R.id.btnSearch1);
        btnHome4 = (Button)findViewById(R.id.btnHome1);
        btnList5 = (Button)findViewById(R.id.btnList5);
        edtSearch3 = (EditText)findViewById(R.id.edtSearch1);
        tvSelectedDate = (TextView)findViewById(R.id.tvSelectedDate);

            year1 = (int)getIntent().getExtras().get("year");
            month1 = (int)getIntent().getExtras().get("month");
            day1 = (int)getIntent().getExtras().get("day");

            btnNewEvent1.setOnClickListener(this);
            btnSearch3.setOnClickListener(this);
            btnHome4.setOnClickListener(this);
            btnList5.setOnClickListener(this);

            lstvEvents.setOnItemClickListener(this);

            tvSelectedDate.setText(monthLetter(month1)+" "+day1+", "+year1);


            //INICIO CREACIÓN LISTA
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

            //Se declara una variable
            ArrayList<String> arrPerm = new ArrayList<>();//Esta variable ayuda a añadir los permisos que no se hayan otorgado

            //Este if coprueba si la aplicación posee permiso para leer el calendario.
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
                arrPerm.add(Manifest.permission.READ_CALENDAR);//Aqui se añade el permiso de leer calendario a una lista de permisos pendientes
            }

            //En caso de que la app no tenga permiso de leer el calendario, le pregunta al usuario si le da ese permiso para leer el calendario
            if(!arrPerm.isEmpty()) {//Si la lista de permisos pendientes NO esta vacia, se ejecuta este if
                String[] permissions = new String[arrPerm.size()];
                permissions = arrPerm.toArray(permissions);
                ActivityCompat.requestPermissions(this, permissions, 1);
            }


            // Ejecutar consulta de eventos para el listView lstvEvents
            Cursor cur = null;//El cursor almacenara la lista de eventos
            ContentResolver cr = getContentResolver();//El content Resolver correra la consulta y obtendra la lista de eventos
            Uri uri = CalendarContract.Events.CONTENT_URI;
            //String selection = "(" + CalendarContract.Events.OWNER_ACCOUNT +" = ?)";
            String selection = "(" + CalendarContract.Events.OWNER_ACCOUNT +" = ?)";

            String[] selectionArgs = new String[] {MainActivity.activeAccount};//Aqui se fija la cuenta de la cual se quieren revisar eventos.


            // El cursor almacenara la consulta hecha por el contentResolver
            //selecctionArgs vendrian siendo los filtros. Aqui se encuentra el correo del usuario
            cur = cr.query(uri, mProjection, selection, selectionArgs, null);


            //Se declaran variables para ir construyendo la lista de eventos del listView lstvEvents
            adapter = null;//El adapter ira almacenando los eventos para luego pasarle la lista a lstvEvents
            Calendar cal = Calendar.getInstance();//El Calendar nos permitira extraer el dia, mes y año de la fecha de inicio

            // El cursor comienza a recorrer los eventos de la consulta de uno en uno para obtener sus datos
            while (cur.moveToNext()) {

                //Se extraen los datos del evento en turno
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));//Se extrae el titulo del evento
                String description = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));//Se extrae la descripción del evento
                String eDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));//Se extrae la fecha de inicio del evento.
                String sDate = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));//Se extrae la fecha de inicio del evento.
                int id = cur.getInt(cur.getColumnIndex(CalendarContract.Events._ID));//IDS
                //sDate sale en un tipo de String llamado timeStamp, el cual ocupa traducirse a una variable de tipo Date

                //Se convierte el sDate a una variable de tipo fecha (Date)
                //Revisar si metodo stringToDate realmente se ocupa o si esta de mas
                Date time=new Date((long) Long.parseLong(sDate));


                //Extraer el dia, mes y año de la fecha de inicio
                cal.setTime(time);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH)+1;//NOTA: Los meses comienzan en 0 (y no en 1), por ello se le suma 1 al resultado.
                int day = cal.get(Calendar.DAY_OF_MONTH);

                //Extraer hora de inicio
                int hour = cal.get(Calendar.HOUR)+6;
                int minutes = cal.get(Calendar.MINUTE);




                //AQUI TRADUCIR MES A LETRAS

                //El contenido de cada item del listView lstvEvents serán Strings que contengan la fecha de inicio, titulo y descripción del evento.
                if((day == day1) && (month == month1) && (year == year1)){
                    String itemContent = hour+":"+minutes+"-";

                    time=new Date((long) Long.parseLong(eDate));


                    //Extraer el dia, mes y año de la fecha de inicio
                    cal.setTime(time);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH)+1;//NOTA: Los meses comienzan en 0 (y no en 1), por ello se le suma 1 al resultado.
                    day = cal.get(Calendar.DAY_OF_MONTH);

                    //Extraer hora de inicio
                    hour = cal.get(Calendar.HOUR)+6;
                    minutes = cal.get(Calendar.MINUTE);


                    //Aqui se contruye el String que sera contenido de lstvEvents
                    itemContent += hour+":"+minutes+ " "+title + /*" "+ eDate +*/": "+description;

                    //Aqui se añade el item al listView lstvEvents
                    myStringArray1.add(itemContent);
                    adapter = new ArrayAdapter<String>
                            (this, android.R.layout.simple_list_item_1, myStringArray1);
                    eventIDs.add(id);
                    //Log.d("horaTest1",title+"-"+Integer.toString(sHour)+":"+Integer.toString(sMinutes)+" "+duration);
                    Log.d("horaTest2", String.valueOf(stringToDate(sDate, "EEE MMM d HH:mm:ss zz yyyy")));


                }
            }
            //Aqui el adapter le pasa la lista de eventos completa a lstvEvents
            lstvEvents.setAdapter(adapter);
            //FIN CREACIÓN LISTA
        //} catch (Exception e){ Log.d("lo que salio mal", e.getMessage()); }


    }

    private String monthLetter(int month){
        String monthLetter="";//Esta variable es para poder representar a los meses con abreviaciones (ej. Ene, Feb, Mar, etc.) en lugar de usar numeros

        //Aqui se traduce el mes de la fecha de inicio a su correspondiente abreviación
        switch(month){
            case 1: monthLetter = "Ene"; break;
            case 2: monthLetter = "Feb"; break;
            case 3: monthLetter = "Mar"; break;
            case 4: monthLetter = "Abr"; break;
            case 5: monthLetter = "May"; break;
            case 6: monthLetter = "Jun"; break;
            case 7: monthLetter = "Jul"; break;
            case 8: monthLetter = "Ago"; break;
            case 9: monthLetter = "Sep"; break;
            case 10: monthLetter = "Oct"; break;
            case 11: monthLetter = "Nov"; break;
            default: monthLetter = "Dic"; break;
        }

        return monthLetter;
    }

    //Revisar si se ocupa o si esta demas este metodo
    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == btnNewEvent1.getId()){
            //Esto es para cambiar a la actividad "AddEnventActivity.java"
            Intent intent = new Intent(getApplication(), AddEventActivity.class);//Se crea un intent para poder pasar a la siguiente pantalla
            Bundle bundle = new Bundle();//Probablemente esta linea sea innecesaria

            intent.putExtras(bundle);//Probablemente esta linea sea innecesaria
            startActivity(intent);//Se inicia el intent para pasar a la pantalla de Agregar evento nuevo (AddEventActivity.java)
        } else if (v.getId() == btnSearch3.getId()){
            try{
                Intent intent = new Intent(getApplication(),SearchActivity.class);//Se crea la intent para poder cambiar de pantalla
                MainActivity.searchWord = edtSearch3.getText().toString();//Se obtiene la palabra que esta en la barra de busqueda. Si no hya nada no hay filtro de palabras
                startActivity(intent);
            } catch(Exception e){
                Log.d("SALIOMAL3", e.getMessage());
            }

        } else if (v.getId() == btnHome4.getId()){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        } else if (v.getId() == btnList5.getId()){
            Intent intent = new Intent(getApplication(), ListActivity.class);//Se crea la intent para poder cambiar de pantalla
            startActivity(intent);//Se ejecuta la intent para pasar a la pantalla de Lista de eventos (ListActivity.java)
        }
    }

    //Este metodo pasará al usuario a la pantalla de detalles del evento dependiendo del item seleccionado en el ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Se crea el intent para pasar de pantalla
        Intent intent = new Intent(getApplication(), EventDetailsActivity.class);
        //Se pasa como parametro  el id del evento. Se toma de la lista de ids paralela
        intent.putExtra("id", eventIDs.get(position));
        startActivity(intent);
    }
}
