package crimson_twilight.immersive_energy.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import blusunrize.immersiveengineering.common.config.IEServerConfig;
import com.google.common.collect.Maps;

import blusunrize.immersiveengineering.common.world.IEWorldGen;
import crimson_twilight.immersive_energy.ImmersiveEnergy;
import crimson_twilight.immersive_energy.api.energy.FuelHandler;
import crimson_twilight.immersive_energy.common.compat.IEnCompatModule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

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
			public final ForgeConfigSpec.ConfigValue<Integer> base_solar;
			public final ForgeConfigSpec.ConfigValue<Integer> storage_solar;
			public final ForgeConfigSpec.ConfigValue<Integer> thoriumRodMaxDamage;
			public final ForgeConfigSpec.ConfigValue<Integer> thoriumRodDecay;
			public final ForgeConfigSpec.ConfigValue<Integer> uraniumRodMaxDamage;
			public final ForgeConfigSpec.ConfigValue<Integer> uraniumRodDecay;
			public final ForgeConfigSpec.ConfigValue<Integer> burnerCapacity;
			public final ForgeConfigSpec.ConfigValue<List<String>> burner_fuels;

			public final FluidBattery fluidBattery;

			Machines(ForgeConfigSpec.Builder builder){
				builder.push("Machines");
				base_solar = builder
						.comment("Base power generation of solar panel FE/t, default=5")
						.define("base_solar",Integer.valueOf(5));
				storage_solar = builder
						.comment("Solar panel internal capacity, default is quarter of LV capacitor")
						.define("storage_solar",Integer.valueOf(IEServerConfig.MACHINES.lvCapConfig.storage.getAsInt() / 4));

				thoriumRodMaxDamage = builder
						.comment("Durability of thorium rods, default=32600")
						.define("thoriumRodMaxDamage",Integer.valueOf(32600));

				uraniumRodMaxDamage = builder
						.comment("Durability of uranium rods, default=31800")
						.define("uraniumRodMaxDamage",Integer.valueOf(31800));

				burnerCapacity = builder
						.comment("Fluid capacity of the Gas Burner (in mB), default=2000")
						.define("burnerCapacity",Integer.valueOf(2000));

				thoriumRodDecay = builder
						.comment("Decay chance of thorium rods (do not touch), default=6538")
						.define("thoriumRodDecay",Integer.valueOf(6538));
				uraniumRodDecay = builder
						.comment("Decay chance of uranium rods (do not touch), default=6538")
						.define("uraniumRodDecay",Integer.valueOf(5538));
				List<String> l_burner_fuels = Arrays.asList(new String[]{
						"biodiesel, 16",
						"ethanol, 8",
						"creosote, 4",
						"gasoline, 20",
						"methanol, 20"
				});
				burner_fuels = builder
						.comment("List of Gas Burner fuels. Format: fluid_name, tick_per_mB_used")
						.define("burner_fuels", l_burner_fuels);
				fluidBattery= new FluidBattery(builder);
				builder.pop();
			}


			public static class FluidBattery
			{
				public final ForgeConfigSpec.ConfigValue<Integer> fluidCapacity;
				public final ForgeConfigSpec.ConfigValue<Integer> IFAmount;
				public final ForgeConfigSpec.ConfigValue<Integer> maxInput;
				public final ForgeConfigSpec.ConfigValue<Integer> maxOutput;
				FluidBattery(ForgeConfigSpec.Builder builder){
					builder.push("Liquid Battery");
					fluidCapacity = builder
							.comment("Fluid capacity of one tank of the Fluid Battery (in mB), default=144000")
							.define("fluidCapacity",Integer.valueOf(144000));
					IFAmount = builder
							.comment("Energy exchange amount (in IF per mb), default=2048")
							.define("IFAmount",Integer.valueOf(2048));
					maxInput = builder
							.comment("Amount of energy that can be inputted to one energy port(in IF/Tick), default=32768")
							.define("maxInput",Integer.valueOf(32768));
					maxOutput = builder
							.comment("Amount of energy that can be outputted on one energy port(in IF/Tick), default=32768")
							.define("maxInput",Integer.valueOf(32768));
					builder.pop();
				}
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

				builder.pop();

			}

		}

		@SubscribeEvent
		public static void onConfigReload(ModConfigEvent ev){

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
						ResourceLocation fluidRL = new ResourceLocation(fluid);
						if (ForgeRegistries.FLUIDS.containsKey(fluidRL))
						{
							FuelHandler.registerGasBurnerFuel(fluidRL, amount);
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
