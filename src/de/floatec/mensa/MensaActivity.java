package de.floatec.mensa;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.http.util.ByteArrayBuffer;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MensaActivity extends Activity {
	
	private int activDay;
	private String weeks[];
	Calendar calendar = Calendar.getInstance();
	SharedPreferences prefs;
	LinearLayout ll;
	MensaReader mr;
	private Button buttondi, buttonmo, buttonmi, buttondon, buttonfr;
	 private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	private void setColorDefoult() {
		buttonmo.setBackgroundColor(Color.GRAY);
		buttondi.setBackgroundColor(Color.GRAY);
		buttonmi.setBackgroundColor(Color.GRAY);
		buttondon.setBackgroundColor(Color.GRAY);
		buttonfr.setBackgroundColor(Color.GRAY);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		super.onCreate(savedInstanceState);
		
		mr = new MensaReader();
		setContentView(R.layout.main);
		weeks=new String[3];
		
		Calendar myCalMo = Calendar.getInstance(); 
		Calendar myCalFr = Calendar.getInstance(); 
		myCalMo.add(myCalMo.DAY_OF_MONTH,-myCalMo.get(myCalMo.DAY_OF_WEEK)+2);
		myCalFr.add(myCalFr.DAY_OF_MONTH,-myCalFr.get(myCalFr.DAY_OF_WEEK)+6);
		if( calendar.get(Calendar.DAY_OF_WEEK)==7){
			myCalMo.add(myCalMo.DAY_OF_MONTH,+7);
			myCalFr.add(myCalFr.DAY_OF_MONTH,+7);
		}
		weeks[0]=  FORMAT.format(myCalMo.getTime())+" - "+ FORMAT.format(myCalFr.getTime());
		myCalMo.add(myCalMo.DAY_OF_MONTH,+7);
		
		myCalFr.add(myCalFr.DAY_OF_MONTH,+7);
		weeks[1]=  FORMAT.format(myCalMo.getTime())+" - "+ FORMAT.format(myCalFr.getTime());
		myCalMo.add(myCalMo.DAY_OF_MONTH,+7);
		myCalFr.add(myCalFr.DAY_OF_MONTH,+7);
		weeks[2]=  FORMAT.format(myCalMo.getTime())+" - "+ FORMAT.format(myCalFr.getTime());
		Spinner s = (Spinner) findViewById(R.id.week);
		ArrayAdapter adapter = new ArrayAdapter(this,
		android.R.layout.simple_spinner_item, weeks);
		s.setAdapter(adapter);
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        mr.setWeekOffset(pos);
		        reloadUi();
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		
		
		ll = (LinearLayout) findViewById(R.id.content);
	
		buttonmo = (Button) findViewById(R.id.mo);
		buttonmo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reloadUi(0);
				setColorDefoult();
				v.setBackgroundColor(Color.rgb(255, 127, 36));
			}
		});
		buttondi = (Button) findViewById(R.id.di);
		buttondi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reloadUi(1);
				setColorDefoult();
				v.setBackgroundColor(Color.rgb(255, 127, 36));
			}
		});
		buttonmi = (Button) findViewById(R.id.mi);
		buttonmi.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reloadUi(2);
				setColorDefoult();
				v.setBackgroundColor(Color.rgb(255, 127, 36));
			}
		});
		buttondon = (Button) findViewById(R.id.don);
		buttondon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reloadUi(3);
				setColorDefoult();
				v.setBackgroundColor(Color.rgb(255, 127, 36));
			}
		});
		buttonfr = (Button) findViewById(R.id.fr);
		buttonfr.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reloadUi(4);
				setColorDefoult();
				v.setBackgroundColor(Color.rgb(255, 127, 36));
			}
		});
		start();
		// reloadUi(weekday);//erst nach button intialisierung

	}
	
	
	private void start() {
		
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		if (weekday == 1 || weekday == 7) {
			weekday = 0;
		} else {
			weekday = weekday - 2;
		}
		switch (weekday) {
		case 0:
			buttonmo.performClick();
			break;
		case 1:
			buttondi.performClick();
			break;
		case 2:
			buttonmi.performClick();
			break;
		case 3:
			buttondon.performClick();
			break;
		case 4:
			buttonfr.performClick();
			break;

		default:
			break;
		}
	}
	/**
	 * reloads the last selected da
	 */
	public void reloadUi() {
		reloadUi(activDay);
	}
