package kentington.diyplanets;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;

public class TerraformShipIntel extends BaseMissionIntel {
	
	protected SectorEntityToken cache;
	protected StarSystemAPI cacheSystem;
	protected TerraformShipBarEvent event;
	
	public TerraformShipIntel(SectorEntityToken cache, TerraformShipBarEvent event) {
		this.cache = cache;
		cacheSystem = cache.getStarSystem();
		this.event = event;
		Misc.makeImportant(cache, "terraformship");
		
		setDuration(90f);
		Global.getSector().getIntelManager().addIntel(this, false, null);setImportant(true);
		setMissionState(MissionState.ACCEPTED);
		missionAccepted();
		Global.getSector().addScript(this);
		
		cache.getMemoryWithoutUpdate().set("$terraformship_eventRef", this);
	}
	
	@Override
	public boolean callEvent(String ruleId, InteractionDialogAPI dialog,
							 List<Token> params, Map<String, MemoryAPI> memoryMap) {
		String action = params.get(0).getString(memoryMap);
		
		MemoryAPI memory = cache.getMemoryWithoutUpdate();
		
		if (action.equals("putValuesInMemory")) {
			memory.set("$terraformship_marketOnOrAt", event.getShownAt().getOnOrAt(), 0);
			memory.set("$terraformship_marketName", event.getShownAt().getName(), 0);
		} else {
			this.setMissionState(MissionState.COMPLETED);
			this.endMission();
			this.setImportant(false);
		}
		
		return true;
	}
	
	@Override
	protected void notifyEnding() {
		float timeout = 120f + (float) Math.random() * 120f;
		BarEventManager.getInstance().setTimeout(TerraformBarEventCreator.class, timeout);
	}

	protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {
		Color tc = getBulletColorForMode(mode);
		
		bullet(info);
		addDays(info, "to complete", duration - elapsedDays, tc, 0f);
		unindent(info);
	}
	
	
	@Override
	public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
		Color c = getTitleColor(mode);
		info.addPara(getName(), c, 0f);
		addBulletPoints(info, mode);
		
	}
	
	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
		

		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE);
		if (spec != null) {
			info.addImage(spec.getIconName(), width, 80, opad);
		}
		
		addBulletPoints(info, ListInfoMode.IN_DESC);
		
		if (cacheSystem == null) {
			return; // hack to make old save work...
		}
		
		if (isEnding()) {
			info.addPara("You've found and looted the Eridani-Utopia terraforming rig in the " +
					cacheSystem.getNameWithLowercaseType() + ".", opad);
		} else {
			info.addPara("You've learned that there is a derelict Eridani-Utopia terraforming rig to be found in the " +
					cacheSystem.getNameWithLowercaseType() + ".", opad);
			
			info.addPara("You are not the only explorer who has been sold this rig's location, and if you do not make haste, others will surely salvage it first.", opad);
		}
		
	}
	
	@Override
	public String getIcon() {
		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE);
		return spec.getIconName();
	}
	
	@Override
	public Set<String> getIntelTags(SectorMapAPI map) {
		Set<String> tags = super.getIntelTags(map);
		tags.add(Tags.INTEL_MISSIONS);
		tags.add(Tags.INTEL_ACCEPTED);
		tags.add(Tags.INTEL_EXPLORATION);
		return tags;
	}
	
	public String getSortString() {
		return "Terraforming Rig";
	}
	
	public String getName() {
		if (isEnded() || isEnding()) {
			return "Terraforming Rig - Looted";
		}
		return "Terraforming Rig";
	}
	
	@Override
	public FactionAPI getFactionForUIColors() {
		return super.getFactionForUIColors();
	}

	public String getSmallDescriptionTitle() {
		return getName();
	}

	@Override
	public SectorEntityToken getMapLocation(SectorMapAPI map) {
		return cache;
	}
	
	@Override
	public boolean shouldRemoveIntel() {
		return super.shouldRemoveIntel();
	}

	@Override
	public String getCommMessageSound() {
		return "ui_discovered_entity";
	}
	
	@Override
	protected MissionResult createAbandonedResult(boolean withPenalty) {
		event.failed=true;
		cache.setExpired(true);
		cache.getContainingLocation().removeEntity(cache);
		return new MissionResult();
	}
	
	@Override
	protected MissionResult createTimeRanOutFailedResult() {
		return createAbandonedResult(true);
	}

	@Override
	public void advanceMission(float arg0) {
	}

	@Override
	public void endMission() {
		endAfterDelay();
	}

	@Override
	public void missionAccepted() {
	}
}
