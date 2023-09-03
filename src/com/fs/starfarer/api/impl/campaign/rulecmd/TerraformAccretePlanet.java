package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI.SurveyLevel;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.campaign.CampaignEngine;
import com.fs.starfarer.campaign.CampaignEntity;
import com.fs.starfarer.campaign.CampaignPlanet;
import com.fs.starfarer.campaign.RingBand;
import com.fs.starfarer.campaign.StarSystem;
import com.fs.starfarer.campaign.econ.Market;
import com.fs.starfarer.loading.specs.PlanetSpec;

public class TerraformAccretePlanet extends BaseCommandPlugin
{

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		
		SectorEntityToken entity = dialog.getInteractionTarget();
		
		LocationAPI loc=entity.getContainingLocation();
		ArrayList<SectorEntityToken> roidsToRemove = new ArrayList<SectorEntityToken>();
		boolean wasBelt=true;
		for(SectorEntityToken roid : loc.getAsteroids())
		{
			if(roid.getOrbitFocus()==entity.getOrbitFocus())
				roidsToRemove.add(roid);
		}
		for(CampaignTerrainAPI terr : loc.getTerrainCopy())
		{
			if((terr.getClass()==AsteroidFieldTerrainPlugin.class && terr.getOrbitFocus() == entity.getOrbitFocus() && terr.getCircularOrbitRadius()/entity.getCircularOrbitRadius() < 1.1f && terr.getCircularOrbitRadius()/entity.getCircularOrbitRadius() > 0.9f))
			{
				loc.removeEntity(terr);
				wasBelt=false;
			}
			else if(terr.getType().equals("asteroid_belt") && terr.getLocation().x==entity.getOrbitFocus().getLocation().x)
			{
				loc.removeEntity(terr);
			}		
		}
		if(wasBelt)
		{
			for(SectorEntityToken token : loc.getAllEntities())
			{
				if(token instanceof RingBand && token.getLocation().x==entity.getOrbitFocus().getLocation().x)
				{
					roidsToRemove.add(token);
				}
			}
		}
		for(SectorEntityToken roid : roidsToRemove)
		{
			loc.removeEntity(roid);
		}
		
		CampaignPlanet newPlanet=new CampaignPlanet((String)null, Global.getSector().getPlayerPerson().getName().getFullName()+"'s Creation", "barren", 100f, 0f, 0f, (CampaignEntity) ((StarSystem)loc).getStar());
		newPlanet.setOrbit(entity.getOrbit());
		PlanetSpecAPI myspec = newPlanet.getSpec();
		
		for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
			if (spec.getPlanetType().equals("barren")) {
				myspec.setAtmosphereColor(spec.getAtmosphereColor());
				myspec.setAtmosphereThickness(spec.getAtmosphereThickness());
				myspec.setAtmosphereThicknessMin(spec.getAtmosphereThicknessMin());
				myspec.setCloudColor(spec.getCloudColor());
				myspec.setCloudRotation(spec.getCloudRotation());
				myspec.setCloudTexture(spec.getCloudTexture());
				myspec.setGlowColor(spec.getGlowColor());
				myspec.setGlowTexture(spec.getGlowTexture());
				myspec.setIconColor(spec.getIconColor());
				myspec.setPlanetColor(spec.getPlanetColor());
				myspec.setStarscapeIcon(spec.getStarscapeIcon());
				myspec.setTexture(spec.getTexture());
				myspec.setUseReverseLightForGlow(spec.isUseReverseLightForGlow());
				((PlanetSpec)myspec).planetType="barren";
				((PlanetSpec)myspec).name=spec.getName();
				((PlanetSpec)myspec).descriptionId=((PlanetSpec)spec).descriptionId;
				break;
			}
		}
		
		newPlanet.applySpecChanges();
		newPlanet.getGraphics();
		Market newMarket=(Market) CampaignEngine.getInstance().createMarket(Global.getSector().genUID(), Global.getSector().getPlayerPerson().getName().getFullName()+"'s Creation", 0);
		newMarket.setSurveyLevel(SurveyLevel.NONE);
		newMarket.setFactionId("neutral");
		newMarket.setPlanetConditionMarketOnly(true);
		newPlanet.setMarket(newMarket);
		newMarket.setPrimaryEntity(newPlanet);
		loc.addEntity(newPlanet);
		newPlanet.setContainingLocation(loc);
		
		PlanetSpecAPI spec=((StarSystem)loc).getStar().getSpec();
		if(spec.isBlackHole()||spec.isNebulaCenter()||spec.isPulsar())
		{
			newMarket.addCondition("cold");
			newMarket.addCondition("dark");
		}
		else if(spec.getName().toLowerCase().contains("giant"))
		{
			newMarket.addCondition("hot");
		}
		newMarket.addCondition("no_atmosphere");
		newMarket.addCondition("ore_ultrarich");
		newMarket.addCondition("rare_ore_ultrarich");
		float num = (float)Math.random();
		if(num>0.75f)
			newMarket.addCondition("volatiles_plentiful");
		else if(num>0.5f)
			newMarket.addCondition("volatiles_abundant");
		else if(num>0.25f)
			newMarket.addCondition("volatiles_diffuse");
		else
			newMarket.addCondition("volatiles_trace");
		num=(float)Math.random();
		if(num>0.75f)
			newMarket.addCondition("organics_plentiful");
		else if(num>0.5f)
			newMarket.addCondition("organics_abundant");
		else if(num>0.25f)
			newMarket.addCondition("organics_common");
		else
			newMarket.addCondition("organics_trace");
		
		loc.removeEntity(entity);
		
		for(FleetMemberAPI ship : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy())
		{
			if(ship.getHullSpec().getBaseHullId().equals("planetary_accretor"))
			{
				Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(ship);
				break;
			}
		}
		
		return true;
	}

}
