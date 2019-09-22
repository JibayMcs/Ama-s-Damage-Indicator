package fr.zeamateis.damage_indicator.client.particle.type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NumericParticleType extends ParticleType<NumericParticleType> implements IParticleData {
	public static final IParticleData.IDeserializer<NumericParticleType> DESERIALIZER = new IParticleData.IDeserializer<NumericParticleType>() {
		@Override
		public NumericParticleType deserialize(ParticleType<NumericParticleType> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			return (NumericParticleType)particleTypeIn;
		}

		@Override
		public NumericParticleType read(ParticleType<NumericParticleType> particleTypeIn, PacketBuffer buffer) {
			return (NumericParticleType)particleTypeIn;
		}
	};

	private double number;
	private int color;

	public NumericParticleType(boolean alwaysShow) {
		super(alwaysShow, DESERIALIZER);
	}

	@Override
	public void write(PacketBuffer buffer) {
		buffer.writeDouble(number);
		buffer.writeInt(color);
	}

	@Override
	public String getParameters() {
		return getType().getRegistryName() + " " + number + " " + color;
	}

	@Override
	public ParticleType<NumericParticleType> getType() {
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public double getNumber() {
		return number;
	}

	public void setNumber(double numberIn) {
		number = numberIn;
	}

	@OnlyIn(Dist.CLIENT)
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}