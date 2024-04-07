package kentington.diyplanets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.PlanetSpec;

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
		
		if(!planet.getPlanetEntity().isStar() && !planet.getPlanetEntity().isGasGiant() && !planet.hasCondition("habitable") && !planet.hasCondition("very_cold") && !planet.hasCondition("very_hot") && !planet.hasCondition("no_atmosphere") && !planet.hasCondition("thin_atmosphere") && !planet.hasCondition("toxic_atmosphere") && !planet.hasCondition("irradiated") && !planet.hasCondition("dark") && !planetType.equals("Volcanic") && !planetType.equals("Cryovolcanic") &&  !planetType.equals("rocky_ice") && !planetType.equals("frozen") && !planetType.equals("Toxic") && !planetType.equals("Barren") && !planetType.equals("Barren-desert"))
		{
			planet.addCondition("habitable");
		}
		
		if(planetCat.equals("cat_irradiated") && !planet.hasCondition("irradiated"))
		{
			if(planet.hasCondition("ore_rich") || planet.hasCondition("ore_ultrarich") || planet.hasCondition("rare_ore_rich") || planet.hasCondition("rare_ore_ultrarich"))
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet,"barren_castiron");
						break;
					default:
						planetType=ChangePlanetClass(planet,"rocky_metallic");
						break;
				}
			}
			else
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet,"barren");
						break;
					case 1:
						planetType=ChangePlanetClass(planet,"barren2");
						break;
					default:
						planetType=ChangePlanetClass(planet,"barren3");
						break;
				}
			}
			planetCat="cat_barren";
		}
		if(planetCat.equals("cat_lava") && !planet.hasCondition("very_hot"))
		{
			if(planet.hasCondition("ore_rich") || planet.hasCondition("ore_ultrarich") || planet.hasCondition("rare_ore_rich") || planet.hasCondition("rare_ore_ultrarich"))
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet,"barren_castiron");
						break;
					default:
						planetType=ChangePlanetClass(planet,"rocky_metallic");
						break;
				}
			}
			else
			{
				int which = Misc.random.nextInt(3);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet,"barren");
						break;
					case 1:
						planetType=ChangePlanetClass(planet,"barren2");
						break;
					default:
						planetType=ChangePlanetClass(planet,"barren3");
						break;
				}
			}
			planetCat="cat_barren";
		}
		if(planetCat.equals("cat_cryovolcanic") && !planet.hasCondition("tectonic_activity") && !planet.hasCondition("extreme_tectonic_activity"))
		{
			if(planet.hasCondition("very_cold"))
			{
				int which = Misc.random.nextInt(4);
				switch(which)
				{
					case 0:
						planetType=ChangePlanetClass(planet,"frozen");
						break;
					case 1:
						planetType=ChangePlanetClass(planet,"frozen1");
						break;
					case 2:
						planetType=ChangePlanetClass(planet,"frozen2");
						break;
					default:
						planetType=ChangePlanetClass(planet,"frozen3");
						break;
				}
				planetCat="cat_frozen";
			}
			else if(planet.hasCondition("cold"))
			{
				planetType=ChangePlanetClass(planet,"rocky_ice");
				planetCat="cat_frozen";
			}
			else
			{
				planetType=ChangePlanetClass(planet,"water");
				planetCat="cat_hab3";
			}
		}
		if(planetCat.equals("cat_frozen") && !planet.hasCondition("very_cold") && !planet.hasCondition("cold"))
		{
			planetType=ChangePlanetClass(planet,"water");
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_toxic") && !planet.hasCondition("toxic_atmosphere"))
		{
			int which = Misc.random.nextInt(3);
			switch(which)
			{
				case 0:
					planetType=ChangePlanetClass(planet,"barren");
					break;
				case 1:
					planetType=ChangePlanetClass(planet,"barren2");
					break;
				default:
					planetType=ChangePlanetClass(planet,"barren3");
					break;
			}
			planetCat="cat_barren";
		}
		if(planetType.equals("rocky_ice") && !planet.hasCondition("cold") && !planet.hasCondition("very_cold"))
		{
			planetType=ChangePlanetClass(planet,"water");
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_barren") && !planet.hasCondition("no_atmosphere") && !planet.hasCondition("thin_atmosphere") && !planet.hasCondition("toxic_atmosphere"))
		{
			planetType=ChangePlanetClass(planet,"barren-desert");
			planetCat="cat_hab1";
		}
		if(planetCat.equals("cat_hab1") && planet.hasCondition("habitable"))
		{
			planetType=ChangePlanetClass(planet,"desert");
			AddOrImproveFarmland(planet);
			planetCat="cat_hab2";
		}
		if(planetCat.equals("cat_hab2") && planet.hasCondition("cold") && planet.hasCondition("hydrated"))
		{
			planetType=ChangePlanetClass(planet,"tundra");
			AddOrImproveFarmland(planet);
			planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(planetCat.equals("cat_hab2") && planet.hasCondition("hot") && planet.hasCondition("hydrated"))
		{
			planetType=ChangePlanetClass(planet,"jungle");
			AddOrImproveFarmland(planet);
			planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(((planetCat.equals("cat_hab2")&& planet.hasCondition("hydrated")) || (planetCat.equals("cat_hab3") && !planetType.equals("terran-eccentric") && !planetType.equals("water"))) && (planet.hasCondition("dense_atmosphere") || planet.hasCondition("extreme_weather") || planet.hasCondition("irradiated") || planet.hasCondition("poor_light") || planet.hasCondition("dark")))
		{
			planetType=ChangePlanetClass(planet,"terran-eccentric");
			AddOrImproveFarmland(planet);
			if(planet.hasCondition("hydrated"))
				planet.removeCondition("hydrated");
			planetCat="cat_hab3";
		}
		if(planet.hasCondition("habitable") && !planetType.equals("terran") && !planetType.equals("water") && ((planetCat.equals("cat_hab2") && planet.hasCondition("hydrated")) || planetCat.equals("cat_hab3")) && !(planet.hasCondition("dense_atmosphere") || planet.hasCondition("extreme_weather") || planet.hasCondition("irradiated") || planet.hasCondition("poor_light") || planet.hasCondition("dark") || planet.hasCondition("hot") || planet.hasCondition("very_hot") || planet.hasCondition("cold") || planet.hasCondition("very_cold")))
		{
			planetType=ChangePlanetClass(planet,"terran");
			AddOrImproveFarmland(planet);
			if(planet.hasCondition("hydrated"))
				planet.removeCondition("hydrated");
			planetCat="cat_hab4";
		}
		
		if(planetType.equals("water") && !planet.hasCondition("water_surface"))
			planet.addCondition("water_surface");
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
		if(planet.hasCondition("farmland_rich"))
		{
			planet.removeCondition("farmland_rich");
			planet.addCondition("farmland_bountiful");
			planet.getFirstCondition("farmland_bountiful").setSurveyed(true);
		}
		else if(planet.hasCondition("farmland_adequate"))
		{
			planet.removeCondition("farmland_adequate");
			planet.addCondition("farmland_rich");
			planet.getFirstCondition("farmland_rich").setSurveyed(true);
		}
		else if(planet.hasCondition("farmland_poor"))
		{
			planet.removeCondition("farmland_poor");
			planet.addCondition("farmland_adequate");
			planet.getFirstCondition("farmland_adequate").setSurveyed(true);
		}
		else if(!planet.hasCondition("farmland_bountiful"))
		{
			if(planet.getPlanetEntity().getSpec().getPlanetType().equals("terran"))
			{
				planet.addCondition("farmland_rich");
				planet.getFirstCondition("farmland_rich").setSurveyed(true);
			}
			else
			{
				planet.addCondition("farmland_poor");
				planet.getFirstCondition("farmland_poor").setSurveyed(true);
			}
		}
	}
}
