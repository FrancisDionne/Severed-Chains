package legend.game.combat;

import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import legend.game.combat.bent.BattleEntity27c;
import legend.game.net.ActionPacket;
import legend.game.net.BattleAction;
import legend.game.net.NetServer;
import legend.game.net.StartTurnPacket;
import legend.game.scripting.ScriptState;

public class ServerBattleController implements BattleController {
  private final NetServer server;

  private final Int2ObjectMap<ChannelHandlerContext> players = new Int2ObjectOpenHashMap<>();

  private ScriptState<? extends BattleEntity27c> currentBent;

  /** Actions sent from another client */
  private BattleAction action;

  public ServerBattleController(final NetServer server) {
    this.server = server;
  }

  public void addPlayer(final int charSlot, final ChannelHandlerContext player) {
    this.players.put(charSlot, player);
  }

  private boolean isMe(final int charSlot) {
    return !this.players.containsKey(charSlot);
  }

  @Override
  public void startTurn(final ScriptState<? extends BattleEntity27c> bent) {
    this.currentBent = bent;

    if(!this.isMe(bent.innerStruct_00.charSlot_276)) {
      System.out.println("Sending turn " + bent.name);
      this.players.get(bent.innerStruct_00.charSlot_276).writeAndFlush(new StartTurnPacket(bent.index));
    }
  }

  @Override
  public void action(final BattleAction action) {
    final ActionPacket packet = new ActionPacket(action);
    for(final ChannelHandlerContext player : this.players.values()) {
      player.writeAndFlush(packet);
    }
  }

  @Override
  public boolean canTakeAction() {
    return this.isMe(this.currentBent.innerStruct_00.charSlot_276);
  }

  @Override
  public void handleAction(final BattleAction action) {
    this.action = action;
  }

  @Override
  public BattleAction getAction() {
    final BattleAction action = this.action;
    this.action = null;
    return action;
  }
}
