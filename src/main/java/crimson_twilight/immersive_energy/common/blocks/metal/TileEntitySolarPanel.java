package crimson_twilight.immersive_energy.common.blocks.metal;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IEEnums.IOSideConfig;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;

import blusunrize.immersiveengineering.api.wires.*;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;

import blusunrize.immersiveengineering.api.wires.impl.ImmersiveConnectableTileEntity;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IDirectionalTile;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ITileDrop;

import blusunrize.immersiveengineering.common.config.IEClientConfig;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.util.EnergyHelper.IEForgeEnergyWrapper;

import blusunrize.immersiveengineering.common.util.EnergyHelper.IIEInternalFluxHandler;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.wires.IEWireTypes;
import com.google.common.collect.ImmutableList;
import crimson_twilight.immersive_energy.common.IEnTileTypes;
import crimson_twilight.immersive_energy.common.compat.IEnCompatModule;
import crimson_twilight.immersive_energy.common.compat.SereneSeasonsHelper;
import crimson_twilight.immersive_energy.common.config.IEnServerConfig;
import javafx.geometry.Side;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;


import javax.annotation.Nullable;
import java.util.*;

public class TileEntitySolarPanel extends ImmersiveConnectableTileEntity implements IImmersiveConnectable,  ITickableTileEntity, IDirectionalTile, IIEInternalFluxHandler, ITileDrop, IFluxProvider, IOBJModelCallback<BlockState> {

    public boolean active;
    private int energyGeneration;
    public Direction facing = Direction.NORTH;
    protected WireType wireType;
    HashMap<Connection, boolean> Connections;


    public  TileEntitySolarPanel ()
    {
        super(IEnTileTypes.SOLAR.get());
    }
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {

        facing = Direction.byIndex (nbt.getInt("facing"));
        active = nbt.getBoolean("active");
        energyStorage.readFromNBT(nbt);
    }

