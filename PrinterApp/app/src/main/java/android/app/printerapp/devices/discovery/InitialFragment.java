package android.app.printerapp.devices.discovery;

import android.app.Fragment;
import android.app.printerapp.R;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by alberto-baeza on 2/4/15.
 */
public class InitialFragment extends Fragment{

    private Button mScanButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        /**
         * Since API level 11, thread policy has changed and now does not allow network operation to
         * be executed on UI thread (NetworkOnMainThreadException), so we have to add these lines to
         * permit it.
         */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;

        //If is not new
        if (savedInstanceState == null) {

            //Show custom option menu
            setHasOptionsMenu(false);

            rootView = inflater.inflate(R.layout.initial_fragment_layout,
                    container, false);

            mScanButton = (Button) rootView.findViewById(R.id.initial_scan_button);
            mScanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                    new DiscoveryController(getActivity());

                }
            });
        }



        return rootView;
    }






}
