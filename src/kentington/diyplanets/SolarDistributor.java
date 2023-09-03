package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class SolarDistributor extends TerraformIndustryNeedsArtifact {
	
	public SolarDistributor()
	{
		super();
		TERRAFORM_TIME=240f;
	
		hazardAmount=0.25f;
	
		hazard="poor_light";
		tag="SolarDistributor";
		artifactRequired="solar_reflector";
		stationType = "stellar_shade";
		stationName = "Stellar Shade";
		stationType2 = "stellar_mirror";
		stationName2 = "Stellar Mirror";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.METALS, 2);
	}
}