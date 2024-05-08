package legend.game.models;

public class Polygon {
  public final Vertex[] vertices;
  public int clut;
  public int tpage;

  public Polygon(final int vertexCount) {
    this.vertices = new Vertex[vertexCount];

    for(int i = 0; i < vertexCount; i++) {
      this.vertices[i] = new Vertex();
    }
  }
}
