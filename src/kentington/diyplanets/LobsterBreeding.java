package kentington.diyplanets;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI.MessageClickAction;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI.MarketInteractionMode;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.ResourceDepositsCondition;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class LobsterBreeding extends BaseIndustry {

	static float LOBSTER_BREEDING_TIME=180f;
	
	@Override
	public void apply() {
		super.apply(true);
	}
	
	@Override
	public boolean isAvailableToBuild() {
		
		if (!super.isAvailableToBuild()) return false;
		
		boolean canAquaculture = market.getPlanetEntity() != null && market.hasCondition("water_surface");
		if(!canAquaculture) return false;
		
		for (MarketConditionAPI mc : market.getConditions()) {
			String commodity = ResourceDepositsCondition.COMMODITY.get(mc.getId());
			if (commodity != null && commodity.equals(Commodities.LOBSTER)) return false;
		}
		return true;
	}

	@Override
	public boolean showWhenUnavailable() {
		return false;
	}
	
	@Override
	protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();
		
		String pre = "Alpha-level AI core currently assigned. ";
		if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			pre = "Alpha-level AI core. ";
		}
		if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
			TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
			text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
					"Doubles program speed.", 0f, highlight,
					"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION);
			tooltip.addImageWithText(opad);
			return;
		}
		
		tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
				"Doubles program speed.", opad, highlight,
				"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION);
	}
	
	@Override
	protected void applyAlphaCoreSupplyAndDemandModifiers() {
		demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
	}
	
	@Override
	public float getBuildOrUpgradeProgress() {
		if (isDisrupted()) {
			return 0f;
		}
		if(building)
			return Math.min(1f, buildProgress / buildTime);
		return Math.min(1f, buildProgress / LOBSTER_BREEDING_TIME);
	}
	
	@Override
	public String getBuildOrUpgradeDaysText() {
		if (isDisrupted()) {
			int left = (int) getDisruptedDays();
			if (left < 1) left = 1;
			String days = "days";
			if (left == 1) days = "day";
			
			return "" + left + " " + days + "";
		}
		
		if(building)
		{
			int right = (int) (buildTime - buildProgress);
			if (right < 1) right = 1;
			String daze = "days";
			if (right == 1) daze = "day";
			
			return "Building: " + right + " " + daze + " left";
		}
		
		int left = (int) (LOBSTER_BREEDING_TIME - buildProgress);
		if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
		{
			left = (int) (LOBSTER_BREEDING_TIME - buildProgress)/2;
		}
		if (left < 1) left = 1;
		String days = "days";
		if (left == 1) days = "day";
		
		return left + " " + days;
	}
	
	@Override
	public String getBuildOrUpgradeProgressText() {
		if (isDisrupted()) {
			int left = (int) getDisruptedDays();
			if (left < 1) left = 1;
			String days = "days";
			if (left == 1) days = "day";
			
			return "Disrupted: " + left + " " + days + " left";
		}
		
		if(building)
		{
			int right = (int) (buildTime - buildProgress);
			if (right < 1) right = 1;
			String daze = "days";
			if (right == 1) daze = "day";
			
			return "Building: " + right + " " + daze + " left";
		}
		
		int left = (int) (LOBSTER_BREEDING_TIME - buildProgress);
		if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
		{
			left = (int) (LOBSTER_BREEDING_TIME - buildProgress)/2;
		}
		if (left < 1) left = 1;
		String days = "days";
		if (left == 1) days = "day";
		
		return "Breeding: " + left + " " + days + " left";
	}
	
	@Override
	public void finishBuildingOrUpgrading() {
		buildProgress = 0;
		buildTime = 1f;
		if (building) {
			buildingFinished();
			reapply();
			building=false;
		} else {
			market.addCondition("volturnian_lobster_pens");
			market.removeIndustry("lobsterbreeding", null, false);
			if (market.isPlayerOwned()) {
				MessageIntel intel = new MessageIntel(getCurrentName() + " at " + market.getName(), Misc.getBasePlayerColor());
				intel.addLine(BaseIntelPlugin.BULLET + "Program completed");
				intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
				intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
				Global.getSector().getCampaignUI().addMessage(intel, MessageClickAction.COLONY_INFO, market);
				Global.getSector().getFaction("sindrian_diktat").adjustRelationship(Global.getSector().getPlayerFaction().getId(), -50);
			}
		}
	}
	
	@Override
	public void advance(float amount) {
		boolean disrupted = isDisrupted();
		if (!disrupted && wasDisrupted) {
			disruptionFinished();
		}
		wasDisrupted = disrupted;
		
		if (!disrupted) {
			float days = Global.getSector().getClock().convertToDays(amount);
			if (DebugFlags.COLONY_DEBUG) {
				days *= 100f;
			}
			
			if(building)
			{
				buildProgress += days;
			}
			else
			{
				buildProgress += days;
				if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
				{
					buildProgress += days;
				}
			}
			
			if (building && buildProgress >= buildTime) {
				finishBuildingOrUpgrading();
			}
			else if(!building && buildProgress >= LOBSTER_BREEDING_TIME)
				finishBuildingOrUpgrading();
		}
		
	}
	
	@Override
	public boolean isBuilding() {
		return true;
	}
	

	
	@Override
	public void notifyBeingRemoved(MarketInteractionMode mode, boolean forUpgrade) {
		super.notifyBeingRemoved(mode, forUpgrade);
		
		if(aiCoreId != null && mode == null)
		{
			CargoAPI cargo = getCargoForInteractionMode(MarketInteractionMode.REMOTE);
			if (cargo != null) {
				cargo.addCommodity(aiCoreId, 1);
			}
		}
	}
}
