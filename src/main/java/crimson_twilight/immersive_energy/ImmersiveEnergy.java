package crimson_twilight.immersive_energy;

import crimson_twilight.immersive_energy.client.ClientProxy;
import crimson_twilight.immersive_energy.common.config.IEnServerConfig;
import crimson_twilight.immersive_energy.common.util.network.IEnPacketHandler;



import crimson_twilight.immersive_energy.client.RenderGuiHandler;
import crimson_twilight.immersive_energy.common.CommonProxy;
import crimson_twilight.immersive_energy.common.IEnContent;
import crimson_twilight.immersive_energy.common.compat.IEnCompatModule;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.logging.Logger;

//@Mod(modid = ImmersiveEnergy.MODID, name = ImmersiveEnergy.MODNAME, version = ImmersiveEnergy.MODVERSION, dependencies = "required-after:immersiveengineering;after:sereneseasons", useMetadata = true)
public class ImmersiveEnergy 
{

    public static final String MODID = "immersive_energy";
    public static final String MODNAME = "Immersive Energy";
    public static final String MODVERSION= "0.7.0";

    public static final ItemGroup creativeTab = new ItemGroup(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(IEnContent.blockGenerators0);
		}
	};

	public static ImmersiveEnergy INSTANCE = new ImmersiveEnergy();
	public static CommonProxy proxy = DistExecutor.safeRunForDist(()-> ClientProxy::new, ()->CommonProxy::new);

	//public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);


	public ImmersiveEnergy()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IEnServerConfig.ALL);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	public void setup(FMLCommonSetupEvent e)
    {
		proxy.preInit();
		IEnContent.preInit();
		IEnPacketHandler.preInit();

		IEnContent.preInitEnd();
		proxy.preInitEnd();
		//---------

		IEnContent.init();

		proxy.init();

		IEnContent.initEnd();
		proxy.initEnd();
		//---------

		MinecraftForge.EVENT_BUS.register(new RenderGuiHandler());
		IEnContent.postInit();
		proxy.postInit();



/*
		for(Item item : IEnContent.registeredIEnItems)
		{
			if(item instanceof IEnArrowBase)
			{
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new BehaviorProjectileDispense()
				{
					@Override
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
					{
						IEnArrowBase arrowItem = ((IEnArrowBase) item);
						final EntityIEnArrow arrow = new EntityIEnArrow(worldIn, arrowItem.getDropItem().copy(), position.getX(), position.getY(), position.getZ()).setIgnoreInvulnerability(arrowItem.getIgnoreInvulnerability());
						arrow.setLogic(arrowItem.getLogic());
						arrow.setDamage(arrowItem.getArrowDamage());
						arrow.setKnockbackStrength(arrowItem.getKnockback());
						arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
						if(arrowItem.isFlaming())
						{
							arrow.setFire(100);
						}
						return arrow;
					}
				});
			}
		}*/
		IEnContent.postInitEnd();
		proxy.postInitEnd();

    }
/*
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) 
    {
    	IEnContent.init();
    	proxy.init();
		IEnWorldGen ienWorldGen = new IEnWorldGen();
		GameRegistry.registerWorldGenerator(ienWorldGen, 0);
		MinecraftForge.EVENT_BUS.register(ienWorldGen);
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		IEnContent.initEnd();
    	proxy.initEnd();
    }*/

	public void loadComplete(FMLLoadCompleteEvent event){
		IEnCompatModule.doModulesLoadComplete();
	}


	public void serverStarting(FMLServerStartingEvent event){
		proxy.serverStarting();
    }




}
