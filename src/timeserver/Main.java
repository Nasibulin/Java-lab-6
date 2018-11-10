package timeserver;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

public class Main extends Application {
    private static final double TILE_WIDTH = 350;
    private static final double TILE_HEIGHT = 350;
    private Tile clockTile;
    private static final int PORT = 8080;
    private static String HOST = "localhost";
    private static String fuser = "gettime";
    private static String fserver = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        //gridPane.setAlignment(Pos.CENTER);
        //gridPane.setCenterShape(true);
        gridPane.setPadding(new Insets(5));
        gridPane.setPrefSize(320, 320);

        Button saveButt = new Button("Get Time");
        saveButt.setMinWidth(75);

        saveButt.setOnAction((ActionEvent event) -> {

            try {
                clockTile.setTime(ZonedDateTime.ofLocal(getDateTime(), ZoneId.systemDefault(), ZoneOffset.UTC));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        gridPane.add(clockTile, 1, 1, 1, 1);
        gridPane.add(saveButt, 1, 2, 1, 1);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(10);

        Scene scene = new Scene(gridPane);
        scene.setCamera(camera);

        primaryStage.setTitle("Date-Time Update app");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void init() {
        clockTile = TileBuilder.create()
                .skinType(Tile.SkinType.CLOCK)
                .backgroundColor(Color.web("#42586c"))
                .prefSize(TILE_WIDTH, TILE_HEIGHT)
                .title("Установка Даты/Времени")
                .text("Нажмите, чтобы получить время с сервера...")
                .dateVisible(true)
                .locale(Locale.getDefault())
                .running(false)
                .build();
        clockTile.setTime(15416734);
    }


    public static LocalDateTime getDateTime() throws IOException {

        try (
                Socket server = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                PrintWriter out = new PrintWriter(server.getOutputStream(), true)
        ) {
            out.println(fuser);
            fserver = in.readLine();
        }
        return LocalDateTime.parse(fserver);
    }

}
