package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;
import sun.misc.Perf;
import uwu.noctura.Noctura;
import uwu.noctura.module.impl.other.Performance;

public class VboRenderList extends ChunkRenderContainer
{
    public void renderChunkLayer(EnumWorldBlockLayer layer) {
        boolean opt = Noctura.INSTANCE.getModuleManager().getModule(Performance.class).optimizeChunkLayer.isEnabled() && Noctura.INSTANCE.getModuleManager().getModule(Performance.class).isToggled();
        if(opt){
            if (this.initialized && !this.renderChunks.isEmpty()) {
                VertexBuffer vertexBuffer = null;
                for (RenderChunk renderchunk : this.renderChunks) {
                    VertexBuffer currentBuffer = renderchunk.getVertexBufferByLayer(layer.ordinal());
                    if (currentBuffer != vertexBuffer) {
                        if (vertexBuffer != null) {
                            vertexBuffer.unbindBuffer();
                        }
                        vertexBuffer = currentBuffer;
                        vertexBuffer.bindBuffer();
                        this.setupArrayPointers();
                    }

                    GlStateManager.pushMatrix();
                    this.preRenderChunk(renderchunk);
                    renderchunk.multModelviewMatrix();
                    vertexBuffer.drawArrays(7);
                    GlStateManager.popMatrix();
                }

                if (vertexBuffer != null) {
                    vertexBuffer.unbindBuffer();
                }

                OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
                GlStateManager.resetColor();
                this.renderChunks.clear();
            }
        }else{
            if (this.initialized)
            {
                for (RenderChunk renderchunk : this.renderChunks)
                {
                    VertexBuffer vertexbuffer = renderchunk.getVertexBufferByLayer(layer.ordinal());
                    GlStateManager.pushMatrix();
                    this.preRenderChunk(renderchunk);
                    renderchunk.multModelviewMatrix();
                    vertexbuffer.bindBuffer();
                    this.setupArrayPointers();
                    vertexbuffer.drawArrays(7);
                    GlStateManager.popMatrix();
                }

                OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
                GlStateManager.resetColor();
                this.renderChunks.clear();
            }
        }
    }

    private void setupArrayPointers()
    {
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
