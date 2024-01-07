package legend.core.opengl;

import org.joml.Vector3f;

public class FontShaderOptions implements ShaderOptions<FontShaderOptions> {
  private final Shader<FontShaderOptions>.UniformVec3 colourUniform;

  public final Vector3f colour = new Vector3f();

  public FontShaderOptions(final Shader<FontShaderOptions>.UniformVec3 colourUniform) {
    this.colourUniform = colourUniform;
  }

  public FontShaderOptions colour(final Vector3f colour) {
    this.colourUniform.set(colour);
    return this;
  }
}
