package org.jlab.service.rtpc;

import java.io.FileNotFoundException;


import java.util.ArrayList;
import java.util.List;

import org.jlab.clas.reco.ReconstructionEngine;
import org.jlab.coda.jevio.EvioException;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import org.jlab.io.hipo.HipoDataSync;
import org.jlab.rec.rtpc.banks.HitReader;
import org.jlab.rec.rtpc.banks.RecoBankWriter;
import org.jlab.rec.rtpc.banks.RecoBankWriter2;
import org.jlab.rec.rtpc.hit.Hit;
import org.jlab.rec.rtpc.hit.HitParameters;
import org.jlab.rec.rtpc.hit.HitReconstruction;
import org.jlab.rec.rtpc.hit.PadAve;
import org.jlab.rec.rtpc.hit.PadFit;
import org.jlab.rec.rtpc.hit.PadHit;
import org.jlab.rec.rtpc.hit.TrackFinder;




public class RTPCEngine extends ReconstructionEngine{

	public int test = 1;
	public HitParameters params = new HitParameters();
	
	public RTPCEngine() {
		super("RTPC","charlesg","3.0");
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean processDataEvent(DataEvent event) {
		HitReader hitRead = new HitReader();
		hitRead.fetch_RTPCHits(event);
		
		List<Hit> hits = new ArrayList<Hit>();
		//I) get the hits
		hits = hitRead.get_RTPCHits();
		
		//II) process the hits
		//1) exit if hit list is empty
		if(hits.size()==0 ) {
			return true;
		}
		if(event.hasBank("RTPC::adc") && test == 1)
		{
			test = 0;
			PadHit phit = new PadHit();
			phit.bonus_shaping(hits,params);
			
			TrackFinder TF = new TrackFinder();
			TF.FindTrack(params);
			//PadFit pfit = new PadFit();
			//pfit.Fitting(params);
			
			//PadAve pave = new PadAve();
			//pave.TimeAverage(params);
			
		}
		
		//HitReconstruction reco = new HitReconstruction();
	
		//reco.Reco(params);
		
		/*for(Hit h : hits) {
			System.out.println("Hit  "+h.get_Id()+" CellID "+h.get_cellID()+" ADC "+h.get_ADC()+" true Edep "+h.get_EdepTrue()+" Edep "+h.get_Edep()+" Time "+h.get_Time()+" "+
		" true X "+h.get_PosXTrue()+" X "+h.get_PosX()+" true Y "+h.get_PosYTrue()+" Y "+h.get_PosY()+" true Z "+h.get_PosZTrue()+" Z "+h.get_PosZ());
		}*/
		
		/*RecoBankWriter2 writer = new RecoBankWriter2();
		
		DataBank recoBank = writer.fillRTPCHitsBank(event, params);
		
		event.appendBanks(recoBank);*/
		
		return true;
	}
	
	public static void main(String[] args) throws FileNotFoundException, EvioException{
		//double starttime = System.nanoTime();
		String inputFile = "/Users/davidpayette/Desktop/newcoattest/clara/installation/plugins/clas12/take133.hipo";
		//String inputFile = args[0];
		String outputFile = "/Users/davidpayette/Documents/clas12-offline-software/tout_working.hipo";
		
		System.err.println(" \n[PROCESSING FILE] : " + inputFile);

		RTPCEngine en = new RTPCEngine();
		en.init();
		
		
		
		HipoDataSource reader = new HipoDataSource();	
		HipoDataSync writer = new HipoDataSync();
		reader.open(inputFile);
		writer.open(outputFile);
		while(reader.hasEvent() ){			
			DataEvent event = reader.getNextEvent();
			
			en.processDataEvent(event);
			writer.writeEvent(event);

		}
		writer.close();
		//System.out.println(System.nanoTime() - starttime);
	}
}
