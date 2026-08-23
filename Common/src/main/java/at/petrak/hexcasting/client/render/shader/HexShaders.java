package at.petrak.hexcasting.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.function.Consumer;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

// https://github.com/VazkiiMods/Botania/blob/3a43accc2fbc439c9f2f00a698f8f8ad017503db/Common/src/main/java/vazkii/botania/client/core/helper/CoreShaders.java
public class HexShaders {
    private static final ShaderProgram grayscale = new ShaderProgram(
            modLoc("core/hexcasting__grayscale"),
            DefaultVertexFormat.NEW_ENTITY,
            ShaderDefines.EMPTY
    );

    public static ShaderProgram grayscale() {
        return grayscale;
    }
}
