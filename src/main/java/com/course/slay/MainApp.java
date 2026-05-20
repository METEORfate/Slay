package com.course.slay;

import com.course.slay.ui.GameApplication;
import javafx.application.Application;

public final class MainApp {
    private MainApp() {
    }

    public static void main(String[] args) {
        Application.launch(GameApplication.class, args);
    }
}

