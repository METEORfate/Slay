package com.course.slay.ui;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardRarity;
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
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameApplication extends Application {
    private static final double MAP_NODE_SIZE = 56;
    private static final double MAP_ROUTE_LEFT_RATIO = 0.24;
    private static final double MAP_ROUTE_RIGHT_RATIO = 0.76;
    private static final double MAP_ROUTE_TOP_RATIO = 0.18;
    private static final double MAP_ROUTE_BOTTOM_RATIO = 0.73;
    private static final double HAND_CARD_WIDTH = 168;
    private static final double HAND_CARD_HEIGHT = 252;
    private static final double HAND_CARD_SLOT_HEIGHT = 318;
    private static final double HAND_CARD_SPACING = 4;
    private static final double HAND_CARD_HOVER_SCALE = 1.04;
    private static final double HAND_CARD_HOVER_PULL = 30;
    private static final double HAND_CARD_FAN_STEP_DEGREES = 5.5;
    private static final double HAND_CARD_MAX_ROTATION_DEGREES = 13.5;
    private static final double HAND_CARD_ARC_STEP = 10;
    private static final double HAND_CARD_MAX_ARC_Y = 24;
    private static final double RESOURCE_ICON_SLOT = 32;
    private static final double RESOURCE_ICON_SIZE = 27;
    private static final double BATTLE_ACTION_BUTTON_SIZE = 54;
    private static final double BATTLE_ACTION_ICON_SIZE = 40;
    private static final double BATTLE_ACTION_BUTTON_GAP = 12;
    private static final double PILE_ICON_SLOT_WIDTH = 124;
    private static final double PILE_ICON_SLOT_HEIGHT = 92;
    private static final double PILE_ICON_WIDTH = 112;
    private static final double PILE_ICON_HEIGHT = 84;
    private static final String MAP_BACKGROUND_RESOURCE = "/assets/backgrounds/map/mapBackground.png";
    private static final String MAP_LEGEND_RESOURCE = "/assets/backgrounds/map/tuli.png";
    private static final String BATTLE_UI_BASE = "/assets/ui/";
    private static final String BATTLE_SCENE_BACKGROUND_RESOURCE = BATTLE_UI_BASE + "战斗背景.png";
    private static final String BATTLE_ENERGY_ORB_RESOURCE = BATTLE_UI_BASE + "能量球.png";
    private static final String BATTLE_END_TURN_RESOURCE = BATTLE_UI_BASE + "结束回合按钮.png";
    private static final String BATTLE_DRAW_PILE_RESOURCE = BATTLE_UI_BASE + "抽牌堆.png";
    private static final String BATTLE_DISCARD_PILE_RESOURCE = BATTLE_UI_BASE + "弃牌堆.png";
    private static final String BATTLE_PLAYER_FRAME_RESOURCE = BATTLE_UI_BASE + "狂战士头像.png";
    private static final String BATTLE_GOLD_RESOURCE = BATTLE_UI_BASE + "金币.png";
    private static final String BATTLE_DECK_BUTTON_RESOURCE = BATTLE_UI_BASE + "牌组按钮.png";
    private static final String BATTLE_SETTINGS_RESOURCE = BATTLE_UI_BASE + "设置按钮.png";
    private static final String BATTLE_BACKGROUND_BASE = "/assets/backgrounds/battle/";
    private static final String BATTLE_BACKGROUND_MANIFEST = BATTLE_BACKGROUND_BASE + "manifest.txt";
    private static final String COMMON_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "普通卡.png";
    private static final String RARE_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "稀有卡.png";
    private static final String LEGENDARY_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "传说卡.png";
    private static final String SPECIAL_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "特殊卡.png";

    private final GameEngine engine = new GameEngine();
    private final Random uiRandom = new Random();
    private final Map<CardRarity, Image> cardTemplateImages = new EnumMap<>(CardRarity.class);
    private final Map<String, Image> uiImages = new HashMap<>();
    private final Map<String, Rectangle2D> uiImageViewports = new HashMap<>();

    private Stage stage;
    private Label playerLabel;
    private Label enemyLabel;
    private Label intentLabel;
    private Label energyLabel;
    private Label pileLabel;
    private Label drawPileCountLabel;
    private Label discardPileCountLabel;
    private Label statusLabel;
    private Label playerHeaderNameLabel;
    private Label playerHeaderHealthLabel;
    private Label playerBlockLabel;
    private Label enemyBlockLabel;
    private ProgressBar playerHealthBar;
    private ProgressBar enemyHealthBar;
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
    private Image mapLegendImage;
    private double mapViewportOffsetY = Double.NaN;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("暗黑远征：卡牌试炼");
        stage.setMinWidth(960);
        stage.setMinHeight(540);

        double initialWidth = Math.min(1600, Screen.getPrimary().getVisualBounds().getWidth() * 0.9);
        double initialHeight = Math.min(900, Screen.getPrimary().getVisualBounds().getHeight() * 0.9);
        Scene scene = new Scene(createMainMenu(), initialWidth, initialHeight);
        stage.setScene(scene);
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
        characterCards.setFillHeight(false);
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
            mapViewportOffsetY = Double.NaN;
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
        card.setPrefHeight(460);
        card.setMinHeight(460);
        card.setMaxHeight(460);
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

    private StackPane createMapView() {
        RunState run = engine.getRunState();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050506;");

        StackPane overlayLayer = new StackPane();
        overlayLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlayLayer.setPickOnBounds(false);

        HBox header = createMapHeader(run, overlayLayer);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setMargin(header, new Insets(18, 28, 0, 28));

        root.getChildren().addAll(createRouteMap(run), header, overlayLayer);
        return root;
    }

    private HBox createMapHeader(RunState run, StackPane overlayLayer) {
        Label character = mapStatusLabel("角色 " + run.getPlayer().getName());
        Label health = mapStatusLabel("生命 " + run.getPlayer().getHealth() + "/" + run.getPlayer().getMaxHealth());
        Label gold = mapStatusLabel("金币 " + run.getGold());
        Label potions = mapStatusLabel("药水 0");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deckButton = new Button("牌组");
        deckButton.setStyle(mapHeaderButtonStyle());
        deckButton.setOnAction(event -> showMapDeckOverlay(overlayLayer, run));

        Button settingsButton = new Button("设置");
        settingsButton.setStyle(mapHeaderButtonStyle());
        settingsButton.setOnAction(event -> showMapSettingsOverlay(overlayLayer, run));

        HBox header = new HBox(14, character, health, gold, potions, spacer, deckButton, settingsButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefHeight(58);
        header.setMinHeight(58);
        header.setMaxHeight(58);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setStyle("-fx-background-color: rgba(14, 13, 11, 0.78);"
                + "-fx-border-color: rgba(221, 184, 114, 0.56);"
                + "-fx-border-width: 1.4;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.58), 16, 0.28, 0, 4);");
        return header;
    }

    private Label mapStatusLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #f3e3bc;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 6 10 6 10;"
                + "-fx-background-color: rgba(58, 42, 25, 0.48);"
                + "-fx-background-radius: 5;"
                + "-fx-border-color: rgba(242, 192, 120, 0.28);"
                + "-fx-border-radius: 5;");
        return label;
    }

    private Pane createRouteMap(RunState run) {
        Image background = new Image(MAP_BACKGROUND_RESOURCE);

        ImageView backgroundView = new ImageView(background);
        backgroundView.setSmooth(true);
        backgroundView.setCache(true);
        backgroundView.setPreserveRatio(false);

        Pane routeCanvas = new Pane();
        Pane viewport = new Pane(routeCanvas);
        viewport.setMinSize(0, 0);
        viewport.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        viewport.setStyle("-fx-background-color: #050506;");

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);

        viewport.widthProperty().addListener((observable, oldValue, newValue) ->
                layoutRouteMap(run, routeCanvas, backgroundView, viewport.getWidth(), viewport.getHeight()));
        viewport.heightProperty().addListener((observable, oldValue, newValue) ->
                layoutRouteMap(run, routeCanvas, backgroundView, viewport.getWidth(), viewport.getHeight()));
        Platform.runLater(() -> layoutRouteMap(run, routeCanvas, backgroundView, viewport.getWidth(), viewport.getHeight()));

        viewport.addEventFilter(ScrollEvent.SCROLL, event -> {
            double maxOffsetY = Math.max(0, routeCanvas.getPrefHeight() - viewport.getHeight());
            double currentOffset = Double.isNaN(mapViewportOffsetY) ? maxOffsetY : mapViewportOffsetY;
            mapViewportOffsetY = clamp(currentOffset - event.getDeltaY() * 1.35, 0, maxOffsetY);
            routeCanvas.setTranslateY(-mapViewportOffsetY);
            event.consume();
        });
        return viewport;
    }

    private void layoutRouteMap(RunState run, Pane routeCanvas, ImageView backgroundView,
                                double viewportWidth, double viewportHeight) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return;
        }

        Image background = backgroundView.getImage();
        double imageWidth = background.isError() || background.getWidth() <= 0 ? 1086 : background.getWidth();
        double imageHeight = background.isError() || background.getHeight() <= 0 ? 1448 : background.getHeight();
        double imageRatio = imageHeight / imageWidth;
        double canvasWidth = Math.max(viewportWidth, viewportHeight * 1.68 / imageRatio);
        double canvasHeight = canvasWidth * imageRatio;

        backgroundView.setFitWidth(canvasWidth);
        backgroundView.setFitHeight(canvasHeight);
        routeCanvas.setLayoutX((viewportWidth - canvasWidth) / 2.0);
        routeCanvas.setPrefSize(canvasWidth, canvasHeight);
        routeCanvas.setMinSize(canvasWidth, canvasHeight);
        routeCanvas.setMaxSize(canvasWidth, canvasHeight);
        routeCanvas.getChildren().setAll(backgroundView);

        List<Integer> floors = run.getMap().getFloors();
        int maxFloor = floors.stream().mapToInt(Integer::intValue).max().orElse(1);

        for (MapNode node : run.getMap().getNodes()) {
            for (String nextId : node.getNextNodeIds()) {
                run.getMap().findNode(nextId).ifPresent(target ->
                        routeCanvas.getChildren().add(createMapConnection(node, target, maxFloor, canvasWidth, canvasHeight)));
            }
        }

        for (MapNode node : run.getMap().getNodes()) {
            Button button = createMapNodeButton(node);
            button.setLayoutX(mapNodeX(node, canvasWidth));
            button.setLayoutY(mapNodeY(node, maxFloor, canvasHeight));
            routeCanvas.getChildren().add(button);
        }

        double maxOffsetY = Math.max(0, canvasHeight - viewportHeight);
        if (Double.isNaN(mapViewportOffsetY) || mapViewportOffsetY > maxOffsetY) {
            mapViewportOffsetY = maxOffsetY;
        }
        mapViewportOffsetY = clamp(mapViewportOffsetY, 0, maxOffsetY);
        routeCanvas.setTranslateY(-mapViewportOffsetY);
    }

    private Button createMapNodeButton(MapNode node) {
        Button button = new Button();
        button.setGraphic(createMapNodeIcon(node.getType(), 46));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setPrefSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setMinSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setMaxSize(MAP_NODE_SIZE, MAP_NODE_SIZE);
        button.setStyle(mapNodeStyle(node));
        button.setOpacity(node.isCompleted() ? 0.58 : node.isAvailable() ? 1.0 : 0.5);
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

    private ImageView createMapNodeIcon(MapNodeType type, double fitSize) {
        ImageView icon = new ImageView(mapLegendImage());
        icon.setViewport(mapLegendIconViewport(type));
        icon.setFitWidth(fitSize);
        icon.setFitHeight(fitSize);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        icon.setMouseTransparent(true);
        return icon;
    }

    private Image mapLegendImage() {
        if (mapLegendImage == null) {
            mapLegendImage = new Image(MAP_LEGEND_RESOURCE);
        }
        return mapLegendImage;
    }

    private Rectangle2D mapLegendIconViewport(MapNodeType type) {
        return switch (type) {
            case EVENT -> new Rectangle2D(190, 175, 230, 155);
            case SHOP -> new Rectangle2D(190, 330, 235, 145);
            case REST -> new Rectangle2D(185, 630, 250, 145);
            case NORMAL -> new Rectangle2D(195, 780, 230, 150);
            case ELITE, BOSS -> new Rectangle2D(190, 930, 250, 160);
        };
    }

    private Path createMapConnection(MapNode from, MapNode to, int maxFloor, double canvasWidth, double canvasHeight) {
        double offset = MAP_NODE_SIZE / 2;
        double startX = mapNodeX(from, canvasWidth) + offset;
        double startY = mapNodeY(from, maxFloor, canvasHeight) + offset;
        double endX = mapNodeX(to, canvasWidth) + offset;
        double endY = mapNodeY(to, maxFloor, canvasHeight) + offset;
        int hash = Math.abs((from.getId() + to.getId()).hashCode());
        double normalX = endY - startY;
        double normalY = startX - endX;
        double normalLength = Math.max(1, Math.hypot(normalX, normalY));
        normalX /= normalLength;
        normalY /= normalLength;

        Path path = new Path(new MoveTo(startX, startY));
        for (int i = 1; i <= 3; i++) {
            double t = i / 4.0;
            double wave = ((hash >> (i * 3)) % 17 + 9) * (i % 2 == 0 ? -1 : 1);
            path.getElements().add(new LineTo(
                    startX + (endX - startX) * t + normalX * wave,
                    startY + (endY - startY) * t + normalY * wave
            ));
        }
        path.getElements().add(new LineTo(endX, endY));
        path.setFill(null);
        path.setStroke(Color.web(nodeConnectionColor(from)));
        path.setStrokeWidth(from.isCompleted() ? 3.4 : 2.8);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.getStrokeDashArray().addAll(11.0, 10.0, 3.0, 9.0);
        path.setOpacity(from.isCompleted() ? 0.86 : from.isAvailable() ? 0.72 : 0.44);
        return path;
    }

    private void showMapDeckOverlay(StackPane overlayLayer, RunState run) {
        DeckSummary summary = DeckSummary.from(run.getDeck());
        Label title = new Label("当前远征牌组");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 28px; -fx-font-weight: bold;");

        VBox deckContent = createDeckContent(summary, "拥有的卡牌");
        deckContent.setPrefSize(760, 300);
        VBox.setVgrow(deckContent, Priority.ALWAYS);

        Button close = new Button("关闭");
        close.setStyle(primaryButtonStyle());
        close.setOnAction(event -> closeMapOverlay(overlayLayer));

        VBox panel = new VBox(16, title, deckContent, close);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(900, 500);
        panel.setPadding(new Insets(24));
        panel.setStyle(mapModalPanelStyle());
        showMapOverlay(overlayLayer, panel);
    }

    private void showMapSettingsOverlay(StackPane overlayLayer, RunState run) {
        Label title = new Label("设置");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 28px; -fx-font-weight: bold;");

        ImageView legendImage = new ImageView(new Image(MAP_LEGEND_RESOURCE));
        legendImage.setFitWidth(180);
        legendImage.setPreserveRatio(true);
        legendImage.setSmooth(true);

        VBox legendRows = new VBox(10,
                legendExplanationRow(MapNodeType.EVENT, "不明事件，可能带来收益或风险。"),
                legendExplanationRow(MapNodeType.SHOP, "商人补给，可购买卡牌或删除卡牌。"),
                legendExplanationRow(MapNodeType.REST, "休息营地，可恢复 30% 最大生命或升级卡牌。"),
                legendExplanationRow(MapNodeType.NORMAL, "普通敌人，胜利后获得奖励。"),
                legendExplanationRow(MapNodeType.ELITE, "精英敌人，战斗更危险，金币奖励更高。"),
                legendExplanationRow(MapNodeType.BOSS, "终点首领，击败后完成本次远征。")
        );

        HBox legend = new HBox(22, legendImage, legendRows);
        legend.setAlignment(Pos.CENTER_LEFT);

        Label saveHint = new Label("当前版本未实现持久化存档，“保存并退出”会返回主菜单。");
        saveHint.setWrapText(true);
        saveHint.setStyle("-fx-text-fill: #cfc4ae; -fx-font-size: 13px;");

        Button close = new Button("返回");
        close.setStyle(secondaryButtonStyle());
        close.setOnAction(event -> closeMapOverlay(overlayLayer));

        Button saveExit = new Button("保存并退出");
        saveExit.setStyle(primaryButtonStyle());
        saveExit.setOnAction(event -> setRoot(createMainMenu()));

        HBox actions = new HBox(12, close, saveExit);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox panel = new VBox(18, title, legend, saveHint, actions);
        panel.setMaxSize(760, 560);
        panel.setPadding(new Insets(26));
        panel.setStyle(mapModalPanelStyle());
        showMapOverlay(overlayLayer, panel);
    }

    private HBox legendExplanationRow(MapNodeType type, String text) {
        StackPane icon = new StackPane(createMapNodeIcon(type, 48));
        icon.setMinSize(54, 54);
        icon.setPrefSize(54, 54);
        icon.setMaxSize(54, 54);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: radial-gradient(center 50% 42%, radius 72%, rgba(232, 205, 145, 0.92), rgba(72, 50, 28, 0.72));"
                + "-fx-border-color: rgba(44, 31, 19, 0.78);"
                + "-fx-border-width: 1.6;"
                + "-fx-border-radius: 27;"
                + "-fx-background-radius: 27;"
                + "-fx-effect: dropshadow(gaussian, rgba(24, 13, 6, 0.58), 8, 0.22, 0, 3);");

        Label label = new Label(type.getDisplayName() + "：" + text);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #eee3c6; -fx-font-size: 15px;");
        HBox row = new HBox(12, icon, label);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void showMapOverlay(StackPane overlayLayer, Node panel) {
        StackPane backdrop = new StackPane(panel);
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.62);");
        StackPane.setAlignment(panel, Pos.CENTER);
        overlayLayer.getChildren().setAll(backdrop);
        overlayLayer.setPickOnBounds(true);
    }

    private void closeMapOverlay(StackPane overlayLayer) {
        overlayLayer.getChildren().clear();
        overlayLayer.setPickOnBounds(false);
    }

    private String mapModalPanelStyle() {
        return "-fx-background-color: rgba(27, 24, 20, 0.96);"
                + "-fx-border-color: #d6aa65;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 28, 0.32, 0, 8);";
    }

    private String mapHeaderButtonStyle() {
        return "-fx-background-color: rgba(210, 162, 86, 0.88);"
                + "-fx-text-fill: #20150d;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: rgba(255, 231, 165, 0.72) rgba(95, 54, 24, 0.78) rgba(55, 30, 16, 0.9) rgba(255, 225, 150, 0.72);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 7 15 7 15;"
                + "-fx-cursor: hand;";
    }

    private double mapNodeX(MapNode node, double canvasWidth) {
        double left = canvasWidth * MAP_ROUTE_LEFT_RATIO;
        double right = canvasWidth * MAP_ROUTE_RIGHT_RATIO;
        double laneRatio = node.getLane() / 6.0;
        return left + (right - left) * laneRatio - MAP_NODE_SIZE / 2;
    }

    private double mapNodeY(MapNode node, int maxFloor, double canvasHeight) {
        double top = canvasHeight * MAP_ROUTE_TOP_RATIO;
        double bottom = canvasHeight * MAP_ROUTE_BOTTOM_RATIO;
        double floorRatio = maxFloor <= 1 ? 0 : (node.getFloor() - 1.0) / (maxFloor - 1.0);
        return bottom - (bottom - top) * floorRatio - MAP_NODE_SIZE / 2;
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

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private StackPane createBattleView() {
        updateBattleBackgroundSelection();
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #020203;");

        StackPane backgroundLayer = createBattleBackgroundLayer();
        StackPane overlayLayer = new StackPane();
        overlayLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlayLayer.setPickOnBounds(false);

        BorderPane battleLayer = new BorderPane();
        battleLayer.setPadding(new Insets(14, 24, 12, 24));
        battleLayer.setTop(createBattleHeader(overlayLayer));
        battleLayer.setCenter(createBattleCenter());
        battleLayer.setBottom(createHandArea());

        root.getChildren().addAll(backgroundLayer, battleLayer, overlayLayer);
        refreshBattle();
        return root;
    }

    private void updateBattleBackgroundSelection() {
        BattleState battle = engine.getState();
        if (battle != backgroundBattle) {
            backgroundBattle = battle;
            var resource = GameApplication.class.getResource(BATTLE_SCENE_BACKGROUND_RESOURCE);
            selectedBattleBackground = resource == null ? randomBattleBackgroundResource() : resource.toExternalForm();
            selectedBattleBackgroundImage = selectedBattleBackground == null ? null : new Image(selectedBattleBackground);
        }
    }

    private HBox createBattleHeader(StackPane overlayLayer) {
        RunState run = engine.getRunState();
        String nodeName = run != null && run.getCurrentNode() != null ? run.getCurrentNode().getName() : "单场战斗";

        Label title = new Label(nodeName);
        title.setStyle("-fx-text-fill: #f2e8cf; -fx-font-size: 20px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.88), 4, 0.6, 0, 1);");

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #d7d0c0; -fx-font-size: 13px; -fx-font-weight: bold;");

        VBox battleStatus = new VBox(1, title, statusLabel);
        battleStatus.setAlignment(Pos.CENTER);
        battleStatus.setPadding(new Insets(7, 18, 7, 18));
        battleStatus.setMinWidth(250);
        battleStatus.setStyle("-fx-background-color: linear-gradient(to right, rgba(13, 10, 10, 0.16), rgba(16, 13, 13, 0.78) 22%, rgba(16, 13, 13, 0.78) 78%, rgba(13, 10, 10, 0.16));"
                + "-fx-border-color: rgba(98, 76, 58, 0.18) rgba(179, 142, 90, 0.48) rgba(77, 45, 37, 0.42) rgba(179, 142, 90, 0.48);"
                + "-fx-border-width: 0 1 1 1;");

        HBox goldCounter = createResourceCounter(BATTLE_GOLD_RESOURCE, String.valueOf(run == null ? 0 : run.getGold()), "金币");

        HBox leftStatus = new HBox(10, createPlayerHeaderPanel(), goldCounter);
        leftStatus.setAlignment(Pos.TOP_LEFT);

        Button deckButton = createBattleIconButton(BATTLE_DECK_BUTTON_RESOURCE, "牌组");
        deckButton.setOnAction(event -> showBattleDeckOverlay(overlayLayer));

        Button settingsButton = createBattleIconButton(BATTLE_SETTINGS_RESOURCE, "设置");
        settingsButton.setOnAction(event -> showBattleSettingsOverlay(overlayLayer));

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox actions = new HBox(BATTLE_ACTION_BUTTON_GAP, deckButton, settingsButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(14, leftStatus, leftSpacer, battleStatus, rightSpacer, actions);
        header.setAlignment(Pos.TOP_CENTER);
        header.setPadding(new Insets(0, 0, 6, 0));
        return header;
    }

    private void showBattleDeckOverlay(StackPane overlayLayer) {
        List<Card> deck = currentDeck();
        DeckSummary summary = DeckSummary.from(deck);

        Label title = new Label("当前牌组");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 28px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.9), 5, 0.6, 0, 2);");

        HBox stats = new HBox(12,
                statBox("总张数", summary.getTotalCards() + " 张"),
                statBox("平均费用", String.format(Locale.ROOT, "%.2f", summary.getAverageCost())),
                statBox("攻击", summary.getAttackCount() + " 张"),
                statBox("技能", summary.getSkillCount() + " 张"),
                statBox("战术", summary.getTacticCount() + " 张")
        );
        stats.setAlignment(Pos.CENTER_LEFT);

        ScrollPane cards = createBattleDeckCardGrid(deck);
        VBox.setVgrow(cards, Priority.ALWAYS);

        Button close = new Button("关闭");
        close.setStyle(primaryButtonStyle());
        close.setOnAction(event -> closeBattleOverlay(overlayLayer));

        VBox panel = new VBox(16, title, stats, cards, close);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(980, 680);
        panel.setPadding(new Insets(24));
        panel.setStyle(battleModalPanelStyle());
        showBattleOverlay(overlayLayer, panel);
    }

    private ScrollPane createBattleDeckCardGrid(List<Card> deck) {
        TilePane cardGrid = new TilePane();
        cardGrid.setHgap(12);
        cardGrid.setVgap(14);
        cardGrid.setPrefColumns(5);
        cardGrid.setPadding(new Insets(8));

        if (deck.isEmpty()) {
            Label empty = new Label("当前没有可查看的卡牌。");
            empty.setStyle("-fx-text-fill: #ddd6c3; -fx-font-size: 15px; -fx-font-weight: bold;");
            cardGrid.getChildren().add(empty);
        } else {
            for (Card card : deck) {
                StackPane face = cardFace(card);
                face.setMouseTransparent(true);
                StackPane slot = new StackPane(face);
                slot.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
                slot.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
                slot.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
                cardGrid.getChildren().add(slot);
            }
        }

        ScrollPane scroll = new ScrollPane(cardGrid);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(420);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"
                + "-fx-padding: 0;");
        return scroll;
    }

    private void showBattleSettingsOverlay(StackPane overlayLayer) {
        Label title = new Label("游戏菜单");
        title.setStyle("-fx-text-fill: #f6dfad; -fx-font-size: 28px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.9), 5, 0.6, 0, 2);");

        Button continueButton = new Button("继续游戏");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(primaryButtonStyle() + "-fx-font-size: 16px; -fx-padding: 10 28 10 28;");
        continueButton.setOnAction(event -> closeBattleOverlay(overlayLayer));

        Button mainMenuButton = new Button("返回主菜单");
        mainMenuButton.setMaxWidth(Double.MAX_VALUE);
        mainMenuButton.setStyle(secondaryButtonStyle() + "-fx-font-size: 15px; -fx-padding: 10 28 10 28;");
        mainMenuButton.setOnAction(event -> setRoot(createMainMenu()));

        Button exitButton = new Button("退出游戏");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setStyle(secondaryButtonStyle() + "-fx-font-size: 15px; -fx-padding: 10 28 10 28;");
        exitButton.setOnAction(event -> stage.close());

        VBox actions = new VBox(12, continueButton, mainMenuButton, exitButton);
        actions.setAlignment(Pos.CENTER);
        actions.setPrefWidth(240);

        VBox panel = new VBox(20, title, actions);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxSize(380, 320);
        panel.setPadding(new Insets(28));
        panel.setStyle(battleModalPanelStyle());
        showBattleOverlay(overlayLayer, panel);
    }

    private void showBattleOverlay(StackPane overlayLayer, Node panel) {
        StackPane backdrop = new StackPane(panel);
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.58);");
        StackPane.setAlignment(panel, Pos.CENTER);
        overlayLayer.getChildren().setAll(backdrop);
        overlayLayer.setPickOnBounds(true);
    }

    private void closeBattleOverlay(StackPane overlayLayer) {
        overlayLayer.getChildren().clear();
        overlayLayer.setPickOnBounds(false);
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

        Label tip = new Label("非 Boss 战胜利后可选择奖励牌或跳过换金币；营地可恢复 30% 最大生命或升级，商店可购买或删牌。");
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

        Label name = new Label(entry.name()
                + "｜" + entry.rarity().getDisplayName()
                + "｜" + entry.type().getDisplayName()
                + "｜费用 " + entry.cost());
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
        root.setTop(createNodeChoiceHeader("营地", run, "休息恢复最大生命的 30%，或升级一张未升级卡牌。"));

        int restHealAmount = GameEngine.restHealAmount(run.getPlayer().getMaxHealth());
        Button restButton = new Button("休息 +" + restHealAmount + " 生命（30% 最大生命）");
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
                    ? cardButtonStyle()
                    : cardButtonStyle() + "-fx-opacity: 0.45;");
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
        VBox enemyUnit = new VBox(5, enemyHud, enemyEffectTarget);
        enemyUnit.setAlignment(Pos.CENTER);
        enemyUnit.setTranslateY(8);

        playerLabel = new Label();
        playerBlockLabel = new Label();
        playerHealthBar = healthBar();
        VBox playerHud = combatantHud("玩家", playerLabel, playerHealthBar, playerBlockLabel);
        CharacterPortrait playerPortrait = CharacterPortrait.player();
        playerEffectLayer = createEffectLayer();
        playerEffectTarget = createEffectTarget(playerPortrait, playerEffectLayer);
        VBox playerUnit = new VBox(4, playerHud, playerEffectTarget);
        playerUnit.setAlignment(Pos.CENTER);
        playerUnit.setTranslateY(52);

        HBox units = new HBox(210, playerUnit, enemyUnit);
        units.setAlignment(Pos.BOTTOM_CENTER);
        units.setPadding(new Insets(42, 56, 22, 56));

        StackPane battlefield = new StackPane(units);
        battlefield.setMinHeight(300);
        battlefield.setPadding(new Insets(0, 0, 4, 0));
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
        handBox = new HBox(HAND_CARD_SPACING);
        handBox.setAlignment(Pos.BOTTOM_CENTER);
        handBox.setFillHeight(false);
        handBox.setPadding(new Insets(0, 24, 0, 24));
        handBox.setMinHeight(HAND_CARD_SLOT_HEIGHT);
        handBox.setPrefHeight(HAND_CARD_SLOT_HEIGHT);

        ScrollPane handScroll = new ScrollPane(handBox);
        handScroll.setFitToHeight(true);
        handScroll.setFitToWidth(true);
        handScroll.setPannable(true);
        handScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        handScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        handScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;"
                + "-fx-padding: 0;");

        StackPane handWell = new StackPane(handScroll);
        handWell.setMinHeight(HAND_CARD_SLOT_HEIGHT);
        handWell.setPrefHeight(HAND_CARD_SLOT_HEIGHT);
        handWell.setMaxHeight(HAND_CARD_SLOT_HEIGHT + 12);
        handWell.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        endTurnButton = new Button("结束回合");
        Node endTurnArt = createUiImageView(BATTLE_END_TURN_RESOURCE, 220, 92);
        endTurnButton.setGraphic(endTurnArt);
        endTurnButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        endTurnButton.setPrefSize(226, 96);
        endTurnButton.setMinSize(226, 96);
        endTurnButton.setMaxSize(226, 96);
        endTurnButton.setOnAction(event -> {
            List<Set<CardVisualEffect>> enemyVisualEffects = engine.endTurn();
            playEnemyCardVisualEffects(enemyVisualEffects);
            afterBattleAction();
        });
        endTurnButton.setStyle(endTurnButtonStyle());

        energyLabel = new Label();
        energyLabel.setAlignment(Pos.CENTER);
        energyLabel.setStyle("-fx-text-fill: #efe5ff; -fx-font-size: 30px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(30, 0, 48, 0.95), 8, 0.75, 0, 2);");

        StackPane energyCore = new StackPane(createUiImageView(BATTLE_ENERGY_ORB_RESOURCE, 132, 132), energyLabel);
        energyCore.setAlignment(Pos.CENTER);
        energyCore.setPrefSize(136, 136);
        energyCore.setMinSize(136, 136);
        energyCore.setMaxSize(136, 136);
        energyLabel.setPrefSize(96, 50);
        energyLabel.setMinSize(96, 50);
        energyLabel.setMaxSize(96, 50);
        StackPane.setAlignment(energyLabel, Pos.CENTER);

        pileLabel = new Label();
        pileLabel.setWrapText(true);
        pileLabel.setTextAlignment(TextAlignment.CENTER);
        pileLabel.setStyle("-fx-text-fill: #d7d0c2; -fx-font-size: 12px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.85), 3, 0.55, 0, 1);");

        drawPileCountLabel = pileCountLabel();
        discardPileCountLabel = pileCountLabel();

        VBox energyPanel = new VBox(0,
                energyCore,
                createPileCounter(BATTLE_DRAW_PILE_RESOURCE, "抽牌堆", drawPileCountLabel));
        energyPanel.setAlignment(Pos.CENTER);
        energyPanel.setMinWidth(168);
        energyPanel.setPrefWidth(168);
        energyPanel.setPadding(new Insets(0, 6, 10, 0));

        VBox actionPanel = new VBox(6,
                endTurnButton,
                createPileCounter(BATTLE_DISCARD_PILE_RESOURCE, "弃牌堆", discardPileCountLabel),
                pileLabel);
        actionPanel.setAlignment(Pos.CENTER);
        actionPanel.setMinWidth(246);
        actionPanel.setPrefWidth(246);
        actionPanel.setPadding(new Insets(0, 0, 8, 4));

        HBox handArea = new HBox(12, energyPanel, handWell, actionPanel);
        handArea.setAlignment(Pos.BOTTOM_CENTER);
        handArea.setPadding(new Insets(0, 0, 0, 0));
        handArea.setStyle("-fx-background-color: transparent;");
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
        title.setStyle("-fx-text-fill: #caa775; -fx-font-size: 13px; -fx-font-weight: bold;");

        nameLabel.setStyle("-fx-text-fill: #f7f0df; -fx-font-size: 15px; -fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 3, 0.55, 0, 1);");
        blockLabel.setStyle("-fx-text-fill: #d6e9ff; -fx-font-size: 13px; -fx-font-weight: bold;");

        VBox box = new VBox(5, title, nameLabel, healthBar, blockLabel);
        for (Label label : extraLabels) {
            label.setStyle("-fx-text-fill: #f0c172; -fx-font-size: 13px; -fx-font-weight: bold;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 3, 0.5, 0, 1);");
            label.setWrapText(true);
            box.getChildren().add(label);
        }
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(268);
        box.setMaxWidth(286);
        box.setPadding(new Insets(9, 15, 10, 15));
        box.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(18, 14, 13, 0.90), rgba(7, 6, 7, 0.86));"
                + "-fx-border-color: rgba(205, 169, 108, 0.42) rgba(67, 43, 36, 0.70) rgba(35, 21, 20, 0.82) rgba(205, 169, 108, 0.32);"
                + "-fx-border-width: 1.4;"
                + "-fx-border-radius: 4;"
                + "-fx-background-radius: 4;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.70), 12, 0.32, 0, 4);");
        return box;
    }

    private StackPane createBattleBackgroundLayer() {
        StackPane backdrop = new StackPane();
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        if (selectedBattleBackgroundImage != null && !selectedBattleBackgroundImage.isError()) {
            ImageView background = new ImageView(selectedBattleBackgroundImage);
            background.setManaged(false);
            background.setPreserveRatio(false);
            background.setSmooth(true);
            background.setCache(true);
            backdrop.widthProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            backdrop.heightProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            Platform.runLater(() -> fitBattleBackground(background, backdrop));
            backdrop.getChildren().add(background);
        } else {
            Region fallback = new Region();
            fallback.setStyle("-fx-background-color: linear-gradient(to bottom, #0b0c10, #171016 55%, #080607);");
            fallback.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            backdrop.getChildren().add(fallback);
        }

        Region depthShade = new Region();
        depthShade.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        depthShade.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(0, 0, 0, 0.18), rgba(0, 0, 0, 0.04) 45%, rgba(0, 0, 0, 0.48));");

        Region edgeVignette = new Region();
        edgeVignette.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        edgeVignette.setStyle("-fx-background-color: radial-gradient(center 50% 48%, radius 78%, rgba(0, 0, 0, 0.00), rgba(0, 0, 0, 0.12) 58%, rgba(0, 0, 0, 0.64) 100%);");

        backdrop.getChildren().addAll(depthShade, edgeVignette);
        return backdrop;
    }

    private ImageView createUiImageView(String resourcePath, double fitWidth, double fitHeight) {
        ImageView view = new ImageView();
        var resource = GameApplication.class.getResource(resourcePath);
        if (resource != null) {
            Image image = uiImages.computeIfAbsent(resourcePath, ignored -> new Image(resource.toExternalForm()));
            view.setImage(image);
            view.setViewport(uiViewport(resourcePath, image));
        }
        view.setFitWidth(fitWidth);
        view.setFitHeight(fitHeight);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setCache(true);
        view.setMouseTransparent(true);
        return view;
    }

    private Rectangle2D uiViewport(String resourcePath, Image image) {
        Rectangle2D override = uiViewportOverride(resourcePath, image);
        if (override != null) {
            return override;
        }
        return uiImageViewports.computeIfAbsent(resourcePath, ignored -> trimOpaqueViewport(image));
    }

    private Rectangle2D uiViewportOverride(String resourcePath, Image image) {
        if (BATTLE_GOLD_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 260, 225, 475, 475);
        }
        if (BATTLE_DECK_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 500, 190, 570, 650);
        }
        return null;
    }

    private Rectangle2D clampedViewport(Image image, double x, double y, double width, double height) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0) {
            return Rectangle2D.EMPTY;
        }
        double safeX = clamp(x, 0, imageWidth - 1);
        double safeY = clamp(y, 0, imageHeight - 1);
        double safeWidth = clamp(width, 1, imageWidth - safeX);
        double safeHeight = clamp(height, 1, imageHeight - safeY);
        return new Rectangle2D(safeX, safeY, safeWidth, safeHeight);
    }

    private Rectangle2D trimOpaqueViewport(Image image) {
        int width = (int) Math.round(image.getWidth());
        int height = (int) Math.round(image.getHeight());
        var reader = image.getPixelReader();
        if (reader == null || width <= 0 || height <= 0) {
            return Rectangle2D.EMPTY;
        }

        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (reader.getColor(x, y).getOpacity() > 0.06) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return new Rectangle2D(0, 0, width, height);
        }
        return new Rectangle2D(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private void clipToBounds(Region region) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());
        region.setClip(clip);
    }

    private StackPane createIconSlot(String resourcePath,
                                     double slotWidth,
                                     double slotHeight,
                                     double iconWidth,
                                     double iconHeight) {
        StackPane slot = new StackPane(createUiImageView(resourcePath, iconWidth, iconHeight));
        slot.setAlignment(Pos.CENTER);
        slot.setPrefSize(slotWidth, slotHeight);
        slot.setMinSize(slotWidth, slotHeight);
        slot.setMaxSize(slotWidth, slotHeight);
        return slot;
    }

    private HBox createResourceCounter(String resourcePath, String value, String tooltipText) {
        Label amount = new Label(value);
        amount.setMinHeight(RESOURCE_ICON_SLOT);
        amount.setAlignment(Pos.CENTER_LEFT);
        amount.setStyle("-fx-text-fill: #f0dcc0;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.78), 3, 0.55, 0, 1);");

        HBox counter = new HBox(5,
                createIconSlot(resourcePath, RESOURCE_ICON_SLOT, RESOURCE_ICON_SLOT, RESOURCE_ICON_SIZE, RESOURCE_ICON_SIZE),
                amount);
        counter.setAlignment(Pos.CENTER_LEFT);
        counter.setMinHeight(RESOURCE_ICON_SLOT);
        counter.setPadding(new Insets(0, 2, 0, 0));
        Tooltip.install(counter, new Tooltip(tooltipText));
        return counter;
    }

    private Button createBattleIconButton(String resourcePath, String tooltipText) {
        Button button = new Button();
        button.setGraphic(createIconSlot(resourcePath,
                BATTLE_ACTION_BUTTON_SIZE,
                BATTLE_ACTION_BUTTON_SIZE,
                BATTLE_ACTION_ICON_SIZE,
                BATTLE_ACTION_ICON_SIZE));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setPrefSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setMinSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setMaxSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setTooltip(new Tooltip(tooltipText));
        button.setStyle(battleIconButtonStyle(false, false));
        button.setOnMouseEntered(event -> {
            button.setScaleX(1.04);
            button.setScaleY(1.04);
            button.setStyle(battleIconButtonStyle(true, false));
        });
        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setTranslateY(0);
            button.setStyle(battleIconButtonStyle(false, false));
        });
        button.setOnMousePressed(event -> {
            button.setScaleX(0.98);
            button.setScaleY(0.98);
            button.setTranslateY(1);
            button.setStyle(battleIconButtonStyle(true, true));
        });
        button.setOnMouseReleased(event -> {
            button.setScaleX(button.isHover() ? 1.04 : 1.0);
            button.setScaleY(button.isHover() ? 1.04 : 1.0);
            button.setTranslateY(0);
            button.setStyle(battleIconButtonStyle(button.isHover(), false));
        });
        return button;
    }

    private StackPane createPlayerHeaderPanel() {
        playerHeaderNameLabel = new Label();
        playerHeaderNameLabel.setStyle("-fx-text-fill: #f1e7d2;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.90), 4, 0.6, 0, 1);");

        playerHeaderHealthLabel = new Label();
        playerHeaderHealthLabel.setStyle("-fx-text-fill: #d16458;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.90), 4, 0.6, 0, 1);");
        playerHeaderHealthLabel.setMinSize(86, 24);
        playerHeaderHealthLabel.setAlignment(Pos.CENTER_LEFT);

        StackPane panel = new StackPane(createUiImageView(BATTLE_PLAYER_FRAME_RESOURCE, 276, 92), playerHeaderHealthLabel);
        panel.setPrefSize(276, 92);
        panel.setMinSize(276, 92);
        panel.setMaxSize(276, 92);
        clipToBounds(panel);
        StackPane.setAlignment(playerHeaderHealthLabel, Pos.TOP_LEFT);
        StackPane.setMargin(playerHeaderHealthLabel, new Insets(53, 0, 0, 112));
        return panel;
    }

    private VBox createPileCounter(String resourcePath, String tooltipText, Label countLabel) {
        StackPane iconSlot = new StackPane(createUiImageView(resourcePath, PILE_ICON_WIDTH, PILE_ICON_HEIGHT), countLabel);
        iconSlot.setAlignment(Pos.CENTER);
        iconSlot.setPrefSize(PILE_ICON_SLOT_WIDTH, PILE_ICON_SLOT_HEIGHT);
        iconSlot.setMinSize(PILE_ICON_SLOT_WIDTH, PILE_ICON_SLOT_HEIGHT);
        iconSlot.setMaxSize(PILE_ICON_SLOT_WIDTH, PILE_ICON_SLOT_HEIGHT);
        iconSlot.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.74), 9, 0.35, 0, 3);");

        VBox pile = new VBox(0, iconSlot);
        pile.setAlignment(Pos.CENTER);
        pile.setPrefWidth(PILE_ICON_SLOT_WIDTH);
        pile.setMinWidth(PILE_ICON_SLOT_WIDTH);
        pile.setMaxWidth(PILE_ICON_SLOT_WIDTH);
        pile.setOnMouseEntered(event -> {
            pile.setScaleX(1.05);
            pile.setScaleY(1.05);
        });
        pile.setOnMouseExited(event -> {
            pile.setScaleX(1.0);
            pile.setScaleY(1.0);
        });
        Tooltip.install(pile, new Tooltip(tooltipText));
        StackPane.setAlignment(countLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(countLabel, new Insets(0, 0, 8, 0));
        return pile;
    }

    private Label pileCountLabel() {
        Label label = new Label("0");
        label.setAlignment(Pos.CENTER);
        label.setMinSize(22, 18);
        label.setMaxSize(34, 22);
        label.setStyle("-fx-text-fill: #f2eee4;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: transparent;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 4, 0.72, 0, 1);");
        return label;
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
        playerHeaderNameLabel.setText(state.getPlayer().getName());
        playerHeaderHealthLabel.setText(state.getPlayer().getHealth() + "/" + state.getPlayer().getMaxHealth());
        playerHealthBar.setProgress((double) state.getPlayer().getHealth() / state.getPlayer().getMaxHealth());
        enemyHealthBar.setProgress((double) state.getEnemy().getHealth() / state.getEnemy().getMaxHealth());
        playerBlockLabel.setText("格挡：" + state.getPlayer().getBlock());
        enemyBlockLabel.setText("格挡：" + state.getEnemy().getBlock());
        intentLabel.setText(enemyIntentText(state));
        energyLabel.setText(state.getEnergy() + "/" + state.getMaxEnergy());
        drawPileCountLabel.setText(String.valueOf(state.getDrawPile().size()));
        discardPileCountLabel.setText(String.valueOf(state.getDiscardPile().size()));
        pileLabel.setText("当前牌组 " + state.getDeck().size() + " 张");
        statusLabel.setText(statusText(state.getStatus()));

        refreshHand(state);
        endTurnButton.setDisable(state.getStatus() != GameStatus.IN_PROGRESS);
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
            StackPane slot = createHandCardSlot(card, index, totalCards, canPlay, () -> {
                Set<CardVisualEffect> visualEffects = card.getVisualEffects();
                if (engine.playCard(index)) {
                    playPlayerCardVisualEffects(visualEffects);
                }
                afterBattleAction();
            });
            handBox.getChildren().add(slot);
        }
    }

    private StackPane createHandCardSlot(Card card, int index, int totalCards, boolean canPlay, Runnable playAction) {
        double baseRotate = cardRotation(index, totalCards);
        double baseTranslateY = cardFanTranslateY(index, totalCards);
        double baseViewOrder = Math.abs(index - (totalCards - 1) / 2.0);

        StackPane face = cardFace(card);
        face.setMouseTransparent(true);
        face.setRotate(baseRotate);
        face.setTranslateY(baseTranslateY);
        face.setOpacity(canPlay ? 1.0 : 0.64);

        StackPane slot = new StackPane(face);
        slot.setAlignment(Pos.CENTER);
        slot.setPickOnBounds(true);
        slot.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setMinSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setViewOrder(baseViewOrder);
        slot.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Tooltip.install(slot, new Tooltip(card.getName() + "\n费用：" + card.getCost() + "\n" + card.getDescription()));

        slot.setOnMouseClicked(event -> {
            if (!canPlay || !face.getBoundsInParent().contains(event.getX(), event.getY())) {
                return;
            }
            playAction.run();
            event.consume();
        });
        slot.setOnMouseEntered(event -> {
            double radians = Math.toRadians(baseRotate);
            face.setRotate(baseRotate * 0.35);
            face.setTranslateX(Math.sin(radians) * HAND_CARD_HOVER_PULL);
            face.setTranslateY(baseTranslateY - Math.cos(radians) * HAND_CARD_HOVER_PULL);
            face.setScaleX(HAND_CARD_HOVER_SCALE);
            face.setScaleY(HAND_CARD_HOVER_SCALE);
            face.setOpacity(1.0);
            face.setEffect(cardHoverEffect(card.getRarity()));
            slot.setViewOrder(-1);
        });
        slot.setOnMouseExited(event -> {
            face.setRotate(baseRotate);
            face.setTranslateX(0);
            face.setTranslateY(baseTranslateY);
            face.setScaleX(1.0);
            face.setScaleY(1.0);
            face.setOpacity(canPlay ? 1.0 : 0.64);
            face.setEffect(null);
            slot.setViewOrder(baseViewOrder);
        });
        slot.setOnMousePressed(event -> {
            if (canPlay) {
                face.setEffect(cardActiveEffect(card.getRarity()));
            }
        });
        slot.setOnMouseReleased(event -> {
            if (slot.isHover()) {
                face.setEffect(cardHoverEffect(card.getRarity()));
            }
        });
        return slot;
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
        ImageView template = new ImageView(cardTemplateImage(card.getRarity()));
        template.setFitWidth(HAND_CARD_WIDTH);
        template.setFitHeight(HAND_CARD_HEIGHT);
        template.setPreserveRatio(true);
        template.setSmooth(true);
        template.setCache(true);

        Label cost = new Label(String.valueOf(card.getCost()));
        cost.setMinSize(34, 34);
        cost.setMaxSize(34, 34);
        cost.setAlignment(Pos.CENTER);
        cost.setStyle("-fx-text-fill: " + cardCostTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.92), 4, 0.75, 0, 1);");
        StackPane costBadge = new StackPane(createUiImageView(BATTLE_ENERGY_ORB_RESOURCE, 38, 38), cost);
        costBadge.setPrefSize(42, 42);
        costBadge.setMinSize(42, 42);
        costBadge.setMaxSize(42, 42);
        StackPane.setAlignment(costBadge, Pos.TOP_LEFT);
        StackPane.setMargin(costBadge, new Insets(15, 0, 0, 9));

        Label name = new Label(card.getName());
        name.setWrapText(true);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setAlignment(Pos.CENTER);
        name.setMaxWidth(HAND_CARD_WIDTH - 64);
        name.setMinHeight(30);
        name.setMaxHeight(34);
        name.setStyle("-fx-text-fill: " + cardTitleTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 3, 0.7, 0, 1);");
        StackPane.setAlignment(name, Pos.TOP_CENTER);
        StackPane.setMargin(name, new Insets(17, 16, 0, 42));

        Label type = new Label(card.getType().getDisplayName());
        type.setAlignment(Pos.CENTER);
        type.setTextAlignment(TextAlignment.CENTER);
        type.setMinSize(64, 19);
        type.setMaxSize(76, 20);
        type.setStyle("-fx-text-fill: " + cardTypeTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.82), 3, 0.65, 0, 1);");
        StackPane.setAlignment(type, Pos.BOTTOM_CENTER);
        StackPane.setMargin(type, new Insets(0, 0, 12, 0));

        Label desc = new Label(card.getDescription());
        desc.setWrapText(true);
        desc.setTextAlignment(TextAlignment.CENTER);
        desc.setAlignment(Pos.CENTER);
        desc.setMaxWidth(HAND_CARD_WIDTH - 42);
        desc.setMinHeight(64);
        desc.setMaxHeight(64);
        desc.setStyle("-fx-text-fill: " + cardDescriptionTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + cardDescriptionTextEffect(card.getRarity()));
        StackPane.setAlignment(desc, Pos.BOTTOM_CENTER);
        StackPane.setMargin(desc, new Insets(0, 20, 36, 20));

        StackPane face = new StackPane(
                template,
                name,
                desc,
                type,
                costBadge
        );
        face.setAlignment(Pos.CENTER);
        face.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        face.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        face.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        return face;
    }

    private Region cardRegion(double width, double height, String style) {
        Region region = new Region();
        region.setPrefSize(width, height);
        region.setMinSize(width, height);
        region.setMaxSize(width, height);
        region.setStyle(style);
        return region;
    }

    private Image cardTemplateImage(CardRarity rarity) {
        return cardTemplateImages.computeIfAbsent(rarity, key -> {
            String resourcePath = cardTemplateResource(key);
            var resource = GameApplication.class.getResource(resourcePath);
            if (resource == null) {
                throw new IllegalStateException("Missing card template: " + resourcePath);
            }
            return new Image(resource.toExternalForm());
        });
    }

    private String cardTemplateResource(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON_CARD_TEMPLATE_RESOURCE;
            case UNCOMMON -> RARE_CARD_TEMPLATE_RESOURCE;
            case RARE -> RARE_CARD_TEMPLATE_RESOURCE;
            case LEGENDARY -> LEGENDARY_CARD_TEMPLATE_RESOURCE;
            case SPECIAL -> SPECIAL_CARD_TEMPLATE_RESOURCE;
        };
    }

    private String cardTitleTextColor(CardRarity rarity) {
        return switch (rarity) {
            case COMMON, LEGENDARY -> "#fff0c8";
            case UNCOMMON, RARE -> "#eefbff";
            case SPECIAL -> "#f3ecff";
        };
    }

    private String cardDescriptionTextColor(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> "#211c16";
            case UNCOMMON, RARE -> "#e9f8ff";
            case LEGENDARY -> "#2b1a0b";
            case SPECIAL -> "#ece9f1";
        };
    }

    private String cardCostTextColor(CardRarity rarity) {
        return switch (rarity) {
            case COMMON, LEGENDARY -> "#fff2c8";
            case UNCOMMON, RARE -> "#e5f8ff";
            case SPECIAL -> "#eaffd8";
        };
    }

    private String cardTypeTextColor(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> "#3a3026";
            case UNCOMMON, RARE -> "#d8f4ff";
            case LEGENDARY -> "#3b210e";
            case SPECIAL -> "#efe5ff";
        };
    }

    private String cardDescriptionTextEffect(CardRarity rarity) {
        return switch (rarity) {
            case COMMON, LEGENDARY -> "-fx-effect: dropshadow(gaussian, rgba(255, 245, 215, 0.46), 1.4, 0.25, 0, 0);";
            case UNCOMMON, RARE, SPECIAL -> "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.78), 2, 0.6, 0, 1);";
        };
    }

    private javafx.scene.effect.DropShadow cardHoverEffect(CardRarity rarity) {
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setRadius(22);
        glow.setSpread(0.25);
        glow.setOffsetY(4);
        glow.setColor(cardGlowColor(rarity, 0.76));
        return glow;
    }

    private javafx.scene.effect.DropShadow cardActiveEffect(CardRarity rarity) {
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setRadius(28);
        glow.setSpread(0.34);
        glow.setOffsetY(3);
        glow.setColor(cardGlowColor(rarity, 0.92));
        return glow;
    }

    private Color cardGlowColor(CardRarity rarity, double opacity) {
        return switch (rarity) {
            case COMMON -> Color.web("#7b2441", opacity);
            case UNCOMMON, RARE -> Color.web("#6134ff", opacity);
            case LEGENDARY -> Color.web("#9b3b18", opacity);
            case SPECIAL -> Color.web("#6b2cff", opacity);
        };
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
            reward.setStyle(cardButtonStyle());
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
        String background;
        String border;
        if (node.isCompleted()) {
            background = "radial-gradient(center 50% 42%, radius 70%, rgba(160, 151, 119, 0.78), rgba(73, 63, 42, 0.76))";
            border = "#78936f";
        } else if (node.isAvailable()) {
            background = "radial-gradient(center 50% 42%, radius 70%, rgba(232, 205, 145, 0.96), rgba(110, 79, 42, 0.88))";
            border = "#f2c078";
        } else {
            background = "radial-gradient(center 50% 42%, radius 70%, rgba(117, 99, 68, 0.72), rgba(48, 40, 28, 0.72))";
            border = "#5c4b34";
        }
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: #17222a;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 2.2;"
                + "-fx-border-radius: 28;"
                + "-fx-background-radius: 28;"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 0;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(24, 13, 6, 0.72), 10, 0.24, 0, 4);";
    }

    private String nodeConnectionColor(MapNode node) {
        if (node.isCompleted()) {
            return "#514b2d";
        }
        if (node.isAvailable()) {
            return "#3f2a17";
        }
        return "#3a3328";
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

    private record CardPalette(
            String frameTop,
            String frameMiddle,
            String frameBottom,
            String frameHighlight,
            String frameShadow,
            String innerStroke,
            String glow,
            String titleTop,
            String titleBottom,
            String artworkTop,
            String artworkBottom,
            String artworkStroke,
            String textTop,
            String textBottom,
            String textStroke,
            String titleText,
            String descText,
            String costTop,
            String costMiddle,
            String costBottom,
            String costText,
            String railTop,
            String railBottom,
            String gemTop,
            String gemBottom
    ) {
    }

    private String cardButtonStyle() {
        return "-fx-background-color: transparent;"
                + "-fx-padding: 0;"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: transparent;"
                + "-fx-cursor: hand;";
    }

    private String cardFrameStyle(CardPalette palette) {
        return "-fx-background-color: linear-gradient(to bottom, "
                + palette.frameTop() + ", "
                + palette.frameMiddle() + " 52%, "
                + palette.frameBottom() + ");"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: "
                + palette.frameHighlight() + " "
                + palette.frameShadow() + " "
                + palette.frameShadow() + " "
                + palette.frameHighlight() + ";"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 16;"
                + "-fx-effect: innershadow(gaussian, rgba(255, 255, 255, 0.12), 5, 0.2, 0, 1);";
    }

    private String cardTitlePlateStyle(CardPalette palette) {
        return "-fx-background-color: linear-gradient(to bottom, "
                + palette.titleTop() + ", " + palette.titleBottom() + ");"
                + "-fx-background-radius: 7 9 5 5;"
                + "-fx-border-color: " + palette.frameHighlight() + ";"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 7 9 5 5;";
    }

    private String cardArtworkStyle(CardPalette palette) {
        return "-fx-background-color: radial-gradient(center 50% 45%, radius 82%, "
                + palette.artworkTop() + ", " + palette.artworkBottom() + ");"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: " + palette.artworkStroke() + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.78), 15, 0.42, 0, 2);";
    }

    private String cardTextPanelStyle(CardPalette palette) {
        return "-fx-background-color: linear-gradient(to bottom, "
                + palette.textTop() + ", " + palette.textBottom() + ");"
                + "-fx-background-radius: 5 5 9 9;"
                + "-fx-border-color: " + palette.textStroke() + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 5 5 9 9;"
                + "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.48), 8, 0.3, 0, 1);";
    }

    private String cardRailStyle(CardPalette palette) {
        return "-fx-background-color: linear-gradient(to bottom, "
                + palette.railTop() + ", " + palette.railBottom() + ");"
                + "-fx-background-radius: 5;"
                + "-fx-border-color: " + palette.frameShadow() + ";"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 5;";
    }

    private String cardGemStyle(CardPalette palette) {
        return "-fx-background-color: radial-gradient(center 50% 42%, radius 72%, "
                + palette.gemTop() + ", " + palette.gemBottom() + ");"
                + "-fx-background-radius: 9;"
                + "-fx-border-color: " + palette.frameHighlight() + ";"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 9;";
    }

    private String cardCostStyle(CardPalette palette) {
        return "-fx-background-color: radial-gradient(center 45% 38%, radius 72%, "
                + palette.costTop() + ", "
                + palette.costMiddle() + " 60%, "
                + palette.costBottom() + " 100%);"
                + "-fx-background-radius: 18;"
                + "-fx-border-color: "
                + palette.frameHighlight() + " "
                + palette.frameShadow() + " "
                + palette.frameShadow() + " "
                + palette.frameHighlight() + ";"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 18;"
                + "-fx-text-fill: " + palette.costText() + ";"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.78), 8, 0.35, 0, 2);";
    }

    private CardPalette cardPalette(CardRarity rarity) {
        return switch (rarity) {
            case COMMON -> new CardPalette(
                    "#6d695d",
                    "#312f2a",
                    "#141311",
                    "#c4bda8",
                    "#171512",
                    "rgba(205, 196, 168, 0.42)",
                    "rgba(216, 207, 183, 0.30)",
                    "#4d493f",
                    "#1f1f1c",
                    "#484943",
                    "#171816",
                    "rgba(184, 177, 156, 0.72)",
                    "#d9d0bf",
                    "#958c7c",
                    "#3f3a32",
                    "#f5ead5",
                    "#1d1914",
                    "#fff0b0",
                    "#df7828",
                    "#6d2414",
                    "#fff8e8",
                    "#676156",
                    "#211f1b",
                    "#d5ccb7",
                    "#726755"
            );
            case UNCOMMON, RARE -> new CardPalette(
                    "#5ea9c8",
                    "#1d5e78",
                    "#0a2535",
                    "#b7efff",
                    "#062033",
                    "rgba(128, 225, 255, 0.48)",
                    "rgba(77, 204, 255, 0.44)",
                    "#2f6f89",
                    "#0d2d40",
                    "#30495a",
                    "#101b24",
                    "rgba(126, 219, 255, 0.62)",
                    "#2f3840",
                    "#171b20",
                    "#395d70",
                    "#eefbff",
                    "#d9eef7",
                    "#dff8ff",
                    "#1ba6e3",
                    "#075178",
                    "#f4fdff",
                    "#4386a5",
                    "#0c2739",
                    "#d4f6ff",
                    "#2f8dbc"
            );
            case LEGENDARY -> new CardPalette(
                    "#b7792a",
                    "#5f2e12",
                    "#21100a",
                    "#ffe0a3",
                    "#2f1308",
                    "rgba(255, 176, 69, 0.50)",
                    "rgba(255, 129, 28, 0.46)",
                    "#754116",
                    "#24100a",
                    "#704022",
                    "#1d0e0a",
                    "rgba(219, 128, 46, 0.70)",
                    "#e4d2b5",
                    "#ab9474",
                    "#5b3419",
                    "#fff2ce",
                    "#2f1b0d",
                    "#fff4bc",
                    "#d96d1d",
                    "#77300d",
                    "#fff7de",
                    "#9a5b20",
                    "#2b1208",
                    "#ffe0a0",
                    "#b46c22"
            );
            case SPECIAL -> new CardPalette(
                    "#7b5aa0",
                    "#294f25",
                    "#171020",
                    "#c9b5ff",
                    "#150a22",
                    "rgba(168, 238, 114, 0.46)",
                    "rgba(154, 88, 230, 0.42)",
                    "#4e356f",
                    "#172517",
                    "#345b36",
                    "#121f16",
                    "rgba(142, 222, 97, 0.58)",
                    "#353236",
                    "#18171a",
                    "#57406f",
                    "#f4ecff",
                    "#e9dff1",
                    "#e5ffd2",
                    "#59b934",
                    "#1b5d18",
                    "#f7ffe8",
                    "#6b4f8e",
                    "#18341b",
                    "#f0d7ff",
                    "#68c144"
            );
        };
    }

    private double cardRotation(int index, int totalCards) {
        if (totalCards <= 1) {
            return 0;
        }
        double middle = (totalCards - 1) / 2.0;
        double rotation = (index - middle) * HAND_CARD_FAN_STEP_DEGREES;
        return clamp(rotation, -HAND_CARD_MAX_ROTATION_DEGREES, HAND_CARD_MAX_ROTATION_DEGREES);
    }

    private double cardFanTranslateY(int index, int totalCards) {
        if (totalCards <= 1) {
            return 0;
        }
        double middle = (totalCards - 1) / 2.0;
        return Math.min(Math.abs(index - middle) * HAND_CARD_ARC_STEP, HAND_CARD_MAX_ARC_Y);
    }

    private String endTurnButtonStyle() {
        return "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
                + "-fx-padding: 0;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 12, 0.34, 0, 4);";
    }

    private String battleIconButtonStyle(boolean hover, boolean pressed) {
        String background = pressed
                ? "linear-gradient(to bottom, rgba(22, 15, 20, 0.96), rgba(9, 7, 10, 0.98))"
                : hover
                ? "linear-gradient(to bottom, rgba(55, 39, 54, 0.94), rgba(15, 10, 18, 0.96))"
                : "linear-gradient(to bottom, rgba(36, 30, 28, 0.84), rgba(12, 10, 11, 0.88))";
        String border = hover
                ? "rgba(159, 88, 199, 0.82) rgba(118, 28, 45, 0.82) rgba(67, 16, 30, 0.92) rgba(159, 88, 199, 0.72)"
                : "rgba(178, 139, 91, 0.45) rgba(67, 45, 45, 0.58) rgba(42, 23, 25, 0.72) rgba(118, 90, 73, 0.45)";
        String shadow = hover
                ? "dropshadow(gaussian, rgba(115, 39, 168, 0.58), 13, 0.34, 0, 3)"
                : "dropshadow(gaussian, rgba(0, 0, 0, 0.58), 7, 0.24, 0, 2)";
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: #e4d7c5;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 3;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: " + (hover ? "1.4" : "1") + ";"
                + "-fx-border-radius: 3;"
                + "-fx-padding: 0;"
                + "-fx-cursor: hand;"
                + "-fx-effect: " + shadow + ";";
    }

    private String battleModalPanelStyle() {
        return "-fx-background-color: linear-gradient(to bottom, rgba(31, 24, 25, 0.98), rgba(12, 10, 14, 0.98));"
                + "-fx-border-color: rgba(206, 153, 88, 0.78) rgba(91, 25, 40, 0.86) rgba(45, 13, 23, 0.92) rgba(126, 88, 156, 0.74);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.78), 30, 0.34, 0, 8);";
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
