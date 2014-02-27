/**
 * Copyright 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package com.jogamp.graph.curve.opengl;

import javax.media.opengl.GL2ES2;

import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.graph.curve.Region;

/** A GLRegion is the OGL binding of one or more OutlineShapes
 *  Defined by its vertices and generated triangles. The Region
 *  defines the final shape of the OutlineShape(s), which shall produced a shaded
 *  region on the screen.
 *
 *  Implementations of the GLRegion shall take care of the OGL
 *  binding of the depending on its context, profile.
 *
 * @see Region, RegionFactory, OutlineShape
 */
public abstract class GLRegion extends Region {

    /**
     * Create a GLRegion using the passed render mode
     *
     * <p> In case {@link Region#VBAA_RENDERING_BIT} is being requested the default texture unit
     * {@link Region#TWO_PASS_DEFAULT_TEXTURE_UNIT} is being used.</p>
     *
     * @param rs the RenderState to be used
     * @param renderModes bit-field of modes, e.g. {@link Region#VARIABLE_CURVE_WEIGHT_BIT}, {@link Region#VBAA_RENDERING_BIT}
     */
    public static GLRegion create(int renderModes) {
        return Region.create(renderModes);
    }

    protected GLRegion(int renderModes) {
        super(renderModes);
    }

    /**
     * Updates a graph region by updating the ogl related
     * objects for use in rendering if {@link #isDirty()}.
     * <p>Allocates the ogl related data and initializes it the 1st time.<p>
     * <p>Called by {@link #draw(GL2ES2, RenderState, int, int, int)}.</p>
     */
    protected abstract void update(GL2ES2 gl, RegionRenderer renderer);

    protected abstract void destroyImpl(GL2ES2 gl, RegionRenderer renderer);

    protected abstract void clearImpl(final GL2ES2 gl, final RegionRenderer renderer);

    /**
     * Clears all data, i.e. triangles, vertices etc.
     */
    public void clear(final GL2ES2 gl, final RegionRenderer renderer) {
        clearImpl(gl, renderer);
        clearImpl();
    }

    /**
     * Delete and clear the associated OGL objects.
     */
    public final void destroy(GL2ES2 gl, RegionRenderer renderer) {
        clear(gl, renderer);
        destroyImpl(gl, renderer);
    }

    /**
     * Renders the associated OGL objects specifying
     * current width/hight of window for multi pass rendering
     * of the region.
     * @param matrix current {@link PMVMatrix}.
     * @param renderer the {@link RegionRenderer} to be used
     * @param texWidth desired texture width for multipass-rendering.
     *        The actual used texture-width is written back when mp rendering is enabled, otherwise the store is untouched.
     */
    public final void draw(GL2ES2 gl, RegionRenderer renderer, int[/*1*/] texWidth) {
        if(isDirty()) {
            update(gl, renderer);
            setDirty(false);
        }
        drawImpl(gl, renderer, texWidth);
    }

    protected abstract void drawImpl(GL2ES2 gl, RegionRenderer renderer, int[/*1*/] texWidth);
}
