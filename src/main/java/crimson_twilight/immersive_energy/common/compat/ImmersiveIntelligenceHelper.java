package crimson_twilight.immersive_energy.common.compat;

public class ImmersiveIntelligenceHelper extends IEnCompatModule
{
	@Override
	public void preInit()
	{
		IEnCompatModule.ii=true;
	}

	@Override
	public void registerRecipes()
	{

	}

	@Override
	public void init()
	{
		//for compat that requires events
		//MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit()
	{

	}
}