    /*
    @Override
    public void writeCustomNBT(NBTTagCompound nbt, boolean descPacket) {
        nbt.setInteger("facing", facing.ordinal());
        nbt.setBoolean("active", active);
        energyStorage.writeToNBT(nbt);
    }
*/

    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        nbt.putInt("facing", facing.getIndex());
        nbt.putBoolean("active", active);
        energyStorage.writeToNBT(nbt);
    }

    @Override
    public Direction getFacing() {
        if (facing == Direction.DOWN || facing == Direction.UP) {
            facing = Direction.NORTH;
        }
        return facing;
    }

    @Override
    public void setFacing(Direction facing) {
        if (facing == Direction.DOWN || facing == Direction.UP) {
            facing = Direction.NORTH;
        }
        this.facing = facing;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return PlacementLimitation.HORIZONTAL;
    }

    @Override
    public boolean mirrorFacingOnPlacement(LivingEntity placer) {
        return false;
    }

    public boolean canHammerRotate(Direction side, Vector3d hit, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canRotate(Direction axis) {
        return true;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        return super.getCapability(capability, facing);
    }
/*
    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return super.hasCapability(capability, facing);
    }


*/

    @Override
    public void markDirty(){
        super.markDirty();

        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.notifyNeighborsOfStateChange(pos, state.getBlock());
    }

    @Override
    public void tick() {
        energyGeneration = IEnServerConfig.MACHINES.base_solar.get();
        if (!world.isRemote) {
            markDirty();
        }
        active = false;
        if (world.isDaytime() && world.canBlockSeeSky(getPos())) {
            active = true;
            float modifier = 1;
            float f = world.getCelestialAngleRadians(1.0F);
            float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            modifier = modifier * MathHelper.cos(f);
            modifier = modifier * (pos.getY() / world.getHeight());
            Biome biome = world.getBiome(pos);

            modifier = IEnCompatModule.serene ? SereneSeasonsHelper.calculateModifier(world, biome, modifier) : calculateVanillaModifier(world, biome, modifier);
            //Would need rework for climate integration later on
            if (world.isRaining()) {
                modifier = modifier * 0.01f / world.rainingStrength;
            }

            if (world.isRaining()) {
                modifier = modifier * 0.9f;
            }

            if (world.isThundering() ) {
                modifier = modifier * 0.001f;
            }

            if (!world.isRemote) {
                if (energyGeneration * modifier < 1) {
                    energyStorage.modifyEnergyStored(-1);
                } else {
                    energyStorage.modifyEnergyStored((int) (energyGeneration * modifier));
                }
//FIXME - allow Solar panel to be stack in grids
            /*    if (energyStorage.getEnergyStored() > 0) {
                    int temp = this.transferEnergy(energyStorage.getEnergyStored(), true, 0);
                    if (temp > 0) {
                        energyStorage.modifyEnergyStored(-this.transferEnergy(temp, false, 0));
                        markDirty();
                    }
                }*/
                currentTickAccepted = 0;
            }
        }
    }

    private float calculateVanillaModifier(World world, Biome biome, float modifier) {
        return modifier;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return super.getMaxRenderDistanceSquared() * IEClientConfig.increasedTileRenderdistance.get();
    }

    @Override
    public void readOnPlacement(LivingEntity placer, ItemStack stack) {
        if (stack.hasTag()) {
            if (ItemNBTHelper.hasKey(stack, "energyStorage"))
                energyStorage.setEnergy(ItemNBTHelper.getInt(stack, "energyStorage"));
        }
    }


    @Override
    public List<ItemStack> getTileDrops(LootContext context) {
        ItemStack stack = new ItemStack(getBlockState().getBlock(), 1);
        writeCustomNBT(stack.getOrCreateTag(), false);
        return ImmutableList.of(stack);
    }
/*
    @Override
    public Vector3d getRaytraceOffset(IImmersiveConnectable link) {
        return new Vector3d(0.5f, 0.156f, 0.5f);
    }*/

    @Override
    public Vector3d getConnectionOffset(Connection con, ConnectionPoint point) {

        float xo = facing.getDirectionVec().getX() * .5f + .5f;
        float yo = facing.getDirectionVec().getY() * .5f + 0.156f;
        float zo = facing.getDirectionVec().getZ() * .5f + .5f;

        return new Vector3d(xo,yo,zo);
    }

    boolean inICNet = false;
    public int currentTickAccepted = 0;
    private FluxStorage energyStorage = new FluxStorage(getMaxStorage(), getMaxInput(), 0);

    boolean firstTick = true;


    IEForgeEnergyWrapper energyWrapper;

    @Override
    public IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
        if (facing != this.facing)
            return null;
        if (energyWrapper == null || energyWrapper.side != this.facing)
            energyWrapper = new IEForgeEnergyWrapper(this, this.facing);
        return energyWrapper;
    }

    @Override
    public FluxStorage getFluxStorage() {
        return energyStorage;
    }

    @Override
    public IOSideConfig getEnergySideConfig(Direction facing) {
        return IOSideConfig.OUTPUT;
    }

    @Override
    public boolean canConnect() {
        return true;
    }


    public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vector3i offset) {
        if(world.getBlockState(target.getPosition()).getBlock() != world.getBlockState(getPos()).getBlock()){
            return false;
        }
        return this.wireType == null && (cableType.getCategory().equals(WireType.LV_CATEGORY) || cableType.getCategory().equals(WireType.MV_CATEGORY));
    }

    @Override
    public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget) {
        this.wireType = cableType;
        markDirty();
    }

    @Override
    public void removeCable(@Nullable Connection connection, ConnectionPoint attachedPoint) {
        this.wireType = null;
        markDirty();
    }

    @Nullable
    @Override
    public ConnectionPoint getTargetedPoint(TargetingInfo info, Vector3i offset) {
        return new ConnectionPoint(pos, 0);
    }

    private int getMaxStorage() {
        return IEnServerConfig.MACHINES.storage_solar.get();
    }

    @Override
    public Collection<ConnectionPoint> getConnectionPoints() {
        return Arrays.asList(new ConnectionPoint(pos, 0));
    }

    @Override
    public BlockPos getConnectionMaster(@Nullable WireType cableType, TargetingInfo target)
    {
        return this.pos;
    }



    public int getMaxInput() {
        //Sorry Blu
        return	IEServerConfig.WIRES.energyWireConfigs.get(IEWireTypes.IEWireType.ELECTRUM).connectorRate.get();
    }

    public int getMaxOutput() {
        //Sorry Blu
        return	IEServerConfig.WIRES.energyWireConfigs.get(IEWireTypes.IEWireType.ELECTRUM).connectorRate.get();
    }
