package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class AtmoPump2 extends TerraformIndustryNeedsArtifact {

	public AtmoPump2()
	{
		super();
		TERRAFORM_TIME=600f;
	
		hazardAmount=0.5f;
	
		hazard="no_atmosphere";
		tag="AtmoPump";
		artifactRequired="atmo_sublimator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.VOLATILES,2);
	}
}