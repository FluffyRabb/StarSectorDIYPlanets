package kentington.diyplanets;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI.MarketInteractionMode;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class WaterLauncher extends BaseIndustry {
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
	
	@Override
	public boolean isAvailableToBuild() {
		if (!super.isAvailableToBuild()) return false;
		if(market.getPlanetEntity() == null || market.getPlanetEntity().getSpec() == null || market.getPlanetEntity().getSpec().getPlanetType() == null)
			return false;
		
		String planetType = market.getPlanetEntity().getSpec().getPlanetType();
		
		return planetType.equals(Planets.ICE_GIANT) || planetType.equals(Planets.FROZEN) || planetType.equals(Planets.FROZEN1) || planetType.equals(Planets.FROZEN2) || planetType.equals(Planets.FROZEN3) || planetType.equals(Planets.CRYOVOLCANIC) || planetType.equals(Planets.ROCKY_ICE) || planetType.equals(Planets.PLANET_WATER);
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
		return Math.min(1f, buildProgress / buildTime);
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
			int right = (int) (buildTime - buildProgress);
			if (right < 1) right = 1;
			String daze = "days";
			if (right == 1) daze = "day";
			
			return "Building: " + right + " " + daze + " left";
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
		
			int right = (int) (buildTime - buildProgress);
			if (right < 1) right = 1;
			String daze = "days";
			if (right == 1) daze = "day";
			
			return "Building: " + right + " " + daze + " left";
	}
	
	@Override
	public void finishBuildingOrUpgrading() {
		buildProgress = 0;
		buildTime = 1f;
		if (building) {
			buildingFinished();
			reapply();
			building=false;
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
			
			if (building && buildProgress >= buildTime) {
				finishBuildingOrUpgrading();
			}
		}
		
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