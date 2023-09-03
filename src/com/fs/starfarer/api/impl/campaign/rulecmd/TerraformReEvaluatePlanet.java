package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

import kentington.diyplanets.TerraformingUtilities;

public class TerraformReEvaluatePlanet extends BaseCommandPlugin
{

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		SectorEntityToken entity = dialog.getInteractionTarget();
		TerraformingUtilities.EvaluatePlanetClass(entity.getMarket());
		
		return true;
	}

}
