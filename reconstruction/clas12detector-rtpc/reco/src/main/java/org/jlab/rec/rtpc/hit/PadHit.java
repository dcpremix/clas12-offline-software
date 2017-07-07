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

public void bonus_shaping(List<Hit> rawHits)
//int main()
{


//______________________________________________________________________________________________
//  __________________________________________ Variables _________________________________________
//______________________________________________________________________________________________
    

  
  int StepSize = 10; // step size of the signal before integration (arbitrary value)
  int BinSize = 40; // electronics integrates the signal over 40 ns
  int NBinKept = 3; // only 1 bin over 3 is kept by the daq
  int TrigWindSize = 10000; // Trigger window should be 10 micro
  int NTrigSampl = TrigWindSize/BinSize; // number of time samples

  Map<Integer, double[]> R_adc = new HashMap<Integer, double[]>(); // Raw depositions for CellID, ADC

  Vector PadN = new Vector();  // used to read only cell with signal, one entry for each hit         
  Vector PadNum = new Vector();// used to read only cell with signal, one entry for each cell
  Vector Pad = new Vector();
  Vector ADC = new Vector();
  Vector Time_o = new Vector();
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

 JFrame j1 = new JFrame();
 j1.setSize(800, 600);
 EmbeddedCanvas c1 = new EmbeddedCanvas();
 GraphErrors g1 = new GraphErrors();
 GraphErrors g2 = new GraphErrors();
 g1.setMarkerSize(0);
 g2.setMarkerSize(0);


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
/*
    evn_v = eve;
    z_v = vz;
    p_v = Math.sqrt(px*px+py*py+pz*pz);
    pt_v = Math.sqrt(px*px+py*py);
    th_v = Math.atan2(pz,pt_v);
    phi_v = Math.atan2(py,px);
    px_v = px;
    py_v = py;
    pz_v = pz;
*/
  double valtest[] = new double[TrigWindSize];


      for(Hit hit : rawHits){

    	 CellID = hit.get_cellID();
    	 //System.out.println(CellID);
    	 Time = hit.get_Time();
    	 //if(CellID == 10449) System.out.println(Time);
    	 totEdep = hit.get_EdepTrue();

 // searches in PadN if CellID already exists

        if(PadN.contains(CellID)){ // this pad has already seen signal
          for(int t=0;t<TrigWindSize;t+=StepSize){
            valtest = R_adc.get(CellID);
            valtest[t] += EtoS(Time,t,totEdep);
            R_adc.put(CellID, valtest);
            if(CellID == 10449) g1.addPoint(t, valtest[t],0,0);
          }
        }
        else{ // first signal on this pad
          for(int t=0;t<TrigWindSize;t+=StepSize){
        	valtest[t] = EtoS(Time,t,totEdep);
            R_adc.put(CellID, valtest);
            if(CellID == 10449) g1.addPoint(t, valtest[t],0,0);
          }
          PadNum.add(CellID);
        }
        
        PadN.add(CellID);

       } // c
      if(CellID == 10449)
      {
      c1.draw(g1);
      j1.add(c1);
      j1.setVisible(true);
      }
  
//--Signal created on pads with StepSize ns steps


//--For each pad
// Read the signal on it
// Integrates it into BinSize long bins
// Keeps only 1 bin over 3
// Fits the results with the double Gaussian

      inte=0;
      for(int p=0;p<PadNum.size();p++){
        inte_tot = 0;
        for(int t=0;t<TrigWindSize;t+=StepSize){
          if(t%BinSize==0 && t>0){ // integration over BinSize
            if(t%(BinSize*NBinKept)==0){ // one BinSize over NBinKept is read out, hence added to the histogram
              g2.setPoint(t/(BinSize*NBinKept),t,inte);
              inte_tot+=inte;
              if(max_inte<inte){max_inte=inte; max_t=t;}       
            }
            inte=0;
          }
          else inte+=0.5*(R_adc.get(PadNum.get(p))[t-1]+R_adc.get(PadNum.get(p))[t])*StepSize;
        }
        F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])",max_t-StepSize*4,max_t+StepSize*4);
        f1.setParameter(0,5*max_inte);
        f1.setParameter(1,max_t);
        f1.setParameter(2, 165);
        DataFitter.fit(f1, g2, "QER");
        
//   debug->cd();
//        g_pad_inte->Draw();
        //g2.Fit("mgaus","QER");
//        gPad->Update();
//        getchar();
        // Filling output tree
        //evn = eve;
        if(0<f1.getParameter(1) && f1.getParameter(1)<10000){ // the fit is not robust for now
          Pad.add(PadNum.get(p));
          ADC.add(inte_tot);  // use the signal integral for now
          Time_o.add(f1.getParameter(1));
          flag_event=true;
        }
      /*
        // Cleaning
        max_inte=0;
        max_t=0;
        if(eve!=tree->GetEntries()-1){
          for(int ii=g_pad_inte->GetN();ii>-1;ii--){
            g_pad_inte->RemovePoint(ii);
          }
        }
*/
      }
//--each pad has its daq signal associated to it


//______________________________________________________________________________________________
//  _________________________________________ Cleaning ___________________________________________
//______________________________________________________________________________________________
/*
      if(eve!=tree->GetEntries()-1){
        for(int ii=g_pad_inte->GetN();ii>-1;ii--){
          g_pad_inte->RemovePoint(ii);
        }
      }

      // Fills the output trees
      if(flag_event==true) IsRec=1;

    } 
    else{ // CellID->size()==0
      evn = eve;
      Pad->push_back(-999);
      ADC->push_back(0);  
      Time_o->push_back(-999);
      IsRec=0;
    }

    Rec->Fill();
    Gen->Fill();



  fout->Write();

  delete fout;

return 0;
*/
    
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