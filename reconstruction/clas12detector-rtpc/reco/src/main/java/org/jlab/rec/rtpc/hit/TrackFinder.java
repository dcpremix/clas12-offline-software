package org.jlab.rec.rtpc.hit;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import org.jlab.groot.data.*;
import org.jlab.groot.fitter.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.math.F1D;
import org.jlab.groot.ui.TCanvas;

public class TrackFinder {
	
	public void FindTrack(HitParameters params){
		boolean draw = true;
		HashMap<Integer, double[]> ADCMap = params.get_R_adc();
		//HashMap<Integer, HashMap<Integer, int[]>> TIDMap = new HashMap<Integer, HashMap<Integer, int[]>>();
		HashMap<Integer, HashMap<Integer,Vector<Integer>>> TIDMap = new HashMap<Integer, HashMap<Integer,Vector<Integer>>>();
		//HashMap<Integer, double[]> PadThresh = new HashMap<Integer, double[]>();
		Vector<Integer> PadNum = params.get_PadNum();
		Vector<Integer> TIDVec = new Vector<>();
		TIDVec.add(1);
		int Pad = 0;
		int TrigWindSize = params.get_TrigWindSize();
		int StepSize = params.get_StepSize();
		double thresh = 0.001;
		double ADC = 0;
		int maxconcpads = 0;
		int concpads = 0;
		int maxconctime = 0;
		double checkpadphi = 0;
		double checkpadphiprev = 0;
		double checkpadz = 0;
		double checkpadzprev = 0;
		double PadPhi = 0;
		double PadZ = 0;
		int TID = 0;
		boolean breakTIDloop = false;
		int padindexmax = 0;
		double a = 0;
		double b = 0;
		int adjthresh = 2;
		int event = params.get_eventnum();
		
		//g.setTitleX("Phi");
		//g.setTitleY("Z");
		//g.setMarkerSize(5);
		
		EmbeddedCanvas c = new EmbeddedCanvas();
		JFrame j = new JFrame();
		j.setSize(800,600);
		int test = 1; 
		try {
			FileWriter write2 = new FileWriter("/Users/davidpayette/Documents/FileOutput/Output" + event + ".txt",true);
		

		//loop over all times
		for(int t = 0; t < TrigWindSize; t += StepSize)
		{
			concpads = 0;
			//loop over all pads for each time slice
			for(int p = 0; p < PadNum.size(); p++)
			{
				Pad = PadNum.get(p);
				//System.out.println(Pad + " " + t);
				ADC = ADCMap.get(Pad)[t];
				
				//only pads which have an ADC value above threshold will be assigned a TID
				if(ADC > thresh)
				{					
					//returns the row and column of the current Pad
					PadPhi = PadPhi(Pad);
					PadZ = PadZ(Pad);
					//g.addPoint(PadPhi, PadZ, 0, 0);
					
					//loop through all TID's in a vector which will grow to include all future TID's
					for(int i = 0; i < TIDVec.size(); i++)
					{
						breakTIDloop = false;
						TID = TIDVec.get(i);
						//System.out.println(TID);
						
						//if TID is already in the map
						if(TIDMap.containsKey(TID))
						{
							if(t>0)
							{
								padindexmax = Math.max(TIDMap.get(TID).get(t).size(),TIDMap.get(TID).get(t-StepSize).size());
							}
							else
							{
								padindexmax = TIDMap.get(TID).get(t).size();
							}
							//loop through all pads in TIDMap and compare there row and column to current Pad
							for(int padindex = 0; padindex < padindexmax; padindex++)
							{

								//Check previous time slice for adjacency
								if(t>0)
								{
									if(padindex < TIDMap.get(TID).get(t-StepSize).size())
									{
										checkpadphiprev = PadPhi(TIDMap.get(TID).get(t-StepSize).get(padindex));
										checkpadzprev = 	PadZ(TIDMap.get(TID).get(t-StepSize).get(padindex));
										//System.out.println("Previous time slice " + PadPhi + " " + PadZ + " " + " " + checkpadphiprev + " " + checkpadzprev);
										if((Math.abs(checkpadphiprev-PadPhi) <= adjthresh && Math.abs(checkpadzprev - PadZ) <= adjthresh))
										{
											//System.out.println("Found 3");
											TIDMap.get(TID).get(t).add(Pad);
											breakTIDloop = true;
											break;
										}
									}
								}
								if(padindex < TIDMap.get(TID).get(t).size())
								{
									checkpadphi = PadPhi(TIDMap.get(TID).get(t).get(padindex));								
									checkpadz = 	PadZ(TIDMap.get(TID).get(t).get(padindex));
									//System.out.println("Current time slice " + PadPhi + " " + PadZ + " " + " " + checkpadphi + " " + checkpadz);
									//Check current time slice for adjacency
									if((Math.abs(checkpadphi-PadPhi) <= adjthresh) && (Math.abs(checkpadz - PadZ) <= adjthresh))
									{
										//System.out.println("Found 2");
										TIDMap.get(TID).get(t).add(Pad);
										breakTIDloop = true;
										break;
									}
								}
							}						
						}
						//TID not already in map
						else
						{
							//System.out.println("nope " + TID + " " + t + " " + Pad);
							TIDMap.put(TID, new HashMap<Integer, Vector<Integer>>());
							for(int time = 0; time < TrigWindSize; time += StepSize)
							{
								TIDMap.get(TID).put(time, new Vector<>());//add TID to map
							}
							TIDMap.get(TID).get(t).add(Pad);
							TIDVec.add(TID+1);
							break;
						}
						if(breakTIDloop){break;}
					}

					concpads++;
					if(concpads > maxconcpads) 
					{ 
						maxconcpads = concpads;
						maxconctime = t;					
					}
				}
				
			}	
			
		}
		HashMap<Integer,GraphErrors> graphmap = new HashMap<Integer,GraphErrors>();
		for(int testTID = 1; testTID <= TIDMap.size(); testTID++)
		{
			for(int a1 = 0; a1 < TrigWindSize; a1+=StepSize)
			{
				for(int a2 = 0; a2 < TIDMap.get(testTID).get(a1).size(); a2++)
				{	
					//g[testTID].setMarkerColor(testTID);
					a = PadPhi(TIDMap.get(testTID).get(a1).get(a2));
					b = PadZ(TIDMap.get(testTID).get(a1).get(a2));
					//System.out.println(a + " " + b);
					if(!graphmap.containsKey(testTID))
					{
						graphmap.put(testTID, new GraphErrors());
					}
					graphmap.get(testTID).addPoint(a, b, 0, 0);
					write2.write(testTID + "\t" + a1 + "\t" + PadPhi(TIDMap.get(testTID).get(a1).get(a2)) + "\t" + PadZ(TIDMap.get(testTID).get(a1).get(a2)) + "\n");
				}
			}
		}
		
		System.out.println(maxconcpads + " " + maxconctime + " " + TIDVec.size());
		if(draw == true)
		{
			for(int i = 1; i <= TIDMap.size(); i++)
			{
				graphmap.get(i).setMarkerColor(i);
				graphmap.get(i).setMarkerSize(3);
				c.draw(graphmap.get(i),"same");
			}
				j.add(c);
				j.setVisible(true);
		}
		write2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double PadPhi(int cellID) {
		
		double PAD_W = 2.79; // in mm
		double PAD_S = 80.0; //in mm
        double PAD_L = 4.0; // in mm
	    double RTPC_L=400.0; // in mm
	    
	    double phi_pad = 0;
		    
	    double Num_of_Rows = (2.0*(Math.PI)*PAD_S)/PAD_W;
        double Num_of_Cols = RTPC_L/PAD_L;
	    double TotChan = Num_of_Rows*Num_of_Cols;
		    
	    double PI=Math.PI;
		    
	    

	    double phi_per_pad = PAD_W/PAD_S; // in rad
		
		double chan = (double)cellID;
		double col = chan%Num_of_Cols;
		double row=(chan-col)/Num_of_Cols;
        
        
          //double z_shift = 0.;
   
        phi_pad=(row*phi_per_pad)+(phi_per_pad/2.0);
        if(phi_pad>= 2.0*PI) {
        	phi_pad -= 2.0*PI;
        }
        if(phi_pad<0) 
        	{
        	phi_pad += 2.0*PI;
        	}
   
       return row;
		
	}
	
	private double PadZ(int cellID)
	{
	
		double RTPC_L=400.0; // in mm
		double PAD_L = 4.0; // in mm
		double Num_of_Cols = RTPC_L/PAD_L;
		double z0 = -(RTPC_L/2.0); // front of RTPC in mm at the center of the pad
		double chan = (double)cellID;
		double col = chan%Num_of_Cols;
        double row=(chan-col)/Num_of_Cols;
        double z_shift = row%4;
        double z_pad = 0;
        
        
        z_pad=z0+(col*PAD_L)+(PAD_L/2.0)+z_shift;
        return col;
	}

}
