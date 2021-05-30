package crimson_twilight.immersive_energy.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.google.common.collect.Maps;

import blusunrize.immersiveengineering.common.world.IEWorldGen;
import crimson_twilight.immersive_energy.ImmersiveEnergy;
import crimson_twilight.immersive_energy.api.energy.FuelHandler;
import crimson_twilight.immersive_energy.common.compat.IEnCompatModule;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fluids.FluidRegistry;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;

@EventBusSubscriber(modid = ImmersiveEnergy.MODID, bus = Bus.MOD)

public class IEnServerConfig
	{
		public static HashMap<String, Boolean> manual_bool = new HashMap<String, Boolean>();
		public static HashMap<String, Integer> manual_int = new HashMap<String, Integer>();
		public static HashMap<String, int[]> manual_intA = new HashMap<String, int[]>();


		public static final Machines MACHINES;
		public static final Tools TOOLS;

		public static final ForgeConfigSpec ALL;

		static {
			ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
			MACHINES = new Machines(builder);
			TOOLS = new Tools(builder);

			ALL = builder.build();
		}

		public static class Machines
		{
			@Comment({"Power config for Solar Panels.", "Parameters: Base gen"})
			@Mapped(mapClass = Config.class, mapName = "manual_int")
			public static int base_solar = 5;
			@SuppressWarnings("static-access")
			@Comment({"Power storage config for Solar Panels.", "Parameters: Storage"})
			@Mapped(mapClass = Config.class, mapName = "manual_int")
			public static int storage_solar = IEConfig.machines.capacitorLV_storage/4;
			@Comment({"Durability of Thorium Rods.", "Parameters: durability"})
			@RangeInt(min = 1)
			public static int thoriumRodMaxDamage = 32600;
			@Comment({"Decay chance of Thorium Rods.", "Parameters: chance"})
			@RangeInt(min = 1)
			public static int thoriumRodDecay = 6538;
			@Comment({"Durability of Thorium Rods.", "Parameters: durability"})
			@RangeInt(min = 1)
			public static int uraniumRodMaxDamage = 31800;
			@Comment({"Decay chance of Uranium Rods.", "Parameters: chance"})
			@RangeInt(min = 1)
			public static int uraniumRodDecay = 5338;
			@Comment({"Fluid capacity of the Gas Burner (in mB)"})
			@RangeInt(min = 1)
			public static int burnerCapacity = 2000;
			@Comment({"List of Gas Burner fuels. Format: fluid_name, tick_per_mB_used"})
			public static String[] burner_fuels = new String[]{
					"biodiesel, 16",
					"ethanol, 8",
					"creosote, 4",
					"gasoline, 20",
					"methanol, 20"
			};

			@SubConfig
			FluidBattery fluidBattery;

			public static class FluidBattery
			{
				@Comment({"Fluid capacity of one tank of the Fluid Battery (in mB). Default: 144000"})
				@RangeInt(min = 1)
				@net.minecraftforge.common.config.Config.RequiresMcRestart
				public static int fluidCapacity = 144000;

				@Comment({"Energy exchange amount (in IF per mb). Default: 2048"})
				@RangeInt(min = 1)
				@net.minecraftforge.common.config.Config.RequiresMcRestart
				public static int IFAmount = 2048;

				@Comment({"Amout of energy that can be inputted to one energy port(in IF/Tick). Default: 32768"})
				@RangeInt(min = 1)
				@net.minecraftforge.common.config.Config.RequiresMcRestart
				public static int maxInput = 32768;

				@Comment({"Amout of energy that can be outputted on one energy port(in IF/Tick). Default: 32768"})
				@RangeInt(min = 1)
				@net.minecraftforge.common.config.Config.RequiresMcRestart
				public static int maxOutput = 32768;
			}
		}

		
		public static class Tools
		{
			public final ForgeConfigSpec.ConfigValue<Integer> shock_arrow_regular_damage;
			public final ForgeConfigSpec.ConfigValue<Integer> shock_arrow_electric_damage;
			public final ForgeConfigSpec.ConfigValue<Integer> shock_arrow_knockback;
			public final ForgeConfigSpec.ConfigValue<Boolean> shock_arrow_ignore;

			public final ForgeConfigSpec.ConfigValue<Integer> penetrating_arrow_regular_damage;
			public final ForgeConfigSpec.ConfigValue<Integer> penetrating_arrow_penetrating_damage;
			public final ForgeConfigSpec.ConfigValue<Integer> penetrating_arrow_knockback;

			public final ForgeConfigSpec.ConfigValue<Boolean> penetrating_arrow_ignore;

			public final ForgeConfigSpec.ConfigValue<Integer> armor_plates_upgrade_resist;
			public final ForgeConfigSpec.ConfigValue<Integer> heat_base_resist;
			public final ForgeConfigSpec.ConfigValue<Integer> heat_upgrade_resist;

			public final ForgeConfigSpec.ConfigValue<Boolean> nail_gun_no_invulnerability;

			public final ForgeConfigSpec.ConfigValue<List<String>> nailbox_nails;

			 Tools(ForgeConfigSpec.Builder builder){
				builder.push("Tools");
				shock_arrow_regular_damage = builder
						.comment("Base damage of Shocking Arrow, default=2")
						.define("shock_arrow_regular_damage",Integer.valueOf(2));
				shock_arrow_electric_damage = builder
						.comment("Electric damage of Shocking Arrow, default=3")
						.define("shock_arrow_electric_damage",Integer.valueOf(3));
				shock_arrow_knockback = builder
						.comment("Base knockback of Shocking Arrow, default=0")
						.define("shock_arrow_knockback",Integer.valueOf(0));

				penetrating_arrow_regular_damage = builder
						.comment("Base damage of Piercing Arrow, default=2")
						.define("penetrating_arrow_regular_damage",Integer.valueOf(2));
				penetrating_arrow_penetrating_damage = builder
						.comment("Electric damage of Piercing Arrow, default=3")
						.define("penetrating_arrow_penetrating_damage",Integer.valueOf(3));
				penetrating_arrow_knockback = builder
						.comment("Base knockback of Piercing Arrow, default=0")
						.define("penetrating_arrow_knockback",Integer.valueOf(1));


				armor_plates_upgrade_resist = builder
						.comment("Resistance to damage added by Additional Armor Plates upgrade, default=3")
						.define("armor_plates_upgrade_resist",Integer.valueOf(3));
				heat_base_resist = builder
						.comment("Base resistance to heat, default=100")
						.define("heat_base_resist",Integer.valueOf(100));
				heat_upgrade_resist = builder
						.comment("Resistance to heat added by each Heat Resistant Plates upgrade, default=5000")
						.define("heat_upgrade_resist",Integer.valueOf(5000));

				shock_arrow_ignore = builder
						.comment("Does the Shocking Arrow ignore invulnerability frames, default=false")
						.define("shock_arrow_ignore",Boolean.valueOf(false));

				penetrating_arrow_ignore = builder
						.comment("Does the Shocking Arrow ignore invulnerability frames, default=false")
						.define("penetrating_arrow_ignore",Boolean.valueOf(true));

				nail_gun_no_invulnerability = builder
						.comment("Does the Shocking Arrow ignore invulnerability frames, default=false")
						.define("nail_gun_no_invulnerability",Boolean.valueOf(true));

				nailbox_nails = builder
						 .comment("A whitelist of foods allowed in the nailbox, formatting: [mod id]:[item name]")
						 .define("nailbox_nails", Collections.emptyList());

			}

		}

		public static void preInit(FMLPreInitializationEvent event)
		{
			onConfigUpdate();
		}
		
		private static void onConfigUpdate() 
		{
			
		}

		public static void validateAndMapValues(Class confClass)
		{
			for(Field f : confClass.getDeclaredFields())
			{
				if(!Modifier.isStatic(f.getModifiers()))
					continue;
				Mapped mapped = f.getAnnotation(Mapped.class);
				if(mapped!=null)
					try
					{
						Class c = mapped.mapClass();
						if(c!=null)
						{
							Field mapField = c.getDeclaredField(mapped.mapName());
							if(mapField!=null)
							{
								Map map = (Map)mapField.get(null);
								if(map!=null)
									map.put(f.getName(), f.get(null));
							}
						}
					} catch(Exception e)
					{
						e.printStackTrace();
					}
				else if(f.getAnnotation(SubConfig.class)!=null)
					validateAndMapValues(f.getType());
				else if(f.getAnnotation(RangeDouble.class)!=null)
					try
					{
						RangeDouble range = f.getAnnotation(RangeDouble.class);
						Object valObj = f.get(null);
						double val;
						if(valObj instanceof Double)
							val = (double)valObj;
						else
							val = (float)valObj;
						if(val < range.min())
							f.set(null, range.min());
						else if(val > range.max())
							f.set(null, range.max());
					} catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}
				else if(f.getAnnotation(RangeInt.class)!=null)
					try
					{
						RangeInt range = f.getAnnotation(RangeInt.class);
						int val = (int)f.get(null);
						if(val < range.min())
							f.set(null, range.min());
						else if(val > range.max())
							f.set(null, range.max());
					} catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}
			}
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		public @interface Mapped
		{
			
			Class mapClass();

			String mapName();
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.FIELD)
		public @interface SubConfig
		{
		}

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent ev)
		{
			if(ev.getModID().equals(ImmersiveEnergy.MODID))
			{
				ConfigManager.sync(ImmersiveEnergy.MODID, net.minecraftforge.common.config.Config.Type.INSTANCE);
				onConfigUpdate();
			}
		}
		
		public static void addBurnerFuel(String[] fuels)
		{
			for (int i = 0; i < fuels.length; i++)
			{
				String str = fuels[i];

				if (str.isEmpty()) continue;

				String fluid = null;
				int amount = 0;

				String remain = str;

				int index = 0;

				while (remain.indexOf(",") != -1)
				{
					int endPos = remain.indexOf(",");

					String current = remain.substring(0, endPos).trim();

					if (index == 0) fluid = current;

					remain = remain.substring(endPos + 1);
					index++;
				}
				String current = remain.trim();

				try
				{
					amount = Integer.parseInt(current);
					if (amount <= 0)
					{
						throw new RuntimeException("Negative value for fuel tick/mB for gas burner fuel " + (i + 1));
					}
					else
					{
						fluid = fluid.toLowerCase(Locale.ENGLISH);
						if (FluidRegistry.getFluid(fluid) != null)
						{
							FuelHandler.registerGasBurnerFuel(FluidRegistry.getFluid(fluid), amount);
						}
						else 
						{
							new RuntimeException("Invalid fluid name for gas burner fuel " + (i + 1));
						}
					}
				} catch (NumberFormatException e)
				{
					throw new RuntimeException("Invalid value for fuel tick/mB for gas burner fuel " + (i + 1));
				}
			}

		}
	}
