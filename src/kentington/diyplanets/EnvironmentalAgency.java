package kentington.diyplanets;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI.MessageClickAction;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI.MarketInteractionMode;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class EnvironmentalAgency extends BaseIndustry {

	static float TERRAFORM_TIME=360f;
	
	float hazardReduction=0f;
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
	
	@Override
	public boolean isAvailableToBuild() {
		if (!super.isAvailableToBuild()) return false;
		
		return market.hasCondition("pollution");
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
		return Math.min(1f, buildProgress / TERRAFORM_TIME);
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
		
		int left = (int) (TERRAFORM_TIME - buildProgress);
		if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
		{
			left = (int) (TERRAFORM_TIME - buildProgress)/2;
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
		
		int left = (int) (TERRAFORM_TIME - buildProgress);
		if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
		{
			left = (int) (TERRAFORM_TIME - buildProgress)/2;
		}
		if (left < 1) left = 1;
		String days = "days";
		if (left == 1) days = "day";
		
		return "Terraforming: " + left + " " + days + " left";
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
			market.removeCondition("pollution");
			market.getHazard().unmodifyFlat("EnvironmentalAgency");
			market.removeIndustry("environmentalagency", null, false);
			if (market.isPlayerOwned()) {
				MessageIntel intel = new MessageIntel(getCurrentName() + " at " + market.getName(), Misc.getBasePlayerColor());
				intel.addLine(BaseIntelPlugin.BULLET + "Program completed");
				intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
				intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
				Global.getSector().getCampaignUI().addMessage(intel, MessageClickAction.COLONY_INFO, market);
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
			else if(this.getMaxDeficit(Commodities.HEAVY_MACHINERY).two < 2)
			{
				if(this.getMaxDeficit(Commodities.HEAVY_MACHINERY).two > 0)
					days *= 0.125f;
				buildProgress += days;
				if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
				{
					buildProgress += days;
				}
				
				hazardReduction = -0.25f * buildProgress/TERRAFORM_TIME;
				
				market.getHazard().modifyFlat("EnvironmentalAgency",hazardReduction,"Environmental Agency");
			}
			
			if (building && buildProgress >= buildTime) {
				finishBuildingOrUpgrading();
			}
			else if(!building && buildProgress >= TERRAFORM_TIME)
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
		
		market.getHazard().unmodifyFlat("EnvironmentalAgency");
		if(aiCoreId != null && mode == null)
		{
			CargoAPI cargo = getCargoForInteractionMode(MarketInteractionMode.REMOTE);
			if (cargo != null) {
				cargo.addCommodity(aiCoreId, 1);
			}
		}
	}
	
	@Override
	public boolean isUpgrading() {
		return !building;
	}
}