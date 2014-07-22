package android.app.printerapp.devices;

import android.app.printerapp.ItemListActivity;
import android.app.printerapp.devices.database.DatabaseController;
import android.app.printerapp.devices.database.DeviceInfo.FeedEntry;
import android.app.printerapp.model.ModelPrinter;
import android.content.ClipData;
import android.content.res.Resources;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;


/**
 * this class will handle drag listeners for empty cells on the GridView, 
 * it will only listen to printers, and will update the position on the DB also
 * @author alberto-baeza
 *
 */
public class DevicesEmptyDragListener implements OnDragListener{
	
	int mPosition;
	
	//Constructor
	public DevicesEmptyDragListener(int pos){

		mPosition = pos;
	}
		

	@Override
	public boolean onDrag(View v, DragEvent event) {
		
			//Get the drop event
			int action = event.getAction();
		    switch (action) {
		    
		    //If it's a drop
		    case DragEvent.ACTION_DROP:{
		    	
	    		CharSequence tag = event.getClipData().getDescription().getLabel();
	    		ClipData.Item item = event.getClipData().getItemAt(0);
	    		
	    		//If it's a printer
	    		if (tag.equals("printer")){
		    		
		    		Log.i("DRAG", item.getText().toString());
		    		
		    		for (ModelPrinter p : DevicesListController.getList()){
		    			
		    			if (p.getName().equals(item.getText().toString())){
		    				
		    				//update position
		    				p.setPosition(mPosition);
		    				//update database
		    				DatabaseController.updateDB(FeedEntry.DEVICES_POSITION, p.getName(), String.valueOf(mPosition));
		    				//notify
		    				ItemListActivity.notifyAdapters();
		    			}
		    			
		    		}
		    		
	    		}
		    			

		    	//Remove highlight
		    	v.setBackgroundColor(Resources.getSystem().getColor(android.R.color.transparent));
		    	
		    } break;
		    
		    //TODO Disabled background hover
		    case DragEvent.ACTION_DRAG_ENTERED:{
		    	 	
		    	//Highlight on hover
		    	//v.setBackgroundColor(Resources.getSystem().getColor(android.R.color.holo_blue_light));
		    	
		    }break;
		    case DragEvent.ACTION_DRAG_EXITED:{
		    	
		    	//Remove highlight
		    	//v.setBackgroundColor(Resources.getSystem().getColor(android.R.color.transparent));
		    	
		    }break;

		      default:
		      break;
		    }
			
			return true;
		}
	

}
