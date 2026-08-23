package at.petrak.hexcasting.client.entity;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.world.level.Level;

public class WallScrollRenderState extends DisplayEntityRenderState {
    public HexPattern pattern;
    public boolean showStrokeOrder;
    public boolean isAncient;
    public int blockSize;
    public Level level;

    public boolean getShowsStrokeOrder() {
        return showStrokeOrder;
    }

    @Override
    public boolean hasSubState() {
        return false;
    }
}
