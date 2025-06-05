package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class AtmoFilter extends TerraformIndustryNeedsArtifact {
	
	public AtmoFilter()
	{
		super();
		TERRAFORM_TIME=420f;
	
		hazardAmount=0.5f;
	
		hazard=Conditions.TOXIC_ATMOSPHERE;
		tag="AtmoFilter";
		artifactRequired="atmo_mineralizer";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.ORGANICS, 2);
	}
}