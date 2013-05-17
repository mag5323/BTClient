package com.example.btclient;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{
	private static final int REQUEST_ENABLE_BT = 2;
	private EditText msg;
	private BluetoothAdapter BTAdapter = null;
	private ArrayList<HashMap<String, String>> list;
	private SimpleAdapter adapter;
	private ListView menu;
	private BTReceiver receiver;
	//private final UUID MY_UUID = UUID.fromString("fa87c0f0-afac-llde-8a39-0800200c9a66");
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		msg = (EditText)findViewById(R.id.msg);
		menu = (ListView)findViewById(R.id.menu);
		
		BTAdapter = BluetoothAdapter.getDefaultAdapter();
		if(BTAdapter == null){
			Toast.makeText(this, "¸Ë¸m¤£¤ä´©Bluetooth", Toast.LENGTH_LONG).show();			
			return;
		}else if(!BTAdapter.isEnabled()){
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_ENABLE_BT);	
		}
		
		list = new ArrayList<HashMap<String, String>>();
		
		//·j´M»·ºÝÂÅªÞ¸Ë¸m
		if(BTAdapter.isDiscovering()){
			BTAdapter.cancelDiscovery();
		}
		BTAdapter.startDiscovery();
		Toast.makeText(this, "·j´M»·ºÝÂÅªÞ¸Ë¸m", Toast.LENGTH_LONG).show();
		receiver = new BTReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		
		adapter = new SimpleAdapter(this, list, 
				  R.layout.menuitem, 
				  new String[]{"name", "mac"},
				  new int[]{R.id.name, R.id.mac}
				  );
			
		menu.setOnItemClickListener(this);
		menu.setAdapter(adapter);			
		
	}//end of onCreate
	
	@Override
	public void onItemClick(AdapterView<?> av, View view, int arg2, long arg3){
		BTAdapter.cancelDiscovery();
		BluetoothDevice device = BTAdapter.getRemoteDevice(list.get(arg2).get("mac"));
		Toast.makeText(this, device.getAddress(), Toast.LENGTH_LONG).show();
		
		try{
			BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
			socket.connect();
			BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
			bout.write(msg.getText().toString().getBytes());
			bout.flush();
			bout.close();
			socket.close();			
		}catch(IOException e){
			Toast.makeText(this, "IO/Exception", Toast.LENGTH_LONG).show();			
		}
	}
	
	private class BTReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("name", device.getName());
				map.put("mac", device.getAddress());
				list.add(map);				
			}
			
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
