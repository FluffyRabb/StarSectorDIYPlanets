package kentington.diyplanets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI.MessageClickAction;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin.InstallableItemDescriptionMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI.MarketInteractionMode;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import kentington.diyplanets.TerraformInstallableItemPlugin.TerraformEffect;

public class TerraformIndustryNeedsArtifact extends BaseIndustry{
	
	public SpecialItemData artifact = null;
	
	float TERRAFORM_TIME=360f;
	
	float hazardReduction=0f;
	float hazardAmount=0f;
	
	String hazard="";
	String tag="";
	String artifactRequired="";
	String disqualifyCondition=null;
	String stationType = null;
	String stationType2 = null;
	String stationName = null;
	String stationName2 = null;
	
	boolean requiresTerran = false;
	boolean reEvaluateMining = false;
	
	SectorEntityToken stationEntity = null;
	SectorEntityToken stationEntity2 = null;
	
	@Override
	public void apply() {
		super.apply(true);
	}
	
	public void setArtifact(SpecialItemData artifact) {
		if (artifact == null && this.artifact != null) {
			TerraformEffect effect = TerraformInstallableItemPlugin.ARTIFACT_EFFECTS.get(this.artifact.getId());
			if (effect != null) {
				effect.unapply(this);
			}
		}
		this.artifact = artifact;
	}
	
