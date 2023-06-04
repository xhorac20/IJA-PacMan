package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @param map    The map variable stores the current state of the map
 * @param player The variable player stores the current state of the player
 * @param ghosts The ghosts variable stores the current state of the ghost list
 */ // The GameSnapshot class is used to save the current state of the game, including the map, player, and ghost list
public record GameSnapshot(Map map, Player player, List<Ghost> ghosts) implements Serializable {
    public GameSnapshot(Map map, Player player, List<Ghost> ghosts) {
        this.map = new Map(map);
        this.player = new Player(player);
        this.ghosts = new ArrayList<>();
        for (Ghost ghost : ghosts) {
            this.ghosts.add(new Ghost(ghost));
        }
    }

    public Map getMapCopy() {
        return new Map(this.map.getMap());
    }

    public Player getPlayerCopy() {
        return new Player(this.player.getX(), this.player.getY(), getMapCopy());
    }

    public List<Ghost> getGhostListCopy() {
        List<Ghost> ghostCopyList = new ArrayList<>();
        for (Ghost ghost : this.ghosts) {
            ghostCopyList.add(new Ghost(ghost.getX(), ghost.getY(), getMapCopy()));
        }
        return ghostCopyList;
    }

}