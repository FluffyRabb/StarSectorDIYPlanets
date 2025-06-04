package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.DataForEncounterSide;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.FleetMemberData;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Drops;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec.DropData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.FleetAdvanceScript;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.BaseFIDDelegate;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;

public class GenesisStationSalvageGenFromSeed extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		final SectorEntityToken entity = dialog.getInteractionTarget();
		final MemoryAPI memory = getEntityMemory(memoryMap);

		final CampaignFleetAPI defenders;
		defenders = FleetFactoryV3.createFleet(new FleetParamsV3(null, null,
				Factions.REMNANTS,
				1.2f,
				FleetTypes.PATROL_LARGE,
				300,
				0, 0, 0, 0, 0, 0));
		
		dialog.setInteractionTarget(defenders);
		
		final FIDConfig config = new FIDConfig();
		config.leaveAlwaysAvailable = true;
		config.showCommLinkOption = false;
		config.showEngageText = false;
		config.showFleetAttitude = false;
		config.showTransponderStatus = false;
		config.showWarningDialogWhenNotHostile = false;
		config.alwaysAttackVsAttack = true;
		config.impactsAllyReputation = true;
		config.impactsEnemyReputation = false;
		config.pullInAllies = false;
		config.pullInEnemies = false;
		config.pullInStations = false;
		config.lootCredits = false;
		
		config.firstTimeEngageOptionText = "Engage the AI defenses";
		config.afterFirstTimeEngageOptionText = "Re-engage the AI defenses";
		config.noSalvageLeaveOptionText = "Continue";
		
		config.dismissOnLeave = false;
		config.printXPToDialog = true;
		
		long seed = memory.getLong(MemFlags.SALVAGE_SEED);
		config.salvageRandom = Misc.getRandom(seed, 75);
		
		
		final FleetInteractionDialogPluginImpl plugin = new FleetInteractionDialogPluginImpl(config);
		
		final InteractionDialogPlugin originalPlugin = dialog.getPlugin();
		config.delegate = new BaseFIDDelegate() {
			@Override
			public void notifyLeave(InteractionDialogAPI dialog) {
				// nothing in there we care about keeping; clearing to reduce savefile size
				defenders.getMemoryWithoutUpdate().clear();
				// there's a "standing down" assignment given after a battle is finished that we don't care about
				defenders.clearAssignments();
				defenders.deflate();
				
				dialog.setPlugin(originalPlugin);
				dialog.setInteractionTarget(entity);
				
				//Global.getSector().getCampaignUI().clearMessages();
				
				if (plugin.getContext() instanceof FleetEncounterContext) {
					FleetEncounterContext context = (FleetEncounterContext) plugin.getContext();
					if (context.didPlayerWinLastEngagement()) {
						
						SDMParams p = new SDMParams();
						p.entity = entity;
						p.factionId = defenders.getFaction().getId();
						
						SalvageDefenderModificationPlugin plugin = Global.getSector().getGenericPlugins().pickPlugin(
												SalvageDefenderModificationPlugin.class, p);
						if (plugin != null) {
							plugin.reportDefeated(p, entity, defenders);
						}
						
						memory.unset("$hasDefenders");
						memory.unset("$defenderFleet");
						memory.set("$defenderFleetDefeated", true);
						entity.removeScriptsOfClass(FleetAdvanceScript.class);
						FireBest.fire(null, dialog, memoryMap, "BeatDefendersContinue");
						Global.getSector().getPlayerFaction().addKnownShip("planetary_accretor", true);
					} else {
						boolean persistDefenders = false;
						if (context.isEngagedInHostilities()) {
							persistDefenders |= !Misc.getSnapshotMembersLost(defenders).isEmpty();
							for (FleetMemberAPI member : defenders.getFleetData().getMembersListCopy()) {
								if (member.getStatus().needsRepairs()) {
									persistDefenders = true;
									break;
								}
							}
						}
						
						if (persistDefenders) {
							if (!entity.hasScriptOfClass(FleetAdvanceScript.class)) {
								defenders.setDoNotAdvanceAI(true);
								defenders.setContainingLocation(entity.getContainingLocation());
								// somewhere far off where it's not going to be in terrain or whatever
								defenders.setLocation(1000000, 1000000);
								entity.addScript(new FleetAdvanceScript(defenders));
							}
							memory.expire("$defenderFleet", 10); // defenders may have gotten damaged; persist them for a bit
						}
						dialog.dismiss();
					}
				} else {
					dialog.dismiss();
				}
			}
			@Override
			public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
				bcc.aiRetreatAllowed = false;
				bcc.objectivesAllowed = false;
				bcc.enemyDeployAll = true;
			}
			@Override
			public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
				DataForEncounterSide winner = context.getWinnerData();
				DataForEncounterSide loser = context.getLoserData();

				if (winner == null || loser == null) return;
			
				float playerContribMult = context.computePlayerContribFraction();
				
				List<DropData> dropRandom = new ArrayList<DropData>();
				List<DropData> dropValue = new ArrayList<DropData>();
				
				float valueMultFleet = Global.getSector().getPlayerFleet().getStats().getDynamic().getValue(Stats.BATTLE_SALVAGE_MULT_FLEET);
				float valueModShips = context.getSalvageValueModPlayerShips();
				
				for (FleetMemberData data : winner.getEnemyCasualties()) {
					if (config.salvageRandom.nextFloat() < playerContribMult) {
						DropData drop = new DropData();
						drop.chances = 1;
						drop.value = -1;
						switch (data.getMember().getHullSpec().getHullSize()) {
						case CAPITAL_SHIP:
							drop.group = Drops.AI_CORES3;
							drop.chances = 2;
							break;
						case CRUISER:
							drop.group = Drops.AI_CORES3;
							break;
						case DESTROYER:
							drop.group = Drops.AI_CORES2;
							break;
						case FIGHTER:
						case FRIGATE:
							drop.group = Drops.AI_CORES1;
							break;
						default:
							break;
						}
						if (drop.group != null) {
							dropRandom.add(drop);
						}
					}
				}
				
				float fuelMult = Global.getSector().getPlayerFleet().getStats().getDynamic().getValue(Stats.FUEL_SALVAGE_VALUE_MULT_FLEET);
				
				CargoAPI extra = SalvageEntity.generateSalvage(config.salvageRandom, valueMultFleet + valueModShips, 1f, 1f, fuelMult, dropValue, dropRandom);
				for (CargoStackAPI stack : extra.getStacksCopy()) {
					if (stack.isFuelStack()) {
						stack.setSize((int)(stack.getSize() * fuelMult));
					}
					salvage.addFromStack(stack);
				}
				
			}
			
		};
		
		
		dialog.setPlugin(plugin);
		plugin.init(dialog);
	
		return true;
	}

	
}