package com.dabgroup;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DriveDroid extends Activity {
	public class AzioneShowMenu implements OnClickListener {
		DriveDroid activity;

		public AzioneShowMenu(DriveDroid activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.scan:
				// Launch the DeviceListActivity to see devices and do scan
				Intent serverIntent = new Intent(DriveDroid.this,
						DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				break;
			case R.id.start:
				MyView1 view1 = new MyView1(activity);
				// Numbers distance = new Numbers(activity);
				distance = new Numbers(activity);
				ldistance.addView(distance);
				lview1.addView(view1);
				lview2.removeAllViewsInLayout();
				menu.setVisibility(FrameLayout.GONE);
				ldistance.setVisibility(FrameLayout.VISIBLE);
				lview1.setVisibility(FrameLayout.VISIBLE);
				lview2.setVisibility(FrameLayout.GONE);
				lview3.setVisibility(FrameLayout.GONE);
				break;
			case R.id.discoverable:
				ensureDiscoverable();
				break;
			case R.id.exit:
				mBluetoothAdapter.disable();
				finish();
				break;
			default:
				break;
			}
		}

	}

	public LinearLayout menu;
	public RelativeLayout ldistance;
	public RelativeLayout lview1;
	public RelativeLayout lview2;
	public RelativeLayout lview3;
	private Activity activity;
	private Numbers distance;
	// Debugging
	private static final String TAG = "BluetoothChat";
	static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private TextView mTitle;
	// private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	// public ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	// mie variabili
	public ListView wordsList;
	public TextView messages;
	public Button mSpeakButton;
	public int leftRight = 0;
	public int backForward = 0;
	public static final int REQUEST_CODE = 1234;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setBackgroundDrawableResource(R.drawable.pro_sfondo);

		setContentView(R.layout.frame);
		menu = (LinearLayout) findViewById(R.id.menu);
		ldistance = (RelativeLayout) findViewById(R.id.distance);
		lview1 = (RelativeLayout) findViewById(R.id.view1);
		lview2 = (RelativeLayout) findViewById(R.id.view2);
		lview3 = (RelativeLayout) findViewById(R.id.view3);

		menu.setVisibility(FrameLayout.VISIBLE);
		ldistance.setVisibility(FrameLayout.GONE);
		lview1.setVisibility(FrameLayout.GONE);
		lview2.setVisibility(FrameLayout.GONE);
		lview3.setVisibility(FrameLayout.GONE);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		Button scan = (Button) findViewById(R.id.scan);
		Button start = (Button) findViewById(R.id.start);
		Button discoverable = (Button) findViewById(R.id.discoverable);
//		ImageButton exit = (ImageButton) findViewById(R.id.exit);

		scan.setOnClickListener(new AzioneShowMenu(this));
		start.setOnClickListener(new AzioneShowMenu(this));
		discoverable.setOnClickListener(new AzioneShowMenu(this));
//		exit.setOnClickListener(new AzioneShowMenu(this));

		/** riprovo a fare danno -> OnCreate **/

		mSpeakButton = (Button) findViewById(R.id.button_speech);
		mSendButton = (Button) findViewById(R.id.button_send);
		wordsList = (ListView) findViewById(R.id.listViewMatches);
		messages = (TextView) findViewById(R.id.textViewBody);
		messages.setMovementMethod(new ScrollingMovementMethod());
		messages.append("\n\n");
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);

		// Disable button if no recognition service is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			mSpeakButton.setEnabled(false);
			mSpeakButton.setText("Recognizer not present");
		}

		wordsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String item = ((TextView) arg1).getText().toString();
				if (item.equals("help") || item.equals("help ")
						|| item.equals("Help") || item.equals("Help ")) {
					messages.append("Comandi possibili: \n"
							+ getString(R.string.FW) + "\n"
							+ getString(R.string.BW) + "\n"
							+ getString(R.string.RI) + "\n"
							+ getString(R.string.LE) + "\n"
							+ "ripetendo il comando aumenterÃ  l'intensitÃ .");
				} else if (item.equals("avanti")) {
					controllo(item,1,0);
				} else if (item.equals("indietro")) {
					controllo(item,-1,0);
				} else if (item.equals("destra")) {
					controllo(item,0,1);
				} else if (item.equals("sinistra")) {
					controllo(item,0,-1);
				} else if (item.equals("stop")) {
					sendMessage("XX0");
				}
				wordsList.setVisibility(FrameLayout.GONE);

			}
		});

		mSendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				String text = mOutEditText.getText().toString();
				if (text.equals("help") || text.equals("Help")
						|| text.equals("help ") || text.equals("Help ")) {
					messages.append("Comandi possibili: \n"
							+ getString(R.string.FW) + "\n"
							+ getString(R.string.BW) + "\n"
							+ getString(R.string.RI) + "\n"
							+ getString(R.string.LE) + "\n"
							+ "ripetendo il comando aumenterà  l'intensità .");
				} else if (text.equals("avanti") || text.equals("Avanti")
						|| text.equals("avanti ") || text.equals("Avanti ")) {
					controllo(text,1,0);
				} else if (text.equals("indietro") || text.equals("Indietro")
						|| text.equals("indietro ") || text.equals("Indietro ")) {
					controllo(text,-1,0);
				} else if (text.equals("destra") || text.equals("Destra")
						|| text.equals("destra ") || text.equals("Destra ")) {
					controllo(text,0,1);
				} else if (text.equals("sinistra") || text.equals("Sinistra")
						|| text.equals("sinistra ") || text.equals("Sinistra ")) {
					controllo(text,0,-1);
				} else if (text.equals("stop") || text.equals("Stop")
						|| text.equals("stop ") || text.equals("Stop ")) {
					messages.append(text + "\n");
					sendMessage("XX0");
				} else if (text.equals("FW0") || text.equals("FW1")
						|| text.equals("FW2") || text.equals("FW3")
						|| text.equals("FW4")) {
					sendMessage(text);
				} else if (text.equals("BW0") || text.equals("BW1")
						|| text.equals("BW2") || text.equals("BW3")
						|| text.equals("BW4")) {
					sendMessage(text);
				}
				mOutEditText.setText("");
			}
		});
	}

	public void controllo(String testo, int a, int b) {
		messages.append(testo + "\n");
		backForward += a;
		leftRight += b;
		manageDirections(leftRight, backForward);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
		mOutEditText.setOnEditorActionListener(mWriteListener);
		mChatService = new BluetoothChatService(this, mHandler);
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		setPersistent(isTaskRoot());
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		setPersistent(true);
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// Invia messaggi
	protected void sendMessage(String message) {
		Log.d("ENTRATO", "Messaggio: " + message); // CONTROLLO
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			/*
			 * Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
			 * .show();
			 */
			return;
		}

		// E' disponibile qualcosa da inviare?
		if (message.length() > 0) {
			// Prendi i bytes del messaggio e comunica al BluetoothChatService di scrivere
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset del buffer e pulizia della casella di testo
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	// Listener per l'azione di invio nell'EditText
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// Invia se sul button c'e' l'azione ACTION_UP
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			if (D) {
				Log.i(TAG, "END onEditorAction");
			}
			return false;
		}
	};

	// Handler che prende le informazione dal BluetoothChatService
	public final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:		// Cambio lo stato della TextView nella Home
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
//				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				String readMessage = new String(readBuf, 0, msg.arg1);
				if (ldistance.isShown()) {
					Log.d("ATTIVO", "" + readMessage);
					distance.setDistance(readMessage);
				}
				break;
			case MESSAGE_DEVICE_NAME: // Salva il nome del device collegato
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			wordsList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, matches));
			wordsList.setVisibility(FrameLayout.VISIBLE);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu men) {
		// TODO Auto-generated method stub
		sendMessage("XX0");
		ldistance.setVisibility(FrameLayout.GONE);
		lview1.setVisibility(FrameLayout.GONE);
		lview2.setVisibility(FrameLayout.GONE);
		lview3.setVisibility(FrameLayout.GONE);
		ldistance.removeAllViewsInLayout();
		lview1.removeAllViewsInLayout();
		lview2.removeAllViewsInLayout();
		menu.setVisibility(FrameLayout.GONE);
		return super.onMenuOpened(featureId, men);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			menu.setVisibility(FrameLayout.VISIBLE);
			return true;
		case R.id.view1:
			menu.setVisibility(FrameLayout.GONE);
			MyView1 view1 = new MyView1(this);
			distance = new Numbers(this);
			ldistance.addView(distance);
			lview1.addView(view1);
			ldistance.setVisibility(FrameLayout.VISIBLE);
			lview1.setVisibility(FrameLayout.VISIBLE);
			lview2.setVisibility(FrameLayout.GONE);
			lview3.setVisibility(FrameLayout.GONE);
			return true;
		case R.id.view2:
			menu.setVisibility(FrameLayout.GONE);
			MyView2 view2 = new MyView2(this);
			distance = new Numbers(this);
			ldistance.addView(distance);
			lview2.addView(view2);
			ldistance.setVisibility(FrameLayout.VISIBLE);
			lview2.setVisibility(FrameLayout.VISIBLE);
			lview1.setVisibility(FrameLayout.GONE);
			lview3.setVisibility(FrameLayout.GONE);
			return true;
		case R.id.view3:
			lview3.setVisibility(FrameLayout.VISIBLE);
			lview3.bringToFront();
			lview1.setVisibility(FrameLayout.GONE);
			lview2.setVisibility(FrameLayout.GONE);
			return true;
		case R.id.uscita:
			sendMessage("XX0");
			mBluetoothAdapter.disable();
			finish();
			break;
		}
		return false;
	}

	public void manageDirections(int lr, int bf) {
		if (lr <= -4) {
			sendMessage("SX4");
		} else if (lr >= 4) {
			sendMessage("DX4");
		} else {
			switch (lr) {
			case -3:
				sendMessage("SX3");
				break;
			case -2:
				sendMessage("SX2");
				break;
			case -1:
				sendMessage("SX1");
				break;
			case 0:
				sendMessage("SX0");
				break;
			case 1:
				sendMessage("DX1");
				break;
			case 2:
				sendMessage("DX2");
				break;
			case 3:
				sendMessage("DX3");
				break;
			default:
				break;
			}
		}

		if (bf <= -4) {
			sendMessage("BW4");
		} else if (bf >= 4) {
			sendMessage("FW4");
		} else {
			switch (bf) {
			case -3:
				sendMessage("BW3");
				break;
			case -2:
				sendMessage("BW2");
				break;
			case -1:
				sendMessage("BW1");
				break;
			case 0:
				sendMessage("FW0");
				break;
			case 1:
				sendMessage("FW1");
				break;
			case 2:
				sendMessage("FW2");
				break;
			case 3:
				sendMessage("FW3");
				break;
			default:
				break;
			}
		}
	}

	public void speakButtonClicked(View v) {
		startVoiceRecognitionActivity();
	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Voice recognition Demo...");
		startActivityForResult(intent, REQUEST_CODE);
	}

}