package legend.core.opengl;

import legend.game.types.Translucency;

import javax.annotation.Nullable;

public class TextObj implements Obj {
  private final Mesh mesh;

  public TextObj(final Mesh mesh) {
    this.mesh = mesh;
  }

  @Override
  public boolean shouldRender(@Nullable final Translucency translucency) {
    return translucency == null;
  }

  @Override
  public void render(@Nullable final Translucency translucency) {
    this.mesh.draw();
  }

  @Override
  public void delete() {
    this.mesh.delete();
  }
}
