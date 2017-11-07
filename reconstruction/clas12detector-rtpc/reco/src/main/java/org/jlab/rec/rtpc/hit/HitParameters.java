package org.jlab.rec.rtpc.hit;

import java.util.HashMap;
import java.util.Vector;

public class HitParameters {
	
	private int _StepSize = 10;
	private int _BinSize = 40; 
	private int _NBinKept = 3; 
	private int _TrigWindSize = 10000;
	private int _eventnum = 0; 
	private HashMap<Integer, double[]> _R_adc = new HashMap<Integer, double[]>();
	private HashMap<Integer, Vector<Double>> _TimeMap = new HashMap<Integer, Vector<Double>>();
	private Vector<Integer> _PadN = new Vector<Integer>();  // used to read only cell with signal, one entry for each hit         
	private Vector<Integer> _PadNum = new Vector<Integer>();// used to read only cell with signal, one entry for each cell
	private Vector<Integer> _Pad = new Vector<Integer>();
	private Vector<Double> _ADC = new Vector<Double>();
	private Vector<Double> _Time_o = new Vector<Double>();
	private Vector<Double> _weightave = new Vector<Double>();
	private Vector<Double> _maxinte = new Vector<Double>();
	private Vector<Double> _time = new Vector<Double>();
	private Vector<Double> _XVec = new Vector<Double>();
	private Vector<Double> _YVec = new Vector<Double>();
	private Vector<Double> _ZVec = new Vector<Double>();
	

	
	
	 public int get_StepSize(){return _StepSize;} // step size of the signal before integration (arbitrary value)
	 public int get_BinSize(){return _BinSize;} // electronics integrates the signal over 40 ns
	 public int get_NBinKept(){return _NBinKept;} // only 1 bin over 3 is kept by the daq
	 public int get_TrigWindSize(){return _TrigWindSize;} // Trigger window should be 10 micro
	 public HashMap<Integer, double[]> get_R_adc(){return _R_adc;}
	 public HashMap<Integer, Vector<Double>> get_TimeMap(){return _TimeMap;}
	 public Vector<Integer> get_PadN(){return _PadN;}
	 public Vector<Integer> get_PadNum(){return _PadNum;}
	 public Vector<Integer> get_Pad(){return _Pad;}
	 public Vector<Double> get_ADC(){return _ADC;}
	 public Vector<Double> get_Time_o(){return _Time_o;}
	 public int get_eventnum(){return _eventnum;}
	 public Vector<Double> get_weightave() {return _weightave;}
	 public Vector<Double> get_maxinte() {return _maxinte;}
	 public Vector<Double> get_time() {return _time;}
	 public Vector<Double> get_XVec() {return _XVec;}
	 public Vector<Double> get_YVec() {return _YVec;}
	 public Vector<Double> get_ZVec() {return _ZVec;}
	 
	 
		public void set_R_adc(HashMap<Integer, double[]> _R_adc){this._R_adc = _R_adc;}
		public void set_TimeMap(HashMap<Integer, Vector<Double>> _TimeMap){this._TimeMap = _TimeMap;}
		public void set_PadN(Vector<Integer> _PadN){this._PadN = _PadN;}
		public void set_PadNum(Vector<Integer> _PadNum){this._PadNum = _PadNum;}
		public void set_Pad(Vector<Integer> _Pad){this._Pad = _Pad;}
		public void set_ADC(Vector<Double> _ADC){this._ADC = _ADC;}
		public void set_Time_o(Vector<Double> _Time_o){this._Time_o = _Time_o;}
		public void set_eventnum(int _eventnum){this._eventnum = _eventnum;}
		public void set_weightave(Vector<Double> _weightave) {this._weightave = _weightave;}
		public void set_maxinte(Vector<Double> _maxinte) {this._maxinte = _maxinte;}
		public void set_time(Vector<Double> _time) {this._time = _time;}
		public void set_XVec(Vector<Double> _XVec) {this._XVec = _XVec;}
		public void set_YVec(Vector<Double> _YVec) {this._YVec = _YVec;}
		public void set_ZVec(Vector<Double> _ZVec) {this._ZVec = _ZVec;}
		
		 
	 

	 
}
