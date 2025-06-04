package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI.SurveyLevel;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.campaign.CampaignPlanet;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

import kentington.diyplanets.TerraformingUtilities;

public class TerraformGasGiantToStar extends BaseCommandPlugin
{

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		SectorEntityToken entity = dialog.getInteractionTarget();
		
		TerraformingUtilities.ChangePlanetClass(entity.getMarket(), StarTypes.BROWN_DWARF);
		((CampaignPlanet)entity).addTag(Tags.STAR);
		((CampaignPlanet)entity).getMarket().setSurveyLevel(SurveyLevel.FULL);
		entity.getContainingLocation().addCorona(entity, 450, 1, 0, 1);
		ArrayList<String> conditions = new ArrayList<String>();
		for(MarketConditionAPI cond : entity.getMarket().getConditions())
		{
			conditions.add(cond.getId());
		}
		for(String cond : conditions)
		{
			entity.getMarket().removeCondition(cond);
		}
		
		for(PlanetAPI planet : entity.getContainingLocation().getPlanets())
		{
			if(planet.isStar() || planet.hasTag(Tags.STAR))
				continue;
			MarketAPI market = planet.getMarket();
			if(market!=null)
			{
				float factionRepGain=0;
				if(market.hasCondition(Conditions.VERY_COLD))
				{
					market.removeCondition(Conditions.VERY_COLD);
					market.addCondition(Conditions.COLD);
					factionRepGain=0.1f;
				}
				else if(market.hasCondition(Conditions.COLD))
				{
					market.removeCondition(Conditions.COLD);
					factionRepGain=0.05f;
				}
				else if(market.hasCondition(Conditions.HOT))
				{
					market.removeCondition(Conditions.HOT);
					market.addCondition(Conditions.VERY_HOT);
					factionRepGain=-0.1f;
				}
				else if(!market.hasCondition(Conditions.VERY_HOT))
				{
					market.addCondition(Conditions.HOT);
					factionRepGain=-0.05f;
				}
				if(market.hasCondition(Conditions.DARK))
				{
					market.removeCondition(Conditions.DARK);
					market.addCondition(Conditions.POOR_LIGHT);
				}
				else if(market.hasCondition(Conditions.POOR_LIGHT))
				{
					market.removeCondition(Conditions.POOR_LIGHT);
				}
				TerraformingUtilities.EvaluatePlanetClass(market);
				if(market.getFaction()!=null && !market.getFaction().isNeutralFaction() && !market.getFaction().isPlayerFaction())
				{
					market.getFaction().adjustRelationship("player", factionRepGain);
					CoreReputationPlugin.addAdjustmentMessage(factionRepGain, market.getFaction(), null, null, null, null, Misc.getHighlightColor(), true, 0f, "Heated planet");
				}
			}
		}
		
		Global.getSector().getPlayerFleet().getCargo().removeItems(CargoItemType.SPECIAL, new SpecialItemData("gravity_oscillator",null), 1);
		
		return true;
	}

}
