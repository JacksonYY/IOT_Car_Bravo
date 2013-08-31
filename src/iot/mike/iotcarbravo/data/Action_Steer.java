package iot.mike.iotcarbravo.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;


public class Action_Steer {
	private static class Action_EmotorHolder{
		public static Action_Steer action_Steer = new Action_Steer();
	}
	
	public static Action_Steer getInstance(){
		return Action_EmotorHolder.action_Steer;
	}
	
	private static class Param{
		private int A;
		private int B;
		
		public synchronized void setA(int a){
			this.A = a;
		}
		
		public synchronized void setB(int b) {
			this.B = b;
		}
		
		public int getB(){
			return this.B;
		}
		
		public int getA(){
			return this.A;
		}
		
		public Param(){
			A = 0; B = 0;
		}
		@Override
		protected Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}
		
		public synchronized void RightAngle() {
			A = A + 5;
			if(A >= 60) {
				A = 60;
			}
		}
		
		public synchronized void LeftAngle() {
			A = A - 5;
			if(A <= -60) {
				A = -60;
			}
		}
		
		public synchronized void UpAngle() {
			B = B + 5;
			if(B >= 60) {
				B = 60;
			}
		}
		
		public synchronized void downAngle() {
			B = B - 5;
			if(B <= -60) {
				B = -60;
			}
		}
	}
	
	private String action;
	private Param param;
	
	private Action_Steer(){
		action = "steer";
		param = new Param();
	}
	
	public String getOrder() throws JSONException{
		JSONObject action = new JSONObject();
		JSONObject param = new JSONObject();
		
		param.put("A", this.getA());
		param.put("B", this.getB());
		
		action.put("action", this.action);
		action.put("param", param);
		
		return action.toString();
	}
	
	public void reset(){
		param.setA(0);
		param.setB(0);
	}
	
	public synchronized void setA(int a){
		param.setA(a);;
	}
	
	public synchronized void setB(int b) {
		param.setB(b);
	}
	
	public int getA(){
		return param.getA();
	}
	
	public int getB(){
		return param.getB();
	}
	
	public synchronized void RightAngle() {
		param.RightAngle();
	}
	
	public synchronized void LeftAngle() {
		param.LeftAngle();
	}
	
	public synchronized void UpAngle() {
		param.UpAngle();
	}
	
	public synchronized void DownAngle() {
		param.downAngle();
	}
}