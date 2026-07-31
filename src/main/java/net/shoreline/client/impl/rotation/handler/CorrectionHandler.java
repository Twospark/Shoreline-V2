package net.shoreline.client.impl.rotation.handler;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.shoreline.client.impl.modules.client.RotationsModule;

public class CorrectionHandler
{
    private final RotationsModule rotationsConfig = RotationsModule.INSTANCE;

    public void correctInput(ClientInput clientInput, float yawDelta)
    {
        final Input input = clientInput.keyPresses;
        int x = (input.right() ? 1 : 0)   - (input.left() ? 1 : 0);
        int z = (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0);

        InputDirection direction = InputDirection.fromInput(x, z);
        if (direction == null)
        {
            return; // no correction needed.
        }

        int steps = Math.round(yawDelta / 45f);
        direction = direction.rotate(steps);
        Input corrected = new Input(
                direction.forward, direction.backward,
                direction.left, direction.right,
                input.jump(), input.shift(), input.sprint());

        float sideways = corrected.left() == corrected.right()
                ? 0f
                : (corrected.left() ? 1f : -1f);
        float forwards = corrected.forward() == corrected.backward()
                ? 0f
                : (corrected.forward() ? 1f : -1f);
        Vec2 vector = new Vec2(sideways, forwards);

        clientInput.keyPresses = corrected;
        clientInput.moveVector = rotationsConfig.getNormalize().getValue()
                ? vector.normalized()
                : vector;
    }

    private enum InputDirection
    {
        // KEEP ORDER!!!
        FORWARD(true, false, false, false),
        FORWARD_R(true, false, false, true),
        RIGHT(false, false, false, true),
        BACKWARD_R(false, true, false, true),
        BACKWARD(false, true, false, false),
        BACKWARD_L(false, true, true, false),
        LEFT(false, false, true, false),
        FORWARD_L(true, false, true, false);

        final boolean forward, backward, left, right;

        InputDirection(boolean forward, boolean backward, boolean left, boolean right)
        {
            this.forward = forward;
            this.backward = backward;
            this.left = left;
            this.right = right;
        }

        private InputDirection rotate(int steps)
        {
            return values()[Math.floorMod(ordinal() + steps, values().length)];
        }

        static InputDirection fromInput(int x, int z)
        {
            if (x == 0 && z == 0)
            {
                return null;
            }

            if (z > 0)
            {
                return x == 0 ? FORWARD  : x > 0 ? FORWARD_R  : FORWARD_L;
            }

            if (z < 0)
            {
                return x == 0 ? BACKWARD : x > 0 ? BACKWARD_R : BACKWARD_L;
            }

            return x > 0 ? RIGHT : LEFT;
        }
    }
}
