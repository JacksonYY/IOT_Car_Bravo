package iot.mike.iotcarbravo.activities;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import h264.com.VView;

import iot.mike.iotcarbravo.data.Action_Emotor;
import iot.mike.iotcarbravo.data.Action_Steer;
import iot.mike.iotcarbravo.data.ResultType;
import iot.mike.iotcarbravo.data.Result_GPS;
import iot.mike.iotcarbravo.data.Result_List;
import iot.mike.iotcarbravo.data.Result_OKCamera;
import iot.mike.iotcarbravo.data.Result_USBCamera;
import iot.mike.iotcarbravo.mapview.OfflineMapView;
import iot.mike.iotcarbravo.net.SocketManager;
import iot.mike.iotcarbravo.setting.SettingData;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class NoKeyBoardActivity extends Activity {
	private Button SpeedUP_BTN;
	private Button SpeedAVG_BTN;
	private Button Stop_BTN;
	
	private Button CameraUP_BTN;
	private Button CameraDOWN_BTN;
	private Button CameraLEFT_BTN;
	private Button CameraRIGHT_BTN;
	
	private Button Start_BTN;
	private Button End_BTN;
	private OfflineMapView mapView;
	private static VView videoView;
	
	private SocketManager socketManager = SocketManager.getInstance();
	
	private Thread initNOKeyBoardThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = ResultType.ReadyOK;
			MainctivityHandler_NoKeyBoard.sendMessage(message);
		}
	});
	
	private Timer addSpeedTimer = null;
	private class addSpeedTimerTask extends TimerTask {
		//加速
		@Override
		public void run() {
			Action_Emotor.getInstance().addSpeed();
			try {
				Log.e("fdk",Action_Emotor.getInstance().getOrder());
				socketManager.sendOrder(Action_Emotor.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
	}
	
	private Timer reduceSpeedTimer = null;
	private class reduceSpeedTimerTask extends TimerTask {
		//刹车减速
		@Override
		public void run() { 
		Action_Emotor.getInstance().reduceSpeed();
			try {
				socketManager.sendOrder(Action_Emotor.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private Timer keepSpeedTimer = null;
	private class keepSpeedTimerTask extends TimerTask {
		//保持匀速
		@Override
		public void run() {
			try {
				socketManager.sendOrder(Action_Emotor.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private Timer rightTurnTimer = null;
	private class rightTurnTimerTask extends TimerTask {
		//摄像头右转
		@Override
		public void run() {
			Action_Steer.getInstance().RightAngle();
			try {
				socketManager.sendOrder(Action_Steer.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private Timer leftTurnTimer = null;
	private class leftTurnTimerTask extends TimerTask {
		//摄像头左转
		@Override
		public void run() {
			Action_Steer.getInstance().LeftAngle();
			try {
				socketManager.sendOrder(Action_Steer.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private Timer upTurnTimer = null;
	private class upTurnTimerTask extends TimerTask {
		//摄像头向上转
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Action_Steer.getInstance().UpAngle();
			try {
				//Log.e("33",Action_Steer.getInstance().getOrder());
				socketManager.sendOrder(Action_Steer.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private Timer downTurnTimer = null;
	private class downTurnTimerTask extends TimerTask {
		//摄像头向下转
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Action_Steer.getInstance().DownAngle();
			try {
				socketManager.sendOrder(Action_Steer.getInstance().getOrder());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (SettingData.CtrlMode == SettingData.KeyBoard) {
			Intent intent = new Intent(getApplicationContext(), 
					KeyBoradActivity.class);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nokeyboard);
		initNOKeyBoardViews();
		socketManager.setKeyBoardActivityHandler(MainctivityHandler_NoKeyBoard);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nokeyboard, menu);
		return true;
	}

	
	private void initNOKeyBoardViews(){
		initNOKeyBoardThread.start();
		
		createGravitySensor();
		
		socketManager.setKeyBoardActivityHandler(MainctivityHandler_NoKeyBoard);
		
		videoView = (VView)findViewById(R.id.videoView);
		mapView = (OfflineMapView)findViewById(R.id.mapView);
		SpeedAVG_BTN = (Button)findViewById(R.id.speedAVG_BTN);
		SpeedAVG_BTN.setOnTouchListener(new MyOnTouchListener(socketManager, 
				Action_Emotor.getInstance(), 
				Action_Steer.getInstance()));
		SpeedUP_BTN = (Button)findViewById(R.id.speedUP_BTN);
		SpeedUP_BTN.setOnTouchListener(new MyOnTouchListener(socketManager,
				Action_Emotor.getInstance(), 
				Action_Steer.getInstance()));
		Stop_BTN = (Button)findViewById(R.id.stop_BTN);
		Stop_BTN.setOnTouchListener(new MyOnTouchListener(socketManager, 
				Action_Emotor.getInstance(), 
				Action_Steer.getInstance()));
		
		CameraDOWN_BTN = (Button)findViewById(R.id.camera_DOWN_BTN);
		CameraDOWN_BTN.setOnTouchListener(new MyOnTouchListener(socketManager,
				Action_Emotor.getInstance(),
				Action_Steer.getInstance()));
		
		CameraLEFT_BTN = (Button)findViewById(R.id.camera_LEFT_BTN);
		CameraLEFT_BTN.setOnTouchListener(new MyOnTouchListener(socketManager,
				Action_Emotor.getInstance(),
				Action_Steer.getInstance()));
		CameraRIGHT_BTN = (Button)findViewById(R.id.camera_RIGHT_BTN);
		CameraRIGHT_BTN.setOnTouchListener(new MyOnTouchListener(socketManager, 
				Action_Emotor.getInstance(), 
				Action_Steer.getInstance()));
		
		CameraUP_BTN = (Button)findViewById(R.id.camera_UP_BTN);
		CameraUP_BTN.setOnTouchListener(new MyOnTouchListener(socketManager,
				Action_Emotor.getInstance(), 
				Action_Steer.getInstance()));
		
		Start_BTN = (Button)findViewById(R.id.Start_BTN);
		Start_BTN.setOnClickListener(new  MyClickListener(socketManager,
				Action_Emotor.getInstance(),Action_Steer.getInstance()));
		
		End_BTN = (Button)findViewById(R.id.End_BTN);
		End_BTN.setOnClickListener(new MyClickListener(socketManager, 
				Action_Emotor.getInstance(),Action_Steer.getInstance()));
	}

	private class MyClickListener implements OnClickListener {
		private SocketManager socketManager;
		private Action_Emotor action_Emotor;
		private Action_Steer action_Steer;
		
		public MyClickListener(SocketManager socketManager,
					Action_Emotor action_Emotor,
					Action_Steer action_Steer) {
			this.socketManager  = socketManager;
			this.action_Emotor = action_Emotor;
			this.action_Steer = action_Steer;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.Start_BTN:
				Log.e("x","yaoyuanwansui");
				socketManager.startLink();
				action_Emotor.reset();
				action_Steer.reset();
				break;
				
			case R.id.End_BTN :
				socketManager.endLink();
				action_Emotor.reset();
				action_Steer.reset();
			default:
				break;
			}
		}
		
	}
	private class MyOnTouchListener implements OnTouchListener{
    	private SocketManager socketManager;
    	private Action_Emotor action_Emotor;
    	private Action_Steer action_Steer;
    	
    	public MyOnTouchListener(SocketManager socketManager,
    			Action_Emotor action_Emotor,
    			Action_Steer action_Steer){
    		this.socketManager = socketManager;
    		this.action_Emotor = action_Emotor;
    		this.action_Steer = action_Steer;
    	}
    	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (v.getId()) {
					case R.id.speedAVG_BTN:{    //匀速
						if(keepSpeedTimer != null) {
							keepSpeedTimer.cancel();
							keepSpeedTimer = null;
						}
						
						keepSpeedTimer = new Timer();
						keepSpeedTimer.schedule(new keepSpeedTimerTask(),0,500);
						break;
					}
					
					case R.id.speedUP_BTN:{		//加速
						if(addSpeedTimer != null) {
							addSpeedTimer.cancel();
							addSpeedTimer = null;
						}
						addSpeedTimer = new Timer();
						addSpeedTimer.schedule(new addSpeedTimerTask(), 0, 500);
						break;
					}
					
					case R.id.stop_BTN:{		//刹车减速
						if(reduceSpeedTimer != null) {
							reduceSpeedTimer.cancel();
							reduceSpeedTimer = null;
						}
						reduceSpeedTimer = new Timer();
						reduceSpeedTimer.schedule(new reduceSpeedTimerTask(),0,500);
						break;
					}
					
					case R.id.camera_DOWN_BTN:{
						if(downTurnTimer != null) {
							downTurnTimer.cancel();
							downTurnTimer = null;
						}
						downTurnTimer = new Timer();
						downTurnTimer.schedule(new downTurnTimerTask(), 0, 500);
						break;
					}
					
					case R.id.camera_LEFT_BTN:{
						if(leftTurnTimer != null) {
							leftTurnTimer.cancel();
							leftTurnTimer = null;
						}
						leftTurnTimer = new Timer();
						leftTurnTimer.schedule(new leftTurnTimerTask(),0, 500);
					break;
					}
					
					case R.id.camera_RIGHT_BTN:{
						if(rightTurnTimer != null) {
							rightTurnTimer.cancel();
							rightTurnTimer = null;
						}
						rightTurnTimer = new Timer();
						rightTurnTimer.schedule(new reduceSpeedTimerTask(),0,500);
						break;
					}
					
					case R.id.camera_UP_BTN:{
						if(upTurnTimer != null) {
							upTurnTimer.cancel();
							upTurnTimer = null;
						}
						upTurnTimer = new Timer();
						upTurnTimer.schedule(new upTurnTimerTask(), 0,500);
						break;
					}
					
				}
			}else if (event.getAction() == KeyEvent.ACTION_UP) {
				switch (v.getId()) {
					case R.id.speedAVG_BTN:{
						if(keepSpeedTimer != null) {
							keepSpeedTimer.cancel();
							keepSpeedTimer = null;
						}
					
						Action_Emotor.getInstance().reset();
						break;
					}
					
					case R.id.speedUP_BTN:{
						if(addSpeedTimer != null) {
							addSpeedTimer.cancel();
							addSpeedTimer = null;
						}
						Action_Emotor.getInstance().reset();
						break;
					}
					
					case R.id.stop_BTN:{
						if(reduceSpeedTimer != null) {
							reduceSpeedTimer.cancel();
							reduceSpeedTimer = null;
						}
						Action_Emotor.getInstance().reset();					
						break;
					}
					
					case R.id.camera_DOWN_BTN:{
						if(downTurnTimer != null) {
							downTurnTimer.cancel();
							downTurnTimer = null;
						}
						Action_Steer.getInstance().reset();
						break;
					}
					
					case R.id.camera_LEFT_BTN:{
						if(leftTurnTimer != null) {
							leftTurnTimer.cancel();
							leftTurnTimer = null;
						}
						Action_Steer.getInstance().reset();
						break;
					}
					
					case R.id.camera_RIGHT_BTN:{
						if(rightTurnTimer != null) {
							rightTurnTimer.cancel();
							rightTurnTimer = null;
						}
						Action_Steer.getInstance().reset();
						break;
					}
					
					case R.id.camera_UP_BTN:{
						if(upTurnTimer != null) {
							upTurnTimer.cancel();
							upTurnTimer = null;
						}
						Action_Steer.getInstance().reset();
						break;
					}
					
				}
			}
			return false;
		}
    }
	
	private Handler MainctivityHandler_NoKeyBoard = new Handler(){
		@Override
		public void handleMessage(Message message){
			switch (message.what) {
				case ResultType.Result_GPS:{
					Result_GPS result_GPS = Result_GPS.getInstance();
					mapView.setLocation(result_GPS.getLongtitude(), 
							result_GPS.getLatitude(), 
							18, 
							result_GPS.getSpeed(), 
							result_GPS.getHeight());
					break;
				}
				
				case ResultType.Result_OKCamera:{
					Result_OKCamera result_OKCamera = 
							Result_OKCamera.getInstance();
					socketManager.sendVideo(result_OKCamera.getFrameData());
					break;
				}
				
				case ResultType.Result_List:{
					Result_List result_List = 
							Result_List.getInstance();
					String[] lists = result_List.getParams();
					String list_Str = "";
					for (String list : lists) {
						list_Str += list + "\n";
					}
					Toast.makeText(getApplicationContext(), 
							list_Str, Toast.LENGTH_LONG).show();
					break;
				}
				
				case ResultType.Result_USBCamera:{
					Result_USBCamera result_USBCamera = 
							Result_USBCamera.getInstance();
					socketManager.sendVideo(result_USBCamera.getFrameData());
					break;
				}
				
				case ResultType.StartLink:{
					break;
				}
				
				case ResultType.ReadyOK:{
					videoView.playVideo();
					break;
				}
				default:{
					
					break;
				}
			}
		}
	};
	
	//--------------------------------------------------------------------------------
    //设置感应器
	private boolean isTurnLeft =false;	//小车是否右拐
	private int TurnD = 0;						//拐弯的角度
	 
    private SensorManager sensorManager;
    private Sensor sensor;
    private double x, y, z;		//从传感器中读取的数据
    private void createGravitySensor(){
    	// 得到当前手机传感器管理对象
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 加速重力感应对象
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 实例化一个监听器
        SensorEventListener lsn = new SensorEventListener() {
            // 实现接口的方法
            public void onSensorChanged(SensorEvent e) {
                // 得到各轴上的重力加速度
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                z = e.values[SensorManager.DATA_Z];
      
                //-----------------------------------------------------
                if (z > 8 && z < 0) {//判断手机的位置是否正确
					Toast.makeText(getApplicationContext(), "请保持手机的垂直放置", Toast.LENGTH_SHORT).show();
					TurnD = 0;
				}else {
					if (y < 1 && y > -1) {//直线运动
						TurnD = 0;
					}else {
						double degree = x/y;
						if (degree < 0) {
							isTurnLeft = true;
							if (degree >= -1) {//偏转角大于45度(左)
								TurnD = 100;
							}else {
								TurnD = (int) -(100/degree);
								if (TurnD < 16) {//确保指令传输的正确性
									TurnD = 16;
								}
							}
						}else if (degree > 0) {
							isTurnLeft = false;
							if (degree <= 1) {//偏转角大于45度(右)
								TurnD = 100;
							}else {
								TurnD = (int) (100/degree);
								if (TurnD < 16) {//确保指令传输的正确性
									TurnD = 16;
								}
							}
						}
					}
				}
                //判断方向
                if (isTurnLeft) {
					TurnD = -TurnD;
				}
                Action_Emotor action_Emotor = Action_Emotor.getInstance();
                action_Emotor.setX(TurnD);
                try {
					socketManager.sendOrder(Action_Emotor.getInstance().getOrder());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}    // 向小车发送指令
                //Log.e(String.valueOf(TurnD), String.valueOf(isTurnLeft));
                //----------打印值
                //Toast.makeText(getApplicationContext(), String.valueOf(x) + ":" +String.valueOf(y) + ":" + String.valueOf(z), Toast.LENGTH_SHORT).show();
            }
            
            public void onAccuracyChanged(Sensor s, int accuracy) {}
        };
        // 注册listener，第三个参数是检测的精确度
        sensorManager.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
    //-----------------------------------------------------------------------------
}
