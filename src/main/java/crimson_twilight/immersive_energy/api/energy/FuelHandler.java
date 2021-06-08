package crimson_twilight.immersive_energy.api.energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FuelHandler 
{
	static final HashMap<ResourceLocation, Integer> gasBurnerAmountTick = new HashMap<>();

	public static void registerGasBurnerFuel(ResourceLocation fuelRL, int tickPermb)
	{
		if(fuelRL != null && !fuelRL.toString().isEmpty())
		{
			gasBurnerAmountTick.put(fuelRL, tickPermb);
		}
	}

	public static void registerGasBurnerFuel(Fluid fuel, int tickPermb)
	{
		if (fuel != null) 
		{
			gasBurnerAmountTick.put(fuel.getRegistryName(), tickPermb);
		}
	}
	
	public static boolean isValidFuel(Fluid fuel)
	{
		if (fuel != null) 
		{
			return gasBurnerAmountTick.containsKey(fuel.getRegistryName());
		}
		return false;
	}
	
	public static HashMap<ResourceLocation, Integer> getFuelPerTick()
	{
		return gasBurnerAmountTick;
	}
	
	public static int getTickPermb(Fluid fuel)
	{
		if (!isValidFuel(fuel)) return 0;
		return gasBurnerAmountTick.get(fuel.getRegistryName());
	}
	//Unused.
	//1.16 does not provide getFluidStack no more
	/*public static List<FluidStack> getBurnerFuels()
	{
		List<FluidStack> fuels = new ArrayList<>();
		for(ResourceLocation fuel : gasBurnerAmountTick.keySet())
		{
			if (fuel != null) 
			{

				fuels.add(FluidRegistry.getFluidStack(fuel, 1));
			}
		}
		return fuels;*/

}
