package com.tecmi.oscar.calendardemo3;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;

import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {
    //Marvel: https://marvelapp.com/45cj4d9/screen/48608874

    //Lista de abreviaciones
    /*
     -clv = CalendarView
     -btn = Button
     -edt = EditText
     -tv = TextView
     */

    private CalendarView clvMain;//Se declara al calendari de la pantalla principal como variable
    //tal vez estas variables ya no sirvan
    private Button btnList;
    private Button btnAddEvent;
    public static TextView tvMainToday;

    //El ublic permite que cualquier otra clase de la app pueda accesar o alterar el elemento indicado
    //EL sttic es para que el valor de esa variable sea el mismo para todas las instancias de la clase
    public static String activeAccount;//Cuenta activa
    public static int calID1;
    public static String searchWord;//Palabra de busqueda. Si es null no filtrara ningun evento
    private EditText edtSearch;//La barra de busqueda para buscar eventos que contengan determinada palabra

    private String estoSalioMal= "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //try{getCalendar(this);} catch (Exception e){Log.d("listaCuentas1", e.getMessage());}

        //AccountManager accountManager = AccountManager.get(this);
        // Account account = getAccount(accountManager);
        //Log.d("cuenta4Activa", "chanfle"+account.type);


//estoSalioMal = getCalendar(this);


        calID1 = Integer.parseInt(getCalendarID(this));
        activeAccount = getCalendar(this);//= setAccount();//S edefine la cuenta cuando se inicia la aplicación

        tvMainToday = (TextView)findViewById(R.id.tvMainToday);//Se asocia el textView que indicará la fecha actual con su respectiva variable en el .java
        try{tvMainToday.setText(activeAccount+" "+calID1);}catch(Exception e){Log.d("grave1", "esto ya valio "+e.getMessage());}//Solo para testing
        edtSearch = (EditText)findViewById(R.id.edtSearch);//Se asocia la barra de busqueda con su respectiva variable en el .java
        clvMain = (CalendarView)findViewById(R.id.clvMain);//Se asocia el calendario de la pantalla principal cno su respectiva variable en el .java

        clvMain.setOnDateChangeListener(this);//Aqui se le da al calendario la capacidad de hacer cosas cuadno se selecciona un dia determinada


        DateFormat df = new SimpleDateFormat("EEE d MMM yyyy");
        String date = df.format(java.util.Calendar.getInstance().getTime());
        String [] dateFrag = date.split(" ");




        tvMainToday.setText("");

        tvMainToday.setText(dateFrag[0]+", "+dateFrag[1]+" "+dateFrag[2]+" "+dateFrag[3]);


    }




    //Metodo para que se ejecuten acciones si se presiona cualquier dia en el calendario clvMain
    //@Override
    public void onSelectedDayChange (CalendarView calendarView, int year, int month, int day){
        Intent intent = new Intent(getApplication(), DayActivity.class);
        intent.putExtra("year", year)
                .putExtra("month", month+1)
                .putExtra("day", day);
        try{startActivity(intent);} catch(Exception e){ Log.d("MALOO", e.getMessage()); }
    }





    //Metodo para cuando se presione el botón + (el de añadir evento nuevo)
    //Sirve para a la pantalla de Agregar evento nuevo (AddEventActivity.java)
    public void onClickNewEvent(View v){
        Intent intent = new Intent(getApplication(), AddEventActivity.class);//Se crea un intent para poder pasar a la siguiente pantalla
        Bundle bundle = new Bundle();//Probablemente esta linea sea innecesaria

        intent.putExtras(bundle);//Probablemente esta linea sea innecesaria
        startActivity(intent);//Se inicia el intent para pasar a la pantalla de Agregar evento nuevo (AddEventActivity.java)
    }

    //Metodo para cuando se presione el botón LISTA DE EVENTOS (el de ver lista de todos los eventos)
    //Sirve para a la pantalla de Lista de eventos (ListActivity.java)
    public void onClickListEvents(View v){
        Intent intent = new Intent(getApplication(), ListActivity.class);//Se crea la intent para poder cambiar de pantalla
        startActivity(intent);//Se ejecuta la intent para pasar a la pantalla de Lista de eventos (ListActivity.java)
    }




    //Metodo que se ejecuta cuando se presiona el botón de busqueda. Es para filtrar eventos por palabras
    public void onClickSearch(View v){


            Intent intent = new Intent(getApplication(),SearchActivity.class);//Se crea la intent para poder cambiar de pantalla
            searchWord = edtSearch.getText().toString();//Se obtiene la palabra que esta en la barra de busqueda. Si no hya nada no hay filtro de palabras
            startActivity(intent);

    }

    public String getCalendar(Context c) {

        String m_calendars = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");
        ContentResolver contentResolver = c.getContentResolver();


        //Se declara una variable
        ArrayList<String> arrPerm = new ArrayList<>();//Esta variable ayuda a añadir los permisos que no se hayan otorgado

        //Este if coprueba si la aplicación posee permiso para leer el calendario.
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.READ_CALENDAR);//Aqui se añade el permiso de leer calendario a una lista de permisos pendientes
        }


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.WRITE_CALENDAR);//Aqui se añade el permiso de leer calendario a una lista de permisos pendientes
        }

        //En caso de que la app no tenga permiso de leer el calendario, le pregunta al usuario si le da ese permiso para leer el calendario
        if(!arrPerm.isEmpty()) {//Si la lista de permisos pendientes NO esta vacia, se ejecuta este if
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);



        try{

            int cont=0;
            if (managedCursor.moveToFirst()){
                //m_calendars = new Calendar[managedCursor.getCount()];
                String calName;
                String calID;
                //int cont= 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                do {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    if(calName.contains("@gmail.com")){m_calendars=calName; Log.d("listaCuentas3", cont+" "+calName+" "+calID); break;}


                    cont++;
                    Log.d("listaCuentas3", cont+"");
                } while(managedCursor.moveToNext());
                managedCursor.close();
            }


        } catch (Exception e){
            Log.d("grave1", e.getMessage());

        }

        return m_calendars;
        //return estoSalioMal;
    }

    public String getCalendarID(Context c) {

        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        String m_calendars = "";
        ContentResolver contentResolver = c.getContentResolver();


        //Se declara una variable
        ArrayList<String> arrPerm = new ArrayList<>();//Esta variable ayuda a añadir los permisos que no se hayan otorgado

        //Este if coprueba si la aplicación posee permiso para leer el calendario.
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.READ_CALENDAR);//Aqui se añade el permiso de leer calendario a una lista de permisos pendientes
        }


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.WRITE_CALENDAR);//Aqui se añade el permiso de leer calendario a una lista de permisos pendientes
        }

        //En caso de que la app no tenga permiso de leer el calendario, le pregunta al usuario si le da ese permiso para leer el calendario
        if(!arrPerm.isEmpty()) {//Si la lista de permisos pendientes NO esta vacia, se ejecuta este if
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        int cont=0;
        if (managedCursor.moveToFirst()){
            //m_calendars = new Calendar[managedCursor.getCount()];
            String calName;
            String calID;
            //int cont= 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getString(idCol);
                if(calName.contains("@gmail.com")){m_calendars=calID; Log.d("listaCuentas3", cont+" "+calName+" "+calID); break;}


                cont++;
                Log.d("listaCuentas3", cont+"");
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return m_calendars;

    }

}