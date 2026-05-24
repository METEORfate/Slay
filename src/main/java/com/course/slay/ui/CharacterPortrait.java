package com.course.slay.ui;

import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class CharacterPortrait extends Pane {
    private static final double WIDTH = 260;
    private static final double HEIGHT = 310;

    private CharacterPortrait() {
        setPrefSize(WIDTH, HEIGHT);
        setMinSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);
        setClip(new Rectangle(WIDTH, HEIGHT));
        getChildren().add(shadow());
    }

    public static CharacterPortrait player() {
        CharacterPortrait portrait = new CharacterPortrait();
        if (!portrait.addImage("/assets/portraits/player.png", 300)) {
            portrait.drawPlayer();
        }
        return portrait;
    }

    public static CharacterPortrait enemy() {
        CharacterPortrait portrait = new CharacterPortrait();
        if (!portrait.addImage("/assets/portraits/minion.png", 300)) {
            portrait.drawEnemy();
        }
        return portrait;
    }

    public static CharacterPortrait boss() {
        CharacterPortrait portrait = new CharacterPortrait();
        if (!portrait.addImage("/assets/portraits/boss.png", 304)) {
            portrait.drawEnemy();
        }
        return portrait;
    }

    private boolean addImage(String resourcePath, double fitHeight) {
        try (var stream = CharacterPortrait.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return false;
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setFitHeight(fitHeight);
            imageView.setFitWidth(WIDTH);
            double scale = image.getWidth() > 0 && image.getHeight() > 0
                    ? Math.min(WIDTH / image.getWidth(), fitHeight / image.getHeight())
                    : 1;
            double renderedWidth = image.getWidth() > 0 ? image.getWidth() * scale : WIDTH;
            double renderedHeight = image.getHeight() > 0 ? image.getHeight() * scale : fitHeight;
            imageView.setLayoutX((WIDTH - renderedWidth) / 2);
            imageView.setLayoutY(Math.max(2, HEIGHT - renderedHeight - 6));
            getChildren().add(imageView);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void drawPlayer() {
        Polygon cloak = new Polygon(
                125, 88,
                82, 255,
                178, 255,
                155, 112
        );
        cloak.setFill(new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, Color.web("#415f76")),
                new Stop(0.55, Color.web("#223345")),
                new Stop(1, Color.web("#131922"))));
        cloak.setStroke(Color.web("#9cc7cf"));
        cloak.setStrokeWidth(2);

        Polygon coat = new Polygon(
                126, 108,
                103, 255,
                154, 255,
                146, 111
        );
        coat.setFill(Color.web("#1c2530"));
        coat.setStroke(Color.web("#67879a"));

        Circle head = new Circle(130, 76, 27);
        head.setFill(Color.web("#d0b38b"));
        head.setStroke(Color.web("#f2d6a0"));
        head.setStrokeWidth(2);

        Polygon hood = new Polygon(
                97, 76,
                130, 34,
                163, 76,
                150, 58,
                130, 52,
                110, 58
        );
        hood.setFill(Color.web("#2f4d61"));
        hood.setStroke(Color.web("#9cc7cf"));
        hood.setStrokeWidth(2);

        Rectangle blade = new Rectangle(186, 60, 7, 158);
        blade.setRotate(18);
        blade.setFill(new LinearGradient(0, 0, 1, 0, true, null,
                new Stop(0, Color.web("#9fb4bd")),
                new Stop(0.5, Color.web("#f1f1de")),
                new Stop(1, Color.web("#798c98"))));

        Rectangle hilt = new Rectangle(166, 187, 50, 8);
        hilt.setRotate(18);
        hilt.setFill(Color.web("#d28a47"));

        Line arm = new Line(154, 127, 190, 188);
        arm.setStroke(Color.web("#8fb6c7"));
        arm.setStrokeWidth(8);

        Circle lantern = new Circle(77, 206, 17);
        lantern.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.8, true, null,
                new Stop(0, Color.web("#fff4a3")),
                new Stop(0.55, Color.web("#de8f38")),
                new Stop(1, Color.web("#6b341d"))));
        lantern.setStroke(Color.web("#f2c078"));
        lantern.setStrokeWidth(2);

        Line lanternChain = new Line(94, 132, 77, 190);
        lanternChain.setStroke(Color.web("#9cc7cf"));
        lanternChain.setStrokeWidth(3);

        getChildren().addAll(blade, hilt, cloak, coat, arm, lanternChain, lantern, head, hood);
    }

    private void drawEnemy() {
        Ellipse mist = new Ellipse(135, 210, 105, 50);
        mist.setFill(Color.web("#38404d", 0.55));

        Polygon mantle = new Polygon(
                132, 58,
                62, 255,
                205, 255,
                178, 96
        );
        mantle.setFill(new LinearGradient(0, 0, 0, 1, true, null,
                new Stop(0, Color.web("#6f3540")),
                new Stop(0.56, Color.web("#34202b")),
                new Stop(1, Color.web("#17121a"))));
        mantle.setStroke(Color.web("#d08a66"));
        mantle.setStrokeWidth(2);

        Polygon mask = new Polygon(
                109, 81,
                130, 47,
                154, 82,
                145, 121,
                116, 121
        );
        mask.setFill(Color.web("#d7c0a4"));
        mask.setStroke(Color.web("#f0d08b"));
        mask.setStrokeWidth(2);

        Circle leftEye = new Circle(122, 89, 4, Color.web("#ffcf6b"));
        Circle rightEye = new Circle(140, 89, 4, Color.web("#ffcf6b"));

        Polygon shoulder = new Polygon(
                92, 122,
                45, 176,
                85, 196,
                131, 138,
                179, 196,
                220, 174,
                170, 120
        );
        shoulder.setFill(Color.web("#4d2932"));
        shoulder.setStroke(Color.web("#aa6f5f"));

        Rectangle spear = new Rectangle(204, 50, 7, 210);
        spear.setRotate(-12);
        spear.setFill(Color.web("#9d917d"));

        Polygon spearHead = new Polygon(199, 45, 213, 12, 227, 45);
        spearHead.setRotate(-12);
        spearHead.setFill(Color.web("#d9d2bb"));
        spearHead.setStroke(Color.web("#f3e3b8"));

        Line arm = new Line(158, 136, 204, 188);
        arm.setStroke(Color.web("#b87965"));
        arm.setStrokeWidth(9);

        getChildren().addAll(mist, spear, spearHead, mantle, shoulder, arm, mask, leftEye, rightEye);
    }

    private Ellipse shadow() {
        Ellipse shadow = new Ellipse(130, 269, 92, 19);
        shadow.setFill(Color.web("#000000", 0.35));
        return shadow;
    }
}
