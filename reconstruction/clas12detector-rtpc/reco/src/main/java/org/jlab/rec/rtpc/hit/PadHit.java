package org.jlab.rec.rtpc.hit;
import java.util.*;
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.fitter.*;
import org.jlab.groot.math.*;
import javax.swing.JFrame;
public class PadHit {



public PadHit(){}






public void bonus_shaping(List<Hit> rawHits, HitParameters params){


//______________________________________________________________________________________________
//  __________________________________________ Variables _________________________________________
//______________________________________________________________________________________________
    	
	int StepSize = params.get_StepSize(); // step size of the signal before integration (arbitrary value)
  	int BinSize = params.get_BinSize(); // electronics integrates the signal over 40 ns
  	int NBinKept = params.get_NBinKept(); // only 1 bin over 3 is kept by the daq
  	int TrigWindSize = params.get_TrigWindSize(); // Trigger window should be 10 micro
 	int NTrigSampl = TrigWindSize/BinSize; // number of time samples

  	HashMap<Integer, double[]> R_adc = params.get_R_adc(); // Raw depositions for CellID, ADC

  	Vector<Integer> PadN = params.get_PadN();  // used to read only cell with signal, one entry for each hit         
  	Vector<Integer> PadNum = params.get_PadNum();// used to read only cell with signal, one entry for each cell
  	Vector<Double> ADC = new Vector<Double>();
  
  	Vector<Integer> Pad = params.get_Pad();
  	//Vector<Double> ADC = params.get_ADC();
  	Vector<Double> Time_o = params.get_Time_o();
  
 


//______________________________________________________________________________________________
//  __________________________________________ Openings __________________________________________
//______________________________________________________________________________________________

   	int CellID = 0; 
  	double Time;
  	double totEdep;
  	int eventnum = params.get_eventnum(); 
  	eventnum++;
  	double testsum = 0; 
  	double testcount = 1; 

//______________________________________________________________________________________________
//  __________________________________________ Readings __________________________________________
//______________________________________________________________________________________________

//--Creating the signal on pads with 1 ns steps.


    // Initializations
    R_adc.clear(); // Raw depositions for CellID, adc
    PadN.clear();
    PadNum.clear();
    //Pad->clear();
    //ADC->clear();  // not reliable for now as the fit fails often
    //Time_o->clear();  



  	for(Hit hit : rawHits){

  		CellID = hit.get_cellID();
  		Time = hit.get_Time();
  		totEdep = hit.get_EdepTrue();

 // searches in PadN if CellID already exists

        if(PadN.contains(CellID)){ // this pad has already seen signal
        		for(int t=0;t<TrigWindSize;t+=StepSize){     
        			R_adc.get(CellID)[t] += EtoS(Time,t,totEdep);               
        		}
        }
        
        else{ // first signal on this pad
        		R_adc.put(CellID, new double[TrigWindSize]);
        		for(int t=0;t<TrigWindSize;t+=StepSize){
        			R_adc.get(CellID)[t] = EtoS(Time,t,totEdep);                       
        		}
        		PadNum.add(CellID);
        }       
        PadN.add(CellID);
        /*if(CellID == 15157 && eventnum == 1){
        	System.out.println(Time + " " + testcount);
        	testsum+=Time;
        	testcount++;
        }*/
   } // c
   //if(eventnum == 1) System.out.println("Result " + testsum/(testcount-1));
      //--Signal created on pads with StepSize ns steps

params.set_ADC(ADC);
params.set_Pad(Pad);
params.set_PadN(PadN);
params.set_PadNum(PadNum);
params.set_R_adc(R_adc);
params.set_Time_o(Time_o);
params.set_eventnum(eventnum);

}


double EtoS(double tini, double t, double e_tot){

	double sig;

	t = noise_elec(t);    // change t to simulate the electronics noise, also modifies the amplitude
	double p0 = 0.0;     
	double p2 = 178.158;    
	double p3 = 165.637;     
	double p4 = 165.165;

	if(t<tini) sig = p0+e_tot*p2*Math.exp(-(t-tini)*(t-tini)/(2*p3*p3))/(0.5*(p3+p4)*Math.sqrt(2*Math.PI));
	else       sig = p0+e_tot*p2*Math.exp(-(t-tini)*(t-tini)/(2*p4*p4))/(0.5*(p3+p4)*Math.sqrt(2*Math.PI)); 
  
	return sig;

}

double noise_elec(double tim){
	Random noise = new Random();
	double sigTelec = 5; // 5 ns uncertainty on the signal
	return noise.nextGaussian()*sigTelec + tim;
}

double drift_V(double tim){
	Random noise = new Random();
	double sigTVdrift = 5; // 5 ns uncertainty on the
	return noise.nextGaussian()*sigTVdrift + tim;
}







}