package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class WeatherRegulator extends TerraformIndustryNeedsArtifact {
	
	public WeatherRegulator()
	{
		super();
		
		TERRAFORM_TIME=240f;
	
		hazardAmount=0.25f;
	
		hazard="extreme_weather";
		tag="WeatherRegulator";
		artifactRequired="weather_core";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.ORGANICS, 2);
	}
}