/*
    public int transferEnergy(int energy, boolean simulate, final int energyType) {
        co

        int received = 0;
        if (!world.isRemote) {
            Set<AbstractConnection> outputs = ImmersiveNetHandler.INSTANCE.getIndirectEnergyConnections(Utils.toCC(this), world);
            int powerLeft = Math.min(Math.min(getMaxOutput(), getMaxInput()), energy);
            final int powerForSort = powerLeft;

            if (outputs.size() < 1)
                return 0;

            int sum = 0;
            HashMap<AbstractConnection, Integer> powerSorting = new HashMap<AbstractConnection, Integer>();
            for (AbstractConnection con : outputs) {
                IImmersiveConnectable end = ApiUtils.toIIC(con.end, world);
                if (con.cableType != null && end != null) {
                    int atmOut = Math.min(powerForSort, con.cableType.getTransferRate());
                    int tempR = end.outputEnergy(atmOut, true, energyType);
                    if (tempR > 0) {
                        powerSorting.put(con, tempR);
                        sum += tempR;
                    }
                }
            }

            if (sum > 0)
                for (AbstractConnection con : powerSorting.keySet()) {
                    IImmersiveConnectable end = ApiUtils.toIIC(con.end, world);
                    if (con.cableType != null && end != null) {
                        float prio = powerSorting.get(con) / (float) sum;
                        int output = (int) (powerForSort * prio);

                        int tempR = end.outputEnergy(Math.min(output, con.cableType.getTransferRate()), true, energyType);
                        int r = tempR;
                        int maxInput = getMaxInput();
                        tempR -= (int) Math.max(0, Math.floor(tempR * con.getPreciseLossRate(tempR, maxInput)));
                        end.outputEnergy(tempR, simulate, energyType);
                        HashSet<IImmersiveConnectable> passedConnectors = new HashSet<IImmersiveConnectable>();
                        float intermediaryLoss = 0;
                        for (Connection sub : con.subConnections) {
                            float length = sub.length / (float) sub.cableType.getMaxLength();
                            float baseLoss = (float) sub.cableType.getLossRatio();
                            float mod = (((maxInput - tempR) / (float) maxInput) / .25f) * .1f;
                            intermediaryLoss = MathHelper.clamp(intermediaryLoss + length * (baseLoss + baseLoss * mod), 0, 1);

                            int transferredPerCon = ImmersiveNetHandler.INSTANCE.getTransferedRates(world.provider.getDimension()).containsKey(sub) ? ImmersiveNetHandler.INSTANCE.getTransferedRates(world.provider.getDimension()).get(sub) : 0;
                            transferredPerCon += r;
                            if (!simulate) {
                                ImmersiveNetHandler.INSTANCE.getTransferedRates(world.provider.getDimension()).put(sub, transferredPerCon);
                                IImmersiveConnectable subStart = ApiUtils.toIIC(sub.start, world);
                                IImmersiveConnectable subEnd = ApiUtils.toIIC(sub.end, world);

                                if (subStart != null && passedConnectors.add(subStart))
                                    subStart.onEnergyPassthrough((int) (r - r * intermediaryLoss));
                                if (subEnd != null && passedConnectors.add(subEnd))
                                    subEnd.onEnergyPassthrough((int) (r - r * intermediaryLoss));
                            }
                        }
                        received += r;
                        powerLeft -= r;
                        if (powerLeft <= 0)
                            break;
                    }
                }
        }
        return received;
    }
*/
    @Override
    public int extractEnergy(@Nullable Direction from, int energy, boolean simulate) {
        return  energyStorage.extractEnergy(energy, simulate);
    }

    @Override
    public int getEnergyStored(@Nullable Direction from) {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(@Nullable Direction from) {
        return getMaxStorage();
    }
}