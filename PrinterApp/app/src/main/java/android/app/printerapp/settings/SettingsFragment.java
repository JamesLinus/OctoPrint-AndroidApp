package android.app.printerapp.settings;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.printerapp.MainActivity;
import android.app.printerapp.R;
import android.app.printerapp.devices.DevicesListController;
import android.app.printerapp.devices.database.DatabaseController;
import android.app.printerapp.model.ModelPrinter;
import android.app.printerapp.octoprint.StateUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Class to manage the application and printer settings
 */
public class SettingsFragment extends Fragment {
	
	private SettingsListAdapter mAdapter;

	public SettingsFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Retain instance to keep the Fragment from destroying itself
		setRetainInstance(true);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//Reference to View
		View rootView = null;
				
		//If is not new
		if (savedInstanceState==null){
			
			//Show custom option menu
			setHasOptionsMenu(true);

            //Update the actionbar to show the up carat/affordance
            ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			
			//Inflate the fragment
			rootView = inflater.inflate(R.layout.settings_layout,
					container, false);
			
			/*********************************************************/
			
			getNetworkSsid(rootView);
			
			mAdapter = new SettingsListAdapter(getActivity(), R.layout.settings_row, DevicesListController.getList());
			ListView l = (ListView) rootView.findViewById(R.id.lv_settings);
			l.setAdapter(mAdapter);

			TextView tv = (TextView) rootView.findViewById(R.id.tv_version);
			tv.setText(setBuildVersion());

            notifyAdapter();
			
			
			
		}
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.settings_menu, menu);
	}
	
	//Option menu
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
	   
	   switch (item.getItemId()) {

           case android.R.id.home:
               getActivity().onBackPressed();
               return true;
	   
	   case R.id.settings_menu_add: //Add a new printer


           optionAddPrinter();

		   return true;
    
       default:
           return super.onOptionsItemSelected(item);
	   }
	}
	

	
	//Return network without quotes
	public void getNetworkSsid(View v){
		 
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();   		
		
		TextView tv = (TextView) v.findViewById(R.id.tv_network);
		tv.setText(wifiInfo.getSSID().replace("\"", ""));
		
		ImageView iv = (ImageView) v.findViewById(R.id.imageView_signal);
		
		int signal = wifiInfo.getRssi();
		
		if ((signal <= 0) && (signal > -40)){
			iv.setImageResource(R.drawable.stat_sys_wifi_signal_4);
		} else if ((signal <= -40) && (signal > -60)){
			iv.setImageResource(R.drawable.stat_sys_wifi_signal_3);
		} else if ((signal <= -60) && (signal > -70)){
			iv.setImageResource(R.drawable.stat_sys_wifi_signal_2);
		} else if ((signal <= -70) && (signal > -80)){
			iv.setImageResource(R.drawable.stat_sys_wifi_signal_1);
		} else iv.setImageResource(R.drawable.stat_sys_wifi_signal_0);

	}

	public String setBuildVersion(){
		
		String s = "Version v.";

		 try{

             //Get version name from package
             PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
             String fString = pInfo.versionName;

             //Parse version and date
             String hash = fString.substring(0,fString.indexOf(" "));
             String date = fString.substring(fString.indexOf(" "), fString.length());

             //Format hash
             String [] fHash = hash.split(";");

             //Format date
             SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm",new Locale("es", "ES"));
             String fDate = sdf.format(new java.util.Date(date));

             //Get version code / Jenkins build
             String code;
             if (pInfo.versionCode == 0) code = "IDE";
             else code = "#"+ pInfo.versionCode;

             //Build string
             s = s + fHash[0] + " " + fHash[1] + " " + fDate + " " + code;

		  }catch(Exception e){
			  
			  e.printStackTrace();
		  }
		 
		 return s;
	}

    public void notifyAdapter(){
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Add a new printer to the database by IP instead of service discovery
     */
    private void optionAddPrinter(){

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());


        adb.setTitle(R.string.settings_add_title);

        //Inflate the view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.settings_add_printer_dialog, null, false);

        final EditText et_name = (EditText) v.findViewById(R.id.et_name);
        final EditText et_address = (EditText) v.findViewById(R.id.et_address);

        adb.setView(v);

        //On insertion write the printer onto the database and start updating the socket
        adb.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ModelPrinter m = new ModelPrinter(et_name.getText().toString(), "/" + et_address.getText().toString(), StateUtils.TYPE_CUSTOM);

                if (!DevicesListController.checkExisting(m)) {

                    DevicesListController.addToList(m);
                    m.setId(DatabaseController.writeDb(m.getName(), m.getAddress(), String.valueOf(m.getPosition()), String.valueOf(m.getType()),
                            MainActivity.getCurrentNetwork(getActivity())));
                    //m.setLinked(getActivity());
                    notifyAdapter();

                }

            }
        });

        adb.setNegativeButton(R.string.cancel, null);

        adb.setView(v);

        adb.show();


    }
	
}
