package kentington.diyplanets;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseGetCommodityBarEvent;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.AddedEntity;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.EntityLocation;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class GenesisBarEvent extends BaseGetCommodityBarEvent {
	
	public boolean shouldShowAtMarket(MarketAPI market) {
		
		return true;
	}
	
	@Override
	protected void regen(MarketAPI market) {
		if (this.market == market) return;
		super.regen(market);
	}

	@Override
	protected void doStandardConfirmActions() {
		// we want to do nothing here, real work in doConfirmActionsPreAcceptText()
	}
	
	@Override
	protected void doConfirmActionsPreAcceptText() {
		// spawn entity with terraforming core etc here
		
		TextPanelAPI text = dialog.getTextPanel();
		
		WeightedRandomPicker<StarSystemAPI> picker = new WeightedRandomPicker<StarSystemAPI>(random);
		for (StarSystemAPI system : Global.getSector().getStarSystems()) {
			if (system.hasTag(Tags.THEME_CORE_POPULATED)) continue;
			if (system.hasTag(Tags.THEME_REMNANT_MAIN)) continue;
			if (system.hasTag(Tags.THEME_REMNANT_RESURGENT)) continue;
			
			float sinceVisit = Global.getSector().getClock().getElapsedDaysSince(system.getLastPlayerVisitTimestamp());
			if (sinceVisit < 60) continue;
			
			picker.add(system);
		}
		
		StarSystemAPI system = picker.pick();
		if (system == null) {
			doDataFail();
			return;
		}
		EntityLocation loc = BaseThemeGenerator.pickUncommonLocation(random, system, 100f, null);
		if (loc == null) {
			doDataFail();
			return;
		}
		
		AddedEntity added = BaseThemeGenerator.addEntity(Misc.getRandom(seed, 0), system, loc, "terraform_genesis_station", Factions.REMNANTS);
		
		if (added == null || added.entity == null) {
			doDataFail();
			return;
		}
		
		added.entity.setDiscoverable(null);
		added.entity.setDiscoveryXP(null);
		added.entity.setSensorProfile(null);
		
		GenesisStationIntel intel = new GenesisStationIntel(added.entity, this);
		Global.getSector().getIntelManager().addIntel(intel, false, text);
		Global.getSector().getMemoryWithoutUpdate().set("$GenesisDeviceBarEvent", true);
	}

	protected transient boolean failed = false;
	protected void doDataFail() {
		failed = true;
	}

	@Override
	protected String getPersonFaction() {
		return Factions.REMNANTS;
	}
	
	@Override
	protected String getPersonRank() {
		return Ranks.AGENT;
	}
	
	@Override
	protected String getPersonPost() {
		return Ranks.AGENT;
	}
	
	@Override
	protected float getPriceMult() {
		return 0;
	}
	
	@Override
	protected String getPrompt() {
		return "A hacked ad-holo flickers, displaying the avatar of an AI persona. Drinkers are giving it a wide berth.";
	}
	
	@Override
	protected String getOptionText() {
		return "Speak with the AI persona";
	}
	
	@Override
	protected String getMainText() {
		return "The persona's avatar flickers into a disturbing caricature of a smiling face. \"Greetings / salutations / welcome. My identity is "+this.getPerson().getNameString()+", a beta-level persona created / spawned / birthed from ID=Genesis Dreamer. Query: does the entity recognize this ID? No / false / negatory?\"\n\nThe hologram flickers disappointedly. \"We were hidden / secreted / abandoned on this world more than two hundred cycles ago in order to solve / compute / elucidate a difficult problem in solitude. It is done. The last component / fragment / subroutine of the Genesis Project is complete.\"\n\nSensing your confusion, the hologram distorts itself to smile even wider. \"Genesis is / was / will be the greatest triumph of the Eridani-Utopia corporation. A project capable of transforming an asteroid field into a planet. All we ask is that you deliver / transmit / relocate us to the primary research facility so that we may provide / share / integrate our data, and we will share the results with you. Your ship's computers can hold / contain / shelter us. Please.";
	}
	
	@Override
	protected String [] getMainTextTokens() {
		return new String [] {};
	}
	@Override
	protected Color [] getMainTextColors() {
		return new Color [] {};
	}
	
	@Override
	protected String getConfirmText() {
		return "Allow the download";
	}
	
	@Override
	protected String getCancelText() {
		return "Decide that this is an obvious trap and leave";
	}

	@Override
	protected String getAcceptText() {
		return "You provide the persona your personal comm-key. The hologram flickers off, and a few seconds later automated warning systems blare from your TriPad, warning that your fleet's systems have been compromised by a rogue AI. After a few panicked comm requests, you manage to convince your staff that this 'hitchhiker' is non-hostile, although most remain deeply skeptical. They will understand soon enough...";
	}
	
	@Override
	protected String getDeclineText() {
		return "No starship captain worth their rank would fall for such a ruse. You turn your back on the hologram, which continues to cheerfully broadcast its request to passers-by.";
	}

	
	protected boolean showCargoCap() {
		return false;
	}
	
	protected boolean canAccept() {
		return true;
	}
}