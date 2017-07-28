package org.jlab.rec.rtpc.hit;
import java.util.*;
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.fitter.*;
import org.jlab.groot.math.*;
import javax.swing.JFrame;
public class PadHit {



public PadHit(){}



double sigTelec = 5; // 5 ns uncertainty on the signal
double sigTVdrift = 5; // 5 ns uncertainty on the
Random noise = new Random();

public void bonus_shaping(List<Hit> rawHits, HitParameters params)
//int main()
{


//______________________________________________________________________________________________
//  __________________________________________ Variables _________________________________________
//______________________________________________________________________________________________
    
	//HitParameters params = new HitParameters();
  
  int StepSize = params.get_StepSize(); // step size of the signal before integration (arbitrary value)
  int BinSize = params.get_BinSize(); // electronics integrates the signal over 40 ns
  int NBinKept = params.get_NBinKept(); // only 1 bin over 3 is kept by the daq
  int TrigWindSize = params.get_TrigWindSize(); // Trigger window should be 10 micro
  int NTrigSampl = TrigWindSize/BinSize; // number of time samples

  HashMap<Integer, double[]> R_adc = params.get_R_adc(); // Raw depositions for CellID, ADC

  Vector<Integer> PadN = new Vector<Integer>();  // used to read only cell with signal, one entry for each hit         
  Vector<Integer> PadNum = new Vector<Integer>();// params.get_PadNum();// used to read only cell with signal, one entry for each cell
  Vector<Double> ADC = new Vector<Double>();
  
  Vector<Integer> Pad = params.get_Pad();
  //Vector<Double> ADC = params.get_ADC();
  Vector<Double> Time_o = params.get_Time_o();
  
  int count=0;
  double inte=0;
  double inte_tot; // integral of the signal in BinSize
  double max_inte=0; // maximum of the integral to help the fit
  double max_t=0;    // time at the maximum to help the fit
  int ind=0;
  int ind_c=0;

  boolean flag_event = false;
  

//______________________________________________________________________________________________
//  ___________________________________ Canvas and histograms ____________________________________
//______________________________________________________________________________________________

 /*JFrame j1 = new JFrame();
 j1.setSize(800, 600);
 EmbeddedCanvas c1 = new EmbeddedCanvas();
 GraphErrors g1 = new GraphErrors();
 GraphErrors g2 = new GraphErrors();
 
 F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])",0,1);
 g1.setMarkerSize(0);
 g2.setMarkerSize(0);*/


//______________________________________________________________________________________________
//  __________________________________________ Openings __________________________________________
//______________________________________________________________________________________________

 


  


  


  


  // The root file contains a tree
  // this tree will contain the hit pads and the signal
  
  int evn;

  int IsRec;


  int evn_v;
  double z_v = 0;
  double p_v = 0;
  double pt_v = 0;
  double th_v = 0;
  double phi_v = 0;
  double px_v = 0;
  double py_v = 0;
  double pz_v = 0;

  
  int CellID = 0; 
  double Time;
  double totEdep;
  int eventnum = params.get_eventnum(); 
  
  //int init = 1; 
  //int testid = 0; 
//______________________________________________________________________________________________
//  __________________________________________ Readings __________________________________________
//______________________________________________________________________________________________

//--Creating the signal on pads with 1 ns steps.


    // Initializations
    R_adc.clear(); // Raw depositions for CellID, adc
    PadN.clear();
    PadNum.clear();
    flag_event=false; 
    //Pad->clear();
    //ADC->clear();  // not reliable for now as the fit fails often
    //Time_o->clear();  
    IsRec=0;

  double valtest[] = new double[TrigWindSize];


      for(Hit hit : rawHits){

    	 CellID = hit.get_cellID();
    	 Time = hit.get_Time();
    	 totEdep = hit.get_EdepTrue();


 // searches in PadN if CellID already exists

        if(PadN.contains(CellID)){ // this pad has already seen signal
        	valtest = R_adc.get(CellID);
        	for(int t=0;t<TrigWindSize;t+=StepSize){              	
            R_adc.get(CellID)[t] += EtoS(Time,t,totEdep);                        
          }
        }
        else{ // first signal on this pad
          for(int t=0;t<TrigWindSize;t+=StepSize){
        	valtest[t] = EtoS(Time,t,totEdep);                       
          }
          R_adc.put(CellID, valtest);
          PadNum.add(CellID);
        }
        
        PadN.add(CellID);

       } // c

  
//--Signal created on pads with StepSize ns steps


//--For each pad
// Read the signal on it
// Integrates it into BinSize long bins
// Keeps only 1 bin over 3
// Fits the results with the double Gaussian

eventnum++;
params.set_ADC(ADC);
params.set_Pad(Pad);
params.set_PadN(PadN);
params.set_PadNum(PadNum);
params.set_R_adc(R_adc);
params.set_Time_o(Time_o);
params.set_eventnum(eventnum);

//System.out.println(PadNum.size());

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

	
  //return Math.random().Gaus(tim,sigTelec);
	return noise.nextGaussian()*sigTelec + tim;
}

double drift_V(double tim){

  //return noise->Gaus(tim,sigTVdrift);
return noise.nextGaussian()*sigTVdrift + tim;
}







}