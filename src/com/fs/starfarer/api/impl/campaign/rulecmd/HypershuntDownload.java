package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc.Token;

public class HypershuntDownload extends BaseCommandPlugin
{

	@Override
	public boolean execute(String arg0, InteractionDialogAPI arg1, List<Token> arg2, Map<String, MemoryAPI> arg3) {
		Global.getSector().getPlayerFaction().addKnownShip("terraform_hypershunt", true);
		
		return true;
	}

}
