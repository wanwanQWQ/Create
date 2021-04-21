package com.simibubi.create.content.optics.mirror;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.simibubi.create.content.optics.Beam;
import com.simibubi.create.content.optics.behaviour.AbstractRotatedLightRelayBehaviour;
import com.simibubi.create.foundation.collision.Matrix3d;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;

public class MirrorBehaviour extends AbstractRotatedLightRelayBehaviour<MirrorTileEntity> {
	public static final BehaviourType<MirrorBehaviour> TYPE = new BehaviourType<>();

	public MirrorBehaviour(MirrorTileEntity te) {
		super(te);
	}

	private Vector3d getReflectionAngle(Vector3d inputAngle) {
		inputAngle = inputAngle.normalize();
		Direction.Axis axis = handler.getAxis();
		Vector3d normal;
		if (axis.isHorizontal())
			normal = new Matrix3d().asIdentity()
					.asAxisRotation(axis, AngleHelper.rad(angle))
					.transform(VecHelper.UP);
		else
			normal = new Matrix3d().asIdentity()
					.asAxisRotation(axis, AngleHelper.rad(-angle))
					.transform(VecHelper.SOUTH);

		return inputAngle.subtract(normal.scale(2 * inputAngle.dotProduct(normal)));
	}


	@Nonnull
	@Override
	public Direction getBeamRotationAround() {
		return Direction.getFacingFromAxisDirection(handler.getAxis(), Direction.AxisDirection.POSITIVE);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Override
	protected Stream<Beam> safeConstructSubBeamsFor(Beam beam) {
		Vector3d inDir = beam.getDirection();
		if (inDir == null)
			return Stream.empty();

		return Stream.of(constructOutBeam(beam, getReflectionAngle(inDir).normalize()));
	}
}
