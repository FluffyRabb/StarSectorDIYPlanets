package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc.Token;

import kentington.diyplanets.ProvideMaterialsToHypershunt;

public class TerraformSetupHypershunt extends BaseCommandPlugin {

	@Override
	public boolean execute(String arg0, InteractionDialogAPI dialog, List<Token> arg2, Map<String, MemoryAPI> arg3) {
		SectorEntityToken star = dialog.getInteractionTarget();
		SectorEntityToken shunt = star.getContainingLocation().addCustomEntity(null, "Hypershunt Construction Site", "terraform_hypershunt_site", Global.getSector().getPlayerFaction().getId());
		shunt.setCircularOrbitPointingDown(star, 0, star.getRadius()+150f, (star.getRadius()+150f)/20f);
		
		ProvideMaterialsToHypershunt shuntQuest = new ProvideMaterialsToHypershunt();
		shuntQuest.hypershunt = shunt;
		shuntQuest.createArtificially();
		TextPanelAPI text = dialog.getTextPanel();
		Global.getSector().getIntelManager().addIntel(shuntQuest, false, text);
		
		for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
		{
			if(ship.getHullSpec().getBaseHullId().equals("terraform_hypershunt"))
			{
				Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(ship);
				break;
			}
		}
		return true;
	}

}
