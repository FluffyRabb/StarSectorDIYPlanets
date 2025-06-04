package kentington.diyplanets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetGenDataSpec;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.PlanetSpec;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;

public class TerraformingUtilities {

	public static void EvaluatePlanetClass(MarketAPI planet)
	{
		if(planet.getPlanetEntity() == null || planet.getPlanetEntity().getSpec() == null || planet.getPlanetEntity().getSpec().getPlanetType() == null)
			return;

		String planetType = planet.getPlanetEntity().getSpec().getPlanetType();
		try
		{
			Global.getSettings().getSpec(PlanetGenDataSpec.class, planetType, false);
		} catch (Exception e)
		{
			return;
		}
		
		PlanetGenDataSpec genData=(PlanetGenDataSpec)Global.getSettings().getSpec(PlanetGenDataSpec.class, planetType, false);
		String planetCat = genData.getCategory();
		
		if(!planet.getPlanetEntity().isStar() && !planet.getPlanetEntity().isGasGiant() && !planet.hasCondition(Conditions.HABITABLE) && !planet.hasCondition(Conditions.VERY_COLD) && !planet.hasCondition(Conditions.VERY_HOT) && !planet.hasCondition(Conditions.NO_ATMOSPHERE) && !planet.hasCondition(Conditions.THIN_ATMOSPHERE) && !planet.hasCondition(Conditions.TOXIC_ATMOSPHERE) && !planet.hasCondition(Conditions.IRRADIATED) && !planet.hasCondition(Conditions.DARK) && !planetType.equals("Volcanic") && !planetType.equals("Cryovolcanic") &&  !planetType.equals("rocky_ice") && !planetType.equals("frozen") && !planetType.equals("Toxic") && !planetType.equals("Barren") && !planetType.equals("Barren-desert"))
		{
			planet.addCondition(Conditions.HABITABLE);
		}
		
		if((planetCat.equals("cat_irradiated") && !planet.hasCondition(Conditions.IRRADIATED)) || planetCat.equals("cat_lava") && !planet.hasCondition(Conditions.VERY_HOT))
		{
			if(planet.hasCondition(Conditions.ORE_RICH) || planet.hasCondition(Conditions.ORE_ULTRARICH) || planet.hasCondition(Conditions.RARE_ORE_RICH) || planet.hasCondition(Conditions.RARE_ORE_ULTRARICH))
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet, Planets.BARREN_CASTIRON);
						break;
					default:
						planetType=ChangePlanetClass(planet, Planets.ROCKY_METALLIC);
						break;
				}
			}
			else
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet, Planets.BARREN);
						break;
					case 1:
						planetType=ChangePlanetClass(planet, Planets.BARREN2);
						break;
					default:
						planetType=ChangePlanetClass(planet, Planets.BARREN3);
						break;
				}
			}
			planetCat="cat_barren";
		}
		if(planetCat.equals("cat_cryovolcanic") && !planet.hasCondition(Conditions.TECTONIC_ACTIVITY) && !planet.hasCondition(Conditions.EXTREME_TECTONIC_ACTIVITY))
		{
			if(planet.hasCondition(Conditions.VERY_COLD))
			{
				int which = Misc.random.nextInt(4);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet, Planets.FROZEN);
						break;
					case 1:
						planetType=ChangePlanetClass(planet, Planets.FROZEN1);
						break;
					case 2:
						planetType=ChangePlanetClass(planet, Planets.FROZEN2);
						break;
					default:
						planetType=ChangePlanetClass(planet, Planets.FROZEN3);
						break;
				}
				planetCat="cat_frozen";
			}
			else if(planet.hasCondition(Conditions.COLD))
			{
				planetType=ChangePlanetClass(planet, Planets.ROCKY_ICE);
				planetCat="cat_frozen";
			}
			else
			{
				planetType=ChangePlanetClass(planet, Planets.PLANET_WATER);
				planetCat="cat_hab3";
			}
		}
		if(planetCat.equals("cat_frozen") && !planet.hasCondition(Conditions.VERY_COLD) && !planet.hasCondition(Conditions.COLD))
		{
			planetType=ChangePlanetClass(planet, Planets.PLANET_WATER);
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_toxic") && !planet.hasCondition(Conditions.TOXIC_ATMOSPHERE))
		{
			int which = Misc.random.nextInt(3);
			switch(which)
			{
				case 0:
					planetType=ChangePlanetClass(planet, Planets.BARREN);
					break;
				case 1:
					planetType=ChangePlanetClass(planet, Planets.BARREN2);
					break;
				default:
					planetType=ChangePlanetClass(planet, Planets.BARREN3);
					break;
			}
			planetCat="cat_barren";
		}
		if(planetType.equals(Planets.ROCKY_ICE) && !planet.hasCondition(Conditions.COLD) && !planet.hasCondition(Conditions.VERY_COLD))
		{
			planetType=ChangePlanetClass(planet, Planets.PLANET_WATER);
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_barren") && !planet.hasCondition(Conditions.NO_ATMOSPHERE) && !planet.hasCondition(Conditions.THIN_ATMOSPHERE) && !planet.hasCondition(Conditions.TOXIC_ATMOSPHERE))
		{
			planetType=ChangePlanetClass(planet, Planets.BARREN_DESERT);
			planetCat="cat_hab1";
		}
		if(planetCat.equals("cat_hab1") && planet.hasCondition(Conditions.HABITABLE))
		{
			planetType=ChangePlanetClass(planet, Planets.DESERT);
			AddOrImproveFarmland(planet);
			planetCat="cat_hab2";
		}
		if(planetCat.equals("cat_hab2") && planet.hasCondition(Conditions.COLD) && planet.hasCondition("hydrated"))
		{
			planetType=ChangePlanetClass(planet, Planets.TUNDRA);
			AddOrImproveFarmland(planet);
			planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_hab2") && planet.hasCondition(Conditions.HOT) && planet.hasCondition("hydrated"))
		{
			planetType=ChangePlanetClass(planet,"jungle");
			AddOrImproveFarmland(planet);
			planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(((planetCat.equals("cat_hab2")&& planet.hasCondition("hydrated")) || (planetCat.equals("cat_hab3") && !planetType.equals("terran-eccentric") && !planetType.equals(Planets.PLANET_WATER))) && (planet.hasCondition(Conditions.DENSE_ATMOSPHERE) || planet.hasCondition(Conditions.EXTREME_WEATHER) || planet.hasCondition(Conditions.IRRADIATED) || planet.hasCondition(Conditions.POOR_LIGHT) || planet.hasCondition(Conditions.DARK)))
		{
			planetType=ChangePlanetClass(planet, Planets.PLANET_TERRAN_ECCENTRIC);
			AddOrImproveFarmland(planet);
			if(planet.hasCondition("hydrated"))
				planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(planet.hasCondition(Conditions.HABITABLE) && !planetType.equals(Planets.PLANET_TERRAN) && !planetType.equals(Planets.PLANET_WATER) && ((planetCat.equals("cat_hab2") && planet.hasCondition("hydrated")) || planetCat.equals("cat_hab3")) && !(planet.hasCondition(Conditions.DENSE_ATMOSPHERE) || planet.hasCondition(Conditions.EXTREME_WEATHER) || planet.hasCondition(Conditions.IRRADIATED) || planet.hasCondition(Conditions.POOR_LIGHT) || planet.hasCondition(Conditions.DARK) || planet.hasCondition(Conditions.HOT) || planet.hasCondition(Conditions.VERY_HOT) || planet.hasCondition(Conditions.COLD) || planet.hasCondition((Conditions.VERY_COLD))))
		{
			planetType=ChangePlanetClass(planet, Planets.PLANET_TERRAN);
			AddOrImproveFarmland(planet);
			if(planet.hasCondition("hydrated"))
				planet.removeCondition("hydrated");
			planetCat="cat_hab4";
		}
		
		if(planetType.equals(Planets.PLANET_WATER) && !planet.hasCondition(Conditions.WATER_SURFACE))
			planet.addCondition(Conditions.WATER_SURFACE);
	}
	
	public static String ChangePlanetClass(MarketAPI planet, String newPlanetType)
	{
		PlanetSpecAPI myspec = planet.getPlanetEntity().getSpec();
		
		for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
			if (spec.getPlanetType().equals(newPlanetType)) {
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
				((PlanetSpec)myspec).planetType=newPlanetType;
				((PlanetSpec)myspec).name=spec.getName();
				((PlanetSpec)myspec).descriptionId=((PlanetSpec)spec).descriptionId;
				break;
			}
		}
		
		planet.getPlanetEntity().applySpecChanges();
		return newPlanetType;
	}
	
	public static void AddOrImproveFarmland(MarketAPI planet)
	{
		if(planet.hasCondition(Conditions.FARMLAND_RICH))
		{
			planet.removeCondition(Conditions.FARMLAND_RICH);
			planet.addCondition(Conditions.FARMLAND_BOUNTIFUL);
			planet.getFirstCondition(Conditions.FARMLAND_BOUNTIFUL).setSurveyed(true);
		}
		else if(planet.hasCondition(Conditions.FARMLAND_ADEQUATE))
		{
			planet.removeCondition(Conditions.FARMLAND_ADEQUATE);
			planet.addCondition(Conditions.FARMLAND_RICH);
			planet.getFirstCondition(Conditions.FARMLAND_RICH).setSurveyed(true);
		}
		else if(planet.hasCondition(Conditions.FARMLAND_POOR))
		{
			planet.removeCondition(Conditions.FARMLAND_POOR);
			planet.addCondition(Conditions.FARMLAND_ADEQUATE);
			planet.getFirstCondition(Conditions.FARMLAND_ADEQUATE).setSurveyed(true);
		}
		else if(!planet.hasCondition(Conditions.FARMLAND_BOUNTIFUL))
		{
			if(planet.getPlanetEntity().getSpec().getPlanetType().equals(Planets.PLANET_TERRAN))
			{
				planet.addCondition(Conditions.FARMLAND_RICH);
				planet.getFirstCondition(Conditions.FARMLAND_RICH).setSurveyed(true);
			}
			else
			{
				planet.addCondition(Conditions.FARMLAND_POOR);
				planet.getFirstCondition(Conditions.FARMLAND_POOR).setSurveyed(true);
			}
		}
	}
}
