package kentington.diyplanets;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ProvideMaterialsToHypershunt extends BaseMissionIntel implements EconomyTickListener {

	int monthsRemaining=12;
	String uid;
	public SectorEntityToken hypershunt;
	public Object currentStage=null;
	
	public static enum Stage
	{
		WAITING,
		DELIVERING,
		IGNITING,
		COMPLETED
	}
	
	public static boolean isPlayerProducing(String commodityId, int quantity) {
		for (MarketAPI market : Misc.getPlayerMarkets(true)) {
			CommodityOnMarketAPI com = market.getCommodityData(commodityId);
			if (com.getMaxSupply() >= quantity) return true;
		}
		return false;
	}

	@Override
	public void reportEconomyMonthEnd() {
		if(this.currentStage == Stage.WAITING)
		{
			if(isPlayerProducing(Commodities.METALS,11)&&isPlayerProducing(Commodities.RARE_METALS,9))
			{
				this.currentStage=Stage.DELIVERING;
				this.setNew(true);
				Global.getSector().getCampaignUI().addMessage(this);
			}
		}
		if(this.currentStage==Stage.DELIVERING)
		{
			if(isPlayerProducing(Commodities.METALS,11)&&isPlayerProducing(Commodities.RARE_METALS,9))
				monthsRemaining--;
			else
				this.currentStage=Stage.WAITING;
			if(monthsRemaining <= 0)
			{
				SectorEntityToken star = hypershunt.getOrbitFocus();
				SectorEntityToken shunt = star.getContainingLocation().addCustomEntity(null, "Coronal Hypershunt", "terraform_coronal_tap", Global.getSector().getPlayerFaction().getId());
				shunt.setCircularOrbitPointingDown(star, 0, star.getRadius()+150f, (star.getRadius()+150f)/20f);
				hypershunt.getContainingLocation().removeEntity((hypershunt));
				hypershunt=shunt;
				this.currentStage=Stage.IGNITING;
			}
			this.setNew(true);
			Global.getSector().getCampaignUI().addMessage(this);
		}
	}

	@Override
	public void reportEconomyTick(int arg0) {
	}

	public void createArtificially()
	{
		create(null,false);
	}
	
	protected boolean create(MarketAPI arg0, boolean arg1) {
		Global.getSector().getListenerManager().addListener(this);
		uid = Misc.genUID();
		this.currentStage=Stage.WAITING;
		this.setImportant(true);
		return true;
	}
	
	@Override
	protected void notifyEnding() {
		super.notifyEnding();
		Global.getSector().getListenerManager().removeListener(this);
	}
	
	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		if (currentStage == Stage.WAITING) {
			info.addPara("Produce at least 11 units of metals and 9 units of transplutonics at a colony under your control.", opad, h);
		} else if (currentStage == Stage.DELIVERING) {
			info.addPara("You are currently producing 11 units of metals and 9 units of transplutonics at a colony under your control, satisfying your hypershunt's construction needs.", opad, h);
			info.addPara("Months remaining: %s", opad, h, "" + monthsRemaining);
		} else if (currentStage == Stage.IGNITING){
			info.addPara("The hypershunt's structure has been completed; all that remains is to ignite its wormhole using 20,000 units of fuel.",opad,h);
		} else {
			info.addPara("Construction is complete and the hypershunt is fully active.", opad, h);
		}
	}
	
	@Override
	public String getName() {
		if(hypershunt == null)
			return "Hypershunt Construction";
		if (isEnded() || isEnding()) {
			return hypershunt.getOrbitFocus().getName() + " Hypershunt Construction - Complete";
		}
		return hypershunt.getOrbitFocus().getName() + " Hypershunt Construction";
	}
	
	@Override
	public String getIcon()
	{
		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE);
	    return spec.getIconName();
	}

	@Override
	public void advanceMission(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected MissionResult createAbandonedResult(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MissionResult createTimeRanOutFailedResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endMission() {
		endAfterDelay();
		
	}

	@Override
	public void missionAccepted() {
		// TODO Auto-generated method stub
		
	}
}
