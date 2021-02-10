package slexom.vf.animal_self_feed.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slexom.vf.animal_self_feed.AnimalSelfFeedMod;
import slexom.vf.animal_self_feed.block.FoodContainerBlock;
import slexom.vf.animal_self_feed.inventory.BlockEntityInventory;
import slexom.vf.animal_self_feed.screen.FoodContainerScreenHandler;

public class FoodContainerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, BlockEntityInventory, Tickable {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public FoodContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public FoodContainerBlockEntity() {
        this(AnimalSelfFeedMod.FOOD_CONTAINER_BLOCK_ENTITY);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FoodContainerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if (!world.isClient()) {
            BlockState state = world.getBlockState(this.pos);
            int count = this.getStack(0).getCount();
            int newLevel = 0;
            if (count > 0) {
                newLevel = MathHelper.floor(this.getStack(0).getCount() / 16.0F) + 1;
                newLevel = Math.min(newLevel, 4);
            }
            int currentLevel = state.get(FoodContainerBlock.LEVEL);
            if (currentLevel != newLevel) {
                BlockState blockState = state.with(FoodContainerBlock.LEVEL, newLevel);
                world.setBlockState(this.pos, blockState, 3);
            }
        }
    }

}
