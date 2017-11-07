package org.jlab.rec.rtpc.banks;

//import java.util.List;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
//import org.jlab.rec.rtpc.hit.Hit;
import org.jlab.rec.rtpc.hit.HitParameters;

public class RecoBankWriter2 {

	/**
	 * 
	 * @param hitlist the list of  hits that are of the type Hit.
	 * @return hits bank
	 *
	 */
	public  DataBank fillRTPCHitsBank(DataEvent event, HitParameters params) {
		/*if(hitlist==null)
			return null;
		if(hitlist.size()==0)
			return null;*/
		int listsize = params.get_PadNum().size();
		

		DataBank bank = event.createBank("RTPC::rec", listsize);

		for(int i =0; i< listsize; i++) {
			double x_rec = params.get_XVec().get(i);
			double y_rec = params.get_YVec().get(i);
			double z_rec = params.get_ZVec().get(i);
			double time = params.get_time().get(i);
			//System.out.println(params.get_time().size());
			bank.setInt("id", i, 1);
			bank.setInt("cellID",i, params.get_PadNum().get(i));
			bank.setFloat("posX",i, (float) x_rec);
			bank.setFloat("posY",i, (float) y_rec);
			bank.setFloat("posZ",i, (float) z_rec);
			//bank.setDouble("Edep",i, hitlist.get(i).get_Edep());
			bank.setFloat("time", i, (float) time);

          
		}

		return bank;

	}

	
	
}