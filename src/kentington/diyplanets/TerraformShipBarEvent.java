package kentington.diyplanets;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
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

public class TerraformShipBarEvent extends BaseGetCommodityBarEvent {
	
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
		
		AddedEntity added = BaseThemeGenerator.addEntity(Misc.getRandom(seed, 0), system, loc, "derelict_terraformer", Factions.NEUTRAL);
		
		if (added == null || added.entity == null) {
			doDataFail();
			return;
		}
		
		added.entity.setDiscoverable(null);
		added.entity.setDiscoveryXP(null);
		added.entity.setSensorProfile(null);
		
		added.entity.addTag(Tags.EXPIRES); // so it doesn't get targeted by "analyze entity" missions
		
		TerraformShipIntel intel = new TerraformShipIntel(added.entity, this);
		Global.getSector().getIntelManager().addIntel(intel, false, text);
	}

	protected transient boolean failed = false;
	protected void doDataFail() {
		failed = true;
	}

	@Override
	protected String getPersonFaction() {
		return Factions.INDEPENDENT;
	}
	
	@Override
	protected String getPersonRank() {
		return Ranks.CITIZEN;
	}
	
	@Override
	protected String getPersonPost() {
		return Ranks.CITIZEN;
	}
	
	@Override
	protected float getPriceMult() {
		return 0;
	}
	
	@Override
	protected String getPrompt() {
		return "A spacer with gruesome burns over most of " + getHisOrHer() + " face makes eye contact, seeming to size you up, and beckons you over to " + getHisOrHer() + " table.";
	}
	
	@Override
	protected String getOptionText() {
		return "Approach the scarred spacer";
	}
	
	@Override
	protected String getMainText() {
		return "The " + getManOrWoman() + " pours you a drink as you sit down. " + Misc.ucFirst(getHeOrShe()) + " spins a dramatic tale of exploration and battle which lasts one glass, then another, until finally "
				+ getHeOrShe() + " gets to the point: somewhere in the uncharted depths of the Sector, " + getHisOrHer() + " fleet stumbled across a largely intact Eridani-Utopia terraforming rig. The equipment aboard such a vessel would be priceless in the right hands, capable of permanently altering a planet's habitability.\n\n" +
			"So, you ask, why hasn't " + getHeOrShe() + " retired to " + getHisOrHer() + " own terran paradise?\n\n" +
			"\"Biggest damned drone I ever seen,\" " + getHeOrShe() + " growls. \"We were scavengers, not a bloody armada. The thing tore up half our ships 'fore we even knew we were under attack. Just a handful of us made it to the lifepods - spent cycles drifting in cryo 'til a more cautious scav team thawed us out.\"\n\n" +
			Misc.ucFirst(getHeOrShe())+" grimaces, recalling fallen friends, and continues. \"Now I've got nothing left but scars and stories. Maybe we can help each other out, aye? 20,000 credits buys you the rig's location, and gets me closer to a new ship.\" "+Misc.ucFirst(getHeOrShe())+" pauses, then leans in close. \"You're not the only one I've made this offer to. Some of the others might be on their way right now. Some of 'em might already be dead.\"\n\n";
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
		return "Transfer 20,000 credits to the spacer's account";
	}
	
	@Override
	protected String getCancelText() {
		return "Decline " + getHisOrHer() + " proposal and leave the table";
	}

	@Override
	protected String getAcceptText() {
		CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
		cargo.getCredits().subtract(20000);
		return "Your TriPad beeps to confirm the transaction. A moment later, its Sector map highlights a distant star system. \"There,\" says the scarred spacer. \"May your story have a better ending than mine.\"";
	}
	
	@Override
	protected String getDeclineText() {
		return "The " + getManOrWoman() + " nods tiredly. \"Maybe it's for the best. Don't want any more blood on these hands. But that terraforming rig is still out there, and it's gonna make someone rich.\"";
	}

	
	protected boolean showCargoCap() {
		return false;
	}
	
	protected boolean canAccept() {
		float credits = Global.getSector().getPlayerFleet().getCargo().getCredits().get();
		return credits >= 20000;
	}
}
