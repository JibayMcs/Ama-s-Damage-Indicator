package fr.zeamateis.damage_indicator.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

//TODO Fix render bug in multiplayer, black particle
@OnlyIn(Dist.CLIENT)
public class ParticleNumeric extends Particle {

    private final int customColor;
    private double number;

    ParticleNumeric(World worldIn, double p_i1211_2_, double p_i1211_4_, double p_i1211_6_, double p_i1211_8_, double p_i1211_10_, double p_i1211_12_) {
        this(0, worldIn, p_i1211_2_, p_i1211_4_, p_i1211_6_, p_i1211_8_, p_i1211_10_, p_i1211_12_, 0);
    }

    ParticleNumeric(double numberIn, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i46354_8_, double p_i46354_10_, double p_i46354_12_, int colorIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.particleGravity = 0.1F;
        this.setSize(3.0F, 3.0F);
        this.maxAge = 12;
        this.number = numberIn;
        this.customColor = colorIn;
    }

    @Override
    public void func_225606_a_(IVertexBuilder iVertexBuilder, ActiveRenderInfo activeRenderInfo, float partialTicks) {
        float rotationYaw = (-Minecraft.getInstance().player.rotationYaw);
        float rotationPitch = Minecraft.getInstance().player.rotationPitch;
        Vec3d lvt_4_1_ = activeRenderInfo.getProjectedView();
        float posX = (float) (MathHelper.lerp((double) partialTicks, this.prevPosX, this.posX) - lvt_4_1_.getX());
        float posY = (float) (MathHelper.lerp((double) partialTicks, this.prevPosY, this.posY) - lvt_4_1_.getY());
        float posZ = (float) (MathHelper.lerp((double) partialTicks, this.prevPosZ, this.posZ) - lvt_4_1_.getZ());

        RenderSystem.pushMatrix();

        GL11.glTranslated(posX, posY - 0.5F, posZ);
        GL11.glRotatef(rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);

        GL11.glScaled(-1.0, -1.0, 1.0);
        GL11.glScaled(this.getBoundingBox().getXSize() * 0.04D, this.getBoundingBox().getYSize() * 0.04D, this.getBoundingBox().getZSize() * 0.04D);
        GL11.glScaled(1.0, 1.0, 1.0);


        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

        RenderSystem.disableColorMaterial();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        fontRenderer.drawStringWithShadow(String.valueOf((int) this.number), 0, 0, this.customColor);

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableLighting();
        RenderSystem.enableColorMaterial();

        RenderSystem.popMatrix();

        this.setSize(1.0F, 1.0F);
    }


    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += (float) Math.PI * 0.2 * 2.0F;
        if (this.onGround) {
            this.prevParticleAngle = this.particleAngle = 0.0F;
        }

        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionY -= 0.003D;
        this.motionY = Math.max(this.motionY, -0.14D);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }


    public void setNumber(double numberIn) {
        this.number = numberIn;
    }

}