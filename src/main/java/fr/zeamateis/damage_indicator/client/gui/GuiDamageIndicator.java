package fr.zeamateis.damage_indicator.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import fr.zeamateis.damage_indicator.amy.util.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiDamageIndicator extends IngameGui {

    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation DAMAGE_INDICATOR_BG = new ResourceLocation(DamageIndicatorMod.MODID, "textures/gui/damage_bg.png");
    private int y;
    private int j;
    private float glX;
    private float glY;
    private float prevYawOffset;
    private float prevYaw;
    private float prevPitch;
    private float prevYawHead;
    private float prevPrevYahHead;
    private int scale = 1;

    public GuiDamageIndicator() {
        super(Minecraft.getInstance());
    }

    @Override
    public void renderGameOverlay(float partialTicks) {
        final int zLevel = -90;
        this.mc.gameRenderer.getMouseOver(partialTicks);

        if (this.mc.pointedEntity != null) {
            if (this.mc.pointedEntity instanceof LivingEntity) {

                LivingEntity entityRaytraced = (LivingEntity) this.mc.pointedEntity;


                if (entityRaytraced.isAlive() && !entityRaytraced.isInvisibleToPlayer(this.mc.player)) {

                    switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
                        case BOTTOM_LEFT:
                            RenderSystem.translated(0, this.mc.func_228018_at_().getScaledHeight() - 55, zLevel);
                            break;
                        case TOP_LEFT:
                            RenderSystem.translated(0, 0, zLevel);
                            break;
                        default:
                            RenderSystem.translated(0, 0, zLevel);
                            break;
                    }

                    final double hudScale = 0.5;//MathHelper.clamp(DamageIndicatorMod.getConfig().getClient().hudScale.get(), 0.0D, 1.0D);


                    RenderSystem.scaled(hudScale, hudScale, hudScale);

                    //Draw potions informations
                    this.mc.getTextureManager().bindTexture(ContainerScreen.INVENTORY_BACKGROUND);
                    if (DamageIndicatorMod.getConfig().getClient().showEntityActivesPotions.get()) {
                        Collection<EffectInstance> collection = entityRaytraced.getActivePotionEffects();

                        if (!DamageIndicatorMod.getConfig().getClient().showPotionInfo.get()) {
                            this.drawPotionIconWithInfo(collection, zLevel);
                        } else {
                            this.drawLittlePotionIcon(collection, zLevel);
                        }
                    }


                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableBlend();
                    this.mc.getTextureManager().bindTexture(DAMAGE_INDICATOR_BG);
                    blit(0, 0, 42, 42, 42, 42, 42, 42);

                    if (DamageIndicatorMod.getConfig().getClient().showEntityName.get()) {
                        String entityName = entityRaytraced.getName().getFormattedText();
                        this.drawString(this.mc.fontRenderer, entityName, 45, 5, 16777215);
                    }

                    this.drawEntityOnGui(20, 20, entityRaytraced);
                }
            }
        }
    }

    private void drawLittlePotionIcon(Collection<EffectInstance> collection, double zLevel) {
        RenderSystem.pushMatrix();
        RenderSystem.scaled(0.5, 0.5, 0.5);

        switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
            case BOTTOM_LEFT:
                RenderSystem.translated(17.5, -82, zLevel);
                break;
            case TOP_LEFT:
                RenderSystem.translated(17.5, 82, zLevel);
                break;
            default:
                RenderSystem.translated(1, 42, zLevel);
                break;
        }

        if (!collection.isEmpty()) {
            RenderSystem.enableBlend();
            int i = 0;
            int j = 0;
            PotionSpriteUploader potionspriteuploader = this.mc.getPotionSpriteUploader();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for (EffectInstance effectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                Effect effect = effectinstance.getPotion();
                if (!effect.shouldRenderHUD(effectinstance)) {
                    continue;
                }
                // Rebind in case previous renderHUDEffect changed texture
                this.mc.getTextureManager().bindTexture(ContainerScreen.INVENTORY_BACKGROUND);

                if (effectinstance.isShowIcon()) {
                    int k = 10;
                    int l = 0;

                    switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
                        case BOTTOM_LEFT:
                            l = 58;
                            if (effect.isBeneficial()) {
                                ++i;
                                k = i * 25 - k - 30;
                            } else {
                                ++j;
                                k = j * 25 - k - 30;
                                l -= 26;
                            }
                            break;
                        case TOP_LEFT:
                            if (effect.isBeneficial()) {
                                ++i;
                                k = i * 25 - k - 30;
                            } else {
                                ++j;
                                k = j * 25 - k - 30;
                                l += 26;
                            }
                            break;
                    }

                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    float f = 1.0F;
                    if (effectinstance.isAmbient()) {
                        this.blit(k, l, 165, 166, 24, 24);
                    } else {
                        this.blit(k, l, 141, 166, 24, 24);
                        if (effectinstance.getDuration() <= 200) {
                            int i1 = 10 - effectinstance.getDuration() / 20;
                            f = MathHelper.clamp((float) effectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float) effectinstance.getDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp((float) i1 / 10.0F * 0.25F, 0.0F, 0.25F);
                        }
                    }

                    float f1 = f;
                    int i1 = k;
                    int j1 = l;

                    TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
                    list.add(() -> {
                        this.mc.getTextureManager().bindTexture(textureatlassprite.func_229241_m_().func_229223_g_());
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, f1);
                        blit(i1 + 3, j1 + 3, this.getBlitOffset(), 18, 18, textureatlassprite);
                    });
                    effectinstance.renderHUDEffect(this, k, l, this.getBlitOffset(), f);
                }
            }
            list.forEach(Runnable::run);
        }

        RenderSystem.popMatrix();
    }

    private void drawPotionIconWithInfo(Collection<EffectInstance> collection, double zLevel) {
        RenderSystem.pushMatrix();
        final int i = 1;
        if (!collection.isEmpty()) {
            RenderSystem.scaled(0.5, 0.5, 0.5);

            switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
                case BOTTOM_LEFT:
                    RenderSystem.translated(1, -72, zLevel);
                    this.j = -this.j;
                    break;
                case TOP_LEFT:
                    RenderSystem.translated(1, 42, zLevel);
                    break;
                default:
                    RenderSystem.translated(1, 42, zLevel);
                    break;
            }

            this.j = 31;
            if (collection.size() > 5) {
                this.j = 131 / (collection.size() - 1);
            }


            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableLighting();

            Iterable<EffectInstance> iterable = Ordering.<EffectInstance>natural().sortedCopy(collection);
            this.drawPotionBackground(i, this.j, iterable);
            this.drawPotionEffectIcon(i, this.j, iterable);
            this.drawPotionInformations(i, this.j, iterable);

        }
        RenderSystem.popMatrix();
    }

    private void drawPotionBackground(int p_214079_1_, int p_214079_2_, Iterable<EffectInstance> p_214079_3_) {
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = 42;

        for (EffectInstance effectinstance : p_214079_3_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(p_214079_1_, i, 0, 166, 140, 32);
            i += p_214079_2_;
        }

    }

    private void drawPotionEffectIcon(int p_214077_1_, int p_214077_2_, Iterable<EffectInstance> effectInstances) {
        PotionSpriteUploader potionspriteuploader = this.mc.getPotionSpriteUploader();
        int i = 42;

        for (EffectInstance effectinstance : effectInstances) {
            if (!effectinstance.getPotion().shouldRender(effectinstance)) {
                continue;
            }
            Effect effect = effectinstance.getPotion();
            TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
            this.mc.getTextureManager().bindTexture(textureatlassprite.func_229241_m_().func_229223_g_());
            blit(p_214077_1_ + 6, i + 7, this.getBlitOffset(), 18, 18, potionspriteuploader.getSprite(effect));
            i += p_214077_2_;
        }

    }

    private void drawPotionInformations(int p_214078_1_, int p_214078_2_, Iterable<EffectInstance> p_214078_3_) {
        int i = 42;

        for (EffectInstance effectinstance : p_214078_3_) {
            if (!effectinstance.getPotion().shouldRender(effectinstance)) {
                continue;
            }
            effectinstance.getPotion().renderHUDEffect(effectinstance, this, 0, 0, this.getBlitOffset(), 0);
            if (!effectinstance.getPotion().shouldRenderInvText(effectinstance)) {
                i += p_214078_2_;
                continue;
            }
            String s = I18n.format(effectinstance.getPotion().getName());
            if (effectinstance.getAmplifier() >= 1 && effectinstance.getAmplifier() <= 9) {
                s = s + ' ' + I18n.format("enchantment.level." + (effectinstance.getAmplifier() + 1));
            }

            this.mc.fontRenderer.drawStringWithShadow(s, (float) (p_214078_1_ + 10 + 18), (float) (i + 6), 16777215);
            String s1 = EffectUtils.getPotionDurationString(effectinstance, 1.0F);
            this.mc.fontRenderer.drawStringWithShadow(s1, (float) (p_214078_1_ + 10 + 18), (float) (i + 6 + 10), 8355711);
            i += p_214078_2_;
        }

    }

    private void drawEntityOnGui(int x, int y, LivingEntity entityIn) {
        if (entityIn == null) {
            return;
        }

        if (DamageIndicatorMod.getConfig().getClient().showEntityStats.get()) {
            this.y = y;

            if (DamageIndicatorMod.getConfig().getClient().showMinimalStats.get()) {
                int currentHealth = MathHelper.ceil(entityIn.getHealth());
                float maxHealth = entityIn.getMaxHealth();

                this.getFontRenderer().drawStringWithShadow(TextFormatting.BOLD + String.format("%s/%s", currentHealth, MathHelper.ceil(maxHealth)), x + 38, y - 5, Colors.DARK_RED.getColor());
                this.getFontRenderer().drawStringWithShadow(TextFormatting.BOLD + String.format("%s", entityIn.getTotalArmorValue()), x + 38, y + 9, Colors.WHITE.getColor());

                this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);

                this.blit(x + 25, y - 5, 16, 0, 9, 9);
                this.blit(x + 25, y - 5, 16 + 18 + 36 / 2, 0, 9, 9);
                this.blit(x + 25, y + 8, 34, 9, 9, 9);
            } else {
                // Draw hearts
                int currentHealth = MathHelper.ceil(entityIn.getHealth());
                float maxHealth = entityIn.getMaxHealth();

                int absorptionAmount = MathHelper.ceil(entityIn.getAbsorptionAmount());
                int remainingAbsorption = absorptionAmount;

                int heartsRow = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
                int j2 = Math.max(10 - (heartsRow - 2), 3);

                for (int currentHeartBeingDrawn = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F) - 1; currentHeartBeingDrawn >= 0; --currentHeartBeingDrawn) {
                    final int texturePosX = 16;
                    final int flashingHeartOffset = 0;

                    final int foeOffset = 18;

                    int rowsOfHearts = MathHelper.ceil((float) (currentHeartBeingDrawn + 1) / 10.0F) - 1;
                    int heartToDrawX = x + 25 + currentHeartBeingDrawn % 10 * 8;
                    int heartToDrawY = y - 5 + rowsOfHearts * j2;

                    final int hardcoreModeOffset = 0;

                    this.blit(heartToDrawX, heartToDrawY, 16 + flashingHeartOffset * 9, 9 * hardcoreModeOffset, 9, 9);

                    if (remainingAbsorption > 0) {
                        if (remainingAbsorption == absorptionAmount && absorptionAmount % 2 == 1) {
                            this.blit(heartToDrawX, heartToDrawY, texturePosX + 153, 9 * hardcoreModeOffset, 9, 9);
                            --remainingAbsorption;
                        } else {
                            this.blit(heartToDrawX, heartToDrawY, texturePosX + 144, 9 * hardcoreModeOffset, 9, 9);
                            remainingAbsorption -= 2;
                        }
                    } else {
                        if (currentHeartBeingDrawn * 2 + 1 < currentHealth) {
                            this.blit(heartToDrawX, heartToDrawY, texturePosX + foeOffset + 36 / 2, 9 * hardcoreModeOffset, 9, 9);
                        }

                        if (currentHeartBeingDrawn * 2 + 1 == currentHealth) {
                            this.blit(heartToDrawX, heartToDrawY, texturePosX + foeOffset + 45, 9 * hardcoreModeOffset, 9, 9);
                        }
                    }
                }
                y += (heartsRow - 1) * j2 + 10;

                // Draw armor
                int armor = entityIn.getTotalArmorValue();
                for (int i = 0; i < 10; ++i) {
                    if (armor > 0) {
                        int armorIconX = x + 25 + i * 8;

                        if (i * 2 + 1 < armor) {
                            this.blit(armorIconX, y - 5, 34, 9, 9, 9);
                        }

                        if (i * 2 + 1 == armor) {
                            this.blit(armorIconX, y - 5, 25, 9, 9, 9);
                        }

                        if (i * 2 + 1 > armor) {
                            this.blit(armorIconX, y - 5, 16, 9, 9, 9);
                        }
                    }
                }
            }
        }


        this.glX = (float) (x + this.scaledWidth / 2);
        this.defineScale(entityIn);

        this.prevYawOffset = entityIn.renderYawOffset;
        this.prevYaw = entityIn.rotationYaw;
        this.prevPitch = entityIn.rotationPitch;
        this.prevYawHead = entityIn.rotationYawHead;
        this.prevPrevYahHead = entityIn.prevRotationYawHead;
        entityIn.renderYawOffset = 0.0f;
        entityIn.rotationYaw = 0.0f;
        entityIn.rotationPitch = 0.0f;
        entityIn.rotationYawHead = 0.0f;
        entityIn.prevRotationYawHead = 0.0f;

        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) this.glX, (float) this.glY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack lvt_8_1_ = new MatrixStack();
        lvt_8_1_.func_227861_a_(0.0D, -3.0D, 1000.0D);
        lvt_8_1_.func_227862_a_((float) this.scale, (float) this.scale, (float) this.scale);
        lvt_8_1_.func_227863_a_(Vector3f.field_229180_c_.func_229187_a_(-175.0F));
        Quaternion lvt_9_1_ = Vector3f.field_229183_f_.func_229187_a_(180.0F);
        Quaternion lvt_10_1_ = Vector3f.field_229179_b_.func_229187_a_(1 * 20.0F);
        lvt_9_1_.multiply(lvt_10_1_);
        lvt_8_1_.func_227863_a_(lvt_9_1_);

        EntityRendererManager lvt_16_1_ = Minecraft.getInstance().getRenderManager();
        lvt_10_1_.conjugate();
        lvt_16_1_.func_229089_a_(lvt_10_1_);
        lvt_16_1_.setRenderShadow(false);
        IRenderTypeBuffer.Impl lvt_17_1_ = Minecraft.getInstance().func_228019_au_().func_228487_b_();
        lvt_16_1_.func_229084_a_(entityIn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, lvt_8_1_, lvt_17_1_, 15728880);
        lvt_17_1_.func_228461_a_();
        lvt_16_1_.setRenderShadow(true);
        entityIn.renderYawOffset = this.prevYawOffset;
        entityIn.rotationYaw = this.prevYaw;
        entityIn.rotationPitch = this.prevPitch;
        entityIn.prevRotationYawHead = this.prevYawHead;
        entityIn.rotationYawHead = this.prevPrevYahHead;
        RenderSystem.popMatrix();
        RenderSystem.popMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableTexture();
    }

    private void defineScale(LivingEntity entityIn) {
        int scaleY = MathHelper.ceil(20 / entityIn.getSize(Pose.STANDING).height);
        int scaleX = MathHelper.ceil(18 / entityIn.getSize(Pose.STANDING).width);
        this.scale = Math.min(scaleX, scaleY);
        this.glY = (float) (this.y + (this.scaledHeight / 2 + 20 / 2) + 5);
        if (entityIn instanceof GhastEntity) {
            this.glY -= 10;
            this.scale = 3;
        } else if (entityIn instanceof EndermanEntity) {
            this.glY += 2;
            this.scale = 10;
        } else if (entityIn instanceof CaveSpiderEntity) {
            this.glY -= 5;
            this.scale = 18;
        } else if (entityIn instanceof WolfEntity) {
            WolfEntity wolfEntity = (WolfEntity) entityIn;
            if (wolfEntity.isSitting()) {
                this.glY -= 2.5F;
            }
        } else if (entityIn instanceof CatEntity) {
            CatEntity catEntity = (CatEntity) entityIn;
            if (catEntity.isSitting()) {
                this.glY -= 4.5F;
            }
        }
    }

}