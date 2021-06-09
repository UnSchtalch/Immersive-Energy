package crimson_twilight.immersive_energy.common;

import com.google.common.collect.ImmutableSet;
import java.util.function.Supplier;


import crimson_twilight.immersive_energy.ImmersiveEnergy;

import crimson_twilight.immersive_energy.common.blocks.metal.TileEntitySolarPanel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.Block;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class IEnTileTypes {

    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(
            ForgeRegistries.TILE_ENTITIES, ImmersiveEnergy.MODID);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Block... valid){
        return REGISTER.register(name, () -> new TileEntityType<>(factory, ImmutableSet.copyOf(valid), null));
    }

    public static final RegistryObject<TileEntityType<TileEntitySolarPanel>> SOLAR = register("solarpanel",TileEntitySolarPanel::new, IEnContent.blockGenerators0);


}
