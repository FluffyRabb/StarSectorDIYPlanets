package kentington.diyplanets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.campaign.CampaignPlanet;

public class TerraformingCampaignPlugin extends BaseCampaignPlugin
{
	static SpecialItemData data = new SpecialItemData("gravity_oscillator", null);
	@Override
	public void updateEntityFacts(SectorEntityToken entity, MemoryAPI memory) {
		
		if (entity instanceof AsteroidAPI)
		{
			memory.set("$isAsteroid", true, 0);
			for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
			{
				if(ship.getHullSpec().getBaseHullId().equals("planetary_accretor"))
					memory.set("$hasGenesis", true, 0);
			}
			memory.set("$used_Accretor", false, 0);
		}
		if(entity instanceof CampaignPlanet && ((CampaignPlanet)entity).isGasGiant() && Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoItemType.SPECIAL, data)>0 && entity.getMarket()!=null && entity.getMarket().isPlanetConditionMarketOnly())
		{
			memory.set("$gasGiantToDwarf", true, 0);
		}
		if(entity instanceof CampaignPlanet && TerraformingModPlugin.ReEvaluateMode)
		{
			memory.set("$terraformIsPlanetForReEval", true, 0);
		}
		if(entity.isStar())
		{
			for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
			{
				if(ship.getHullSpec().getBaseHullId().equals("terraform_hypershunt"))
					memory.set("$hasHypershuntRig", true, 0);
			}
		}
	}
	
	@Override
	public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
		if (interactionTarget instanceof AsteroidAPI) {
			return new PluginPick<InteractionDialogPlugin>(new RuleBasedInteractionDialogPluginImpl(), PickPriority.MOD_GENERAL);
		}
		if(interactionTarget.isStar())
		{
			for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
			{
				if(ship.getHullSpec().getBaseHullId().equals("terraform_hypershunt"))
					return new PluginPick<InteractionDialogPlugin>(new RuleBasedInteractionDialogPluginImpl(), PickPriority.MOD_GENERAL);
			}
		}
		return null;
	}
}
