package com.teammetallurgy.atum.entity.undead;

import com.teammetallurgy.atum.init.AtumLootTables;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntityMummy extends EntityUndeadBase {
    private static final AttributeModifier SPEED_BOOST_BURNING = new AttributeModifier(UUID.fromString("2dc2358a-63df-435d-a602-2ff3d6bca8d1"), "Burning speed boost", 0.1D, 0);

    public EntityMummy(World world) {
        super(world);
        this.experienceValue = 8;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        ModifiableAttributeInstance attribute = (ModifiableAttributeInstance) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (this.isBurning() && !attribute.hasModifier(SPEED_BOOST_BURNING)) {
            attribute.applyModifier(SPEED_BOOST_BURNING);
        } else {
            attribute.removeModifier(SPEED_BOOST_BURNING);
        }
    }

    @Override
    protected float getBurnDamage() {
        return 2.0F;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (source.isFireDamage()) {
            amount += 1;
        }
        if (this.isBurning()) {
            amount = (int) (amount * 1.5);
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean flag = super.attackEntityAsMob(entity);

        if (flag) {
            if (this.isBurning() && this.rand.nextFloat() < (float) this.world.getDifficulty().getId() * 0.4F) {
                entity.setFire(2 * this.world.getDifficulty().getId());
            }
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase base = (EntityLivingBase) entity;
                base.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80, 1));
            }
        }

        return flag;
    }

    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return AtumLootTables.MUMMY;
    }
}
