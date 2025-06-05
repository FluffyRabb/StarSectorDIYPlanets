package kentington.diyplanets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI.MessageClickAction;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class TerraformIndustryAddHazard extends TerraformIndustryNeedsArtifact {

	
	String hazardAlsoRemoved=null;
	String hazardAlsoRemoved2=null;
	String disqualifyCondition2=null;
	String oppositeIndustry=null;
	
	boolean allowHabitable=false;
	
	@Override
	public void apply() {
		super.apply(true);
	}
	
	@Override
	public boolean isAvailableToBuild() {
		if(market.getPlanetEntity() == null || market.getPlanetEntity().getSpec() == null || market.getPlanetEntity().getSpec().getPlanetType() == null)
			return false;
		if(disqualifyCondition!=null && market.hasCondition(disqualifyCondition))
			return false;
		if(disqualifyCondition2!=null && market.hasCondition(disqualifyCondition2))
			return false;
		if(!allowHabitable&&market.hasCondition(Conditions.HABITABLE))
			return false;
		if(market.hasIndustry(oppositeIndustry))
			return false;
		return !market.hasCondition(hazard);
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
			if(hazardAlsoRemoved!=null && market.hasCondition(hazardAlsoRemoved))
			{
				market.removeCondition(hazardAlsoRemoved);
			}
			if(hazardAlsoRemoved2!=null && market.hasCondition(hazardAlsoRemoved2))
			{
				market.removeCondition(hazardAlsoRemoved2);
			}
			market.addCondition(hazard);
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
		}
	}
}
