package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class AtmoPump extends TerraformIndustryNeedsArtifact {

	public AtmoPump()
	{
		super();
		TERRAFORM_TIME=300f;
	
		hazardAmount=0.25f;
	
		hazard=Conditions.THIN_ATMOSPHERE;
		tag="AtmoPump";
		artifactRequired="atmo_sublimator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES,2);
	}
}