package com.cowax.cowaxpack.item.common;

import com.cowax.cowaxpack.client.screens.ArtilleryIndicatorScreen;
import com.cowax.cowaxpack.item.ItemScreenProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class ArtilleryIndicator extends Item implements ItemScreenProvider {

    public static final String TAG_CANNON = "Cannons";
    public static final String TAG_TYPE = "Type";
    public static final int MAX_ARTILLERY_COUNT = 5; // Максимальное количество артиллерии

    public ArtilleryIndicator() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pStack.getTag() != null && pStack.getTag().contains(TAG_TYPE)) {
            pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator.type", Component.translatable(pStack.getTag().getString(TAG_TYPE)))
                    .withStyle(ChatFormatting.WHITE));
        }
        pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator_1").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator_2").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator_3").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator_4").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.literal(" ").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("des.cowaxpack.artillery_indicator_5").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPYGLASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (pHand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.fail(pPlayer.getItemInHand(pHand));
        }
        pPlayer.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        pLivingEntity.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
        return pStack;
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        pLivingEntity.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    }

    public boolean checkFull(ItemStack stack) {
        ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
        return tags.size() >= MAX_ARTILLERY_COUNT;
    }

    public boolean addCannon(ItemStack stack, Entity entity) {
        String uuid = entity.getStringUUID();
        ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
        if (tags.isEmpty()) {
            stack.getOrCreateTag().putString(TAG_TYPE, entity.getType().getDescriptionId());
        } else {
            if (!stack.getOrCreateTag().getString(TAG_TYPE).equals(entity.getType().getDescriptionId())) {
                return false;
            }
        }

        List<CompoundTag> list = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            list.add(tags.getCompound(i));
        }
        for (var tag : list) {
            if (tag.getString("UUID").equals(uuid)) {
                return false;
            }
        }
        CompoundTag uuidTag = new CompoundTag();
        uuidTag.putString("UUID", uuid);
        list.add(uuidTag);

        ListTag listTag = new ListTag();
        listTag.addAll(list);
        stack.getOrCreateTag().put(TAG_CANNON, listTag);

        return true;
    }

    public boolean removeCannon(ItemStack stack, String uuid) {
        ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
        List<CompoundTag> list = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < tags.size(); i++) {
            var tag = tags.getCompound(i);
            if (tag.getString("UUID").equals(uuid)) {
                flag = true;
                continue;
            }
            list.add(tags.getCompound(i));
        }
        if (flag) {
            ListTag listTag = new ListTag();
            listTag.addAll(list);
            stack.getOrCreateTag().put(TAG_CANNON, listTag);
            if (listTag.isEmpty()) {
                stack.getOrCreateTag().remove(TAG_TYPE);
            }
        }

        return flag;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Screen getItemScreen(ItemStack stack, Player player, InteractionHand hand) {
        return new ArtilleryIndicatorScreen(stack, hand);
    }

    public void setTarget(ItemStack stack, Player player) {
        ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
        List<CompoundTag> list = new ArrayList<>();

        for (int i = 0; i < tags.size(); i++) {
            var tag = tags.getCompound(i);
            // Здесь должна быть логика поиска сущности и установки цели
            // Пока оставляем заглушку, так как нужна интеграция с SuperbWarfare
            list.add(tag);
        }

        if (list.size() != tags.size()) {
            ListTag listTag = new ListTag();
            listTag.addAll(list);
            stack.getOrCreateTag().put(TAG_CANNON, listTag);
            if (listTag.isEmpty()) {
                stack.getOrCreateTag().remove(TAG_TYPE);
            }
        }
    }

    public InteractionResult bind(ItemStack stack, Player player, Entity entity) {
        if (this.checkFull(stack)) {
            player.displayClientMessage(Component.translatable("des.cowaxpack.artillery_indicator.full").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        if (this.addCannon(stack, entity)) {
            player.displayClientMessage(Component.translatable("des.cowaxpack.artillery_indicator.add", entity.getDisplayName())
                    .withStyle(ChatFormatting.GREEN), true);
            return InteractionResult.SUCCESS;
        } else if (this.removeCannon(stack, entity.getStringUUID())) {
            player.displayClientMessage(Component.translatable("des.cowaxpack.artillery_indicator.remove", entity.getDisplayName())
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(Component.translatable("des.cowaxpack.artillery_indicator.fail", entity.getDisplayName())
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
    }
}
