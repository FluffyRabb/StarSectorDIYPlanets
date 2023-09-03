package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

import kentington.diyplanets.ProvideMaterialsToHypershunt;

public class TerraformFinishShunt extends BaseCommandPlugin {

	@Override
	public boolean execute(String arg0, InteractionDialogAPI dialog, List<Token> arg2, Map<String, MemoryAPI> arg3) {
		SectorEntityToken shunt = dialog.getInteractionTarget();
		for(IntelInfoPlugin intel : Global.getSector().getIntelManager().getIntel())
		{
			if(intel instanceof ProvideMaterialsToHypershunt)
			{
				if(((ProvideMaterialsToHypershunt)intel).hypershunt==shunt)
				{
					((ProvideMaterialsToHypershunt)intel).currentStage=ProvideMaterialsToHypershunt.Stage.COMPLETED;
					intel.setImportant(false);
					((ProvideMaterialsToHypershunt) intel).endMission();
				}
			}
		}
		shunt.getMemory().set("$usable", true);
		return true;
	}

}
