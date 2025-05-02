package me.mashi.apricornplanting;

import com.cobblemon.mod.common.api.tags.CobblemonBlockTags;
import com.cobblemon.mod.common.api.tags.CobblemonItemTags;
import com.cobblemon.mod.common.item.ApricornItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApricornPlanting implements ModInitializer {
	public static final String MOD_ID = "apricornplanting";
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	private static final String APRICORN_PLANTING_ON_ALL_LEAVES_NAME = "ApricornPlantingOnAllLeaves";
	private static final boolean DEFAULT_APRICORN_PLANTING_ON_ALL_LEAVES = false;



	public static final GameRules.Key<GameRules.BooleanRule> APRICORN_PLANTING_ON_ALL_LEAVES =
			GameRuleRegistry.register(
					APRICORN_PLANTING_ON_ALL_LEAVES_NAME,
					GameRules.Category.MISC,
					GameRuleFactory.createBooleanRule(DEFAULT_APRICORN_PLANTING_ON_ALL_LEAVES)
			);

	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register(this::handleBlockClick);
	}

	public ActionResult handleBlockClick(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult bhr) {
		// Checks for if world is on the clientside,
		// if the player is using their offhand,
		// if the player is trying to plant on a block that is not a leaf,
		// if the player is trying to plant on a block that is not an Apricorn,
		// or if the block the player is trying to plant on is not air.
		BlockPos targetedBlockPos = bhr.getBlockPos().add(bhr.getSide().getVector());
		ItemStack playerHandItemStack = playerEntity.getStackInHand(hand);

		if (world.isClient
				|| !world.getGameRules().getBoolean(APRICORN_PLANTING_ON_ALL_LEAVES)
				|| !world.getBlockState(bhr.getBlockPos()).isIn(BlockTags.LEAVES)
				|| !(playerEntity.getStackInHand(hand).getItem() instanceof ApricornItem)
				|| !world.getBlockState(targetedBlockPos).isAir()
		) {
			return ActionResult.PASS;
		}


		if (playerHandItemStack.isIn(CobblemonItemTags.APRICORNS)
				&& world.getBlockState(bhr.getBlockPos()).isIn(CobblemonBlockTags.APRICORN_LEAVES)) {
			if (!(playerHandItemStack.getItem() instanceof ApricornItem)) {
				throw new RuntimeException("The Apricorn you've tried to plant is not derived from ApricornItemClass. Please check your config or contact the mod author.");
			}
			BlockState itemBlockState = ((ApricornItem) playerHandItemStack.getItem()).getBlock()
					.getDefaultState()
					.with(HorizontalFacingBlock.FACING, bhr.getSide().getOpposite());

			// The targetedBlockPos will be filled with the Apricorn.
			world.setBlockState(targetedBlockPos, itemBlockState);
			playerHandItemStack.decrement(1);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
}