/**
 * l�d den content bereich
 * @param day gew�nschter tag(0-4)
 */
	public void reloadUi(int day) {
		activDay=day;
		
		//wenn ih eistellungen cache aktiv
		if( prefs.getBoolean("cache", true)){
		mr.refrashlist();
		}else{
			mr.refrashlistWithoutCache();
		}
		//leert view
		ll.removeAllViews();
		//f�r Mo-Do
		if (day != 4) {
			TextView tw = new TextView(this);
			tw.setText("11:15 - 14:00 Uhr");
			tw.setTextSize(12);
			ll.addView(tw);
			//f�r Fr
		} else {
			TextView tw = new TextView(this);
			tw.setText("11:15 - 13:45 Uhr");
			tw.setTextSize(12);
			ll.addView(tw);
		}
		//tag auslesen
		MenuList ml = mr.readDay(day);
		TextView tw = new TextView(this);
		//gibt alle men�s aus
		for (int i = 0; i < ml.getMenuCount(); i++) {
			tw = new TextView(this);
			tw.setText(ml.getMenu(i).getTitle());
			tw.setTextSize(20);
			tw.setTextColor(Color.BLACK);
			//Fehlerfall�berpr�fung
			if(ml.getMenu(i).getTitle().compareTo("ERROR")!=0){
					tw.setTextColor(Color.BLACK);
					tw.setBackgroundColor(Color.rgb(255, 127, 36));
			}else{
				tw.setTextColor(Color.WHITE);
				tw.setBackgroundColor(Color.RED);
			}
			ll.addView(tw);
			tw = new TextView(this);
			tw.setText(ml.getMenu(i).getText() + " " + ml.getMenu(i).getPrice());
			ll.addView(tw);
		}
		
		
	}
	
	

	public void showZusatzstoffe() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Zusatzstoffe");
 
		builder.setMessage("KENNZEICHNUNGSPFLICHTIGE ZUSATZSTOFFE:	\nS Schweinefleisch	\nVeg Vegetarisch	\n1 mit Farbstoff	\n2 mit Konservierungsstoff	  \n3 mit Antioxidationsmittel	  \n4 mit Geschmacksverst�rker	  \n5 geschwefelt\n6 geschw�rzt	\n7 gewachst	\n8 mit Phosphat	\n9 mit S�uerungsmittel	\n10 enth�lt eine Phenylalaninquelle	\n13 enth�lt Natriumnitrit	\n14 Bio-Kontrollnummer: DE-�KO-007")
				
				;
		AlertDialog alert = builder.create();
		alert.show();
	}


	public boolean onCreateOptionsMenu(Menu menu) {

		
		
		menu.add(0, 3, 0, "Feedback zur App").setIcon(android.R.drawable.ic_menu_send);
		menu.add(0, 4, 0, "Feedback an die Mensa").setIcon(android.R.drawable.ic_menu_send);
		menu.add(0, 5, 0, "Zusatzstoffe").setIcon(android.R.drawable.ic_menu_help);
	
		menu.add(0, 2, 0, "Aktuallisieren").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, 6, 0, "Spenden").setIcon(android.R.drawable.ic_menu_view);
		menu.add(0, 7, 0, "Einstellungen").setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, 1, 0, "�ber").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, -1, 0, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);

		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent browser;
		switch (item.getItemId()) {
		case 3:
			//email an entwickler
			 browser = new Intent(Intent.ACTION_VIEW,
					Uri.parse("mailto:android@floatec.de?subject=Android app:"
							+ getString(R.string.app_name) + " V."
							+ getString(R.string.app_version) + ""));
			startActivity(browser);
			return true;
		case 4:
			//�ffnet ie mensa feedback seite vom studenten werk
			 browser = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.studentenwerk-mannheim.de/egotec/Essen+_+Trinken/Ihr+Feedback-p-32.html"));
			startActivity(browser);
			return true;
		case 2:
			mr.refrashlistWithoutCache();
			return true;
		case 1:
			Intent intent_menu_ueber = new Intent(this,
					UeberSeiteAnzeigen.class);
			startActivity(intent_menu_ueber);
			return true;
		case 7:
			Intent intent_menu_settings = new Intent(this,
					preferences.class);
			startActivity(intent_menu_settings);
			return true;
		case 5:
			showZusatzstoffe();
			return true;
		case 6:
			 Intent browser2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://floatec.de/donate.html"));
	   		 startActivity(browser2);
	   		 return true;
		case -1:
			this.finish();

			return true;

		}

		return false;

	}
}
