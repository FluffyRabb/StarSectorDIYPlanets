package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class SolarShade extends TerraformIndustryNeedsArtifact {
	
	public SolarShade()
	{
		super();
		TERRAFORM_TIME=240f;
		
		hazardAmount=0.25f;
		
		hazard="hot";
		tag="SolarShade";
		artifactRequired="solar_reflector";
		disqualifyCondition="dark";
		stationType = "stellar_shade";
		stationName = "Stellar Shade";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.METALS, 2);
	}
}