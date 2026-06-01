package com.course.slay.ui;

import com.course.slay.domain.BattleState;
import com.course.slay.domain.GameStatus;
import com.course.slay.domain.card.Card;
import com.course.slay.domain.card.CardFactory;
import com.course.slay.domain.card.CardRarity;
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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
import java.util.Comparator;
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
    private static final double HAND_CARD_WIDTH = 186;
    private static final double HAND_CARD_HEIGHT = 279;
    private static final double HAND_CARD_ART_WIDTH = 142;
    private static final double HAND_CARD_ART_HEIGHT = 112;
    private static final double HAND_CARD_ART_TOP_MARGIN = 55;
    private static final double HAND_CARD_ART_CLIP_ARC = 8;
    private static final double COMMON_CARD_TYPE_BOTTOM_MARGIN = 22;
    private static final double DECORATED_CARD_TYPE_BOTTOM_MARGIN = 10;
    private static final double HAND_CARD_SLOT_HEIGHT = 342;
    private static final double HAND_CARD_SIDE_PADDING = 18;
    private static final double HAND_CARD_PREFERRED_STEP = 132;
    private static final double HAND_CARD_HOVER_SCALE = 1.04;
    private static final double HAND_CARD_HOVER_PULL = 30;
    private static final double HAND_CARD_FAN_STEP_DEGREES = 5.5;
    private static final double HAND_CARD_MAX_ROTATION_DEGREES = 13.5;
    private static final double HAND_CARD_ARC_STEP = 10;
    private static final double HAND_CARD_MAX_ARC_Y = 24;
    private static final double BATTLE_BOTTOM_UI_OFFSET_Y = 32;
    private static final double RESOURCE_ICON_SLOT = 32;
    private static final double RESOURCE_ICON_SIZE = 27;
    private static final double BATTLE_ACTION_BUTTON_SIZE = 64;
    private static final double BATTLE_ACTION_ICON_SIZE = 52;
    private static final double BATTLE_ACTION_BUTTON_GAP = 12;
    private static final double BATTLE_INFO_PANEL_ASPECT = 1403.0 / 569.0;
    private static final double PILE_ICON_SLOT_WIDTH = 124;
    private static final double PILE_ICON_SLOT_HEIGHT = 92;
    private static final double PILE_ICON_WIDTH = 112;
    private static final double PILE_ICON_HEIGHT = 84;
    private static final String MAP_BACKGROUND_RESOURCE = "/assets/backgrounds/map/mapBackground.png";
    private static final String MAP_LEGEND_RESOURCE = "/assets/backgrounds/map/tuli.png";
    private static final String BATTLE_UI_BASE = "/assets/ui/";
    private static final String MAIN_MENU_BACKGROUND_RESOURCE = BATTLE_UI_BASE + "开始界面.png";
    private static final String CHARACTER_SELECT_BACKGROUND_RESOURCE = BATTLE_UI_BASE + "选角界面背景.png";
    private static final String CHARACTER_SELECT_DEPART_BUTTON_RESOURCE = BATTLE_UI_BASE + "“出发”按钮.png";
    private static final String CHARACTER_SELECT_BACK_BUTTON_RESOURCE = BATTLE_UI_BASE + "“返回菜单”按钮.png";
    private static final String MAIN_MENU_START_BUTTON_RESOURCE = BATTLE_UI_BASE + "开始游戏按钮.png";
    private static final String MAIN_MENU_EXIT_BUTTON_RESOURCE = BATTLE_UI_BASE + "退出游戏按钮.png";
    private static final String ASSASSIN_CHARACTER_ID = "cinder_seeker";
    private static final double MAIN_MENU_START_BUTTON_X = 603;
    private static final double MAIN_MENU_START_BUTTON_Y = 555;
    private static final double MAIN_MENU_START_BUTTON_WIDTH = 380;
    private static final double MAIN_MENU_BUTTON_GAP = 18;
    private static final double MAIN_MENU_START_BUTTON_FALLBACK_ASPECT = 1536.0 / 440.0;
    private static final double CHARACTER_SELECT_HEADER_SPACE = 32;
    private static final double CHARACTER_SELECT_CHOICE_GAP = 54;
    private static final double CHARACTER_SELECT_CHOICE_AREA_OFFSET_Y = 28;
    private static final double CHARACTER_CHOICE_WIDTH = 292;
    private static final double CHARACTER_CHOICE_HEIGHT = 370;
    private static final double CHARACTER_CHOICE_PORTRAIT_AREA_WIDTH = 280;
    private static final double CHARACTER_CHOICE_PORTRAIT_AREA_HEIGHT = 318;
    private static final double CHARACTER_CHOICE_GLOW_WIDTH = 236;
    private static final double CHARACTER_CHOICE_GLOW_HEIGHT = 280;
    private static final double CHARACTER_SELECT_BUTTON_WIDTH = 305;
    private static final double CHARACTER_SELECT_BUTTON_GAP = 10;
    private static final double CHARACTER_SELECT_BUTTON_BOTTOM_MARGIN = 2;
    private static final String BATTLE_SCENE_BACKGROUND_RESOURCE = BATTLE_UI_BASE + "战斗背景.png";
    private static final String BATTLE_ENERGY_ORB_RESOURCE = BATTLE_UI_BASE + "能量球.png";
    private static final String BATTLE_END_TURN_RESOURCE = BATTLE_UI_BASE + "结束回合按钮.png";
    private static final String BATTLE_DRAW_PILE_RESOURCE = BATTLE_UI_BASE + "抽牌堆.png";
    private static final String BATTLE_DISCARD_PILE_RESOURCE = BATTLE_UI_BASE + "弃牌堆.png";
    private static final String BATTLE_BERSERKER_HEADER_HEALTH_FULL_RESOURCE = BATTLE_UI_BASE + "狂战士头像血槽满.png";
    private static final String BATTLE_BERSERKER_HEADER_HEALTH_EMPTY_RESOURCE = BATTLE_UI_BASE + "狂战士头像血条空.png";
    private static final String BATTLE_ASSASSIN_HEADER_HEALTH_FULL_RESOURCE = BATTLE_UI_BASE + "刺客头像血条满.png";
    private static final String BATTLE_ASSASSIN_HEADER_HEALTH_EMPTY_RESOURCE = BATTLE_UI_BASE + "刺客头像血条空.png";
    private static final String BATTLE_BERSERKER_AVATAR_RESOURCE = BATTLE_UI_BASE + "狂战士头像.png";
    private static final String BATTLE_EMPTY_HEALTH_RESOURCE = BATTLE_UI_BASE + "空血血槽.png";
    private static final String BATTLE_FULL_HEALTH_RESOURCE = BATTLE_UI_BASE + "满血血槽.png";
    private static final String BATTLE_FULL_SHIELD_RESOURCE = BATTLE_UI_BASE + "满护盾槽.png";
    private static final String BATTLE_SHIELD_EFFECT_RESOURCE = BATTLE_UI_BASE + "护盾特效.png";
    private static final String BATTLE_BUFF_EFFECT_RESOURCE = BATTLE_UI_BASE + "增益特效.png";
    private static final String BATTLE_INFO_PANEL_RESOURCE = BATTLE_UI_BASE + "战斗信息组件.png";
    private static final String BATTLE_GOLD_RESOURCE = BATTLE_UI_BASE + "金币.png";
    private static final String BATTLE_DECK_BUTTON_RESOURCE = BATTLE_UI_BASE + "牌组按钮.png";
    private static final String BATTLE_SETTINGS_RESOURCE = BATTLE_UI_BASE + "设置按钮.png";
    private static final String SETTINGS_BACKGROUND_RESOURCE = BATTLE_UI_BASE + "设置界面背景图.png";
    private static final String SETTINGS_BACK_BUTTON_RESOURCE = BATTLE_UI_BASE + "返回按钮.png";
    private static final String SETTINGS_SAVE_EXIT_BUTTON_RESOURCE = BATTLE_UI_BASE + "保存并退出按钮.png";
    private static final double SETTINGS_PANEL_SCALE = 1.12;
    private static final double SETTINGS_PANEL_BASE_WIDTH = 760;
    private static final double SETTINGS_PANEL_BASE_HEIGHT = 560;
    private static final double SETTINGS_PANEL_VIEW_MARGIN = 24;
    private static final double SETTINGS_ACTION_GAP = 1;
    private static final double SETTINGS_ACTION_RIGHT_MARGIN = 5;
    private static final double SETTINGS_ACTION_BOTTOM_MARGIN = 5;
    private static final double SETTINGS_ACTION_BUTTON_WIDTH = 148;
    private static final int DECK_BROWSER_COLUMNS = 5;
    private static final double DECK_BROWSER_TOP_INSET = 18;
    private static final double DECK_BROWSER_SIDE_INSET = 34;
    private static final double DECK_BROWSER_BOTTOM_INSET = 68;
    private static final double DECK_BROWSER_SORT_BAR_HEIGHT = 48;
    private static final double DECK_BROWSER_SORT_BAR_WIDTH_RATIO = 0.94;
    private static final double DECK_BROWSER_CARD_HGAP = 24;
    private static final double DECK_BROWSER_CARD_VGAP = 30;
    private static final double DECK_BROWSER_SIDEBAR_WIDTH = 178;
    private static final double DECK_BROWSER_BODY_GAP = 24;
    private static final String BATTLE_BACKGROUND_BASE = "/assets/backgrounds/battle/";
    private static final String BATTLE_BACKGROUND_MANIFEST = BATTLE_BACKGROUND_BASE + "manifest.txt";
    private static final String COMMON_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "普通卡.png";
    private static final String RARE_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "稀有卡.png";
    private static final String LEGENDARY_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "传说卡.png";
    private static final String SPECIAL_CARD_TEMPLATE_RESOURCE = BATTLE_UI_BASE + "特殊卡.png";
    private static final String ATTACK_CARD_ART_RESOURCE = BATTLE_UI_BASE + "攻击牌默认图像.png";
    private static final String DEFENSE_CARD_ART_RESOURCE = BATTLE_UI_BASE + "防御牌基本图像.png";
    private static final String BUFF_CARD_ART_RESOURCE = BATTLE_UI_BASE + "增益牌默认图像.png";
    private static final double COMBATANT_UNIT_WIDTH = 320;
    private static final double COMBATANT_UNIT_HEIGHT = 370;
    private static final double COMBATANT_UNIT_BASELINE_OFFSET_Y = 112;
    private static final double COMBATANT_VITALS_WIDTH = 250;
    private static final double COMBATANT_VITALS_HEIGHT = 62;
    private static final double COMBATANT_VITALS_TOP = 260;
    private static final double COMBATANT_CAPTION_TOP = 312;
    private static final double COMBATANT_SHIELD_VALUE_LEFT = 3;
    private static final double COMBATANT_SHIELD_VALUE_TOP = 11;
    private static final double COMBATANT_SHIELD_VALUE_WIDTH = 54;
    private static final double COMBATANT_SHIELD_VALUE_HEIGHT = 36;
    private static final double PLAYER_HEADER_WIDTH = 276;
    private static final double PLAYER_HEADER_HEIGHT = 92;
    private static final double PLAYER_HEADER_HEALTH_BAR_LEFT = 80;
    private static final double PLAYER_HEADER_HEALTH_BAR_TOP = 52;
    private static final double PLAYER_HEADER_HEALTH_BAR_WIDTH = 158;
    private static final double PLAYER_HEADER_HEALTH_BAR_HEIGHT = 18;

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
    private Rectangle playerHeaderHealthClip;
    private BattleVitalsBar playerVitalsBar;
    private BattleVitalsBar enemyVitalsBar;
    private Pane handPane;
    private Image attackEffectImage;
    private Image shieldEffectImage;
    private Image buffEffectImage;
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

    private StackPane createMainMenu() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #020203;");
        clipToBounds(root);

        ImageView background = null;
        var backgroundResource = GameApplication.class.getResource(MAIN_MENU_BACKGROUND_RESOURCE);
        if (backgroundResource != null) {
            background = new ImageView(new Image(backgroundResource.toExternalForm()));
            background.setManaged(false);
            background.setPreserveRatio(true);
            background.setSmooth(true);
            background.setCache(true);
            root.getChildren().add(background);
        }

        StackPane startButton = createMainMenuStartButton();
        StackPane exitButton = createMainMenuExitButton();
        root.getChildren().addAll(startButton, exitButton);

        ImageView menuBackground = background;
        if (menuBackground != null) {
            root.widthProperty().addListener((observable, oldValue, newValue) ->
                    layoutMainMenuBackground(menuBackground, startButton, exitButton, root));
            root.heightProperty().addListener((observable, oldValue, newValue) ->
                    layoutMainMenuBackground(menuBackground, startButton, exitButton, root));
            Platform.runLater(() -> layoutMainMenuBackground(menuBackground, startButton, exitButton, root));
        }
        return root;
    }

    private StackPane createMainMenuStartButton() {
        return createMainMenuImageButton(MAIN_MENU_START_BUTTON_RESOURCE, () -> setRoot(createCharacterSelectView()));
    }

    private StackPane createMainMenuExitButton() {
        return createMainMenuImageButton(MAIN_MENU_EXIT_BUTTON_RESOURCE, () -> stage.close());
    }

    private StackPane createMainMenuImageButton(String resourcePath, Runnable action) {
        ImageView image = createMainMenuButtonImage(resourcePath);
        ColorAdjust colorAdjust = new ColorAdjust();
        image.setEffect(colorAdjust);

        StackPane button = new StackPane(image);
        button.setManaged(false);
        button.setAlignment(Pos.CENTER);
        button.setPickOnBounds(true);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        button.setOnMouseEntered(event -> {
            button.setScaleX(1.025);
            button.setScaleY(1.025);
            colorAdjust.setBrightness(0.07);
        });
        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            colorAdjust.setBrightness(0.0);
        });
        button.setOnMousePressed(event -> {
            button.setScaleX(0.985);
            button.setScaleY(0.985);
            colorAdjust.setBrightness(0.03);
        });
        button.setOnMouseReleased(event -> {
            boolean hover = button.isHover();
            button.setScaleX(hover ? 1.025 : 1.0);
            button.setScaleY(hover ? 1.025 : 1.0);
            colorAdjust.setBrightness(hover ? 0.07 : 0.0);
        });
        button.setOnMouseClicked(event -> {
            action.run();
            event.consume();
        });
        return button;
    }

    private ImageView createMainMenuButtonImage(String resourcePath) {
        ImageView image = createUiImageView(resourcePath, MAIN_MENU_START_BUTTON_WIDTH, 0);
        image.setPreserveRatio(true);
        image.setMouseTransparent(true);
        return image;
    }

    private void layoutMainMenuBackground(ImageView background, StackPane startButton, StackPane exitButton, StackPane root) {
        fitBattleBackground(background, root);
        Image image = background.getImage();
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0 || background.getFitWidth() <= 0) {
            return;
        }

        double scale = background.getFitWidth() / image.getWidth();
        double buttonWidth = MAIN_MENU_START_BUTTON_WIDTH * scale;
        double buttonHeight = mainMenuButtonHeight(startButton, buttonWidth);
        double buttonX = background.getLayoutX() + MAIN_MENU_START_BUTTON_X * scale;
        double startButtonY = background.getLayoutY() + MAIN_MENU_START_BUTTON_Y * scale;
        layoutMainMenuButton(startButton, buttonX, startButtonY, buttonWidth, buttonHeight);
        layoutMainMenuButton(exitButton,
                buttonX,
                startButtonY + buttonHeight + MAIN_MENU_BUTTON_GAP * scale,
                buttonWidth,
                buttonHeight);
    }

    private void layoutMainMenuButton(StackPane button, double x, double y, double width, double height) {
        button.resizeRelocate(x, y, width, height);
        if (!button.getChildren().isEmpty() && button.getChildren().get(0) instanceof ImageView buttonImage) {
            buttonImage.setFitWidth(width);
            buttonImage.setFitHeight(0);
        }
    }

    private double mainMenuButtonHeight(StackPane startButton, double buttonWidth) {
        if (!startButton.getChildren().isEmpty() && startButton.getChildren().get(0) instanceof ImageView imageView) {
            Rectangle2D viewport = imageView.getViewport();
            Image image = imageView.getImage();
            double sourceWidth = viewport == null ? image == null ? 0 : image.getWidth() : viewport.getWidth();
            double sourceHeight = viewport == null ? image == null ? 0 : image.getHeight() : viewport.getHeight();
            if (sourceWidth > 0 && sourceHeight > 0) {
                return buttonWidth * sourceHeight / sourceWidth;
            }
        }
        return buttonWidth / MAIN_MENU_START_BUTTON_FALLBACK_ASPECT;
    }

    private StackPane createCharacterSelectView() {
        List<PlayableCharacter> characters = engine.getAvailableCharacters();
        if (characters.isEmpty()) {
            throw new IllegalStateException("at least one playable character is required");
        }

        String[] selectedCharacterId = {characters.get(0).id()};
        List<Button> characterButtons = new ArrayList<>();

        HBox characterChoices = new HBox(CHARACTER_SELECT_CHOICE_GAP);
        characterChoices.setAlignment(Pos.CENTER);
        characterChoices.setFillHeight(false);
        for (PlayableCharacter character : characters) {
            Button choice = createCharacterChoice(character);
            choice.setOnAction(event -> {
                selectedCharacterId[0] = character.id();
                refreshCharacterSelection(characterButtons, selectedCharacterId[0]);
            });
            characterButtons.add(choice);
            characterChoices.getChildren().add(choice);
        }
        refreshCharacterSelection(characterButtons, selectedCharacterId[0]);

        StackPane departButton = createCharacterSelectImageButton(CHARACTER_SELECT_DEPART_BUTTON_RESOURCE, () -> {
            engine.startNewRun(selectedCharacterId[0]);
            mapViewportOffsetY = Double.NaN;
            setRoot(createMapView());
        });

        StackPane backButton = createCharacterSelectImageButton(CHARACTER_SELECT_BACK_BUTTON_RESOURCE,
                () -> setRoot(createMainMenu()));

        VBox actions = new VBox(CHARACTER_SELECT_BUTTON_GAP, departButton, backButton);
        actions.setAlignment(Pos.CENTER);
        actions.setFillWidth(false);

        VBox choiceArea = new VBox(characterChoices);
        choiceArea.setAlignment(Pos.CENTER);
        choiceArea.setPadding(new Insets(CHARACTER_SELECT_HEADER_SPACE, 0, 0, 0));
        choiceArea.setTranslateY(CHARACTER_SELECT_CHOICE_AREA_OFFSET_Y);

        BorderPane contentRoot = new BorderPane();
        contentRoot.setPadding(new Insets(16));
        contentRoot.setStyle("-fx-background-color: transparent;");
        contentRoot.setCenter(choiceArea);
        contentRoot.setBottom(actions);
        BorderPane.setAlignment(actions, Pos.CENTER);
        BorderPane.setMargin(actions, new Insets(0, 0, CHARACTER_SELECT_BUTTON_BOTTOM_MARGIN, 0));

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #050506;");
        clipToBounds(root);
        root.getChildren().addAll(createCharacterSelectBackgroundLayer(), contentRoot);
        return root;
    }

    private StackPane createCharacterSelectBackgroundLayer() {
        StackPane backdrop = new StackPane();
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backdrop.setMouseTransparent(true);

        var resource = GameApplication.class.getResource(CHARACTER_SELECT_BACKGROUND_RESOURCE);
        if (resource != null) {
            ImageView background = new ImageView(new Image(resource.toExternalForm()));
            background.setManaged(false);
            background.setPreserveRatio(false);
            background.setSmooth(true);
            background.setCache(true);
            background.setMouseTransparent(true);
            backdrop.widthProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            backdrop.heightProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            Platform.runLater(() -> fitBattleBackground(background, backdrop));
            backdrop.getChildren().add(background);
        }

        return backdrop;
    }

    private StackPane createCharacterSelectImageButton(String resourcePath, Runnable action) {
        ImageView image = createUiImageView(resourcePath, CHARACTER_SELECT_BUTTON_WIDTH, 0);
        ColorAdjust colorAdjust = new ColorAdjust();
        image.setEffect(colorAdjust);

        double buttonHeight = imageButtonHeight(image, CHARACTER_SELECT_BUTTON_WIDTH);
        StackPane button = new StackPane(image);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(CHARACTER_SELECT_BUTTON_WIDTH, buttonHeight);
        button.setMinSize(CHARACTER_SELECT_BUTTON_WIDTH, buttonHeight);
        button.setMaxSize(CHARACTER_SELECT_BUTTON_WIDTH, buttonHeight);
        button.setPickOnBounds(true);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        button.setOnMouseEntered(event -> {
            button.setScaleX(1.025);
            button.setScaleY(1.025);
            colorAdjust.setBrightness(0.07);
        });
        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            colorAdjust.setBrightness(0.0);
        });
        button.setOnMousePressed(event -> {
            button.setScaleX(0.985);
            button.setScaleY(0.985);
            colorAdjust.setBrightness(0.03);
        });
        button.setOnMouseReleased(event -> {
            boolean hover = button.isHover();
            button.setScaleX(hover ? 1.025 : 1.0);
            button.setScaleY(hover ? 1.025 : 1.0);
            colorAdjust.setBrightness(hover ? 0.07 : 0.0);
        });
        button.setOnMouseClicked(event -> {
            action.run();
            event.consume();
        });
        return button;
    }

    private double imageButtonHeight(ImageView imageView, double buttonWidth) {
        Rectangle2D viewport = imageView.getViewport();
        Image image = imageView.getImage();
        double sourceWidth = viewport == null ? image == null ? 0 : image.getWidth() : viewport.getWidth();
        double sourceHeight = viewport == null ? image == null ? 0 : image.getHeight() : viewport.getHeight();
        if (sourceWidth > 0 && sourceHeight > 0) {
            return buttonWidth * sourceHeight / sourceWidth;
        }
        return buttonWidth * 0.42;
    }

    private Button createCharacterChoice(PlayableCharacter character) {
        Region glow = new Region();
        glow.setPrefSize(CHARACTER_CHOICE_GLOW_WIDTH, CHARACTER_CHOICE_GLOW_HEIGHT);
        glow.setMinSize(CHARACTER_CHOICE_GLOW_WIDTH, CHARACTER_CHOICE_GLOW_HEIGHT);
        glow.setMaxSize(CHARACTER_CHOICE_GLOW_WIDTH, CHARACTER_CHOICE_GLOW_HEIGHT);
        glow.setMouseTransparent(true);

        CharacterPortrait portrait = CharacterPortrait.player(character.id());
        portrait.setMouseTransparent(true);

        StackPane portraitArea = new StackPane(glow, portrait);
        portraitArea.setAlignment(Pos.CENTER);
        portraitArea.setPrefSize(CHARACTER_CHOICE_PORTRAIT_AREA_WIDTH, CHARACTER_CHOICE_PORTRAIT_AREA_HEIGHT);
        portraitArea.setMinSize(CHARACTER_CHOICE_PORTRAIT_AREA_WIDTH, CHARACTER_CHOICE_PORTRAIT_AREA_HEIGHT);
        portraitArea.setMaxSize(CHARACTER_CHOICE_PORTRAIT_AREA_WIDTH, CHARACTER_CHOICE_PORTRAIT_AREA_HEIGHT);
        portraitArea.setMouseTransparent(true);

        Label name = new Label(character.name());
        name.setAlignment(Pos.CENTER);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setPrefWidth(CHARACTER_CHOICE_WIDTH);
        name.setMinWidth(CHARACTER_CHOICE_WIDTH);
        name.setMaxWidth(CHARACTER_CHOICE_WIDTH);
        name.setMouseTransparent(true);

        VBox content = new VBox(4, portraitArea, name);
        content.setAlignment(Pos.CENTER);
        content.setPrefSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        content.setMinSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        content.setMaxSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        content.setMouseTransparent(true);

        Button choice = new Button();
        choice.setGraphic(content);
        choice.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        choice.setUserData(new CharacterChoice(character, portrait, glow, name));
        choice.setPrefSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        choice.setMinSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        choice.setMaxSize(CHARACTER_CHOICE_WIDTH, CHARACTER_CHOICE_HEIGHT);
        choice.setPickOnBounds(true);
        return choice;
    }

    private void refreshCharacterSelection(List<Button> characterButtons, String selectedCharacterId) {
        for (Button button : characterButtons) {
            CharacterChoice choice = (CharacterChoice) button.getUserData();
            boolean selected = choice.character().id().equals(selectedCharacterId);
            button.setStyle(characterChoiceStyle(selected));
            button.setScaleX(selected ? 1.035 : 1.0);
            button.setScaleY(selected ? 1.035 : 1.0);
            choice.portrait().setOpacity(selected ? 1.0 : 0.82);
            choice.portrait().setEffect(characterChoiceAdjust(selected));
            choice.glow().setStyle(characterChoiceGlowStyle(selected));
            choice.name().setStyle(characterChoiceNameStyle(selected));
        }
    }

    private ColorAdjust characterChoiceAdjust(boolean selected) {
        ColorAdjust adjust = new ColorAdjust();
        adjust.setBrightness(selected ? 0.10 : -0.08);
        adjust.setContrast(selected ? 0.08 : -0.04);
        adjust.setSaturation(selected ? 0.10 : -0.18);
        return adjust;
    }

    private String characterChoiceGlowStyle(boolean selected) {
        String glowColor = selected ? "rgba(242, 192, 120, 0.48)" : "rgba(0, 0, 0, 0.22)";
        return "-fx-background-color: radial-gradient(center 50% 55%, radius 72%, "
                + glowColor + ", rgba(242, 192, 120, 0));"
                + "-fx-background-radius: 118;";
    }

    private String characterChoiceNameStyle(boolean selected) {
        String text = selected ? "#fff1c8" : "#d5c4aa";
        String shadow = selected
                ? "dropshadow(gaussian, rgba(94, 43, 18, 0.95), 6, 0.7, 0, 2)"
                : "dropshadow(gaussian, rgba(0, 0, 0, 0.88), 5, 0.6, 0, 1)";
        return "-fx-text-fill: " + text + ";"
                + "-fx-font-size: 27px;"
                + "-fx-font-family: \"STXingkai\", \"STKaiti\", \"KaiTi\", \"FangSong\", \"Serif\";"
                + "-fx-font-weight: bold;"
                + "-fx-effect: " + shadow + ";";
    }

    private record CharacterChoice(
            PlayableCharacter character,
            CharacterPortrait portrait,
            Region glow,
            Label name
    ) {
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
        overlayLayer.setMouseTransparent(true);

        HBox header = createMapHeader(run, overlayLayer);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setMargin(header, new Insets(18, 28, 0, 28));

        root.getChildren().addAll(createRouteMap(run), header, overlayLayer);
        return root;
    }

    private HBox createMapHeader(RunState run, StackPane overlayLayer) {
        StackPane playerHeader = createPlayerHeaderPanel();
        updatePlayerHeaderHealth(run.getPlayer().getHealth(), run.getPlayer().getMaxHealth());
        HBox goldCounter = createResourceCounter(BATTLE_GOLD_RESOURCE, String.valueOf(run.getGold()), "金币");

        HBox leftStatus = new HBox(10, playerHeader, goldCounter);
        leftStatus.setAlignment(Pos.TOP_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane deckButton = createBattleIconButton(BATTLE_DECK_BUTTON_RESOURCE,
                "牌组",
                () -> showMapDeckOverlay(overlayLayer, run));
        StackPane settingsButton = createBattleIconButton(BATTLE_SETTINGS_RESOURCE,
                "设置",
                () -> showMapSettingsOverlay(overlayLayer, run));
        HBox buttons = new HBox(BATTLE_ACTION_BUTTON_GAP, deckButton, settingsButton);
        buttons.setAlignment(Pos.TOP_RIGHT);
        buttons.setMinSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        buttons.setPrefSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        buttons.setMaxSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        settingsButton.toFront();

        HBox header = new HBox(14, leftStatus, spacer, buttons);
        header.setAlignment(Pos.TOP_CENTER);
        header.setPrefHeight(PLAYER_HEADER_HEIGHT);
        header.setMinHeight(PLAYER_HEADER_HEIGHT);
        header.setMaxHeight(PLAYER_HEADER_HEIGHT);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setPadding(new Insets(0, 14, 0, 14));
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
        showDeckOverlay(overlayLayer, run.getDeck());
    }

    private void showMapSettingsOverlay(StackPane overlayLayer, RunState run) {
        showGameMenuOverlay(overlayLayer);
    }

    private void showDeckOverlay(StackPane overlayLayer, List<Card> deck) {
        List<DeckCardView> cards = deckCardViews(deck);
        DeckSortMode[] selectedMode = {DeckSortMode.ACQUIRED};
        Map<DeckSortMode, Boolean> sortDirections = new EnumMap<>(DeckSortMode.class);
        Map<DeckSortMode, Button> sortButtons = new EnumMap<>(DeckSortMode.class);
        for (DeckSortMode mode : DeckSortMode.values()) {
            sortDirections.put(mode, true);
        }

        TilePane cardGrid = new TilePane();
        cardGrid.setAlignment(Pos.TOP_CENTER);
        cardGrid.setTileAlignment(Pos.CENTER);
        cardGrid.setPrefColumns(DECK_BROWSER_COLUMNS);
        cardGrid.setMinWidth(0);
        cardGrid.setHgap(DECK_BROWSER_CARD_HGAP);
        cardGrid.setVgap(DECK_BROWSER_CARD_VGAP);
        cardGrid.setPadding(new Insets(18, 14, 34, 14));

        HBox sortBar = new HBox(0);
        sortBar.setAlignment(Pos.CENTER);
        sortBar.setMinHeight(DECK_BROWSER_SORT_BAR_HEIGHT);
        sortBar.setPrefHeight(DECK_BROWSER_SORT_BAR_HEIGHT);
        sortBar.setMaxHeight(DECK_BROWSER_SORT_BAR_HEIGHT);
        sortBar.setMinWidth(0);
        sortBar.setStyle(deckBrowserSortBarStyle());
        for (DeckSortMode mode : DeckSortMode.values()) {
            Button sortButton = new Button();
            sortButton.setMaxWidth(Double.MAX_VALUE);
            sortButton.setPrefHeight(DECK_BROWSER_SORT_BAR_HEIGHT - 6);
            HBox.setHgrow(sortButton, Priority.ALWAYS);
            sortButton.setOnAction(event -> {
                if (selectedMode[0] == mode) {
                    sortDirections.put(mode, !sortDirections.get(mode));
                } else {
                    selectedMode[0] = mode;
                }
                refreshDeckSortButtons(sortButtons, selectedMode[0], sortDirections);
                refreshDeckBrowserGrid(cardGrid, cards, selectedMode[0], sortDirections.get(selectedMode[0]));
            });
            sortButton.setOnMouseEntered(event ->
                    sortButton.setStyle(deckSortButtonStyle(mode == selectedMode[0], true)));
            sortButton.setOnMouseExited(event ->
                    sortButton.setStyle(deckSortButtonStyle(mode == selectedMode[0], false)));
            sortButtons.put(mode, sortButton);
            sortBar.getChildren().add(sortButton);
        }

        StackPane gridHolder = new StackPane(cardGrid);
        gridHolder.setAlignment(Pos.TOP_CENTER);
        gridHolder.setStyle(deckBrowserGridHolderStyle());

        ScrollPane scroll = new ScrollPane(gridHolder);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setPannable(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(deckBrowserScrollStyle());
        VBox.setVgrow(scroll, Priority.ALWAYS);

        cardGrid.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> deckBrowserCardGridWidth(scroll.getWidth()),
                scroll.widthProperty()));
        cardGrid.maxWidthProperty().bind(cardGrid.prefWidthProperty());
        sortBar.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> deckBrowserSortBarWidth(scroll.getWidth()),
                scroll.widthProperty()));
        sortBar.maxWidthProperty().bind(sortBar.prefWidthProperty());

        VBox browser = new VBox(24, sortBar, scroll);
        browser.setAlignment(Pos.TOP_CENTER);
        browser.setPadding(new Insets(14, 18, 18, 18));
        browser.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        browser.setStyle(deckBrowserBoardInnerStyle());

        StackPane board = new StackPane(browser);
        board.setMinWidth(0);
        board.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        board.setStyle(deckBrowserBoardStyle());
        HBox.setHgrow(board, Priority.ALWAYS);

        HBox body = new HBox(DECK_BROWSER_BODY_GAP, createDeckBrowserSidebar(engine.getRunState()), board);
        body.setAlignment(Pos.TOP_CENTER);
        body.setFillHeight(true);
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox layout = new VBox(12, createDeckBrowserTitleBar(), body);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(DECK_BROWSER_TOP_INSET, DECK_BROWSER_SIDE_INSET,
                DECK_BROWSER_BOTTOM_INSET, DECK_BROWSER_SIDE_INSET));
        layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label hint = new Label("这些牌将会出现在每一场战斗中。");
        hint.setAlignment(Pos.CENTER);
        hint.setTextAlignment(TextAlignment.CENTER);
        hint.setStyle(deckBrowserHintStyle());
        StackPane.setAlignment(hint, Pos.BOTTOM_CENTER);
        StackPane.setMargin(hint, new Insets(0, 0, 22, 0));

        Button close = new Button("返回");
        close.setStyle(deckBrowserCloseButtonStyle());
        close.setMinWidth(118);
        close.setOnMouseEntered(event -> close.setStyle(deckBrowserCloseButtonStyle(true)));
        close.setOnMouseExited(event -> close.setStyle(deckBrowserCloseButtonStyle(false)));
        close.setOnMousePressed(event -> {
            close.setScaleX(0.98);
            close.setScaleY(0.98);
        });
        close.setOnMouseReleased(event -> {
            close.setScaleX(1.0);
            close.setScaleY(1.0);
        });
        close.setOnAction(event -> closeOverlay(overlayLayer));
        StackPane.setAlignment(close, Pos.BOTTOM_LEFT);
        StackPane.setMargin(close, new Insets(0, 0, 22, 34));

        StackPane panel = new StackPane(createDeckBrowserBackdrop(), layout, hint, close);
        panel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        panel.setStyle("-fx-background-color: #050404;");
        clipToBounds(panel);

        refreshDeckSortButtons(sortButtons, selectedMode[0], sortDirections);
        refreshDeckBrowserGrid(cardGrid, cards, selectedMode[0], sortDirections.get(selectedMode[0]));
        showOverlay(overlayLayer, panel);
    }

    private double deckBrowserMaxCardGridWidth() {
        return HAND_CARD_WIDTH * DECK_BROWSER_COLUMNS + DECK_BROWSER_CARD_HGAP * (DECK_BROWSER_COLUMNS - 1);
    }

    private double deckBrowserCardGridWidth(double availableWidth) {
        if (availableWidth <= 0) {
            return deckBrowserMaxCardGridWidth();
        }
        double innerWidth = Math.max(HAND_CARD_WIDTH, availableWidth - 38);
        return Math.min(deckBrowserMaxCardGridWidth(), innerWidth);
    }

    private double deckBrowserSortBarWidth(double availableWidth) {
        return Math.max(360, deckBrowserCardGridWidth(availableWidth) * DECK_BROWSER_SORT_BAR_WIDTH_RATIO);
    }

    private StackPane createDeckBrowserBackdrop() {
        StackPane backdrop = new StackPane();
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backdrop.setMouseTransparent(true);

        var resource = GameApplication.class.getResource(BATTLE_SCENE_BACKGROUND_RESOURCE);
        if (resource != null) {
            ImageView background = new ImageView(new Image(resource.toExternalForm()));
            background.setManaged(false);
            background.setPreserveRatio(false);
            background.setSmooth(true);
            background.setCache(true);
            backdrop.widthProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            backdrop.heightProperty().addListener((observable, oldValue, newValue) -> fitBattleBackground(background, backdrop));
            Platform.runLater(() -> fitBattleBackground(background, backdrop));
            backdrop.getChildren().add(background);
        }

        Region shade = new Region();
        shade.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        shade.setStyle("-fx-background-color: linear-gradient(to bottom, "
                + "rgba(0, 0, 0, 0.76), rgba(10, 7, 8, 0.68) 42%, rgba(0, 0, 0, 0.84));");

        Region bloodWash = new Region();
        bloodWash.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        bloodWash.setStyle("-fx-background-color: radial-gradient(center 50% 24%, radius 72%, "
                + "rgba(114, 20, 18, 0.22), rgba(17, 5, 5, 0.06) 52%, rgba(0, 0, 0, 0.62) 100%);");

        Region edgeVignette = new Region();
        edgeVignette.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        edgeVignette.setStyle("-fx-background-color: radial-gradient(center 50% 48%, radius 76%, "
                + "rgba(0, 0, 0, 0.00), rgba(0, 0, 0, 0.22) 58%, rgba(0, 0, 0, 0.82) 100%);");

        backdrop.getChildren().addAll(shade, bloodWash, edgeVignette);
        return backdrop;
    }

    private StackPane createDeckBrowserTitleBar() {
        Label title = new Label("牌组构筑");
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setStyle(deckBrowserTitleStyle());

        Region leftLine = deckBrowserTitleLine();
        Region rightLine = deckBrowserTitleLine();
        HBox titleRow = new HBox(18, leftLine, title, rightLine);
        titleRow.setAlignment(Pos.CENTER);

        Label crest = new Label("◆");
        crest.setMouseTransparent(true);
        crest.setStyle("-fx-text-fill: rgba(230, 184, 112, 0.66);"
                + "-fx-font-size: 14px;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.95), 4, 0.65, 0, 1);");
        StackPane.setAlignment(crest, Pos.BOTTOM_CENTER);
        StackPane.setMargin(crest, new Insets(0, 0, 1, 0));

        StackPane titleBar = new StackPane(titleRow, crest);
        titleBar.setMinHeight(54);
        titleBar.setPrefHeight(58);
        titleBar.setMaxHeight(64);
        titleBar.setStyle(deckBrowserTitleBarStyle());
        return titleBar;
    }

    private Region deckBrowserTitleLine() {
        Region line = new Region();
        line.setPrefSize(190, 2);
        line.setMaxWidth(190);
        line.setStyle("-fx-background-color: linear-gradient(to right, "
                + "rgba(64, 41, 31, 0.00), rgba(116, 95, 68, 0.82), rgba(161, 37, 27, 0.34), rgba(64, 41, 31, 0.00));");
        HBox.setHgrow(line, Priority.ALWAYS);
        return line;
    }

    private VBox createDeckBrowserSidebar(RunState run) {
        PlayableCharacter character = run == null ? null : run.getPlayableCharacter();

        Label name = new Label(deckBrowserCharacterName(character));
        name.setAlignment(Pos.CENTER);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setMaxWidth(DECK_BROWSER_SIDEBAR_WIDTH - 24);
        name.setStyle(deckBrowserSidebarNameStyle());

        Label archetype = new Label(deckBrowserCharacterArchetype(character));
        archetype.setAlignment(Pos.CENTER);
        archetype.setTextAlignment(TextAlignment.CENTER);
        archetype.setWrapText(true);
        archetype.setMaxWidth(DECK_BROWSER_SIDEBAR_WIDTH - 30);
        archetype.setStyle(deckBrowserSidebarTextStyle());

        Label count = new Label(run == null ? "" : run.getDeck().size() + " 张牌");
        count.setAlignment(Pos.CENTER);
        count.setTextAlignment(TextAlignment.CENTER);
        count.setStyle(deckBrowserSidebarCountStyle());

        VBox sidebar = new VBox(13,
                createDeckBrowserAvatar(character),
                name,
                deckBrowserSidebarDivider(),
                archetype,
                count);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setMinWidth(DECK_BROWSER_SIDEBAR_WIDTH);
        sidebar.setPrefWidth(DECK_BROWSER_SIDEBAR_WIDTH);
        sidebar.setMaxWidth(DECK_BROWSER_SIDEBAR_WIDTH);
        sidebar.setMaxHeight(Double.MAX_VALUE);
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setStyle(deckBrowserSidebarStyle());
        return sidebar;
    }

    private StackPane createDeckBrowserAvatar(PlayableCharacter character) {
        String resourcePath = deckBrowserAvatarResource(character);
        ImageView imageView = new ImageView();
        var resource = GameApplication.class.getResource(resourcePath);
        if (resource != null) {
            Image image = uiImages.computeIfAbsent(resourcePath, ignored -> new Image(resource.toExternalForm()));
            imageView.setImage(image);
            imageView.setViewport(deckBrowserAvatarViewport(character, image));
        }
        imageView.setFitWidth(116);
        imageView.setFitHeight(116);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setMouseTransparent(true);

        Circle clip = new Circle(58, 58, 54);
        imageView.setClip(clip);

        StackPane frame = new StackPane(imageView);
        frame.setPrefSize(122, 122);
        frame.setMinSize(122, 122);
        frame.setMaxSize(122, 122);
        frame.setAlignment(Pos.CENTER);
        frame.setStyle(deckBrowserAvatarFrameStyle());
        return frame;
    }

    private String deckBrowserAvatarResource(PlayableCharacter character) {
        if (character != null && CharacterPortrait.ASSASSIN_CHARACTER_ID.equals(character.id())) {
            return BATTLE_ASSASSIN_HEADER_HEALTH_FULL_RESOURCE;
        }
        return BATTLE_BERSERKER_AVATAR_RESOURCE;
    }

    private Rectangle2D deckBrowserAvatarViewport(PlayableCharacter character, Image image) {
        if (character != null && CharacterPortrait.ASSASSIN_CHARACTER_ID.equals(character.id())) {
            return clampedViewport(image, 0, 0, 420, image.getHeight());
        }
        return clampedViewport(image, 96, 245, 420, 420);
    }

    private String deckBrowserCharacterName(PlayableCharacter character) {
        return character == null ? "狂战士" : character.name();
    }

    private String deckBrowserCharacterArchetype(PlayableCharacter character) {
        return character == null ? "暗黑远征" : character.archetype();
    }

    private Region deckBrowserSidebarDivider() {
        Region divider = new Region();
        divider.setPrefSize(98, 2);
        divider.setMaxWidth(98);
        divider.setStyle("-fx-background-color: linear-gradient(to right, "
                + "rgba(112, 24, 18, 0.00), rgba(156, 114, 68, 0.82), rgba(112, 24, 18, 0.00));");
        return divider;
    }

    private List<DeckCardView> deckCardViews(List<Card> deck) {
        List<DeckCardView> cards = new ArrayList<>();
        for (int i = 0; i < deck.size(); i++) {
            cards.add(new DeckCardView(deck.get(i), i));
        }
        return cards;
    }

    private void refreshDeckBrowserGrid(
            TilePane cardGrid,
            List<DeckCardView> cards,
            DeckSortMode sortMode,
            boolean ascending
    ) {
        cardGrid.getChildren().clear();
        List<DeckCardView> sortedCards = sortedDeckCards(cards, sortMode, ascending);
        if (sortedCards.isEmpty()) {
            Label empty = new Label("当前牌组为空");
            empty.setStyle("-fx-text-fill: #e8ddc8; -fx-font-size: 20px; -fx-font-weight: bold;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.95), 5, 0.7, 0, 2);");
            StackPane emptyState = new StackPane(empty);
            emptyState.setPrefSize(deckBrowserMaxCardGridWidth(), 260);
            cardGrid.getChildren().add(emptyState);
            return;
        }

        for (int i = 0; i < sortedCards.size(); i++) {
            DeckCardView cardView = sortedCards.get(i);
            cardGrid.getChildren().add(deckBrowserCard(cardView.card()));
        }
    }

    private List<DeckCardView> sortedDeckCards(List<DeckCardView> cards, DeckSortMode sortMode, boolean ascending) {
        Comparator<DeckCardView> comparator = deckSortComparator(sortMode);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return cards.stream()
                .sorted(comparator)
                .toList();
    }

    private Comparator<DeckCardView> deckSortComparator(DeckSortMode sortMode) {
        return switch (sortMode) {
            case ACQUIRED -> Comparator.comparingInt(DeckCardView::originalIndex);
            case TYPE -> Comparator
                    .comparingInt((DeckCardView cardView) -> cardView.card().getType().ordinal())
                    .thenComparingInt(cardView -> cardView.card().getCost())
                    .thenComparing(cardView -> cardView.card().getName())
                    .thenComparingInt(DeckCardView::originalIndex);
            case COST -> Comparator
                    .comparingInt((DeckCardView cardView) -> cardView.card().getCost())
                    .thenComparingInt(cardView -> cardView.card().getType().ordinal())
                    .thenComparing(cardView -> cardView.card().getName())
                    .thenComparingInt(DeckCardView::originalIndex);
        };
    }

    private StackPane deckBrowserCard(Card card) {
        StackPane face = cardFace(card);
        face.setMouseTransparent(true);

        StackPane cell = new StackPane(face);
        cell.setAlignment(Pos.CENTER);
        cell.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        cell.setMinSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        cell.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_HEIGHT);
        cell.setStyle("-fx-background-color: transparent;");
        Tooltip.install(cell, new Tooltip(card.getName() + "\n费用：" + card.getCost() + "\n" + card.getDescription()));
        cell.setOnMouseEntered(event -> {
            face.setScaleX(1.025);
            face.setScaleY(1.025);
            face.setEffect(cardHoverEffect(card.getRarity()));
            cell.setViewOrder(-1);
        });
        cell.setOnMouseExited(event -> {
            face.setScaleX(1.0);
            face.setScaleY(1.0);
            face.setEffect(null);
            cell.setViewOrder(0);
        });
        return cell;
    }

    private void refreshDeckSortButtons(
            Map<DeckSortMode, Button> sortButtons,
            DeckSortMode selectedMode,
            Map<DeckSortMode, Boolean> sortDirections
    ) {
        for (Map.Entry<DeckSortMode, Button> entry : sortButtons.entrySet()) {
            DeckSortMode mode = entry.getKey();
            boolean selected = mode == selectedMode;
            boolean ascending = sortDirections.get(mode);
            Button button = entry.getValue();
            button.setText(mode.label + " " + (ascending ? "▲" : "▼"));
            button.setStyle(deckSortButtonStyle(selected));
        }
    }

    private enum DeckSortMode {
        ACQUIRED("获得顺序"),
        TYPE("类型"),
        COST("耗能");

        private final String label;

        DeckSortMode(String label) {
            this.label = label;
        }
    }

    private record DeckCardView(Card card, int originalIndex) {
    }

    private void showGameMenuOverlay(StackPane overlayLayer) {
        StackPane close = createSettingsImageButton(
                SETTINGS_BACK_BUTTON_RESOURCE,
                SETTINGS_ACTION_BUTTON_WIDTH,
                () -> closeOverlay(overlayLayer));

        StackPane saveExit = createSettingsImageButton(
                SETTINGS_SAVE_EXIT_BUTTON_RESOURCE,
                SETTINGS_ACTION_BUTTON_WIDTH,
                () -> setRoot(createMainMenu()));

        VBox actions = new VBox(SETTINGS_ACTION_GAP, close, saveExit);
        actions.setAlignment(Pos.CENTER);
        actions.setFillWidth(false);
        actions.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(actions, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(actions, new Insets(0, SETTINGS_ACTION_RIGHT_MARGIN, SETTINGS_ACTION_BOTTOM_MARGIN, 0));

        Region shade = new Region();
        shade.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        shade.setMouseTransparent(true);
        shade.setStyle("-fx-background-color: linear-gradient(to bottom, "
                + "rgba(0, 0, 0, 0.18), rgba(0, 0, 0, 0.10) 48%, rgba(0, 0, 0, 0.24));"
                + "-fx-background-radius: 10;");

        StackPane panel = new StackPane(shade, actions);
        panel.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> settingsPanelWidth(overlayLayer),
                overlayLayer.widthProperty()));
        panel.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> settingsPanelHeight(overlayLayer),
                overlayLayer.heightProperty()));
        panel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setStyle(settingsModalPanelStyle());
        showOverlay(overlayLayer, panel);
    }

    private StackPane createSettingsImageButton(String resourcePath, double buttonWidth, Runnable action) {
        ImageView image = createUiImageView(resourcePath, buttonWidth, 0);
        double buttonHeight = imageButtonHeight(image, buttonWidth);

        StackPane button = new StackPane(image);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(buttonWidth, buttonHeight);
        button.setMinSize(buttonWidth, buttonHeight);
        button.setMaxSize(buttonWidth, buttonHeight);
        button.setPickOnBounds(true);
        button.setStyle(settingsImageButtonStyle());
        button.setOnMouseClicked(event -> {
            action.run();
            event.consume();
        });
        return button;
    }

    private double settingsPanelWidth(StackPane overlayLayer) {
        return SETTINGS_PANEL_BASE_WIDTH * settingsPanelScale(overlayLayer);
    }

    private double settingsPanelHeight(StackPane overlayLayer) {
        return SETTINGS_PANEL_BASE_HEIGHT * settingsPanelScale(overlayLayer);
    }

    private double settingsPanelScale(StackPane overlayLayer) {
        double availableWidth = overlayLayer.getWidth() - SETTINGS_PANEL_VIEW_MARGIN * 2;
        double availableHeight = overlayLayer.getHeight() - SETTINGS_PANEL_VIEW_MARGIN * 2;
        if (stage != null && stage.getScene() != null) {
            if (availableWidth <= 0) {
                availableWidth = stage.getScene().getWidth() - SETTINGS_PANEL_VIEW_MARGIN * 2;
            }
            if (availableHeight <= 0) {
                availableHeight = stage.getScene().getHeight() - SETTINGS_PANEL_VIEW_MARGIN * 2;
            }
        }
        double widthScale = availableWidth / SETTINGS_PANEL_BASE_WIDTH;
        double heightScale = availableHeight / SETTINGS_PANEL_BASE_HEIGHT;
        return Math.max(0, Math.min(SETTINGS_PANEL_SCALE, Math.min(widthScale, heightScale)));
    }

    private void showOverlay(StackPane overlayLayer, Node panel) {
        StackPane backdrop = new StackPane(panel);
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.62);");
        StackPane.setAlignment(panel, Pos.CENTER);
        overlayLayer.getChildren().setAll(backdrop);
        overlayLayer.setPickOnBounds(true);
        overlayLayer.setMouseTransparent(false);
        overlayLayer.toFront();
    }

    private void closeOverlay(StackPane overlayLayer) {
        overlayLayer.getChildren().clear();
        overlayLayer.setPickOnBounds(false);
        overlayLayer.setMouseTransparent(true);
    }

    private String mapModalPanelStyle() {
        return "-fx-background-color: rgba(27, 24, 20, 0.96);"
                + "-fx-border-color: #d6aa65;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 28, 0.32, 0, 8);";
    }

    private String settingsModalPanelStyle() {
        var backgroundResource = GameApplication.class.getResource(SETTINGS_BACKGROUND_RESOURCE);
        String backgroundImage = backgroundResource == null
                ? ""
                : "-fx-background-image: url(\"" + backgroundResource.toExternalForm() + "\");"
                + "-fx-background-repeat: no-repeat;"
                + "-fx-background-position: center center;"
                + "-fx-background-size: cover;";
        return "-fx-background-color: rgba(27, 24, 20, 0.96);"
                + backgroundImage
                + "-fx-border-color: #d6aa65;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 28, 0.32, 0, 8);";
    }

    private String deckBrowserTitleBarStyle() {
        return "-fx-background-color: linear-gradient(to bottom, "
                + "rgba(19, 16, 14, 0.22), rgba(7, 6, 6, 0.36) 52%, rgba(32, 8, 7, 0.18));"
                + "-fx-border-color: transparent transparent rgba(125, 98, 62, 0.30) transparent;"
                + "-fx-border-width: 0 0 1 0;";
    }

    private String deckBrowserTitleStyle() {
        return "-fx-text-fill: #e7c27a;"
                + "-fx-font-family: \"STXingkai\", \"STKaiti\", \"KaiTi\", \"FangSong\", \"Serif\";"
                + "-fx-font-size: 34px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 5, 0.78, 0, 2);";
    }

    private String deckBrowserBoardStyle() {
        return "-fx-background-color: linear-gradient(to bottom, "
                + "rgba(17, 16, 15, 0.86), rgba(7, 7, 8, 0.88) 54%, rgba(19, 5, 5, 0.84));"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: rgba(168, 132, 78, 0.66) rgba(47, 42, 38, 0.95) rgba(95, 22, 18, 0.74) rgba(47, 42, 38, 0.95);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.92), 28, 0.34, 0, 10);";
    }

    private String deckBrowserBoardInnerStyle() {
        return "-fx-background-color: linear-gradient(to bottom, "
                + "rgba(0, 0, 0, 0.36), rgba(21, 18, 18, 0.22) 46%, rgba(74, 12, 10, 0.18));"
                + "-fx-background-radius: 6;";
    }

    private String deckBrowserGridHolderStyle() {
        return "-fx-background-color: radial-gradient(center 50% 18%, radius 72%, "
                + "rgba(96, 27, 19, 0.20), rgba(9, 8, 8, 0.26) 55%, rgba(0, 0, 0, 0.40) 100%);"
                + "-fx-background-radius: 4;";
    }

    private String deckBrowserSortBarStyle() {
        return "-fx-background-color: linear-gradient(to bottom, "
                + "rgba(26, 24, 23, 0.98), rgba(8, 7, 7, 0.98) 54%, rgba(25, 8, 7, 0.98));"
                + "-fx-background-radius: 4;"
                + "-fx-border-color: rgba(163, 127, 75, 0.76) rgba(44, 39, 35, 0.96) rgba(77, 17, 14, 0.95) rgba(128, 95, 58, 0.72);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 4;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.92), 16, 0.44, 0, 4);";
    }

    private String deckSortButtonStyle(boolean selected) {
        return deckSortButtonStyle(selected, false);
    }

    private String deckSortButtonStyle(boolean selected, boolean hovered) {
        String text = selected ? "#f3cf7d" : hovered ? "#f4e6c6" : "#d4c5a8";
        String background = selected
                ? "linear-gradient(to bottom, rgba(104, 25, 20, 0.44), rgba(26, 10, 9, 0.26))"
                : hovered
                ? "linear-gradient(to bottom, rgba(92, 19, 16, 0.24), rgba(10, 8, 8, 0.18))"
                : "transparent";
        String border = selected
                ? "rgba(224, 171, 95, 0.58)"
                : hovered ? "rgba(151, 43, 35, 0.46)" : "rgba(84, 72, 58, 0.18)";
        return "-fx-background-color: " + background + ";"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: 0 1 0 1;"
                + "-fx-text-fill: " + text + ";"
                + "-fx-font-family: \"KaiTi\", \"STKaiti\", \"Serif\";"
                + "-fx-font-size: 22px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 0 18 0 18;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 3, 0.85, 1, 1);";
    }

    private String deckBrowserScrollStyle() {
        return "-fx-background: transparent;"
                + "-fx-background-color: transparent;"
                + "-fx-control-inner-background: transparent;"
                + "-fx-padding: 0;";
    }

    private String deckBrowserHintStyle() {
        return "-fx-text-fill: #d8c59a;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 6 28 6 28;"
                + "-fx-background-color: linear-gradient(to bottom, rgba(23, 21, 19, 0.82), rgba(6, 5, 5, 0.78));"
                + "-fx-background-radius: 4;"
                + "-fx-border-color: rgba(149, 112, 64, 0.54) rgba(44, 37, 31, 0.72) rgba(83, 18, 14, 0.64) rgba(44, 37, 31, 0.72);"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 4;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 9, 0.64, 0, 2);";
    }

    private String deckBrowserCloseButtonStyle() {
        return deckBrowserCloseButtonStyle(false);
    }

    private String deckBrowserCloseButtonStyle(boolean hovered) {
        String top = hovered ? "rgba(164, 48, 38, 0.98)" : "rgba(130, 32, 28, 0.98)";
        String bottom = hovered ? "rgba(94, 20, 18, 0.98)" : "rgba(54, 12, 12, 0.98)";
        String glow = hovered ? "rgba(192, 51, 38, 0.58)" : "rgba(0, 0, 0, 0.86)";
        return "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");"
                + "-fx-text-fill: #f1d39b;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: rgba(221, 157, 84, 0.78) rgba(50, 14, 13, 0.96) rgba(27, 7, 7, 0.98) rgba(154, 86, 52, 0.72);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 8 24 8 24;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, " + glow + ", 12, 0.42, 0, 3);";
    }

    private String deckBrowserSidebarStyle() {
        return "-fx-background-color: linear-gradient(to bottom, "
                + "rgba(28, 18, 17, 0.88), rgba(8, 7, 7, 0.90) 46%, rgba(44, 8, 8, 0.76));"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: rgba(128, 92, 55, 0.62) rgba(38, 34, 31, 0.92) rgba(90, 18, 16, 0.74) rgba(78, 58, 39, 0.64);"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.90), 24, 0.36, 0, 8);";
    }

    private String deckBrowserAvatarFrameStyle() {
        return "-fx-background-color: radial-gradient(center 50% 42%, radius 70%, "
                + "rgba(106, 20, 18, 0.50), rgba(12, 10, 10, 0.98));"
                + "-fx-background-radius: 64;"
                + "-fx-border-color: rgba(207, 158, 88, 0.68) rgba(35, 33, 31, 0.96) rgba(78, 16, 15, 0.90) rgba(143, 98, 58, 0.72);"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 64;"
                + "-fx-effect: dropshadow(gaussian, rgba(130, 17, 13, 0.34), 15, 0.32, 0, 2);";
    }

    private String deckBrowserSidebarNameStyle() {
        return "-fx-text-fill: #d9b875;"
                + "-fx-font-family: \"STKaiti\", \"KaiTi\", \"Serif\";"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 4, 0.76, 0, 2);";
    }

    private String deckBrowserSidebarTextStyle() {
        return "-fx-text-fill: #bcb1a1;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 2, 0.6, 0, 1);";
    }

    private String deckBrowserSidebarCountStyle() {
        return "-fx-text-fill: #d8c59a;"
                + "-fx-font-size: 13px;"
                + "-fx-padding: 5 12 5 12;"
                + "-fx-background-color: rgba(0, 0, 0, 0.34);"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: rgba(117, 78, 45, 0.54);"
                + "-fx-border-radius: 14;";
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

    private int clampInt(int value, int min, int max) {
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
        overlayLayer.setMouseTransparent(true);

        BorderPane battleLayer = new BorderPane();
        battleLayer.setPadding(new Insets(14, 24, 12, 24));
        battleLayer.setTop(createBattleHeader());
        battleLayer.setCenter(createBattleCenter());
        battleLayer.setBottom(createHandArea());

        StackPane battleInfoPanel = createBattleInfoPanel(root);
        StackPane.setAlignment(battleInfoPanel, Pos.TOP_CENTER);
        StackPane.setMargin(battleInfoPanel, new Insets(0, 0, 0, 0));

        HBox topRightButtons = createBattleTopRightButtons(overlayLayer);
        StackPane.setAlignment(topRightButtons, Pos.TOP_RIGHT);
        StackPane.setMargin(topRightButtons, new Insets(14, 24, 0, 0));

        root.getChildren().addAll(backgroundLayer, battleLayer, battleInfoPanel, topRightButtons, overlayLayer);
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

    private HBox createBattleHeader() {
        RunState run = engine.getRunState();
        HBox goldCounter = createResourceCounter(BATTLE_GOLD_RESOURCE, String.valueOf(run == null ? 0 : run.getGold()), "金币");

        HBox leftStatus = new HBox(10, createPlayerHeaderPanel(), goldCounter);
        leftStatus.setAlignment(Pos.TOP_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Region actionReserve = new Region();
        actionReserve.setMinSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        actionReserve.setPrefSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        actionReserve.setMaxSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        actionReserve.setMouseTransparent(true);

        HBox header = new HBox(14, leftStatus, spacer, actionReserve);
        header.setAlignment(Pos.TOP_CENTER);
        header.setPadding(new Insets(0, 0, 6, 0));
        return header;
    }

    private StackPane createBattleInfoPanel(StackPane root) {
        StackPane panel = new StackPane();
        panel.setPickOnBounds(false);
        panel.setMouseTransparent(true);
        panel.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> clamp(root.getWidth() * 0.22, 230, 390),
                root.widthProperty()));
        panel.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> panel.getPrefWidth() / BATTLE_INFO_PANEL_ASPECT,
                panel.prefWidthProperty()));
        panel.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ImageView frame = createUiImageView(BATTLE_INFO_PANEL_RESOURCE, 430, 174);
        frame.fitWidthProperty().bind(panel.widthProperty());
        frame.fitHeightProperty().bind(panel.heightProperty());

        statusLabel = new Label();
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setTextAlignment(TextAlignment.CENTER);
        statusLabel.setWrapText(false);
        statusLabel.setStyle(battleInfoTitleStyle());
        statusLabel.prefWidthProperty().bind(panel.widthProperty().multiply(0.46));
        statusLabel.maxWidthProperty().bind(panel.widthProperty().multiply(0.50));
        statusLabel.prefHeightProperty().bind(panel.heightProperty().multiply(0.22));
        statusLabel.maxHeightProperty().bind(panel.heightProperty().multiply(0.24));
        statusLabel.translateYProperty().bind(panel.heightProperty().multiply(-0.06));

        panel.getChildren().addAll(frame, statusLabel);
        return panel;
    }

    private HBox createBattleTopRightButtons(StackPane overlayLayer) {
        StackPane deckButton = createBattleIconButton(BATTLE_DECK_BUTTON_RESOURCE,
                "牌组",
                () -> showBattleDeckOverlay(overlayLayer));
        StackPane settingsButton = createBattleIconButton(BATTLE_SETTINGS_RESOURCE,
                "设置",
                () -> showBattleSettingsOverlay(overlayLayer));

        HBox buttons = new HBox(BATTLE_ACTION_BUTTON_GAP, deckButton, settingsButton);
        buttons.setAlignment(Pos.TOP_RIGHT);
        buttons.setMinSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        buttons.setPrefSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        buttons.setMaxSize(battleTopRightButtonsWidth(), BATTLE_ACTION_BUTTON_SIZE);
        buttons.setPickOnBounds(false);
        settingsButton.toFront();
        return buttons;
    }

    private double battleTopRightButtonsWidth() {
        return BATTLE_ACTION_BUTTON_SIZE * 2 + BATTLE_ACTION_BUTTON_GAP;
    }

    private void showBattleDeckOverlay(StackPane overlayLayer) {
        showDeckOverlay(overlayLayer, currentDeck());
    }

    private void showBattleSettingsOverlay(StackPane overlayLayer) {
        showGameMenuOverlay(overlayLayer);
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
        styleCombatantNameLabel(enemyLabel);
        styleCombatantIntentLabel(intentLabel);
        enemyVitalsBar = new BattleVitalsBar();
        CharacterPortrait enemyPortrait = isBossBattle() ? CharacterPortrait.boss() : CharacterPortrait.enemy();
        enemyEffectLayer = createEffectLayer();
        StackPane enemyUnit = createCombatantUnit(enemyPortrait, enemyEffectLayer, enemyVitalsBar, enemyLabel, intentLabel);
        enemyUnit.setTranslateY(COMBATANT_UNIT_BASELINE_OFFSET_Y);

        playerLabel = new Label();
        styleCombatantNameLabel(playerLabel);
        playerVitalsBar = new BattleVitalsBar();
        RunState run = engine.getRunState();
        String playerCharacterId = run == null ? CharacterPortrait.BERSERKER_CHARACTER_ID : run.getPlayableCharacter().id();
        CharacterPortrait playerPortrait = CharacterPortrait.player(playerCharacterId);
        playerEffectLayer = createEffectLayer();
        StackPane playerUnit = createCombatantUnit(playerPortrait, playerEffectLayer, playerVitalsBar, playerLabel, null);
        playerUnit.setTranslateY(COMBATANT_UNIT_BASELINE_OFFSET_Y);

        HBox units = new HBox(210, playerUnit, enemyUnit);
        units.setAlignment(Pos.BOTTOM_CENTER);
        units.setPadding(new Insets(42, 56, 22, 56));

        StackPane battlefield = new StackPane(units);
        battlefield.setMinHeight(300);
        battlefield.setPadding(new Insets(0, 0, 4, 0));
        StackPane.setAlignment(units, Pos.BOTTOM_CENTER);
        return battlefield;
    }

    private StackPane createCombatantUnit(CharacterPortrait portrait,
                                         StackPane effectLayer,
                                         BattleVitalsBar vitalsBar,
                                         Label nameLabel,
                                         Label intentLabel) {
        StackPane effectTarget = createEffectTarget(portrait, effectLayer);
        if (vitalsBar == playerVitalsBar) {
            playerEffectTarget = effectTarget;
        } else if (vitalsBar == enemyVitalsBar) {
            enemyEffectTarget = effectTarget;
        }

        StackPane unit = new StackPane(effectTarget, vitalsBar);
        unit.setPrefSize(COMBATANT_UNIT_WIDTH, COMBATANT_UNIT_HEIGHT);
        unit.setMinSize(COMBATANT_UNIT_WIDTH, COMBATANT_UNIT_HEIGHT);
        unit.setMaxSize(COMBATANT_UNIT_WIDTH, COMBATANT_UNIT_HEIGHT);
        unit.setPickOnBounds(false);

        StackPane.setAlignment(effectTarget, Pos.TOP_CENTER);
        StackPane.setAlignment(vitalsBar, Pos.TOP_CENTER);
        StackPane.setMargin(vitalsBar, new Insets(COMBATANT_VITALS_TOP, 0, 0, 0));

        VBox captions = new VBox(0, nameLabel);
        if (intentLabel != null) {
            captions.getChildren().add(intentLabel);
        }
        captions.setAlignment(Pos.TOP_CENTER);
        captions.setMouseTransparent(true);
        captions.setPrefWidth(COMBATANT_UNIT_WIDTH);
        captions.setMinWidth(COMBATANT_UNIT_WIDTH);
        captions.setMaxWidth(COMBATANT_UNIT_WIDTH);
        unit.getChildren().add(captions);
        StackPane.setAlignment(captions, Pos.TOP_CENTER);
        StackPane.setMargin(captions, new Insets(COMBATANT_CAPTION_TOP, 0, 0, 0));
        return unit;
    }

    private void styleCombatantNameLabel(Label label) {
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setMinSize(230, 25);
        label.setPrefSize(270, 25);
        label.setMaxSize(300, 25);
        label.setStyle("-fx-text-fill: #f7f0df;"
                + "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 4, 0.72, 0, 1);");
    }

    private void styleCombatantIntentLabel(Label label) {
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);
        label.setMinSize(260, 20);
        label.setPrefWidth(290);
        label.setMaxWidth(300);
        label.setStyle("-fx-text-fill: #f0c172;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.92), 4, 0.64, 0, 1);");
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
        handPane = new Pane();
        handPane.setMinHeight(HAND_CARD_SLOT_HEIGHT);
        handPane.setPrefHeight(HAND_CARD_SLOT_HEIGHT);
        handPane.setMaxHeight(HAND_CARD_SLOT_HEIGHT);
        handPane.setPickOnBounds(false);
        handPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (Math.abs(newValue.doubleValue() - oldValue.doubleValue()) > 1) {
                BattleState state = engine.getState();
                if (state != null) {
                    refreshHand(state);
                }
            }
        });

        StackPane handWell = new StackPane(handPane);
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
        handArea.setTranslateY(BATTLE_BOTTOM_UI_OFFSET_Y);
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

    private ImageView createVitalsImage(String resourcePath) {
        ImageView view = createUiImageView(resourcePath, COMBATANT_VITALS_WIDTH, COMBATANT_VITALS_HEIGHT);
        view.setPreserveRatio(false);
        return view;
    }

    private final class BattleVitalsBar extends StackPane {
        private final ImageView healthFill;
        private final ImageView shieldFill;
        private final Rectangle healthClip = new Rectangle(0, COMBATANT_VITALS_HEIGHT);
        private final Rectangle shieldClip = new Rectangle(0, COMBATANT_VITALS_HEIGHT);
        private final Label healthText = new Label();
        private final Label blockText = new Label();

        private BattleVitalsBar() {
            ImageView emptySlot = createVitalsImage(BATTLE_EMPTY_HEALTH_RESOURCE);
            healthFill = createVitalsImage(BATTLE_FULL_HEALTH_RESOURCE);
            shieldFill = createVitalsImage(BATTLE_FULL_SHIELD_RESOURCE);
            healthFill.setClip(healthClip);
            shieldFill.setClip(shieldClip);

            healthText.setAlignment(Pos.CENTER);
            healthText.setTextAlignment(TextAlignment.CENTER);
            healthText.setPrefSize(COMBATANT_VITALS_WIDTH, 28);
            healthText.setMinSize(COMBATANT_VITALS_WIDTH, 28);
            healthText.setMaxSize(COMBATANT_VITALS_WIDTH, 28);

            blockText.setAlignment(Pos.CENTER);
            blockText.setTextAlignment(TextAlignment.CENTER);
            blockText.setPrefSize(COMBATANT_SHIELD_VALUE_WIDTH, COMBATANT_SHIELD_VALUE_HEIGHT);
            blockText.setMinSize(COMBATANT_SHIELD_VALUE_WIDTH, COMBATANT_SHIELD_VALUE_HEIGHT);
            blockText.setMaxSize(COMBATANT_SHIELD_VALUE_WIDTH, COMBATANT_SHIELD_VALUE_HEIGHT);
            blockText.setStyle("-fx-text-fill: #ffffff;"
                    + "-fx-font-size: 18px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 20, 56, 0.96), 5, 0.78, 0, 1);");

            setPrefSize(COMBATANT_VITALS_WIDTH, COMBATANT_VITALS_HEIGHT);
            setMinSize(COMBATANT_VITALS_WIDTH, COMBATANT_VITALS_HEIGHT);
            setMaxSize(COMBATANT_VITALS_WIDTH, COMBATANT_VITALS_HEIGHT);
            setMouseTransparent(true);
            setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.78), 9, 0.42, 0, 2);");
            getChildren().addAll(emptySlot, healthFill, shieldFill, healthText, blockText);

            StackPane.setAlignment(healthText, Pos.CENTER);
            StackPane.setMargin(healthText, new Insets(0, 0, 4, 0));
            StackPane.setAlignment(blockText, Pos.TOP_LEFT);
            StackPane.setMargin(blockText, new Insets(COMBATANT_SHIELD_VALUE_TOP,
                    0,
                    0,
                    COMBATANT_SHIELD_VALUE_LEFT));
            update(1, 1, 0);
        }

        private void update(int health, int maxHealth, int block) {
            int safeMaxHealth = Math.max(1, maxHealth);
            int safeHealth = clampInt(health, 0, safeMaxHealth);
            double ratio = clamp((double) safeHealth / safeMaxHealth, 0, 1);
            double fillWidth = COMBATANT_VITALS_WIDTH * ratio;
            boolean hasBlock = block > 0 && safeHealth > 0;

            healthClip.setWidth(fillWidth);
            shieldClip.setWidth(fillWidth);
            healthFill.setVisible(!hasBlock && fillWidth > 0.5);
            shieldFill.setVisible(hasBlock && fillWidth > 0.5);

            healthText.setText(safeHealth + "/" + safeMaxHealth);
            healthText.setStyle("-fx-text-fill: " + (hasBlock ? "#ecfbff" : "#fff0df") + ";"
                    + "-fx-font-size: 18px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.96), 4, 0.78, 0, 1);");
            blockText.setText(hasBlock ? String.valueOf(block) : "");
            blockText.setVisible(hasBlock);
        }
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

    private ImageView createRawUiImageView(String resourcePath, double fitWidth, double fitHeight) {
        ImageView view = new ImageView();
        var resource = GameApplication.class.getResource(resourcePath);
        if (resource != null) {
            Image image = uiImages.computeIfAbsent(resourcePath, ignored -> new Image(resource.toExternalForm()));
            view.setImage(image);
        }
        view.setFitWidth(fitWidth);
        view.setFitHeight(fitHeight);
        view.setPreserveRatio(false);
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
        if (MAIN_MENU_START_BUTTON_RESOURCE.equals(resourcePath) || MAIN_MENU_EXIT_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 0, 249, 1536, 440);
        }
        if (SETTINGS_BACK_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 204, 118, 1148, 722);
        }
        if (SETTINGS_SAVE_EXIT_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 174, 156, 1188, 650);
        }
        if (CHARACTER_SELECT_DEPART_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 71, 189, 1397, 452);
        }
        if (CHARACTER_SELECT_BACK_BUTTON_RESOURCE.equals(resourcePath)) {
            return clampedViewport(image, 0, 0, 1536, 497);
        }
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

    private StackPane createBattleIconButton(String resourcePath, String tooltipText, Runnable action) {
        StackPane icon = createIconSlot(resourcePath,
                BATTLE_ACTION_BUTTON_SIZE,
                BATTLE_ACTION_BUTTON_SIZE,
                BATTLE_ACTION_ICON_SIZE,
                BATTLE_ACTION_ICON_SIZE);
        icon.setMouseTransparent(true);

        StackPane button = new StackPane(icon);
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setMinSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setMaxSize(BATTLE_ACTION_BUTTON_SIZE, BATTLE_ACTION_BUTTON_SIZE);
        button.setPickOnBounds(true);
        button.setStyle(battleIconButtonStyle(false, false));
        Tooltip.install(button, new Tooltip(tooltipText));
        button.setOnMouseEntered(event -> {
            icon.setScaleX(1.04);
            icon.setScaleY(1.04);
            button.setStyle(battleIconButtonStyle(true, false));
        });
        button.setOnMouseExited(event -> {
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
            icon.setTranslateY(0);
            button.setStyle(battleIconButtonStyle(false, false));
        });
        button.setOnMousePressed(event -> {
            icon.setScaleX(0.98);
            icon.setScaleY(0.98);
            icon.setTranslateY(1);
            button.setStyle(battleIconButtonStyle(true, true));
        });
        button.setOnMouseReleased(event -> {
            icon.setScaleX(button.isHover() ? 1.04 : 1.0);
            icon.setScaleY(button.isHover() ? 1.04 : 1.0);
            icon.setTranslateY(0);
            button.setStyle(battleIconButtonStyle(button.isHover(), false));
        });
        button.setOnMouseClicked(event -> {
            if (action != null) {
                action.run();
                event.consume();
            }
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
        playerHeaderHealthLabel.setStyle("-fx-text-fill: #fff0df;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.90), 4, 0.6, 0, 1);");
        playerHeaderHealthLabel.setPrefSize(PLAYER_HEADER_HEALTH_BAR_WIDTH, 24);
        playerHeaderHealthLabel.setMinSize(PLAYER_HEADER_HEALTH_BAR_WIDTH, 24);
        playerHeaderHealthLabel.setMaxSize(PLAYER_HEADER_HEALTH_BAR_WIDTH, 24);
        playerHeaderHealthLabel.setAlignment(Pos.CENTER);
        playerHeaderHealthLabel.setTextAlignment(TextAlignment.CENTER);

        ImageView emptyHealth = createRawUiImageView(currentPlayerHeaderHealthEmptyResource(),
                PLAYER_HEADER_WIDTH,
                PLAYER_HEADER_HEIGHT);
        ImageView fullHealth = createRawUiImageView(currentPlayerHeaderHealthFullResource(),
                PLAYER_HEADER_WIDTH,
                PLAYER_HEADER_HEIGHT);
        playerHeaderHealthClip = new Rectangle(PLAYER_HEADER_HEALTH_BAR_LEFT,
                PLAYER_HEADER_HEALTH_BAR_TOP,
                PLAYER_HEADER_HEALTH_BAR_WIDTH,
                PLAYER_HEADER_HEALTH_BAR_HEIGHT);
        fullHealth.setClip(playerHeaderHealthClip);

        StackPane panel = new StackPane(emptyHealth, fullHealth, playerHeaderHealthLabel);
        panel.setPrefSize(PLAYER_HEADER_WIDTH, PLAYER_HEADER_HEIGHT);
        panel.setMinSize(PLAYER_HEADER_WIDTH, PLAYER_HEADER_HEIGHT);
        panel.setMaxSize(PLAYER_HEADER_WIDTH, PLAYER_HEADER_HEIGHT);
        clipToBounds(panel);
        StackPane.setAlignment(playerHeaderHealthLabel, Pos.TOP_LEFT);
        StackPane.setMargin(playerHeaderHealthLabel, new Insets(PLAYER_HEADER_HEALTH_BAR_TOP - 3,
                0,
                0,
                PLAYER_HEADER_HEALTH_BAR_LEFT));
        return panel;
    }

    private String currentPlayerHeaderHealthFullResource() {
        if (currentPlayableCharacterIs(ASSASSIN_CHARACTER_ID)) {
            return BATTLE_ASSASSIN_HEADER_HEALTH_FULL_RESOURCE;
        }
        return BATTLE_BERSERKER_HEADER_HEALTH_FULL_RESOURCE;
    }

    private String currentPlayerHeaderHealthEmptyResource() {
        if (currentPlayableCharacterIs(ASSASSIN_CHARACTER_ID)) {
            return BATTLE_ASSASSIN_HEADER_HEALTH_EMPTY_RESOURCE;
        }
        return BATTLE_BERSERKER_HEADER_HEALTH_EMPTY_RESOURCE;
    }

    private boolean currentPlayableCharacterIs(String characterId) {
        RunState run = engine.getRunState();
        return run != null && characterId.equals(run.getPlayableCharacter().id());
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

    private void refreshBattle() {
        BattleState state = engine.getState();
        playerLabel.setText(state.getPlayer().getName());
        enemyLabel.setText(state.getEnemy().getName());
        playerHeaderNameLabel.setText(state.getPlayer().getName());
        updatePlayerHeaderHealth(state.getPlayer().getHealth(), state.getPlayer().getMaxHealth());
        playerVitalsBar.update(state.getPlayer().getHealth(),
                state.getPlayer().getMaxHealth(),
                state.getPlayer().getBlock());
        enemyVitalsBar.update(state.getEnemy().getHealth(),
                state.getEnemy().getMaxHealth(),
                state.getEnemy().getBlock());
        intentLabel.setText(enemyIntentText(state));
        energyLabel.setText(state.getEnergy() + "/" + state.getMaxEnergy());
        drawPileCountLabel.setText(String.valueOf(state.getDrawPile().size()));
        discardPileCountLabel.setText(String.valueOf(state.getDiscardPile().size()));
        pileLabel.setText("当前牌组 " + state.getDeck().size() + " 张");
        statusLabel.setText(battleSceneTitle(state));

        refreshHand(state);
        endTurnButton.setDisable(state.getStatus() != GameStatus.IN_PROGRESS);
    }

    private void updatePlayerHeaderHealth(int health, int maxHealth) {
        int safeMaxHealth = Math.max(1, maxHealth);
        int safeHealth = clampInt(health, 0, safeMaxHealth);
        double ratio = clamp((double) safeHealth / safeMaxHealth, 0, 1);
        playerHeaderHealthLabel.setText(safeHealth + "/" + safeMaxHealth);
        if (playerHeaderHealthClip != null) {
            playerHeaderHealthClip.setWidth(PLAYER_HEADER_HEALTH_BAR_WIDTH * ratio);
        }
    }

    private void refreshHand(BattleState state) {
        handPane.getChildren().clear();
        if (state.getHand().isEmpty()) {
            Label empty = new Label("没有手牌");
            empty.setStyle("-fx-text-fill: #c8c2b6; -fx-font-size: 15px; -fx-font-weight: bold;");
            empty.layoutXProperty().bind(handPane.widthProperty().subtract(empty.widthProperty()).divide(2));
            empty.setLayoutY(HAND_CARD_SLOT_HEIGHT * 0.5);
            handPane.getChildren().add(empty);
            return;
        }

        int totalCards = state.getHand().size();
        double handWidth = currentHandPaneWidth();
        double cardStep = handCardStep(totalCards, handWidth);
        double totalWidth = HAND_CARD_WIDTH + cardStep * (totalCards - 1);
        double startX = HAND_CARD_SIDE_PADDING + (handContentWidth(handWidth) - totalWidth) / 2.0;
        for (int i = 0; i < state.getHand().size(); i++) {
            Card card = state.getHand().get(i);
            int index = i;
            boolean canPlay = state.getStatus() == GameStatus.IN_PROGRESS && state.effectiveCost(card) <= state.getEnergy();
            StackPane slot = createHandCardSlot(card, index, totalCards, canPlay, () -> {
                Set<CardVisualEffect> visualEffects = card.getVisualEffects();
                if (engine.playCard(index)) {
                    playPlayerCardVisualEffects(visualEffects);
                }
                afterBattleAction();
            });
            slot.setLayoutX(startX + index * cardStep);
            slot.setLayoutY(0);
            handPane.getChildren().add(slot);
        }
    }

    private StackPane createHandCardSlot(Card card, int index, int totalCards, boolean canPlay, Runnable playAction) {
        double baseRotate = cardRotation(index, totalCards);
        double baseTranslateY = cardFanTranslateY(index, totalCards);
        double baseViewOrder = Math.abs(index - (totalCards - 1) / 2.0);

        StackPane face = cardFace(card);
        face.setRotate(baseRotate);
        face.setTranslateY(baseTranslateY);
        face.setOpacity(canPlay ? 1.0 : 0.64);
        face.setStyle("-fx-cursor: hand;");

        StackPane slot = new StackPane(face);
        slot.setAlignment(Pos.CENTER);
        slot.setPickOnBounds(false);
        slot.setPrefSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setMinSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setMaxSize(HAND_CARD_WIDTH, HAND_CARD_SLOT_HEIGHT);
        slot.setViewOrder(baseViewOrder);
        slot.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Tooltip.install(slot, new Tooltip(card.getName() + "\n费用：" + card.getCost() + "\n" + card.getDescription()));

        slot.setOnMouseClicked(event -> {
            if (!canPlay) {
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
        if (visualEffects.contains(CardVisualEffect.BUFF)) {
            playBuffEffect(shieldLayer);
        }
        if (visualEffects.contains(CardVisualEffect.ATTACK)) {
            playAttackEffect(attackLayer, attackTarget);
        }
    }

    private void playShieldEffect(StackPane targetLayer) {
        if (targetLayer == null) {
            return;
        }

        ImageView shield = new ImageView(shieldEffectImage());
        shield.setPreserveRatio(true);
        shield.setSmooth(true);
        shield.setCache(true);
        shield.setFitWidth(260);
        shield.setOpacity(0.0);
        shield.setScaleX(0.62);
        shield.setScaleY(0.62);
        shield.setTranslateY(-10);
        shield.setBlendMode(BlendMode.SCREEN);
        shield.setMouseTransparent(true);
        StackPane.setAlignment(shield, Pos.CENTER);

        targetLayer.getChildren().add(shield);

        ParallelTransition animation = new ParallelTransition(
                fade(shield, 0.0, 0.92, 140),
                scale(shield, 0.62, 1.0, 440),
                translate(shield, 0, 6, 440)
        );
        animation.setOnFinished(event -> {
            ParallelTransition exit = new ParallelTransition(
                    fade(shield, shield.getOpacity(), 0.0, 220),
                    scale(shield, 1.0, 1.08, 220)
            );
            exit.setOnFinished(done -> targetLayer.getChildren().remove(shield));
            exit.play();
        });
        animation.play();
    }

    private void playBuffEffect(StackPane targetLayer) {
        if (targetLayer == null) {
            return;
        }

        ImageView buff = new ImageView(buffEffectImage());
        buff.setPreserveRatio(true);
        buff.setSmooth(true);
        buff.setCache(true);
        buff.setFitWidth(270);
        buff.setOpacity(0.0);
        buff.setScaleX(0.5);
        buff.setScaleY(0.5);
        buff.setTranslateY(-16);
        buff.setBlendMode(BlendMode.SCREEN);
        buff.setMouseTransparent(true);
        StackPane.setAlignment(buff, Pos.CENTER);

        targetLayer.getChildren().add(buff);

        ParallelTransition animation = new ParallelTransition(
                fade(buff, 0.0, 0.94, 120),
                scale(buff, 0.5, 1.02, 420),
                translate(buff, 0, -28, 420)
        );
        animation.setOnFinished(event -> {
            ParallelTransition exit = new ParallelTransition(
                    fade(buff, buff.getOpacity(), 0.0, 240),
                    scale(buff, 1.02, 1.12, 240)
            );
            exit.setOnFinished(done -> targetLayer.getChildren().remove(buff));
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

    private Image shieldEffectImage() {
        if (shieldEffectImage == null) {
            String resource = GameApplication.class.getResource(BATTLE_SHIELD_EFFECT_RESOURCE).toExternalForm();
            shieldEffectImage = new Image(resource);
        }
        return shieldEffectImage;
    }

    private Image buffEffectImage() {
        if (buffEffectImage == null) {
            String resource = GameApplication.class.getResource(BATTLE_BUFF_EFFECT_RESOURCE).toExternalForm();
            buffEffectImage = new Image(resource);
        }
        return buffEffectImage;
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

        ImageView cardArt = cardArtView(card);

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
        type.setMinSize(64, 18);
        type.setMaxSize(76, 18);
        type.setStyle("-fx-text-fill: " + cardTypeTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 11px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-smoothing-type: lcd;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.72), 1.2, 0.45, 0, 1);");
        StackPane.setAlignment(type, Pos.BOTTOM_CENTER);
        StackPane.setMargin(type, new Insets(0, 0, cardTypeBottomMargin(card.getRarity()), 0));

        Label desc = new Label(card.getDescription());
        desc.setWrapText(true);
        desc.setLineSpacing(2);
        desc.setTextAlignment(TextAlignment.CENTER);
        desc.setAlignment(Pos.CENTER);
        desc.setMaxWidth(HAND_CARD_WIDTH - 42);
        desc.setMinHeight(60);
        desc.setMaxHeight(60);
        desc.setStyle("-fx-text-fill: " + cardDescriptionTextColor(card.getRarity()) + ";"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-font-smoothing-type: lcd;"
                + cardDescriptionTextEffect(card.getRarity()));
        StackPane.setAlignment(desc, Pos.BOTTOM_CENTER);
        StackPane.setMargin(desc, new Insets(0, 20, 52, 20));

        StackPane face = new StackPane(
                template,
                cardArt,
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

    private ImageView cardArtView(Card card) {
        Image image = cardArtImage(card.getType());
        ImageView art = new ImageView(image);
        art.setViewport(coverViewport(image, HAND_CARD_ART_WIDTH / HAND_CARD_ART_HEIGHT));
        art.setFitWidth(HAND_CARD_ART_WIDTH);
        art.setFitHeight(HAND_CARD_ART_HEIGHT);
        art.setPreserveRatio(false);
        art.setSmooth(true);
        art.setCache(true);
        art.setMouseTransparent(true);

        Rectangle clip = new Rectangle(HAND_CARD_ART_WIDTH, HAND_CARD_ART_HEIGHT);
        clip.setArcWidth(HAND_CARD_ART_CLIP_ARC);
        clip.setArcHeight(HAND_CARD_ART_CLIP_ARC);
        art.setClip(clip);

        StackPane.setAlignment(art, Pos.TOP_CENTER);
        StackPane.setMargin(art, new Insets(HAND_CARD_ART_TOP_MARGIN, 0, 0, 0));
        return art;
    }

    private double cardTypeBottomMargin(CardRarity rarity) {
        return rarity == CardRarity.COMMON
                ? COMMON_CARD_TYPE_BOTTOM_MARGIN
                : DECORATED_CARD_TYPE_BOTTOM_MARGIN;
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

    private Image cardArtImage(CardType type) {
        String resourcePath = cardArtResource(type);
        var resource = GameApplication.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing card art: " + resourcePath);
        }
        return uiImages.computeIfAbsent(resourcePath, ignored -> new Image(resource.toExternalForm()));
    }

    private String cardArtResource(CardType type) {
        return switch (type) {
            case ATTACK -> ATTACK_CARD_ART_RESOURCE;
            case DEFENSE, SKILL -> DEFENSE_CARD_ART_RESOURCE;
            case BUFF, DEBUFF, TACTIC -> BUFF_CARD_ART_RESOURCE;
        };
    }

    private Rectangle2D coverViewport(Image image, double targetAspect) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0 || targetAspect <= 0) {
            return Rectangle2D.EMPTY;
        }

        double imageAspect = imageWidth / imageHeight;
        if (imageAspect > targetAspect) {
            double width = imageHeight * targetAspect;
            double x = (imageWidth - width) / 2.0;
            return new Rectangle2D(x, 0, width, imageHeight);
        }

        double height = imageWidth / targetAspect;
        double y = (imageHeight - height) / 2.0;
        return new Rectangle2D(0, y, imageWidth, height);
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
            case COMMON -> "#f0d49a";
            case UNCOMMON, RARE -> "#e9f8ff";
            case LEGENDARY -> "#f7dfad";
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
            case COMMON, LEGENDARY -> "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 1.2, 0.45, 0, 1);";
            case UNCOMMON, RARE, SPECIAL -> "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.86), 1.4, 0.5, 0, 1);";
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

    private String characterChoiceStyle(boolean selected) {
        String shadow = selected
                ? "dropshadow(gaussian, rgba(242, 192, 120, 0.30), 18, 0.24, 0, 5)"
                : "dropshadow(gaussian, rgba(0, 0, 0, 0.28), 8, 0.16, 0, 3)";
        return "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
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

    private String battleSceneTitle(BattleState state) {
        if (state == null) {
            return "暗城遭遇";
        }
        if (state.getStatus() == GameStatus.VICTORY) {
            return "战斗胜利";
        }
        if (state.getStatus() == GameStatus.DEFEAT) {
            return "远征失败";
        }
        if (state.getStatus() == GameStatus.REWARD_CLAIMED) {
            return "领取奖励";
        }

        if (isBossBattle()) {
            return "王座决战";
        }
        return "暗城遭遇";
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

    private double currentHandPaneWidth() {
        double width = handPane == null ? 0 : handPane.getWidth();
        if (width <= HAND_CARD_WIDTH) {
            double sceneWidth = stage != null && stage.getScene() != null ? stage.getScene().getWidth() : 1200;
            width = sceneWidth - 560;
        }
        return Math.max(width, HAND_CARD_WIDTH + HAND_CARD_SIDE_PADDING * 2);
    }

    private double handContentWidth(double handWidth) {
        return Math.max(HAND_CARD_WIDTH, handWidth - HAND_CARD_SIDE_PADDING * 2);
    }

    private double handCardStep(int totalCards, double handWidth) {
        if (totalCards <= 1) {
            return 0;
        }
        double fitStep = (handContentWidth(handWidth) - HAND_CARD_WIDTH) / (totalCards - 1);
        return Math.max(0, Math.min(HAND_CARD_PREFERRED_STEP, fitStep));
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

    private String settingsImageButtonStyle() {
        return "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
                + "-fx-padding: 0;"
                + "-fx-cursor: hand;";
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

    private String battleInfoTitleStyle() {
        return "-fx-text-fill: #f1d6a0;"
                + "-fx-font-family: \"STXingkai\", \"STKaiti\", \"KaiTi\", \"FangSong\", \"Serif\";"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.95), 5, 0.65, 0, 1);";
    }
}
