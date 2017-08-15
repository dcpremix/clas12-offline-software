package org.jlab.rec.rtpc.hit;

import java.util.HashMap;
import java.util.Vector;

public class PadAve {
	
	public PadAve(){}
	
	
	public void TimeAverage(HitParameters params){
		
		int StepSize = params.get_StepSize(); // step size of the signal before integration (arbitrary value)
		int BinSize = params.get_BinSize(); // electronics integrates the signal over 40 ns
		int NBinKept = params.get_NBinKept(); // only 1 bin over 3 is kept by the daq
		int TrigWindSize = params.get_TrigWindSize(); // Trigger window should be 10 micro
		int NTrigSampl = TrigWindSize/BinSize; // number of time samples
		double inte=0;
		double inte_tot; // integral of the signal in BinSize
		double max_inte=0; // maximum of the integral to help the fit
		double max_t=0; 
		double sumnumer = 0; 
		double sumdenom = 0;
		double weightave = 0;
		HashMap<Integer, double[]> R_adc = params.get_R_adc();
		Vector<Integer> PadNum = params.get_PadNum();
		Vector<Integer> PadN = params.get_PadN();
		Vector<Integer> Pad = params.get_Pad();
		Vector<Double> ADC = params.get_ADC();
		Vector<Double> Time_o = params.get_Time_o();
		boolean flag_event = false; 
		int eventnum = params.get_eventnum();
		
		inte=0;
		for(int p=0;p<PadNum.size();p++){ 	    	
			inte_tot = 0;
			for(int t=0;t<TrigWindSize;t+=StepSize){  	         		         	
				if(t>0) inte+=0.5*(R_adc.get(PadNum.get(p))[t-StepSize]+R_adc.get(PadNum.get(p))[t])*StepSize;	         	
				inte_tot+=inte;	         	
				if(t%BinSize==0 && t>0){ // integration over BinSize
					if(t%(BinSize*NBinKept)==0){ // one BinSize over NBinKept is read out, hence added to the histogram							
						if(max_inte<inte){max_inte=inte; max_t=t;}  
						sumnumer+=inte*t;
						sumdenom+=inte;
					}	             
					inte=0;
				}
			}
			weightave = sumnumer/sumdenom;
			//if(PadNum.get(p) == 15157) System.out.println(eventnum + " " + PadNum.get(p) + " " + weightave);
			sumnumer = 0;
			sumdenom = 0;
			weightave = 0;
		}
		
	}

}
