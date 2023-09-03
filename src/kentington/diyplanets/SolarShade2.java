package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class SolarShade2 extends TerraformIndustryNeedsArtifact {
	
	public SolarShade2()
	{
		super();
		TERRAFORM_TIME=480f;
	
		hazardAmount=0.5f;
	
		hazard="very_hot";
		tag="SolarShade";
		artifactRequired="solar_reflector";
		disqualifyCondition="dark";
		stationType = "stellar_shade";
		stationName = "Stellar Shade";
		stationType2 = "stellar_shade";
		stationName2 = "Stellar Shade";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.METALS, 4);
	}
}