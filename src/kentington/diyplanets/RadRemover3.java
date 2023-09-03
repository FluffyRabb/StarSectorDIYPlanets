package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class RadRemover3 extends TerraformIndustryNeedsArtifact {
	
	public RadRemover3()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="rare_ore_moderate";
		tag="RadRemover";
		artifactRequired="rad_remover";
		requiresTerran = true;
		reEvaluateMining=true;
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}
