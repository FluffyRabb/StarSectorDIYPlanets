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
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin.ListInfoMode;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.impl.campaign.intel.BaseMissionIntel;

public class GenesisStationIntel extends BaseMissionIntel {
	
	protected SectorEntityToken cache;
	protected StarSystemAPI cacheSystem;
	protected GenesisBarEvent event;
	
	public GenesisStationIntel(SectorEntityToken cache, GenesisBarEvent event) {
		this.cache = cache;
		cacheSystem = cache.getStarSystem();
		this.event = event;
		Misc.makeImportant(cache, "genesisstation");
		
		Global.getSector().getIntelManager().addIntel(this, false, null);
		setImportant(true);
		setMissionState(MissionState.ACCEPTED);
		missionAccepted();
		Global.getSector().addScript(this);
		
		cache.getMemoryWithoutUpdate().set("$genesisstation_eventRef", this);
	}
	
	@Override
	public boolean callEvent(String ruleId, InteractionDialogAPI dialog,
							 List<Token> params, Map<String, MemoryAPI> memoryMap) {
		String action = params.get(0).getString(memoryMap);
		
		MemoryAPI memory = cache.getMemoryWithoutUpdate();
		
		if (action.equals("putValuesInMemory")) {
			memory.set("$genesisstation_marketOnOrAt", event.getShownAt().getOnOrAt(), 0);
			memory.set("$genesisstation_marketName", event.getShownAt().getName(), 0);
		} else {
			this.setMissionState(MissionState.COMPLETED);
			this.endMission();
			this.setImportant(false);
		}
		
		return true;
	}
	
	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		float opad = 10f;
		

		CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE);
		if (spec != null) {
			info.addImage(spec.getIconName(), width, 80, opad);
		}
		
		if (cacheSystem == null) {
			return; // hack to make old save work...
		}
		
		if (isEnding()) {
			info.addPara("You've acquired the schematics for the completed Genesis Project, and can produce the device using your own heavy industry.", opad);
		} else {
			info.addPara("You've taken aboard a research AI who wishes to be reunited with the rest of its project in the " +
					cacheSystem.getNameWithLowercaseType() + ".", opad);
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
		return "Genesis Project";
	}
	
	public String getName() {
		if (isEnded() || isEnding()) {
			return "Genesis Project - Complete";
		}
		return "Genesis Project";
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
	public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
		Color c = getTitleColor(mode);
		info.addPara(getName(), c, 0f);
		
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
