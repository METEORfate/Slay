package com.course.slay.domain.run;

import com.course.slay.domain.Player;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.character.CharacterCatalog;
import com.course.slay.domain.character.PlayableCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunState {
    private final PlayableCharacter playableCharacter;
    private final Player player;
    private final ExpeditionMap map;
    private final List<String> runLog;
    private final List<Card> shopCards;
    private int gold;
    private RunPhase phase;
    private MapNode currentNode;

    public RunState(Player player, List<Card> deck, ExpeditionMap map) {
        this(CharacterCatalog.defaultCharacter(), player, deck, map);
    }

    public RunState(PlayableCharacter playableCharacter, Player player, List<Card> deck, ExpeditionMap map) {
        this.playableCharacter = Objects.requireNonNull(playableCharacter, "playableCharacter");
        this.player = Objects.requireNonNull(player, "player");
        this.player.setDeck(Objects.requireNonNull(deck, "deck"));
        this.map = Objects.requireNonNull(map, "map");
        this.runLog = new ArrayList<>();
        this.shopCards = new ArrayList<>();
        this.phase = RunPhase.MAP;
    }

    public PlayableCharacter getPlayableCharacter() {
        return playableCharacter;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Card> getDeck() {
        return player.getDeck();
    }

    public ExpeditionMap getMap() {
        return map;
    }

    public List<String> getRunLog() {
        return runLog;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public List<Card> getShopCards() {
        return shopCards;
    }

    public void setShopCards(List<Card> cards) {
        shopCards.clear();
        shopCards.addAll(Objects.requireNonNull(cards, "cards"));
    }

    public Card removeShopCard(int index) {
        if (index < 0 || index >= shopCards.size()) {
            throw new IndexOutOfBoundsException("shop card index out of range");
        }
        return shopCards.remove(index);
    }

    public void clearShopCards() {
        shopCards.clear();
    }

    public RunPhase getPhase() {
        return phase;
    }

    public void setPhase(RunPhase phase) {
        this.phase = Objects.requireNonNull(phase, "phase");
    }

    public MapNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(MapNode currentNode) {
        this.currentNode = currentNode;
    }

    public void addLog(String message) {
        runLog.add(Objects.requireNonNull(message, "message"));
    }
}
