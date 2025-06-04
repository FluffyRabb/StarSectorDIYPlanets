package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickParams;
import com.fs.starfarer.api.campaign.GenericPluginManagerAPI.GenericPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.ShipRolePick;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent.SkillPickPreference;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.ShipRoles;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

public class TerraformSalvageGenFromSeed extends BaseCommandPlugin {

	public static class SDMParams {
		public SectorEntityToken entity;
		public String factionId;
		public SDMParams() {
		}
	}
	
	
	public static interface SalvageDefenderModificationPlugin extends GenericPlugin {
		float getStrength(SDMParams p, float strength, Random random, boolean withOverride);
		float getProbability(SDMParams p, float probability, Random random, boolean withOverride);
		float getQuality(SDMParams p, float quality, Random random, boolean withOverride);
		float getMaxSize(SDMParams p, float maxSize, Random random, boolean withOverride);
		
		void modifyFleet(SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride);
		void reportDefeated(SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet);
	}
	
	public static final String DEFEATED_DERELICT_STR = "$defeatedDerelictStr";
	public static final float DEFEATED_TO_ADDED_FACTOR = 0.2f;
	public static final float DEFEATED_TO_QUALITY_FACTOR = 0.005f;
	
	public static class SalvageDefenderModificationPluginImpl extends BaseGenericPlugin implements SalvageDefenderModificationPlugin {
		public float getStrength(SDMParams p, float strength, Random random, boolean withOverride) {
			if (withOverride) return strength;
			float bonus = Global.getSector().getMemoryWithoutUpdate().getFloat(DEFEATED_DERELICT_STR) * DEFEATED_TO_ADDED_FACTOR;
			
			float limit = 150f;
			
			if (bonus > limit) bonus = limit;
			return strength + (int) bonus;
		}
		public float getMaxSize(SDMParams p, float maxSize, Random random, boolean withOverride) {
			if (withOverride) return maxSize;
			float bonusSize = 1;
			
			return Math.max(maxSize, bonusSize);
		}
		public float getProbability(SDMParams p, float probability, Random random, boolean withOverride) {
			if (withOverride) return probability;
			return probability;
		}
		public void reportDefeated(SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet) {
			float total = Global.getSector().getMemoryWithoutUpdate().getFloat(DEFEATED_DERELICT_STR);
			for (FleetMemberAPI member : Misc.getSnapshotMembersLost(fleet)) {
				total += member.getFleetPointCost();
			}
			Global.getSector().getMemoryWithoutUpdate().set(DEFEATED_DERELICT_STR, total);
		}
		@Override
		public int getHandlingPriority(Object params) {
			if (!(params instanceof SDMParams)) return 0;
			SDMParams p = (SDMParams) params;
			
			if (Factions.DERELICT.equals(p.factionId)) {
				return 1;
			}
			
			return 0;
		}
		public float getQuality(SDMParams p, float quality, Random random, boolean withOverride) {
			if (withOverride) return quality;
			float bonus = Global.getSector().getMemoryWithoutUpdate().getFloat(DEFEATED_DERELICT_STR) * DEFEATED_TO_QUALITY_FACTOR;
			return quality + bonus;
			
		}
		@Override
		public void modifyFleet(SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride) {
			
		}
	}
	
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		SectorEntityToken entity = dialog.getInteractionTarget();
		
		MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
		if (memoryMap.containsKey(MemKeys.ENTITY)) {
			memory = memoryMap.get(MemKeys.ENTITY);
		}
		
		if (!memory.getBoolean("$defenderFleetDefeated")) {
			memory.set("$hasDefenders", true, 0);
			
			if (!memory.contains("$defenderFleet")) {
				
				FleetParamsV3 fParams = new FleetParamsV3(null, null,
								Factions.DERELICT,
								0f,
								FleetTypes.PATROL_SMALL,
								0,
								0, 0, 0, 0, 0, 0);
				
				fParams.withOfficers = false;
				fParams.maxShipSize = 1;
				
				CampaignFleetAPI defenders = FleetFactoryV3.createFleet(fParams);
				
					defenders.getInflater().setRemoveAfterInflating(false);
					
					defenders.setName("Automated Defenses");
					
					defenders.clearAbilities();
					
					defenders.getFleetData().sort();
					
					memory.set("$defenderFleet", defenders, 0);
					
					long seed = memory.getLong(MemFlags.SALVAGE_SEED);
					Random random = Misc.getRandom(seed, 0);
					
					defenders.getFleetData().clear();
					for (ShipRolePick pick : defenders.getFaction().pickShip(ShipRoles.COMBAT_CAPITAL, ShipPickParams.all(), null, random)) {
						defenders.getFleetData().addFleetMember(pick.variantId);
					}
					for (FleetMemberAPI member : defenders.getFleetData().getMembersListCopy()) {
						member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
					}
					
					PersonAPI person = OfficerManagerEvent.createOfficer(defenders.getFaction(), 20, SkillPickPreference.GENERIC, random);
					defenders.setCommander(person);
					defenders.getFlagship().setCaptain(person);
			}
			
			
			CampaignFleetAPI defenders = memory.getFleet("$defenderFleet");
			if (defenders != null) {
				boolean hasStation = false;
				boolean hasNonStation = false;
				for (FleetMemberAPI member : defenders.getFleetData().getMembersListCopy()) {
					if (member.isStation()) {
						hasStation = true;
					} else {
						hasNonStation = true;
					}
				}
				memory.set("$hasStation", hasStation, 0);
				memory.set("$hasNonStation", hasNonStation, 0);
				
				defenders.setLocation(entity.getLocation().x, entity.getLocation().y);
			}
		} else {
			memory.set("$hasDefenders", false, 0);
			memory.set("$hasStation", false, 0);
		}
		
	
		return true;
	}

	
}