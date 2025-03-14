package legend.game.combat.particles;

import legend.core.memory.Method;
import legend.game.combat.effects.EffectManagerData6c;
import legend.game.combat.effects.EffectManagerParams;

import static legend.game.combat.Battle.seed_800fa754;

public class Instance33 extends ParticleEffectInstance94 {
  public Instance33(final int index, final ParticleEffectData98 particle) {
    super(index, particle);
  }

  @Method(0x800fff30L)
  @Override
  protected void initType() {
    this.particleVelocity_58.x = seed_800fa754.nextInt(769) + 256;
  }

  @Override
  protected void tickType(final EffectManagerData6c<EffectManagerParams.ParticleType> manager) {

  }

  @Override
  protected void beforeRender(final EffectManagerData6c<EffectManagerParams.ParticleType> manager) {

  }
}
