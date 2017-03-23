package net.blogjava.mobile.light;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class Main extends Activity implements SensorEventListener,
		OnClickListener {
	float lastValue = 0;
	float lastdirection = 1;
	float[] LastExtremes = new float[2];
	private final static double Min_SENSITIVITY = 2;
	private final static double Max_SENSITIVITY = 4.5;

	private TextView textView;
	private float lastPoint;
	private int count = 0;
	private boolean flag = true;
	private SensorManager sm;
	private boolean mInitialized;

	@Override
	public void onClick(View view) {
		String msg = "";
		switch (view.getId()) {
			case R.id.btnStart:
				sm = (SensorManager) getSystemService(SENSOR_SERVICE);
				sm.registerListener(this, sm
								.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_FASTEST);
				msg = "begining.";
				break;

			case R.id.btnReset:
				count = 0;
				msg = "reset.";
				break;
			case R.id.btnStop:
				sm.unregisterListener(this);
				count = 0;
				msg = "stop.";
				break;
		}
		textView.setText(String.valueOf(count));
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnStart = (Button) findViewById(R.id.btnStart);
		Button btnReset = (Button) findViewById(R.id.btnReset);
		Button btnStop = (Button) findViewById(R.id.btnStop);
		btnStart.setOnClickListener(this);
		btnReset.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		textView = (TextView) findViewById(R.id.textview);
		textView.setText(String.valueOf(count));
		mInitialized = false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/*if (flag) {
			lastPoint = event.values[1];
			flag = false;
		}
		if (Math.abs(event.values[1] - lastPoint) > 40)
		{
			lastPoint = event.values[1];
			textView.setText(String.valueOf(++count));
		}
		*/
		if (!mInitialized) {
			mInitialized = true;
		} else {
			float[] gravity = new float[3];
			float[] linear_acceleration = new float[3];

			final float alpha = (float) 0.8;

			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			linear_acceleration[2] = event.values[2] - gravity[2];

			float vSum = (float)Math.sqrt((double)(linear_acceleration[0] * linear_acceleration[0]+
					linear_acceleration[1] * linear_acceleration[1] +
					linear_acceleration[2] * linear_acceleration[2])) / 3.0f;
			float direction = (lastValue-vSum>0.4? 1:(lastValue-vSum<-0.4?-1:lastdirection));
			if(direction == -1*lastdirection){
				int type = (direction>0?0:1);
				LastExtremes[type] = lastValue;
				float diff = LastExtremes[type] - LastExtremes[1-type];
				if(diff>Min_SENSITIVITY && diff<Max_SENSITIVITY && LastExtremes[type]>3.5){
					System.out.println("The vSum is: "+vSum+"The difference is:" +diff );
					textView.setText(String.valueOf(++count));
				}
			}
			lastdirection = direction;
			lastValue = vSum;

			//System.out.println(linear_acceleration[0]+" , "+ linear_acceleration[1]+" , "+linear_acceleration[2]+" , The vSum is: "+vSum);

		}
	}
}