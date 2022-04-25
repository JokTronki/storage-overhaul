package de.maxhenkel.storage.items;

import java.util.Objects;

import javax.swing.text.AbstractDocument.Content;

import de.maxhenkel.storage.ChestTier;
import de.maxhenkel.storage.Main;
import de.maxhenkel.storage.blocks.ModBlocks;
import de.maxhenkel.storage.blocks.ModChestBlock;
import de.maxhenkel.storage.blocks.tileentity.ModChestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChestUpgrader extends Item {
  public static final DirectionProperty FACING = HorizontalBlock.FACING;

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public ChestUpgrader(Properties props) {
    super(props);
  }

  @Override
  public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
    World world = context.getLevel();

    if (!world.isClientSide) {
      BlockPos blockPos = context.getClickedPos();
      BlockState clickedBlock = world.getBlockState(context.getClickedPos());
      PlayerEntity playerEntity = Objects.requireNonNull(context.getPlayer());

      // TODO look at line80 at
      // https://github.com/progwml6/ironchest/blob/1.16/src/main/java/com/progwml6/ironchest/common/item/ChestUpgradeItem.java

      rightClickedOnChestState(clickedBlock, context, playerEntity, world);
    }

    return super.onItemUseFirst(stack, context);
  }

  private void rightClickedOnChestState(BlockState clickedBlock, ItemUseContext context, PlayerEntity playerEntity,
      World world) {
    if (blockIsChest(clickedBlock)) {
      upgradeChest(playerEntity, 30, clickedBlock, world, context);
    }
  }

  private boolean blockIsChest(BlockState clickedBlock) {
    return clickedBlock.getBlock() instanceof ModChestBlock;
  }

  public static void upgradeChest(PlayerEntity entity, int seconds, BlockState clickedBlock, World world,
      ItemUseContext context) {

    Minecraft mc = Minecraft.getInstance();

    // Get Tier
    String nextTier = ((ModChestBlock) clickedBlock.getBlock()).getNextTier();
    // Get Direction
    Direction direction = clickedBlock.getValue(FACING);
    // Get Items
    NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);

    TileEntity chestTileEntity = world.getBlockEntity(context.getClickedPos());

    if (nextTier == null) {
      mc.player.chat("This chest is either max tier or can't be upgraded.");
      return;
    }

    BlockState newChest = ModBlocks.MODBLOCK_BY_MAP.get(nextTier)
        .defaultBlockState().setValue(FACING, direction);

    // Remove existing chest
    world.removeBlockEntity(context.getClickedPos());
    world.removeBlock(context.getClickedPos(), false);

    // Add Updated chest back to world
    entity.level.setBlock(context.getClickedPos(), newChest, 1);

    mc.player.chat("Hellos");
    mc.player.chat(String.valueOf(((ModChestBlock) clickedBlock.getBlock()).getTier()));
  }

}
