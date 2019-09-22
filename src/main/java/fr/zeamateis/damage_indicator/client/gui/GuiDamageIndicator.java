package fr.zeamateis.damage_indicator.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import fr.zeamateis.damage_indicator.DamageIndicatorMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
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
        int zLevel = -90;
        this.mc.gameRenderer.getMouseOver(partialTicks);


        if (this.mc.pointedEntity != null) {
            if (this.mc.pointedEntity instanceof LivingEntity) {

                LivingEntity entityRaytraced = (LivingEntity) this.mc.pointedEntity;

                GlStateManager.pushMatrix();

                if (entityRaytraced.isAlive()) {

                    switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
                        case BOTTOM_LEFT:
                            GlStateManager.translated(0, this.mc.mainWindow.getScaledHeight() - 55, zLevel);
                            break;
                        case TOP_LEFT:
                            GlStateManager.translated(0, 0, zLevel);
                            break;
                        default:
                            GlStateManager.translated(0, 0, zLevel);
                            break;
                    }

                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.enableBlend();
                    this.mc.getTextureManager().bindTexture(DAMAGE_INDICATOR_BG);
                    blit(0, 0, 42, 42, 42, 42, 42, 42);

                    if (DamageIndicatorMod.getConfig().getClient().showEntityName.get()) {
                        String entityName = entityRaytraced.getName().getFormattedText();
                        this.drawString(this.mc.fontRenderer, entityName, 45, 5, 16777215);
                    }

                    this.drawEntityOnGui(20, 20, entityRaytraced);

                    if (DamageIndicatorMod.getConfig().getClient().showEntityActivesPotions.get()) {
                        Collection<EffectInstance> collection = entityRaytraced.getActivePotionEffects();

                        if (!DamageIndicatorMod.getConfig().getClient().showPotionInfo.get()) {
                            this.drawPotionIconWithInfo(collection, zLevel);
                        } else {
                            this.drawLittlePotionIcon(collection, zLevel);
                        }
                    }

                }
                GlStateManager.popMatrix();

            }
        }
    }

    private void drawLittlePotionIcon(Collection<EffectInstance> collection, double zLevel) {
        GlStateManager.pushMatrix();
        GlStateManager.scaled(0.5, 0.5, 0.5);

        switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
            case BOTTOM_LEFT:
                GlStateManager.translated(17.5, -82, zLevel);
                break;
            case TOP_LEFT:
                GlStateManager.translated(17.5, 82, zLevel);
                break;
            default:
                GlStateManager.translated(1, 42, zLevel);
                break;
        }

        if (!collection.isEmpty()) {
            GlStateManager.enableBlend();
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

                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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

                    float f_f = f;
                    int k_f = k;
                    int l_f = l;

                    TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
                    list.add(() -> {
                        GlStateManager.color4f(1.0F, 1.0F, 1.0F, f_f);
                        blit(k_f + 3, l_f + 3, this.blitOffset, 18, 18, textureatlassprite);
                    });
                    effect.renderHUDEffect(effectinstance, this, k, l, this.blitOffset, f);

                }
            }

            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_EFFECTS_TEXTURE);
            list.forEach(Runnable::run);
        }

        GlStateManager.popMatrix();
    }

    private void drawPotionIconWithInfo(Collection<EffectInstance> collection, double zLevel) {
        GlStateManager.pushMatrix();
        int i = 1;
        if (!collection.isEmpty()) {
            GlStateManager.scaled(0.5, 0.5, 0.5);

            switch (DamageIndicatorMod.getConfig().getClient().overlayPosition.get()) {
                case BOTTOM_LEFT:
                    GlStateManager.translated(1, -72, zLevel);
                    this.j = -this.j;
                    break;
                case TOP_LEFT:
                    GlStateManager.translated(1, 42, zLevel);
                    break;
                default:
                    GlStateManager.translated(1, 42, zLevel);
                    break;
            }

            this.j = 31;
            if (collection.size() > 5) {
                this.j = 131 / (collection.size() - 1);
            }


            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();

            Iterable<EffectInstance> iterable = Ordering.<EffectInstance>natural().sortedCopy(collection);
            this.drawPotionBackground(i, this.j, iterable);
            this.drawPotionEffectIcon(i, this.j, iterable);
            this.drawPotionInformations(i, this.j, iterable);

        }
        GlStateManager.popMatrix();
    }

    private void drawPotionBackground(int p_214079_1_, int p_214079_2_, Iterable<EffectInstance> p_214079_3_) {
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = 42;

        for (EffectInstance effectinstance : p_214079_3_) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(p_214079_1_, i, 0, 166, 140, 32);
            i += p_214079_2_;
        }

    }

    private void drawPotionEffectIcon(int p_214077_1_, int p_214077_2_, Iterable<EffectInstance> p_214077_3_) {
        this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_EFFECTS_TEXTURE);
        PotionSpriteUploader potionspriteuploader = this.mc.getPotionSpriteUploader();
        int i = 42;

        for (EffectInstance effectinstance : p_214077_3_) {
            if (!effectinstance.getPotion().shouldRender(effectinstance)) {
                continue;
            }
            Effect effect = effectinstance.getPotion();
            blit(p_214077_1_ + 6, i + 7, this.blitOffset, 18, 18, potionspriteuploader.getSprite(effect));
            i += p_214077_2_;
        }

    }

    private void drawPotionInformations(int p_214078_1_, int p_214078_2_, Iterable<EffectInstance> p_214078_3_) {
        int i = 42;

        for (EffectInstance effectinstance : p_214078_3_) {
            if (!effectinstance.getPotion().shouldRender(effectinstance)) {
                continue;
            }
            effectinstance.getPotion().renderHUDEffect(effectinstance, this, 0, 0, this.blitOffset, 0);
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

            // Draw hearts
            this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            int currentHealth = MathHelper.ceil(entityIn.getHealth());

            int absorptionAmount = MathHelper.ceil(entityIn.getAbsorptionAmount());
            int remainingAbsorption = absorptionAmount;

            float maxHealth = entityIn.getMaxHealth();

            int heartsRow = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
            int j2 = Math.max(10 - (heartsRow - 2), 3);

            for (int currentHeartBeingDrawn = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F) - 1; currentHeartBeingDrawn >= 0; --currentHeartBeingDrawn) {
                int texturePosX = 16;
                int flashingHeartOffset = 0;

                int foeOffset = 0;

                foeOffset = 18;

                int rowsOfHearts = MathHelper.ceil((float) (currentHeartBeingDrawn + 1) / 10.0F) - 1;
                int heartToDrawX = x + 25 + currentHeartBeingDrawn % 10 * 8;
                int heartToDrawY = y - 5 + rowsOfHearts * j2;

                int hardcoreModeOffset = 0;

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

            y += 10;
        }


        this.glX = (float) x + this.scaledWidth / 2;
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

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();

        GlStateManager.translatef(this.glX, this.glY, 50.0F);
        GlStateManager.scalef((float) (-this.scale), (float) this.scale, (float) this.scale);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-100.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(0.0f, 1.0F, 0.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entityIn, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        entityIn.renderYawOffset = this.prevYawOffset;
        entityIn.rotationYaw = this.prevYaw;
        entityIn.rotationPitch = this.prevPitch;
        entityIn.rotationYawHead = this.prevYawHead;
        entityIn.prevRotationYawHead = this.prevPrevYahHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    private void defineScale(LivingEntity entityIn) {
        int scaleY = MathHelper.ceil(20 / entityIn.getSize(Pose.STANDING).height);
        int scaleX = MathHelper.ceil(18 / entityIn.getSize(Pose.STANDING).width);
        this.scale = Math.min(scaleX, scaleY);
        this.glY = (float) this.y + (this.scaledHeight / 2 + 20 / 2) + 5;
        if (entityIn instanceof GhastEntity) {
            this.glY -= 10;
        } else if (entityIn instanceof EndermanEntity) {
            this.scale = 10;
        } else if (entityIn instanceof CaveSpiderEntity) {
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