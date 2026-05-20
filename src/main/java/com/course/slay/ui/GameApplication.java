package com.course.slay.ui;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardType;
import com.course.slay.domain.card.CardVisualEffect;
import com.course.slay.domain.card.DeckSummary;
import com.course.slay.domain.character.PlayableCharacter;
import com.course.slay.domain.enemy.Enemy;
import com.course.slay.domain.enemy.EnemyIntent;
import com.course.slay.domain.run.MapNode;
import com.course.slay.domain.run.MapNodeType;
import com.course.slay.domain.run.RunPhase;
import com.course.slay.domain.run.RunState;
import com.course.slay.engine.GameEngine;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class GameApplication extends Application {
    private static final double MAP_LANE_GAP = 92;
    private static final double MAP_FLOOR_GAP = 108;
    private static final double MAP_NODE_SIZE = 42;
    private static final double HAND_CARD_WIDTH = 132;
    private static final double HAND_CARD_HEIGHT = 156;
    private static final String BATTLE_BACKGROUND_BASE = "/assets/backgrounds/battle/";
    private static final String BATTLE_BACKGROUND_MANIFEST = BATTLE_BACKGROUND_BASE + "manifest.txt";

    private final GameEngine engine = new GameEngine();
    private final Random uiRandom = new Random();

    private Stage stage;
    private Label playerLabel;
    private Label enemyLabel;
    private Label intentLabel;
    private Label energyLabel;
    private Label pileLabel;
    private Label statusLabel;
    private Label playerBlockLabel;
    private Label enemyBlockLabel;
    private ProgressBar playerHealthBar;
    private ProgressBar enemyHealthBar;
    private HBox energyOrbBox;
    private HBox handBox;
    private Image attackEffectImage;
    private Image selectedBattleBackgroundImage;
    private BattleState backgroundBattle;
    private String selectedBattleBackground;
    private StackPane playerEffectLayer;
    private StackPane playerEffectTarget;
    private StackPane enemyEffectLayer;
    private StackPane enemyEffectTarget;
    private Button endTurnButton;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("暗黑远征：卡牌试炼");
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setScene(new Scene(createMainMenu(), 1180, 760));
        stage.show();
    }

    private void setRoot(Parent root) {
        stage.getScene().setRoot(root);
    }

    private BorderPane createMainMenu() {
        Label title = new Label("暗黑远征：卡牌试炼");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 42px; -fx-font-weight: bold;");

        Label subtitle = new Label("选择路线，构筑牌组，击败尽头首领");
        subtitle.setStyle("-fx-text-fill: #d6c8ab; -fx-font-size: 18px;");

        Button startButton = new Button("开始远征");
        startButton.setStyle(primaryButtonStyle() + "-fx-font-size: 18px; -fx-padding: 12 34 12 34;");
        startButton.setOnAction(event -> setRoot(createCharacterSelectView()));

        Button exitButton = new Button("退出");
        exitButton.setStyle(secondaryButtonStyle() + "-fx-font-size: 14px;");
        exitButton.setOnAction(event -> stage.close());

        VBox menu = new VBox(18, title, subtitle, startButton, exitButton);
        menu.setAlignment(Pos.CENTER);

        BorderPane root = baseRoot();
        root.setCenter(menu);
        return root;
    }

    private BorderPane createCharacterSelectView() {
        List<PlayableCharacter> characters = engine.getAvailableCharacters();
        if (characters.isEmpty()) {
            throw new IllegalStateException("at least one playable character is required");
        }

        String[] selectedCharacterId = {characters.get(0).id()};
        List<Button> characterButtons = new ArrayList<>();

        Label title = new Label("选择角色");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 38px; -fx-font-weight: bold;");

        Label subtitle = new Label("角色决定初始牌组和本次远征可获得的奖励牌池");
        subtitle.setStyle("-fx-text-fill: #d6c8ab; -fx-font-size: 16px;");

        HBox characterCards = new HBox(18);
        characterCards.setAlignment(Pos.CENTER);
        for (PlayableCharacter character : characters) {
            Button card = createCharacterCard(character);
            card.setOnAction(event -> {
                selectedCharacterId[0] = character.id();
                refreshCharacterSelection(characterButtons, selectedCharacterId[0]);
            });
            characterButtons.add(card);
            characterCards.getChildren().add(card);
        }
        refreshCharacterSelection(characterButtons, selectedCharacterId[0]);

        Button departButton = new Button("出发");
        departButton.setStyle(primaryButtonStyle() + "-fx-font-size: 18px; -fx-padding: 12 34 12 34;");
        departButton.setOnAction(event -> {
            engine.startNewRun(selectedCharacterId[0]);
            setRoot(createMapView());
        });

        Button backButton = new Button("返回主菜单");
        backButton.setStyle(secondaryButtonStyle());
        backButton.setOnAction(event -> setRoot(createMainMenu()));

        HBox actions = new HBox(12, departButton, backButton);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(18, title, subtitle, characterCards, actions);
        content.setAlignment(Pos.CENTER);

        BorderPane root = baseRoot();
        root.setCenter(content);
        return root;
    }

    private Button createCharacterCard(PlayableCharacter character) {
        DeckSummary starterSummary = DeckSummary.from(character.createStarterDeck());

        Label name = new Label(character.name());
        name.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label archetype = new Label(character.archetype());
        archetype.setStyle("-fx-text-fill: #f2c078; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label description = new Label(character.description());
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setStyle("-fx-text-fill: #d6c8ab; -fx-font-size: 13px;");
        description.setMaxWidth(260);

        Label stats = new Label("生命 " + character.maxHealth()
                + "  能量 " + character.maxEnergy()
                + "  每回合抽 " + character.handSize());
        stats.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label deck = new Label("初始牌 " + starterSummary.getTotalCards()
                + " 张｜攻击 " + starterSummary.getAttackCount()
                + "｜技能 " + starterSummary.getSkillCount()
                + "｜战术 " + starterSummary.getTacticCount()
                + "\n奖励池 " + character.createRewardPool().size() + " 张");
        deck.setWrapText(true);
        deck.setTextAlignment(TextAlignment.CENTER);
        deck.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 13px;");

        VBox content = new VBox(11, createCharacterPreview(character), name, archetype, description, stats, deck);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(16));
        content.setPrefWidth(310);
        content.setMinWidth(310);
        content.setMaxWidth(310);

        Button card = new Button();
        card.setGraphic(content);
        card.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        card.setUserData(character);
        card.setPrefWidth(330);
        card.setMinWidth(330);
        card.setMaxWidth(330);
        card.setMinHeight(420);
        return card;
    }

    private StackPane createCharacterPreview(PlayableCharacter character) {
        StackPane preview = new StackPane();
        preview.setPrefSize(210, 130);
        preview.setMinSize(210, 130);
        preview.setMaxSize(210, 130);

        Region glow = new Region();
        glow.setPrefSize(168, 92);
        glow.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, rgba(242, 192, 120, 0.35), rgba(242, 192, 120, 0));"
                + "-fx-background-radius: 84;");

        StackPane portraitHolder = new StackPane();
        portraitHolder.setPrefSize(120, 124);
        var resource = GameApplication.class.getResource("/assets/portraits/player.png");
        if (resource != null) {
            ImageView portrait = new ImageView(new Image(resource.toExternalForm()));
            portrait.setPreserveRatio(true);
            portrait.setSmooth(true);
            portrait.setFitHeight(132);
            portraitHolder.getChildren().add(portrait);
        } else {
            Label fallback = new Label(character.name().substring(0, 1));
            fallback.setAlignment(Pos.CENTER);
            fallback.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 42px; -fx-font-weight: bold;");
            portraitHolder.getChildren().add(fallback);
        }

        preview.getChildren().addAll(glow, portraitHolder);
        return preview;
    }

    private void refreshCharacterSelection(List<Button> characterButtons, String selectedCharacterId) {
        for (Button button : characterButtons) {
            PlayableCharacter character = (PlayableCharacter) button.getUserData();
            button.setStyle(characterCardStyle(character.id().equals(selectedCharacterId)));
        }
    }

    private void showCharacterSelect() {
        setRoot(createCharacterSelectView());
    }

    private BorderPane createMapView() {
        RunState run = engine.getRunState();
        BorderPane root = baseRoot();
        root.setTop(createMapHeader(run));
        root.setCenter(createRouteMap(run));
        return root;
    }

    private HBox createMapHeader(RunState run) {
        Label title = new Label("远征地图");
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label info = new Label(run.getPlayer().getName()
                + "（" + run.getPlayableCharacter().archetype() + "）"
                + "  生命 " + run.getPlayer().getHealth() + "/" + run.getPlayer().getMaxHealth()
                + "  金币 " + run.getGold()
                + "  牌组 " + run.getDeck().size() + " 张");
        info.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button restartButton = new Button("重新远征");
        restartButton.setStyle(primaryButtonStyle());
        restartButton.setOnAction(event -> showCharacterSelect());

        Button deckButton = new Button("牌组");
        deckButton.setStyle(secondaryButtonStyle());
        deckButton.setOnAction(event -> setRoot(createDeckView()));

        Button menuButton = new Button("主菜单");
        menuButton.setStyle(secondaryButtonStyle());
        menuButton.setOnAction(event -> setRoot(createMainMenu()));

        HBox header = new HBox(16, title, info, deckButton, restartButton, menuButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        HBox.setHgrow(info, Priority.ALWAYS);
        return header;
    }

    private StackPane createRouteMap(RunState run) {
        List<Integer> floors = run.getMap().getFloors();
        int maxFloor = floors.stream().mapToInt(Integer::intValue).max().orElse(1);
        int maxLane = run.getMap().getNodes().stream().mapToInt(MapNode::getLane).max().orElse(6);
        double canvasWidth = Math.max(780, (maxLane + 1) * MAP_LANE_GAP + 190);
        double canvasHeight = Math.max(960, maxFloor * MAP_FLOOR_GAP + 130);

        Pane routeCanvas = new Pane();
        routeCanvas.setPrefSize(canvasWidth, canvasHeight);
        routeCanvas.setMinSize(canvasWidth, canvasHeight);
        routeCanvas.setStyle("-fx-background-color: linear-gradient(to bottom, #d3ccb8, #a9a28f);"
                + "-fx-background-radius: 12;");

        for (MapNode node : run.getMap().getNodes()) {
            for (String nextId : node.getNextNodeIds()) {
                run.getMap().findNode(nextId).ifPresent(target -> routeCanvas.getChildren().add(createMapConnection(node, target, maxFloor)));
            }
        }

        for (MapNode node : run.getMap().getNodes()) {
            Button button = createMapNodeButton(node);
            button.setLayoutX(mapNodeX(node));
            button.setLayoutY(mapNodeY(node, maxFloor));
            routeCanvas.getChildren().add(button);
        }

        VBox legend = createMapLegend();
        legend.setLayoutX(canvasWidth - 164);
        legend.setLayoutY(28);
        routeCanvas.getChildren().add(legend);

        Label bossMark = new Label("BOSS");
        bossMark.setStyle("-fx-text-fill: #5b2b2e; -fx-font-size: 17px; -fx-font-weight: bold;");
        bossMark.setLayoutX(canvasWidth / 2 - 24);
        bossMark.setLayoutY(20);
        routeCanvas.getChildren().add(bossMark);

        ScrollPane routeScroll = new ScrollPane(routeCanvas);
        routeScroll.setFitToWidth(true);
        routeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        routeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        routeScroll.setVvalue(1.0);
        routeScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Label hint = new Label("每次远征会随机生成长路线地图。节点仅用图例区分类型，完成当前节点后会解锁相连的上层路线。");
        hint.setWrapText(true);
        hint.setStyle("-fx-text-fill: #cfc4ae; -fx-font-size: 13px;");

        VBox content = new VBox(14, routeScroll, hint);
        content.setAlignment(Pos.CENTER);

        StackPane mapPanel = new StackPane(content);
        mapPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #202632, #151920);"
                + "-fx-border-color: #3b4050; -fx-border-radius: 10; -fx-background-radius: 10;");
        return mapPanel;
    }

    private Button createMapNodeButton(MapNode node) {
        Button button = new Button(node.getType().getIconText());
        button.setTextAlignment(TextAlignment.CENTER);
        button.setPrefSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setMinSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setMaxSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setStyle(mapNodeStyle(node));
        button.setTooltip(new Tooltip(mapTooltip(node)));
        button.setDisable(!node.isAvailable() || node.isCompleted());
        button.setOnAction(event -> {
            if (engine.selectMapNode(node.getId())) {
                RunPhase phase = engine.getRunState().getPhase();
                if (phase == RunPhase.BATTLE) {
                    setRoot(createBattleView());
                } else if (phase == RunPhase.REST_SITE) {
                    setRoot(createRestSiteView());
                } else if (phase == RunPhase.SHOP) {
                    setRoot(createShopView());
                } else if (phase == RunPhase.RUN_DEFEAT) {
                    setRoot(createRunDefeatView());
                } else if (phase == RunPhase.MAP) {
                    setRoot(createMapView());
                }
            }
        });
        return button;
    }

    private Line createMapConnection(MapNode from, MapNode to, int maxFloor) {
        double offset = MAP_NODE_SIZE / 2;
        Line line = new Line(
                mapNodeX(from) + offset,
                mapNodeY(from, maxFloor) + offset,
                mapNodeX(to) + offset,
                mapNodeY(to, maxFloor) + offset
        );
        line.setStrokeWidth(1.6);
        line.setStroke(Paint.valueOf(nodeConnectionColor(from)));
        line.getStrokeDashArray().addAll(5.0, 7.0);
        line.setOpacity(from.isCompleted() ? 0.82 : 0.42);
        return line;
    }

    private VBox createMapLegend() {
        Label title = new Label("图例");
        title.setStyle("-fx-text-fill: #2b3038; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox rows = new VBox(8,
                legendRow(MapNodeType.NORMAL),
                legendRow(MapNodeType.ELITE),
                legendRow(MapNodeType.REST),
                legendRow(MapNodeType.EVENT),
                legendRow(MapNodeType.SHOP),
                legendRow(MapNodeType.BOSS)
        );
        VBox legend = new VBox(10, title, rows);
        legend.setPadding(new Insets(14));
        legend.setStyle("-fx-background-color: rgba(238, 246, 244, 0.86);"
                + "-fx-border-color: rgba(74, 87, 94, 0.45);"
                + "-fx-border-radius: 10; -fx-background-radius: 10;");
        return legend;
    }

    private HBox legendRow(MapNodeType type) {
        Label icon = new Label(type.getIconText());
        icon.setMinSize(28, 28);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: " + nodeColor(type) + ";"
                + "-fx-text-fill: #fff8e8;"
                + "-fx-background-radius: 14;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;");

        Label name = new Label(type.getDisplayName());
        name.setStyle("-fx-text-fill: #2f3540; -fx-font-size: 13px; -fx-font-weight: bold;");
        HBox row = new HBox(8, icon, name);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private double mapNodeX(MapNode node) {
        return 72 + node.getLane() * MAP_LANE_GAP;
    }

    private double mapNodeY(MapNode node, int maxFloor) {
        return 70 + (maxFloor - node.getFloor()) * MAP_FLOOR_GAP;
    }

    private String mapTooltip(MapNode node) {
        String stateText;
        if (node.isCompleted()) {
            stateText = "已完成";
        } else if (node.isAvailable()) {
            stateText = "可进入";
        } else {
            stateText = "未解锁";
        }
        return node.getName() + "\n" + node.getType().getDisplayName() + "\n" + stateText;
    }

    private BorderPane createBattleView() {
        updateBattleBackgroundSelection();
        BorderPane root = baseRoot();
        root.setTop(createBattleHeader());
        root.setCenter(createBattleCenter());
        root.setBottom(createHandArea());
        refreshBattle();
        return root;
    }

    private void updateBattleBackgroundSelection() {
        BattleState battle = engine.getState();
        if (battle != backgroundBattle) {
            backgroundBattle = battle;
            selectedBattleBackground = randomBattleBackgroundResource();
            selectedBattleBackgroundImage = selectedBattleBackground == null ? null : new Image(selectedBattleBackground);
        }
    }

    private HBox createBattleHeader() {
        RunState run = engine.getRunState();
        String nodeName = run != null && run.getCurrentNode() != null ? run.getCurrentNode().getName() : "单场战斗";

        Label title = new Label("战斗：" + nodeName);
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 24px; -fx-font-weight: bold;");

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button restartButton = new Button("重新远征");
        restartButton.setStyle(primaryButtonStyle());
        restartButton.setOnAction(event -> showCharacterSelect());

        Button deckButton = new Button("牌组");
        deckButton.setStyle(secondaryButtonStyle());
        deckButton.setOnAction(event -> setRoot(createDeckView()));

        Button scoutButton = new Button("探查");
        scoutButton.setStyle(secondaryButtonStyle());
        scoutButton.setOnAction(event -> setRoot(createScoutView()));

        Button logButton = new Button("战斗日志");
        logButton.setStyle(secondaryButtonStyle());
        logButton.setOnAction(event -> setRoot(createBattleLogView()));

        HBox header = new HBox(16, title, statusLabel, deckButton, scoutButton, logButton, restartButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 18, 0));
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        return header;
    }

    private BorderPane createBattleLogView() {
        BattleState battle = engine.getState();
        BorderPane root = baseRoot();
        root.setTop(createBattleLogHeader(battle));
        root.setCenter(createBattleLogContent(battle));
        root.setRight(createBattleLogStatusPanel(battle));
        return root;
    }

    private HBox createBattleLogHeader(BattleState battle) {
        Label title = new Label("战斗日志");
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        String enemyName = battle == null ? "无战斗" : battle.getEnemy().getName();
        Label info = new Label("当前敌人：" + enemyName);
        info.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button backButton = new Button("返回战斗");
        backButton.setStyle(primaryButtonStyle());
        backButton.setOnAction(event -> setRoot(createBattleView()));

        HBox header = new HBox(16, title, info, backButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        HBox.setHgrow(info, Priority.ALWAYS);
        return header;
    }

    private ScrollPane createBattleLogContent(BattleState battle) {
        VBox entries = new VBox(8);
        entries.setPadding(new Insets(10));

        List<String> logs = battle == null ? List.of("当前没有战斗记录。") : battle.getBattleLog();
        for (int i = 0; i < logs.size(); i++) {
            Label line = new Label(String.format(Locale.ROOT, "%02d. %s", i + 1, logs.get(i)));
            line.setWrapText(true);
            line.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 14px;");
            entries.getChildren().add(line);
        }

        ScrollPane scroll = new ScrollPane(entries);
        scroll.setFitToWidth(true);
        scroll.setPadding(new Insets(0, 18, 0, 0));
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    private VBox createBattleLogStatusPanel(BattleState battle) {
        Label title = new Label("记录说明");
        title.setStyle(sectionTitleStyle());

        String statusText = battle == null ? "无战斗" : statusText(battle.getStatus());
        Label status = new Label(statusText);
        status.setWrapText(true);
        status.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        Label tip = new Label("这里按时间顺序记录抽牌、出牌、伤害、格挡、敌人行动和胜负结算。日志入口只在战斗界面顶部显示。");
        tip.setWrapText(true);
        tip.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 13px;");

        VBox panel = new VBox(12, title, status, tip);
        panel.setPrefWidth(330);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return panel;
    }

    private BorderPane createScoutView() {
        BattleState battle = engine.getState();

        BorderPane root = baseRoot();
        root.setTop(createScoutHeader(battle));
        root.setCenter(createScoutIntentContent(battle));
        root.setRight(createScoutStatusPanel(battle));
        return root;
    }

    private HBox createScoutHeader(BattleState battle) {
        Label title = new Label("探查");
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        String enemyName = battle == null ? "无敌人" : battle.getEnemy().getName();
        Label info = new Label("当前敌人：" + enemyName);
        info.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button backButton = new Button("返回战斗");
        backButton.setStyle(primaryButtonStyle());
        backButton.setOnAction(event -> setRoot(createBattleView()));

        HBox header = new HBox(16, title, info, backButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        HBox.setHgrow(info, Priority.ALWAYS);
        return header;
    }

    private VBox createScoutIntentContent(BattleState battle) {
        Label title = new Label("敌人意图");
        title.setStyle(sectionTitleStyle());

        Label intent = new Label(enemyIntentText(battle));
        intent.setWrapText(true);
        intent.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label detail = new Label(enemyIntentDetailText(battle));
        detail.setWrapText(true);
        detail.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 15px;");

        VBox content = new VBox(18, title, intent, detail);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return content;
    }

    private VBox createScoutStatusPanel(BattleState battle) {
        Label title = new Label("敌方状态");
        title.setStyle(sectionTitleStyle());

        Label combatState = new Label(enemyCombatStateText(battle));
        combatState.setWrapText(true);
        combatState.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        Label tip = new Label("敌人意图会在玩家回合开始时确定，点击结束回合后会按该意图行动。");
        tip.setWrapText(true);
        tip.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 13px;");

        VBox panel = new VBox(12, title, combatState, tip);
        panel.setPrefWidth(330);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return panel;
    }

    private String enemyCombatStateText(BattleState battle) {
        if (battle == null) {
            return "当前没有可探查的敌人。";
        }
        Enemy enemy = battle.getEnemy();
        return enemy.getName()
                + "  生命 " + enemy.getHealth() + "/" + enemy.getMaxHealth()
                + "\n格挡：" + enemy.getBlock()
                + "\n下一步：" + summarizeEnemyIntent(battle.getEnemyIntent());
    }

    private String enemyIntentText(BattleState battle) {
        if (battle == null) {
            return "意图：无";
        }
        return "意图：" + summarizeEnemyIntent(battle.getEnemyIntent());
    }

    private String enemyIntentDetailText(BattleState battle) {
        if (battle == null || battle.getEnemyIntent() == null) {
            return "当前没有可显示的敌人意图。";
        }
        return battle.getEnemyIntent().detail();
    }

    private String summarizeEnemyIntent(EnemyIntent intent) {
        if (intent == null) {
            return "无";
        }
        return intent.summary();
    }

    private BorderPane createDeckView() {
        RunState run = engine.getRunState();
        List<Card> deck = currentDeck();
        DeckSummary summary = DeckSummary.from(deck);

        BorderPane root = baseRoot();
        root.setTop(createDeckHeader(run));
        root.setCenter(createDeckContent(summary));
        root.setRight(createDeckStatusPanel(run, summary));
        return root;
    }

    private HBox createDeckHeader(RunState run) {
        Label title = new Label("牌组菜单");
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label info = new Label(run == null
                ? "当前未开始远征"
                : run.getPlayer().getName() + "（" + run.getPlayableCharacter().archetype() + "）  生命 "
                + run.getPlayer().getHealth() + "/" + run.getPlayer().getMaxHealth()
                + "  金币 " + run.getGold()
                + "  阶段：" + runPhaseText(run.getPhase()));
        info.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button backButton = new Button("返回");
        backButton.setStyle(primaryButtonStyle());
        backButton.setOnAction(event -> returnFromDeckView());

        HBox header = new HBox(16, title, info, backButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        HBox.setHgrow(info, Priority.ALWAYS);
        return header;
    }

    private VBox createDeckContent(DeckSummary summary) {
        return createDeckContent(summary, "拥有的卡牌");
    }

    private VBox createDeckContent(DeckSummary summary, String cardListTitle) {
        HBox stats = new HBox(12,
                statBox("总张数", summary.getTotalCards() + " 张"),
                statBox("平均费用", String.format(Locale.ROOT, "%.2f", summary.getAverageCost())),
                statBox("攻击", summary.getAttackCount() + " 张"),
                statBox("技能", summary.getSkillCount() + " 张"),
                statBox("战术", summary.getTacticCount() + " 张")
        );
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox cardList = new VBox(10);
        cardList.setPadding(new Insets(8));
        for (DeckSummary.CardEntry entry : summary.getEntries()) {
            cardList.getChildren().add(deckEntryRow(entry));
        }

        ScrollPane scroll = new ScrollPane(cardList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Label title = new Label(cardListTitle);
        title.setStyle(sectionTitleStyle());

        VBox content = new VBox(14, stats, title, scroll);
        content.setPadding(new Insets(0, 18, 0, 0));
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return content;
    }

    private VBox createDeckStatusPanel(RunState run, DeckSummary summary) {
        Label title = new Label("牌组状态");
        title.setStyle(sectionTitleStyle());

        Label deckState = new Label("当前牌组包含 " + summary.getTotalCards()
                + " 张牌，攻击/防御/过牌能力由卡牌类型和描述共同决定。");
        deckState.setWrapText(true);
        deckState.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        String nodeText = "当前节点：无";
        if (run != null && run.getCurrentNode() != null) {
            nodeText = "当前节点：" + run.getCurrentNode().getName()
                    + "（" + run.getCurrentNode().getType().getDisplayName() + "）";
        }
        Label node = new Label(nodeText);
        node.setWrapText(true);
        node.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        Label tip = new Label("非 Boss 战胜利后可选择奖励牌或跳过换金币；营地可升级，商店可购买或删牌。");
        tip.setWrapText(true);
        tip.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 13px;");

        VBox panel = new VBox(12, title, deckState, node, tip);
        panel.setPrefWidth(330);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return panel;
    }

    private VBox statBox(String name, String value) {
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 12px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox box = new VBox(4, nameLabel, valueLabel);
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(118);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #20242d; -fx-border-color: #3d4454; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return box;
    }

    private HBox deckEntryRow(DeckSummary.CardEntry entry) {
        Label count = new Label("x" + entry.count());
        count.setMinWidth(42);
        count.setAlignment(Pos.CENTER);
        count.setStyle("-fx-text-fill: #1a1712; -fx-background-color: #f2c078;"
                + "-fx-background-radius: 15; -fx-font-weight: bold; -fx-padding: 6;");

        Label name = new Label(entry.name() + "｜" + entry.type().getDisplayName() + "｜费用 " + entry.cost());
        name.setMinWidth(230);
        name.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label description = new Label(entry.description());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        HBox row = new HBox(12, count, name, description);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #232833; -fx-border-color: #3d4454; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        HBox.setHgrow(description, Priority.ALWAYS);
        return row;
    }

    private BorderPane createRestSiteView() {
        RunState run = engine.getRunState();
        BorderPane root = baseRoot();
        root.setTop(createNodeChoiceHeader("营地", run, "休息恢复生命，或升级一张未升级卡牌。"));

        Button restButton = new Button("休息 +" + GameEngine.REST_HEAL_AMOUNT + " 生命");
        restButton.setStyle(primaryButtonStyle() + "-fx-font-size: 16px; -fx-padding: 12 20 12 20;");
        restButton.setOnAction(event -> {
            engine.restAtCamp();
            setRoot(createMapView());
        });

        Label upgradeTitle = new Label("选择一张牌升级");
        upgradeTitle.setStyle(sectionTitleStyle());

        VBox upgradeList = new VBox(10);
        upgradeList.setPadding(new Insets(8));
        for (int i = 0; i < run.getDeck().size(); i++) {
            Card card = run.getDeck().get(i);
            int index = i;
            Button upgrade = new Button(upgradeButtonText(card));
            upgrade.setMaxWidth(Double.MAX_VALUE);
            upgrade.setAlignment(Pos.CENTER_LEFT);
            upgrade.setStyle(CardFactory.canUpgrade(card) ? secondaryButtonStyle() : disabledButtonStyle());
            upgrade.setDisable(!CardFactory.canUpgrade(card));
            upgrade.setOnAction(event -> {
                if (engine.upgradeCardAtCamp(index)) {
                    setRoot(createMapView());
                } else {
                    setRoot(createRestSiteView());
                }
            });
            upgradeList.getChildren().add(upgrade);
        }

        ScrollPane scroll = new ScrollPane(upgradeList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(16, restButton, upgradeTitle, scroll);
        content.setPadding(new Insets(0, 18, 0, 0));
        VBox.setVgrow(scroll, Priority.ALWAYS);
        root.setCenter(content);
        root.setRight(createNodeLogPanel(run));
        return root;
    }

    private BorderPane createShopView() {
        RunState run = engine.getRunState();
        BorderPane root = baseRoot();
        root.setTop(createNodeChoiceHeader("商店", run, "购买奖励池卡牌，或花费金币移除一张牌。"));
        root.setCenter(createShopCardList(run));
        root.setRight(createShopRemovePanel(run));
        return root;
    }

    private HBox createNodeChoiceHeader(String titleText, RunState run, String subtitleText) {
        Label title = new Label(titleText);
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label info = new Label(run.getPlayer().getName()
                + "  生命 " + run.getPlayer().getHealth() + "/" + run.getPlayer().getMaxHealth()
                + "  金币 " + run.getGold()
                + "  牌组 " + run.getDeck().size() + " 张"
                + "  " + subtitleText);
        info.setWrapText(true);
        info.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 14px;");

        Button deckButton = new Button("牌组");
        deckButton.setStyle(secondaryButtonStyle());
        deckButton.setOnAction(event -> setRoot(createDeckView()));

        Button leaveButton = new Button(run.getPhase() == RunPhase.SHOP ? "离开商店" : "选择后继续");
        leaveButton.setStyle(primaryButtonStyle());
        leaveButton.setDisable(run.getPhase() != RunPhase.SHOP);
        leaveButton.setOnAction(event -> {
            if (run.getPhase() == RunPhase.SHOP) {
                engine.leaveShop();
                setRoot(createMapView());
            }
        });

        HBox header = new HBox(16, title, info, deckButton, leaveButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        HBox.setHgrow(info, Priority.ALWAYS);
        return header;
    }

    private VBox createShopCardList(RunState run) {
        Label title = new Label("可购买卡牌");
        title.setStyle(sectionTitleStyle());

        HBox goods = new HBox(12);
        goods.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < run.getShopCards().size(); i++) {
            Card card = run.getShopCards().get(i);
            int index = i;

            Label price = new Label(GameEngine.SHOP_CARD_PRICE + " 金币");
            price.setStyle("-fx-text-fill: #f2c078; -fx-font-size: 14px; -fx-font-weight: bold;");
            Button buy = new Button();
            buy.setGraphic(cardFace(card));
            buy.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            buy.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            buy.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            buy.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            buy.setStyle(run.getGold() >= GameEngine.SHOP_CARD_PRICE
                    ? cardStyle(card.getType())
                    : cardStyle(card.getType()) + "-fx-opacity: 0.45;");
            buy.setDisable(run.getGold() < GameEngine.SHOP_CARD_PRICE);
            buy.setOnAction(event -> {
                engine.buyShopCard(index);
                setRoot(createShopView());
            });
            VBox item = new VBox(8, buy, price);
            item.setAlignment(Pos.CENTER);
            goods.getChildren().add(item);
        }

        Label empty = new Label(run.getShopCards().isEmpty() ? "商店货架已空，可以离开继续路线。" : "");
        empty.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 14px;");

        VBox content = new VBox(14, title, goods, empty);
        content.setPadding(new Insets(0, 18, 0, 0));
        return content;
    }

    private VBox createShopRemovePanel(RunState run) {
        Label title = new Label("删牌服务");
        title.setStyle(sectionTitleStyle());

        Label price = new Label("价格：" + GameEngine.SHOP_REMOVE_CARD_PRICE + " 金币｜至少保留 1 张牌");
        price.setWrapText(true);
        price.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 13px;");

        VBox cardList = new VBox(8);
        for (int i = 0; i < run.getDeck().size(); i++) {
            Card card = run.getDeck().get(i);
            int index = i;
            Button remove = new Button("移除【" + card.getName() + "】｜费用 " + card.getCost());
            remove.setMaxWidth(Double.MAX_VALUE);
            remove.setAlignment(Pos.CENTER_LEFT);
            boolean canRemove = run.getGold() >= GameEngine.SHOP_REMOVE_CARD_PRICE && run.getDeck().size() > 1;
            remove.setStyle(canRemove ? secondaryButtonStyle() : disabledButtonStyle());
            remove.setDisable(!canRemove);
            remove.setOnAction(event -> {
                engine.removeDeckCardAtShop(index);
                setRoot(createShopView());
            });
            cardList.getChildren().add(remove);
        }

        ScrollPane scroll = new ScrollPane(cardList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox panel = new VBox(12, title, price, scroll);
        panel.setPrefWidth(360);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return panel;
    }

    private VBox createNodeLogPanel(RunState run) {
        Label title = new Label("远征记录");
        title.setStyle(sectionTitleStyle());

        VBox logs = new VBox(6);
        int start = Math.max(0, run.getRunLog().size() - 8);
        for (int i = start; i < run.getRunLog().size(); i++) {
            Label line = new Label(run.getRunLog().get(i));
            line.setWrapText(true);
            line.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 12px;");
            logs.getChildren().add(line);
        }

        VBox panel = new VBox(10, title, logs);
        panel.setPrefWidth(330);
        panel.setPadding(new Insets(12));
        panel.setStyle("-fx-background-color: #1d212a; -fx-border-color: #343a49; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return panel;
    }

    private String upgradeButtonText(Card card) {
        if (!CardFactory.canUpgrade(card)) {
            return "已升级｜" + card.getName() + "｜" + card.getDescription();
        }
        Card upgraded = CardFactory.upgradeOf(card);
        return "升级 " + card.getName() + " -> " + upgraded.getName()
                + "｜" + card.getDescription() + " -> " + upgraded.getDescription();
    }

    private List<Card> currentDeck() {
        RunState run = engine.getRunState();
        if (run != null) {
            return run.getDeck();
        }
        BattleState battle = engine.getState();
        if (battle != null) {
            return battle.getDeck();
        }
        return List.of();
    }

    private void returnFromDeckView() {
        RunState run = engine.getRunState();
        if (run == null) {
            setRoot(createMainMenu());
            return;
        }

        switch (run.getPhase()) {
            case MAP -> setRoot(createMapView());
            case BATTLE, REWARD -> setRoot(createBattleView());
            case REST_SITE -> setRoot(createRestSiteView());
            case SHOP -> setRoot(createShopView());
            case RUN_VICTORY -> setRoot(createRunVictoryView());
            case RUN_DEFEAT -> setRoot(createRunDefeatView());
            case NOT_STARTED -> setRoot(createMainMenu());
        }
    }

    private StackPane createBattleArea() {
        enemyLabel = new Label();
        intentLabel = new Label();
        enemyBlockLabel = new Label();
        enemyHealthBar = healthBar();
        VBox enemyHud = combatantHud("敌人", enemyLabel, enemyHealthBar, enemyBlockLabel, intentLabel);
        CharacterPortrait enemyPortrait = isBossBattle() ? CharacterPortrait.boss() : CharacterPortrait.enemy();
        enemyEffectLayer = createEffectLayer();
        enemyEffectTarget = createEffectTarget(enemyPortrait, enemyEffectLayer);
        VBox enemyUnit = new VBox(8, enemyHud, enemyEffectTarget);
        enemyUnit.setAlignment(Pos.CENTER);

        playerLabel = new Label();
        playerBlockLabel = new Label();
        playerHealthBar = healthBar();
        VBox playerHud = combatantHud("玩家", playerLabel, playerHealthBar, playerBlockLabel);
        CharacterPortrait playerPortrait = CharacterPortrait.player();
        playerEffectLayer = createEffectLayer();
        playerEffectTarget = createEffectTarget(playerPortrait, playerEffectLayer);
        VBox playerUnit = new VBox(8, playerHud, playerEffectTarget);
        playerUnit.setAlignment(Pos.CENTER);

        HBox units = new HBox(120, playerUnit, enemyUnit);
        units.setAlignment(Pos.BOTTOM_CENTER);
        units.setPadding(new Insets(60, 40, 50, 40));

        StackPane battlefield = new StackPane(createBattlefieldBackdrop(), units);
        battlefield.setPadding(new Insets(0, 0, 16, 0));
        StackPane.setAlignment(units, Pos.BOTTOM_CENTER);
        return battlefield;
    }

    private boolean isBossBattle() {
        RunState run = engine.getRunState();
        return run != null
                && run.getCurrentNode() != null
                && run.getCurrentNode().getType() == MapNodeType.BOSS;
    }

    private StackPane createEffectTarget(CharacterPortrait portrait, StackPane effectLayer) {
        StackPane target = new StackPane(portrait, effectLayer);
        target.setPrefSize(260, 310);
        target.setMinSize(260, 310);
        target.setMaxSize(260, 310);
        return target;
    }

    private StackPane createEffectLayer() {
        StackPane layer = new StackPane();
        layer.setPrefSize(260, 310);
        layer.setMinSize(260, 310);
        layer.setMaxSize(260, 310);
        layer.setPickOnBounds(false);
        layer.setMouseTransparent(true);
        return layer;
    }

    private StackPane createBattleCenter() {
        StackPane center = new StackPane(createBattleArea());
        BattleState state = engine.getState();
        if (state != null && state.getStatus() == GameStatus.VICTORY && !state.getRewardChoices().isEmpty()) {
            Region veil = new Region();
            veil.setStyle("-fx-background-color: rgba(8, 9, 12, 0.62); -fx-background-radius: 10;");
            StackPane.setAlignment(veil, Pos.CENTER);
            center.getChildren().addAll(veil, createRewardModal(state));
        }
        return center;
    }

    private HBox createHandArea() {
        handBox = new HBox(-6);
        handBox.setAlignment(Pos.CENTER);
        handBox.setPadding(new Insets(6, 18, 12, 18));
        handBox.setMinHeight(HAND_CARD_HEIGHT + 12);

        ScrollPane handScroll = new ScrollPane(handBox);
        handScroll.setFitToHeight(true);
        handScroll.setFitToWidth(true);
        handScroll.setPannable(true);
        handScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        handScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        handScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"
                + "-fx-padding: 0;");

        StackPane handWell = new StackPane(handScroll);
        handWell.setMinHeight(HAND_CARD_HEIGHT + 22);
        handWell.setPrefHeight(HAND_CARD_HEIGHT + 26);
        handWell.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(53, 33, 28, 0.92), rgba(24, 17, 18, 0.98));"
                + "-fx-border-color: #6b4938 #211716 #0f0c0d #211716;"
                + "-fx-border-width: 2 0 0 0;"
                + "-fx-effect: innershadow(gaussian, rgba(255, 204, 119, 0.14), 18, 0.25, 0, 2);");

        endTurnButton = new Button("结束回合");
        endTurnButton.setPrefSize(128, 46);
        endTurnButton.setMinSize(128, 46);
        endTurnButton.setOnAction(event -> {
            List<Set<CardVisualEffect>> enemyVisualEffects = engine.endTurn();
            playEnemyCardVisualEffects(enemyVisualEffects);
            afterBattleAction();
        });
        endTurnButton.setStyle(endTurnButtonStyle());

        Label energyTitle = new Label("能量");
        energyTitle.setStyle("-fx-text-fill: #f8d778; -fx-font-size: 15px; -fx-font-weight: bold;");
        energyLabel = new Label();
        energyLabel.setAlignment(Pos.CENTER);
        energyLabel.setStyle("-fx-text-fill: #321509; -fx-font-size: 24px; -fx-font-weight: bold;");

        Region energyRing = new Region();
        energyRing.setPrefSize(106, 106);
        energyRing.setMinSize(88, 88);
        energyRing.setMaxSize(88, 88);
        energyRing.setStyle("-fx-background-color: radial-gradient(center 50% 45%, radius 68%, #fff2a5, #f6a743 58%, #c95b25 78%, #7d2a19 100%);"
                + "-fx-background-radius: 44;"
                + "-fx-border-color: #ffeaa8 #f19c3e #8b2f1c #ffd06a;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 44;"
                + "-fx-effect: dropshadow(gaussian, rgba(255, 139, 36, 0.52), 16, 0.35, 0, 0);");
        energyRing.setPrefSize(88, 88);

        StackPane energyCore = new StackPane(energyRing, energyLabel);
        energyCore.setAlignment(Pos.CENTER);

        energyOrbBox = new HBox(5);
        energyOrbBox.setAlignment(Pos.CENTER);
        VBox energyPanel = new VBox(5, energyTitle, energyCore, energyOrbBox);
        energyPanel.setAlignment(Pos.CENTER);
        energyPanel.setMinWidth(130);
        energyPanel.setPrefWidth(130);
        energyPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(31, 34, 43, 0.94), rgba(17, 19, 26, 0.98));"
                + "-fx-border-color: #4d5668 #242a35 #11141b #596274;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 10 8 10 8;");

        pileLabel = new Label();
        pileLabel.setWrapText(true);
        pileLabel.setTextAlignment(TextAlignment.CENTER);
        pileLabel.setStyle("-fx-text-fill: #d8d0bf; -fx-font-size: 13px;");
        VBox actionPanel = new VBox(12, endTurnButton, pileLabel);
        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setMinWidth(144);
        actionPanel.setPrefWidth(144);
        actionPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(30, 32, 40, 0.84), rgba(18, 15, 18, 0.92));"
                + "-fx-border-color: #3e4556;"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 12;");

        HBox handArea = new HBox(10, energyPanel, handWell, actionPanel);
        handArea.setAlignment(Pos.CENTER);
        handArea.setPadding(new Insets(6, 0, 0, 0));
        HBox.setHgrow(handWell, Priority.ALWAYS);
        return handArea;
    }

    private void afterBattleAction() {
        RunState run = engine.getRunState();
        if (run != null && run.getPhase() == RunPhase.RUN_VICTORY) {
            setRoot(createRunVictoryView());
            return;
        }
        if (run != null && run.getPhase() == RunPhase.RUN_DEFEAT) {
            setRoot(createRunDefeatView());
            return;
        }
        BattleState battle = engine.getState();
        if (battle != null && battle.getStatus() == GameStatus.VICTORY) {
            setRoot(createBattleView());
            return;
        }
        refreshBattle();
    }

    private BorderPane createRunVictoryView() {
        RunState run = engine.getRunState();
        Label title = new Label("远征通关");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 42px; -fx-font-weight: bold;");

        Label detail = new Label("你击败了余烬审判者，完成本次路线。最终牌组：" + run.getDeck().size() + " 张。");
        detail.setWrapText(true);
        detail.setStyle("-fx-text-fill: #d6c8ab; -fx-font-size: 17px;");

        Button again = new Button("再次远征");
        again.setStyle(primaryButtonStyle() + "-fx-font-size: 16px;");
        again.setOnAction(event -> showCharacterSelect());

        Button menu = new Button("返回主菜单");
        menu.setStyle(secondaryButtonStyle());
        menu.setOnAction(event -> setRoot(createMainMenu()));

        VBox content = new VBox(18, title, detail, again, menu);
        content.setAlignment(Pos.CENTER);

        BorderPane root = baseRoot();
        root.setCenter(content);
        return root;
    }

    private BorderPane createRunDefeatView() {
        RunState run = engine.getRunState();
        Label title = new Label("远征失败");
        title.setStyle("-fx-text-fill: #f0a7a7; -fx-font-size: 42px; -fx-font-weight: bold;");

        String playerName = run == null ? "角色" : run.getPlayer().getName();
        Label detail = new Label(playerName + "倒下了。本次路线停在："
                + (run != null && run.getCurrentNode() != null ? run.getCurrentNode().getName() : "未知节点"));
        detail.setWrapText(true);
        detail.setStyle("-fx-text-fill: #d6c8ab; -fx-font-size: 17px;");

        Button again = new Button("重新远征");
        again.setStyle(primaryButtonStyle() + "-fx-font-size: 16px;");
        again.setOnAction(event -> showCharacterSelect());

        Button menu = new Button("返回主菜单");
        menu.setStyle(secondaryButtonStyle());
        menu.setOnAction(event -> setRoot(createMainMenu()));

        VBox content = new VBox(18, title, detail, again, menu);
        content.setAlignment(Pos.CENTER);

        BorderPane root = baseRoot();
        root.setCenter(content);
        return root;
    }

    private VBox combatantHud(String titleText, Label nameLabel, ProgressBar healthBar, Label blockLabel, Label... extraLabels) {
        Label title = new Label(titleText);
        title.setStyle(sectionTitleStyle());

        nameLabel.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 15px; -fx-font-weight: bold;");
        blockLabel.setStyle("-fx-text-fill: #c7dbef; -fx-font-size: 14px;");

        VBox box = new VBox(7, title, nameLabel, healthBar, blockLabel);
        for (Label label : extraLabels) {
            label.setStyle("-fx-text-fill: #f2c078; -fx-font-size: 14px; -fx-font-weight: bold;");
            label.setWrapText(true);
            box.getChildren().add(label);
        }
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(260);
        box.setMaxWidth(270);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: rgba(24, 28, 36, 0.86); -fx-border-color: #555e70; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");
        return box;
    }

    private StackPane createBattlefieldBackdrop() {
        StackPane backdrop = new StackPane();
        backdrop.setMinHeight(330);
        backdrop.setStyle("-fx-background-color: linear-gradient(to bottom, #252b36, #3a3340 54%, #47302b 55%, #211716);"
                + "-fx-border-color: #3b4050;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;");

        Rectangle backdropClip = new Rectangle();
        backdropClip.setArcWidth(20);
        backdropClip.setArcHeight(20);
        backdropClip.widthProperty().bind(backdrop.widthProperty());
        backdropClip.heightProperty().bind(backdrop.heightProperty());
        backdrop.setClip(backdropClip);

        if (selectedBattleBackgroundImage != null && !selectedBattleBackgroundImage.isError()) {
            ImageView background = new ImageView(selectedBattleBackgroundImage);
            background.setManaged(false);
            background.setPreserveRatio(false);
            background.setSmooth(true);
            backdrop.widthProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            backdrop.heightProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            fitBattleBackground(background, backdrop);
            backdrop.getChildren().add(background);
        }

        Region shade = new Region();
        shade.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(4, 5, 8, 0.32), rgba(7, 6, 7, 0.10) 48%, rgba(15, 9, 7, 0.30));"
                + "-fx-background-radius: 10;");

        Region floorGlow = new Region();
        floorGlow.setPrefHeight(90);
        floorGlow.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(226, 145, 67, 0.0), rgba(226, 145, 67, 0.18));"
                + "-fx-background-radius: 0 0 10 10;");
        StackPane.setAlignment(floorGlow, Pos.BOTTOM_CENTER);

        backdrop.getChildren().addAll(shade, floorGlow);
        return backdrop;
    }

    private void fitBattleBackground(ImageView background, StackPane backdrop) {
        Image image = background.getImage();
        double imageWidth = image == null ? 0 : image.getWidth();
        double imageHeight = image == null ? 0 : image.getHeight();
        double viewWidth = backdrop.getWidth();
        double viewHeight = backdrop.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0 || viewWidth <= 0 || viewHeight <= 0) {
            return;
        }

        double scale = Math.max(viewWidth / imageWidth, viewHeight / imageHeight);
        double fittedWidth = imageWidth * scale;
        double fittedHeight = imageHeight * scale;
        background.setFitWidth(fittedWidth);
        background.setFitHeight(fittedHeight);
        background.setLayoutX((viewWidth - fittedWidth) / 2);
        background.setLayoutY((viewHeight - fittedHeight) / 2);
    }

    private String randomBattleBackgroundResource() {
        List<String> backgrounds = loadBattleBackgroundResources();
        if (backgrounds.isEmpty()) {
            return null;
        }
        return backgrounds.get(uiRandom.nextInt(backgrounds.size()));
    }

    private List<String> loadBattleBackgroundResources() {
        List<String> backgrounds = new ArrayList<>();
        try (InputStream stream = GameApplication.class.getResourceAsStream(BATTLE_BACKGROUND_MANIFEST)) {
            if (stream == null) {
                return backgrounds;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String filename = line.trim();
                    if (filename.isBlank() || filename.startsWith("#")) {
                        continue;
                    }
                    String resourcePath = BATTLE_BACKGROUND_BASE + filename;
                    var resource = GameApplication.class.getResource(resourcePath);
                    if (resource != null) {
                        backgrounds.add(resource.toExternalForm());
                    }
                }
            }
        } catch (IOException ignored) {
            return List.of();
        }
        return backgrounds;
    }

    private ProgressBar healthBar() {
        ProgressBar bar = new ProgressBar(1);
        bar.setPrefWidth(210);
        bar.setMinHeight(16);
        bar.setStyle("-fx-accent: #c94f4f;");
        return bar;
    }

    private void refreshBattle() {
        BattleState state = engine.getState();
        playerLabel.setText(state.getPlayer().getName()
                + "  生命 " + state.getPlayer().getHealth() + "/" + state.getPlayer().getMaxHealth());
        enemyLabel.setText(state.getEnemy().getName()
                + "  生命 " + state.getEnemy().getHealth() + "/" + state.getEnemy().getMaxHealth());
        playerHealthBar.setProgress((double) state.getPlayer().getHealth() / state.getPlayer().getMaxHealth());
        enemyHealthBar.setProgress((double) state.getEnemy().getHealth() / state.getEnemy().getMaxHealth());
        playerBlockLabel.setText("格挡：" + state.getPlayer().getBlock());
        enemyBlockLabel.setText("格挡：" + state.getEnemy().getBlock());
        intentLabel.setText(enemyIntentText(state));
        energyLabel.setText(state.getEnergy() + "/" + state.getMaxEnergy());
        pileLabel.setText("抽牌堆：" + state.getDrawPile().size()
                + "\n弃牌堆：" + state.getDiscardPile().size()
                + "\n当前牌组：" + state.getDeck().size());
        statusLabel.setText(statusText(state.getStatus()));

        refreshEnergyOrbs(state);
        refreshHand(state);
        endTurnButton.setDisable(state.getStatus() != GameStatus.IN_PROGRESS);
    }

    private void refreshEnergyOrbs(BattleState state) {
        energyOrbBox.getChildren().clear();
        for (int i = 0; i < state.getMaxEnergy(); i++) {
            Region pip = new Region();
            pip.setPrefSize(12, 12);
            pip.setMinSize(12, 12);
            pip.setMaxSize(12, 12);
            pip.setStyle(energyPipStyle(i < state.getEnergy()));
            energyOrbBox.getChildren().add(pip);
        }
    }

    private void refreshHand(BattleState state) {
        handBox.getChildren().clear();
        if (state.getHand().isEmpty()) {
            Label empty = new Label("没有手牌");
            empty.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 15px; -fx-font-weight: bold;");
            handBox.getChildren().add(empty);
            return;
        }

        int totalCards = state.getHand().size();
        for (int i = 0; i < state.getHand().size(); i++) {
            Card card = state.getHand().get(i);
            int index = i;
            boolean canPlay = state.getStatus() == GameStatus.IN_PROGRESS && card.getCost() <= state.getEnergy();
            double baseRotate = cardRotation(i, totalCards);
            double baseTranslateY = Math.abs(i - (totalCards - 1) / 2.0) * 2.0 - 3.0;
            Button button = new Button();
            button.setGraphic(cardFace(card));
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            button.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            button.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            button.setStyle(cardStyle(card.getType()));
            button.setRotate(baseRotate);
            button.setTranslateY(baseTranslateY);
            button.setOpacity(canPlay ? 1.0 : 0.58);
            button.setTooltip(new Tooltip(card.getName() + "\n费用：" + card.getCost() + "\n" + card.getDescription()));
            button.setDisable(!canPlay);
            button.setOnMouseEntered(event -> {
                if (!button.isDisabled()) {
                    button.setRotate(baseRotate * 0.25);
                    button.setTranslateY(baseTranslateY - 12);
                    button.setScaleX(1.04);
                    button.setScaleY(1.04);
                }
            });
            button.setOnMouseExited(event -> {
                button.setRotate(baseRotate);
                button.setTranslateY(baseTranslateY);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            });
            button.setOnAction(event -> {
                Set<CardVisualEffect> visualEffects = card.getVisualEffects();
                if (engine.playCard(index)) {
                    playPlayerCardVisualEffects(visualEffects);
                }
                afterBattleAction();
            });
            handBox.getChildren().add(button);
        }
    }

    private void playPlayerCardVisualEffects(Set<CardVisualEffect> visualEffects) {
        playCardVisualEffects(visualEffects, playerEffectLayer, enemyEffectLayer, enemyEffectTarget);
    }

    private void playEnemyCardVisualEffects(List<Set<CardVisualEffect>> visualEffectsGroups) {
        for (Set<CardVisualEffect> visualEffects : visualEffectsGroups) {
            playCardVisualEffects(visualEffects, enemyEffectLayer, playerEffectLayer, playerEffectTarget);
        }
    }

    private void playCardVisualEffects(
            Set<CardVisualEffect> visualEffects,
            StackPane shieldLayer,
            StackPane attackLayer,
            StackPane attackTarget
    ) {
        if (visualEffects.contains(CardVisualEffect.SHIELD)) {
            playShieldEffect(shieldLayer);
        }
        if (visualEffects.contains(CardVisualEffect.ATTACK)) {
            playAttackEffect(attackLayer, attackTarget);
        }
    }

    private void playShieldEffect(StackPane targetLayer) {
        if (targetLayer == null) {
            return;
        }

        Circle shield = new Circle(76);
        shield.setFill(Color.web("#8bdcff", 0.14));
        shield.setStroke(Color.web("#bdefff", 0.92));
        shield.setStrokeWidth(5);
        shield.setOpacity(0.0);
        StackPane.setAlignment(shield, Pos.CENTER);

        Circle innerGlow = new Circle(50);
        innerGlow.setFill(Color.TRANSPARENT);
        innerGlow.setStroke(Color.web("#f4fbff", 0.75));
        innerGlow.setStrokeWidth(2);
        innerGlow.setOpacity(0.0);
        StackPane.setAlignment(innerGlow, Pos.CENTER);

        targetLayer.getChildren().addAll(shield, innerGlow);

        ParallelTransition animation = new ParallelTransition(
                fade(shield, 0.0, 0.82, 180),
                scale(shield, 0.68, 1.08, 520),
                fade(innerGlow, 0.0, 0.78, 140),
                scale(innerGlow, 0.75, 1.35, 520)
        );
        animation.setOnFinished(event -> {
            FadeTransition fadeOut = fade(shield, shield.getOpacity(), 0.0, 220);
            FadeTransition innerFadeOut = fade(innerGlow, innerGlow.getOpacity(), 0.0, 220);
            ParallelTransition exit = new ParallelTransition(fadeOut, innerFadeOut);
            exit.setOnFinished(done -> targetLayer.getChildren().removeAll(shield, innerGlow));
            exit.play();
        });
        animation.play();
    }

    private void playAttackEffect(StackPane targetLayer, StackPane shakeTarget) {
        if (targetLayer == null) {
            return;
        }

        ImageView effect = new ImageView(attackEffectImage());
        effect.setPreserveRatio(true);
        effect.setSmooth(true);
        effect.setFitWidth(260);
        effect.setOpacity(0.0);
        effect.setScaleX(0.44);
        effect.setScaleY(0.44);
        effect.setTranslateX(12);
        effect.setTranslateY(-18);
        effect.setMouseTransparent(true);
        StackPane.setAlignment(effect, Pos.CENTER);
        targetLayer.getChildren().add(effect);
        shakeTarget(shakeTarget);

        ParallelTransition animation = new ParallelTransition(
                fade(effect, 0.0, 1.0, 90),
                scale(effect, 0.44, 0.9, 280),
                translate(effect, -10, 8, 280)
        );
        animation.setOnFinished(event -> {
            ParallelTransition exit = new ParallelTransition(
                    fade(effect, effect.getOpacity(), 0.0, 180),
                    scale(effect, 0.9, 1.03, 180)
            );
            exit.setOnFinished(done -> targetLayer.getChildren().remove(effect));
            exit.play();
        });
        animation.play();
    }

    private Image attackEffectImage() {
        if (attackEffectImage == null) {
            String resource = GameApplication.class.getResource("/assets/effects/attack-slash.png").toExternalForm();
            attackEffectImage = new Image(resource);
        }
        return attackEffectImage;
    }

    private void shakeTarget(StackPane target) {
        if (target == null) {
            return;
        }
        target.setTranslateX(0);
        target.setTranslateY(0);

        SequentialTransition shake = new SequentialTransition(
                nudge(target, -12, 0, 36),
                nudge(target, 16, -2, 42),
                nudge(target, -10, 2, 42),
                nudge(target, 7, 0, 36),
                nudge(target, 0, 0, 44)
        );
        shake.play();
    }

    private FadeTransition fade(Node node, double from, double to, int millis) {
        FadeTransition transition = new FadeTransition(Duration.millis(millis), node);
        transition.setFromValue(from);
        transition.setToValue(to);
        return transition;
    }

    private ScaleTransition scale(Node node, double from, double to, int millis) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(millis), node);
        transition.setFromX(from);
        transition.setFromY(from);
        transition.setToX(to);
        transition.setToY(to);
        return transition;
    }

    private TranslateTransition translate(Node node, double byX, double byY, int millis) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(millis), node);
        transition.setByX(byX);
        transition.setByY(byY);
        return transition;
    }

    private TranslateTransition nudge(Node node, double toX, double toY, int millis) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(millis), node);
        transition.setToX(toX);
        transition.setToY(toY);
        return transition;
    }

    private StackPane cardFace(Card card) {
        Region frame = new Region();
        frame.setPrefSize(HAND_CARD_WIDTH - 10, HAND_CARD_HEIGHT - 10);
        frame.setMinSize(HAND_CARD_WIDTH - 10, HAND_CARD_HEIGHT - 10);
        frame.setMaxSize(HAND_CARD_WIDTH - 10, HAND_CARD_HEIGHT - 10);
        frame.setStyle(cardFrameStyle(card.getType()));

        Region titlePlate = new Region();
        titlePlate.setPrefHeight(34);
        titlePlate.setMaxHeight(34);
        titlePlate.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(255, 238, 186, 0.82), rgba(109, 69, 41, 0.74));"
                + "-fx-background-radius: 11 11 4 4;"
                + "-fx-border-color: rgba(255, 245, 194, 0.55);"
                + "-fx-border-width: 0 0 1 0;");
        StackPane.setAlignment(titlePlate, Pos.TOP_CENTER);
        StackPane.setMargin(titlePlate, new Insets(6, 6, 0, 6));

        Label cost = new Label(String.valueOf(card.getCost()));
        cost.setMinSize(30, 30);
        cost.setMaxSize(30, 30);
        cost.setAlignment(Pos.CENTER);
        cost.setStyle("-fx-background-color: radial-gradient(center 45% 38%, radius 70%, #fff4a8, #f2a93a 64%, #8e2f1d 100%);"
                + "-fx-background-radius: 15;"
                + "-fx-border-color: #f9d87a #8d321f #31100d #f4bd4d;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 15;"
                + "-fx-text-fill: #241006;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 7, 0.35, 0, 2);");
        StackPane.setAlignment(cost, Pos.TOP_LEFT);
        StackPane.setMargin(cost, new Insets(0, 0, 0, 0));

        Label name = new Label(card.getName());
        name.setWrapText(true);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(82);
        name.setStyle("-fx-text-fill: #fff8df; -fx-font-size: 15px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(35, 14, 8, 0.85), 3, 0.7, 0, 1);");
        StackPane.setAlignment(name, Pos.TOP_CENTER);
        StackPane.setMargin(name, new Insets(11, 14, 0, 26));

        Label desc = new Label(card.getDescription());
        desc.setWrapText(true);
        desc.setTextAlignment(TextAlignment.CENTER);
        desc.setAlignment(Pos.CENTER);
        desc.setMaxWidth(100);
        desc.setStyle("-fx-text-fill: #fff1c7; -fx-font-size: 12px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(24, 9, 6, 0.92), 4, 0.75, 0, 1);");
        StackPane.setAlignment(desc, Pos.CENTER);
        StackPane.setMargin(desc, new Insets(40, 16, 16, 16));

        StackPane face = new StackPane(frame, titlePlate, name, desc, cost);
        face.setAlignment(Pos.CENTER);
        face.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        face.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        face.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        return face;
    }

    private VBox createRewardModal(BattleState state) {
        Label title = new Label("战斗奖励");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 28px; -fx-font-weight: bold;");

        Label hint = new Label("选择一张卡牌加入当前远征牌组，或跳过奖励获得 "
                + GameEngine.SKIP_REWARD_GOLD + " 金币。");
        hint.setWrapText(true);
        hint.setTextAlignment(TextAlignment.CENTER);
        hint.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 14px;");

        HBox choices = new HBox(12);
        choices.setAlignment(Pos.CENTER);
        for (int i = 0; i < state.getRewardChoices().size(); i++) {
            Card card = state.getRewardChoices().get(i);
            int index = i;
            Button reward = new Button();
            reward.setGraphic(cardFace(card));
            reward.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            reward.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            reward.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            reward.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
            reward.setStyle(cardStyle(card.getType()));
            reward.setOnAction(event -> {
                engine.claimReward(index);
                if (engine.getRunState() != null && engine.getRunState().getPhase() == RunPhase.MAP) {
                    setRoot(createMapView());
                } else {
                    setRoot(createBattleView());
                }
            });
            choices.getChildren().add(reward);
        }

        Button skip = new Button("跳过奖励 +" + GameEngine.SKIP_REWARD_GOLD + " 金币");
        skip.setStyle(secondaryButtonStyle() + "-fx-font-size: 15px; -fx-padding: 10 18 10 18;");
        skip.setOnAction(event -> {
            engine.skipReward();
            if (engine.getRunState() != null && engine.getRunState().getPhase() == RunPhase.MAP) {
                setRoot(createMapView());
            } else {
                setRoot(createBattleView());
            }
        });

        VBox modal = new VBox(16, title, hint, choices, skip);
        modal.setAlignment(Pos.CENTER);
        modal.setMaxWidth(650);
        modal.setPadding(new Insets(24));
        modal.setStyle("-fx-background-color: #20242d; -fx-border-color: #f2c078;"
                + "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
        StackPane.setAlignment(modal, Pos.CENTER);
        return modal;
    }

    private BorderPane baseRoot() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #111217, #1b2029 55%, #291d1a);");
        return root;
    }

    private String characterCardStyle(boolean selected) {
        String background = selected
                ? "linear-gradient(to bottom, #2b303b, #222833 52%, #34251f)"
                : "linear-gradient(to bottom, #20242d, #191d25 52%, #211918)";
        String border = selected ? "#f2c078" : "#3d4454";
        String shadow = selected
                ? "dropshadow(gaussian, rgba(242, 192, 120, 0.34), 18, 0.28, 0, 4)"
                : "dropshadow(gaussian, rgba(0, 0, 0, 0.35), 10, 0.2, 0, 3)";
        return "-fx-background-color: " + background + ";"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 0;"
                + "-fx-cursor: hand;"
                + "-fx-effect: " + shadow + ";";
    }

    private String mapNodeStyle(MapNode node) {
        String color = nodeColor(node.getType());
        String border;
        if (node.isCompleted()) {
            border = "#a7d3a6";
        } else if (node.isAvailable()) {
            border = "#f2c078";
        } else {
            border = "#4a5060";
        }
        return "-fx-background-color: " + color + ";"
                + "-fx-text-fill: #f7f0df;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 21;"
                + "-fx-background-radius: 21;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;";
    }

    private String nodeConnectionColor(MapNode node) {
        if (node.isCompleted()) {
            return "#6f8d73";
        }
        if (node.isAvailable()) {
            return "#8b5a2e";
        }
        return "#4e524f";
    }

    private String nodeColor(MapNodeType type) {
        return switch (type) {
            case NORMAL -> "#686b5a";
            case ELITE -> "#764151";
            case REST -> "#b97033";
            case EVENT -> "#4b657a";
            case SHOP -> "#7b6541";
            case BOSS -> "#5b2b2e";
        };
    }

    private String statusText(GameStatus status) {
        return switch (status) {
            case IN_PROGRESS -> "状态：战斗中";
            case VICTORY -> "状态：胜利，等待领取奖励";
            case DEFEAT -> "状态：失败";
            case REWARD_CLAIMED -> "状态：奖励已领取";
        };
    }

    private String runPhaseText(RunPhase phase) {
        return switch (phase) {
            case NOT_STARTED -> "未开始";
            case MAP -> "地图选择";
            case BATTLE -> "战斗中";
            case REWARD -> "奖励选择";
            case REST_SITE -> "营地";
            case SHOP -> "商店";
            case RUN_VICTORY -> "远征通关";
            case RUN_DEFEAT -> "远征失败";
        };
    }

    private String cardStyle(CardType type) {
        return "-fx-background-color: transparent;"
                + "-fx-padding: 0;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: transparent;"
                + "-fx-cursor: hand;";
    }

    private String cardFrameStyle(CardType type) {
        String body = switch (type) {
            case ATTACK -> "linear-gradient(to bottom, #a43e38, #6d2228 48%, #2d1117)";
            case SKILL -> "linear-gradient(to bottom, #2f7a8c, #20546a 48%, #102a3a)";
            case TACTIC -> "linear-gradient(to bottom, #9a7b32, #625224 48%, #2d2612)";
        };
        String glow = switch (type) {
            case ATTACK -> "rgba(255, 106, 73, 0.42)";
            case SKILL -> "rgba(94, 224, 240, 0.34)";
            case TACTIC -> "rgba(255, 212, 97, 0.32)";
        };
        return "-fx-background-color: " + body + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #f0d18d #8b5b36 #291612 #f7c867;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 14;"
                + "-fx-effect: dropshadow(gaussian, " + glow + ", 14, 0.32, 0, 3);";
    }

    private String cardGemColor(CardType type) {
        return switch (type) {
            case ATTACK -> "#f06d48";
            case SKILL -> "#64d3e3";
            case TACTIC -> "#f0ce62";
        };
    }

    private String energyPipStyle(boolean filled) {
        if (filled) {
            return "-fx-background-color: radial-gradient(center 50% 42%, radius 70%, #fff3a8, #f4a73b 68%, #7a2117 100%);"
                    + "-fx-background-radius: 6;"
                    + "-fx-effect: dropshadow(gaussian, rgba(255, 172, 55, 0.7), 7, 0.35, 0, 0);";
        }
        return "-fx-background-color: #343846;"
                + "-fx-background-radius: 6;"
                + "-fx-border-color: #5a6070;"
                + "-fx-border-radius: 6;"
                + "-fx-border-width: 1;";
    }

    private double cardRotation(int index, int totalCards) {
        if (totalCards <= 1) {
            return 0;
        }
        double middle = (totalCards - 1) / 2.0;
        return (index - middle) * 4.0;
    }

    private String endTurnButtonStyle() {
        return "-fx-background-color: linear-gradient(to bottom, #ecba6b, #b96a32 48%, #6e321f);"
                + "-fx-text-fill: #26150e;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 9;"
                + "-fx-border-color: #ffe6a6 #7c3d22 #24100d #f3bb63;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 9;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.55), 10, 0.3, 0, 4);";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #c96c36;"
                + "-fx-text-fill: #1d1712;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 8 14 8 14;";
    }

    private String secondaryButtonStyle() {
        return "-fx-background-color: #34394a;"
                + "-fx-text-fill: #f7f0df;"
                + "-fx-border-color: #6d748a;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 8;";
    }

    private String disabledButtonStyle() {
        return "-fx-background-color: #242832;"
                + "-fx-text-fill: #8f948f;"
                + "-fx-border-color: #3a404d;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 8;";
    }

    private String sectionTitleStyle() {
        return "-fx-text-fill: #f2c078; -fx-font-size: 17px; -fx-font-weight: bold;";
    }
}