	public SpecialItemData getArtifact()
	{
		return artifact;
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
		demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha Core");
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
		
		if(buildProgress >= TERRAFORM_TIME/2 && this.artifact==null)
			return("Requires core to complete");
		
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
		
		if(buildProgress >= TERRAFORM_TIME/2 && this.artifact==null)
			return("Requires core to complete");
		
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
	public void notifyBeingRemoved(MarketInteractionMode mode, boolean forUpgrade) {
		super.notifyBeingRemoved(mode, forUpgrade);
		if (artifact != null && !forUpgrade && mode != null) {
			CargoAPI cargo = getCargoForInteractionMode(mode);
			if (cargo != null) {
				cargo.addSpecial(artifact, 1);
			}
		}
		if(aiCoreId != null && mode == null)
		{
			CargoAPI cargo = getCargoForInteractionMode(MarketInteractionMode.REMOTE);
			if (cargo != null) {
				cargo.addCommodity(aiCoreId, 1);
			}
		}
		if(mode != null)
			removeStation();
		market.getHazard().unmodifyFlat(tag);
	}

	@Override
	protected boolean addNonAICoreInstalledItems(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
		if (artifact == null) return false;

		float opad = 10f;
		
		SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(artifact.getId());
		
		TooltipMakerAPI text = tooltip.beginImageWithText(spec.getIconName(), 48);
		TerraformEffect effect = TerraformInstallableItemPlugin.ARTIFACT_EFFECTS.get(artifact.getId());
		effect.addItemDescription(text, artifact, InstallableItemDescriptionMode.INDUSTRY_TOOLTIP);
		tooltip.addImageWithText(opad);
		
		return true;
	}

	@Override
	public void initWithParams(List<String> params) {
		super.initWithParams(params);
		
		for (String str : params) {
			if (TerraformInstallableItemPlugin.ARTIFACT_EFFECTS.containsKey(str)) {
				setArtifact(new SpecialItemData(str, null));
				break;
			}
		}
	}

	@Override
	public List<SpecialItemData> getVisibleInstalledItems() {
		List<SpecialItemData> result = super.getVisibleInstalledItems();
		
		if (artifact != null) {
			result.add(artifact);
		}
		
		return result;
	}

	
	@Override
	public boolean isAvailableToBuild() {
		if (!super.isAvailableToBuild()) return false;
		if(market.getPlanetEntity() == null || market.getPlanetEntity().getSpec() == null || market.getPlanetEntity().getSpec().getPlanetType() == null)
			return false;
		if(disqualifyCondition!=null && market.hasCondition(disqualifyCondition))
			return false;
		if(requiresTerran && (!market.hasCondition(Conditions.HABITABLE) || market.hasCondition(Conditions.MILD_CLIMATE)))
			return false;
		return market.hasCondition(hazard);
	}
	
	@Override
	public void finishBuildingOrUpgrading() {
		buildProgress = 0;
		buildTime = 1f;
		if (building) {
			buildingFinished();
			reapply();
			building=false;
			ensureStationEntityIsSetOrCreated();
		} else {
			if(!hazard.equals(Conditions.HABITABLE))
				market.removeCondition(hazard);
			else
				market.addCondition(Conditions.MILD_CLIMATE);
			market.getHazard().unmodifyFlat(tag);
			artifact=null;
			market.removeIndustry(this.id, null, false);
			if (market.isPlayerOwned()) {
				MessageIntel intel = new MessageIntel(getCurrentName() + " at " + market.getName(), Misc.getBasePlayerColor());
				intel.addLine(BaseIntelPlugin.BULLET + "Program completed");
				intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
				intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
				Global.getSector().getCampaignUI().addMessage(intel, MessageClickAction.COLONY_INFO, market);
			}
			TerraformingUtilities.EvaluatePlanetClass(market);
			if(reEvaluateMining && market.hasIndustry(Industries.MINING))
			{
				((BaseIndustry)market.getIndustry(Industries.MINING)).getSupply(Commodities.RARE_ORE).getQuantity().unmodify();
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
			else if(buildProgress >= TERRAFORM_TIME/2 && this.artifact==null)
			{
				hazardReduction = hazardAmount * buildProgress/TERRAFORM_TIME * -1;
				
				market.getHazard().modifyFlat(tag,hazardReduction,this.getCurrentName());
			}
			else if(this.getMaxDeficit(Commodities.HEAVY_MACHINERY).two < 2 && this.getMaxDeficit(Commodities.ORGANICS).two < 2 && this.getMaxDeficit(Commodities.METALS).two < 2)
			{
				if(this.getMaxDeficit(Commodities.HEAVY_MACHINERY).two > 0)
					days *= 0.125f;
				if(this.getMaxDeficit(Commodities.ORGANICS).two > 0)
					days *= 0.125f;
				if(this.getMaxDeficit(Commodities.METALS).two > 0)
					days *= 0.125f;
				buildProgress += days;
				if (Commodities.ALPHA_CORE.equals(aiCoreId)) 
				{
					buildProgress += days;
				}
				
				hazardReduction = hazardAmount * buildProgress/TERRAFORM_TIME * -1;
				
				market.getHazard().modifyFlat(tag,hazardReduction,this.getCurrentName());
			}
			
			if (building && buildProgress >= buildTime) {
				finishBuildingOrUpgrading();
			}
			else if(!building && buildProgress >= TERRAFORM_TIME)
				finishBuildingOrUpgrading();
		}
		
	}
	
	@Override
	public boolean wantsToUseSpecialItem(SpecialItemData data) {
		return artifact == null && 
				data != null && data.getId().equals(artifactRequired);
	}

	@Override
	public List<InstallableIndustryItemPlugin> getInstallableItems() {
		ArrayList<InstallableIndustryItemPlugin> list = new ArrayList<InstallableIndustryItemPlugin>();
		list.add(new TerraformInstallableItemPlugin(this));
		return list;
	}
	
	@Override
	public boolean isBuilding() {
		return true;
	}
	
	void ensureStationEntityIsSetOrCreated() {
		
		if (stationType != null) {
			stationEntity = market.getContainingLocation().addCustomEntity(null, market.getName() + " " + stationName, stationType, market.getFactionId());
			SectorEntityToken primary = market.getPrimaryEntity();
			float orbitRadius = primary.getRadius() + 150f;
			stationEntity.setCircularOrbitWithSpin(primary, (float) Math.random() * 360f, orbitRadius, orbitRadius / 10f, 5f, 5f);
			market.getConnectedEntities().add(stationEntity);
			stationEntity.setMarket(market);
		}
		
		if (stationType2 != null) {
			stationEntity2 = market.getContainingLocation().addCustomEntity(null, market.getName() + " " + stationName2, stationType2, market.getFactionId());
			SectorEntityToken primary = market.getPrimaryEntity();
			float orbitRadius = primary.getRadius() + 150f;
			stationEntity2.setCircularOrbitWithSpin(primary, (float) Math.random() * 360f, orbitRadius, orbitRadius / 10f, 5f, 5f);
			market.getConnectedEntities().add(stationEntity2);
			stationEntity2.setMarket(market);
		}
	}
	
	void removeStation()
	{
		if(stationEntity != null)
		{
			stationEntity.getContainingLocation().removeEntity(stationEntity);
			market.getConnectedEntities().remove(stationEntity);
			stationEntity = null;
		}
		if(stationEntity2 != null)
		{
			stationEntity2.getContainingLocation().removeEntity(stationEntity2);
			market.getConnectedEntities().remove(stationEntity2);
			stationEntity2 = null;
		}
	}
	
	@Override
	public boolean isUpgrading() {
		return !building;
	}
}
