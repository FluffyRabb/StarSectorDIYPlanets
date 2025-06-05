package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class RadRemover2 extends TerraformIndustryNeedsArtifact {
	
	public RadRemover2()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard=Conditions.RARE_ORE_SPARSE;
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
