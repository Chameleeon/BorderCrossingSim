package net.etfbl.pj2;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.GridPane;
import java.io.File;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Main extends Application {
  public static StackPane root = new StackPane();
  protected static ImageView P1;
  protected static ImageView P2;
  protected static ImageView P3;
  protected static ImageView C1;
  protected static ImageView C2;

  protected static ImageView inactiveP1;
  protected static ImageView inactiveP2;
  protected static ImageView inactiveP3;
  protected static ImageView inactiveC1;
  protected static ImageView inactiveC2;

  protected static ImageView activeP1;
  protected static ImageView activeP2;
  protected static ImageView activeP3;
  protected static ImageView activeC1;
  protected static ImageView activeC2;

  private static StackPane activePane;
  private AnimationTimer incidentRenderer;
  private AnimationTimer animationTimer;
  private boolean incidentPopupActive = false;
  private ArrayList<String> addedIncidentVehicles;
  private static StackPane incidentRootPane;
  private static StackPane popupPane;
  private static StackPane incidentPopupPane;
  private GridPane incidentVehiclesContainer;
  private int incidentCol = 0;
  private int incidentRow = 0;
  private int elapsedTimeInSeconds = 0;
  private Text timerText = new Text("Time: 0:00");
  private Timeline timeline;
  private static ImageView popupVehicle;
  private boolean simulationRunning;
  protected static Stage primaryStage;
  private Scene simulationScene;
  private static StackPane simulationPane;
  private static ImageView policeTerminalVehicle1;
  private static ImageView policeTerminalVehicle2;
  private static ImageView policeTerminalVehicle3;
  private static ImageView customsTerminalVehicle1;
  private static ImageView customsTerminalVehicle2;
  private TextFlow eventOutputs;
  private static boolean popupActive = false;
  protected static final Object pauseMonitor = new Object();
  Scene mainScene = new Scene(root, 3840, 2160);
  private static Scene incidentScene;
  private Scene queueScene;
  private StackPane queueStackPane;
  AnimationTimer queueTimer;

  public static void main(String args[]) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Main.primaryStage = primaryStage;
    primaryStage.setTitle("Border Crossing Simulation");

    String relativeImagePath = "data" + File.separator + "icons" + File.separator + "background.png";
    String fullImagePath = System.getProperty("user.dir") + File.separator + relativeImagePath;
    Image backgroundImage = new Image(new File(fullImagePath).toURI().toString());
    ImageView backgroundImageView = new ImageView(backgroundImage);
    backgroundImageView.setPreserveRatio(true);
    root.getChildren().add(backgroundImageView);
    backgroundImageView.fitWidthProperty().bind(root.widthProperty());

    ImageView startButton = createImageView("start_button.png");
    EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        startSimulation();
        incidentScene = null;
        activePane = simulationPane;
      }
    };

    startButton.setOnMouseClicked(clickHandler);

    ImageView exitButton = createImageView("exit_button.png");
    EventHandler<MouseEvent> exitHandler = new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        primaryStage.close();
      }
    };

    exitButton.setOnMouseClicked(event -> {
      if (event.getClickCount() == 1) {
        exitHandler.handle(null);
      }
    });

    VBox menu = new VBox(150);
    menu.setAlignment(Pos.CENTER);
    menu.getChildren().addAll(startButton, exitButton);

    double buttonWidthPercentage = 0.2;
    double buttonHeightPercentage = 0.15;

    startButton.fitWidthProperty().bind(mainScene.widthProperty().multiply(buttonWidthPercentage));
    exitButton.fitWidthProperty().bind(mainScene.widthProperty().multiply(buttonWidthPercentage));
    exitButton.fitHeightProperty().bind(mainScene.heightProperty().multiply(buttonHeightPercentage));
    startButton.fitHeightProperty().bind(mainScene.heightProperty().multiply(buttonHeightPercentage));
    VBox.setVgrow(startButton, Priority.NEVER);
    VBox.setVgrow(exitButton, Priority.NEVER);

    double vboxWidthPercentage = 0.2;
    double vboxHeightPercentage = 0.15;

    menu.prefWidthProperty().bind(primaryStage.widthProperty().multiply(vboxWidthPercentage));
    menu.prefHeightProperty().bind(primaryStage.heightProperty().multiply(vboxHeightPercentage));

    root.getChildren().add(menu);
    root.setAlignment(Pos.CENTER);
    primaryStage.setScene(mainScene);

    primaryStage.show();
  }

  private void startSimulation() {
    if (!simulationRunning) {
      simulationRunning = true;
      simulationPane = new StackPane();
      simulationScene = new Scene(simulationPane, 1000, 800);
      ImageView backgroundImage = createImageView("simulation_background.png");
      backgroundImage.fitWidthProperty().bind(primaryStage.widthProperty());

      eventOutputs = new TextFlow();
      eventOutputs.setPrefWidth(400);
      eventOutputs.setPrefHeight(600);
      eventOutputs.setPickOnBounds(false);
      ScrollPane scrollPane = new ScrollPane(eventOutputs);
      scrollPane.setMaxWidth(400);
      scrollPane.setMaxHeight(500);
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) {
          Text text = new Text(String.valueOf((char) b));
          Platform.runLater(() -> eventOutputs.getChildren().add(text));
        }
      }));
      StackPane eventOutContainer = new StackPane();
      eventOutContainer.getChildren().addAll(scrollPane);
      eventOutContainer.setAlignment(Pos.TOP_LEFT);

      final double heightMultiplier = 0.20;

      activeP1 = createImageView("PoliceTerminal.png");
      activeP1.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      activeP2 = createImageView("PoliceTerminal.png");
      activeP2.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      activeP3 = createImageView("PoliceTerminal.png");
      activeP3.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      activeC1 = createImageView("CustomsTerminal.png");
      activeC1.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      activeC2 = createImageView("CustomsTerminal.png");
      activeC2.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));

      inactiveP1 = createImageView("InactivePoliceTerminal.png");
      inactiveP1.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      inactiveP2 = createImageView("InactivePoliceTerminal.png");
      inactiveP2.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      inactiveP3 = createImageView("InactivePoliceTerminal.png");
      inactiveP3.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      inactiveC1 = createImageView("InactiveCustomsTerminal.png");
      inactiveC1.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));
      inactiveC2 = createImageView("InactiveCustomsTerminal.png");
      inactiveC2.fitHeightProperty().bind(primaryStage.heightProperty().multiply(heightMultiplier));

      P1 = activeP1;
      P2 = activeP2;
      P3 = activeP3;
      C1 = activeC1;
      C2 = activeC2;

      ColumnConstraints col1 = new ColumnConstraints();
      col1.setPercentWidth(15);
      ColumnConstraints col2 = new ColumnConstraints();
      col2.setPercentWidth(15);
      ColumnConstraints col3 = new ColumnConstraints();
      col3.setPercentWidth(15);

      RowConstraints row1 = new RowConstraints();
      row1.setPercentHeight(19);
      RowConstraints row2 = new RowConstraints();
      row2.setPercentHeight(20);

      GridPane terminals = new GridPane();
      terminals.setHgap(5);
      terminals.setVgap(80);

      terminals.prefWidthProperty().bind(primaryStage.widthProperty());

      terminals.add(P1, 0, 1);
      terminals.add(P2, 1, 1);
      terminals.add(P3, 2, 1);
      terminals.add(C1, 0, 0);
      terminals.add(C2, 2, 0);

      terminals.setAlignment(Pos.TOP_CENTER);

      terminals.getColumnConstraints().addAll(col1, col2, col3);
      terminals.getRowConstraints().addAll(row1, row2);
      VBox terminalContainer = new VBox();
      terminalContainer.prefWidthProperty().bind(primaryStage.widthProperty());
      terminalContainer.setAlignment(Pos.TOP_CENTER);
      terminalContainer.getChildren().add(terminals);

      StackPane.setAlignment(terminals, Pos.CENTER);
      simulationPane.getChildren().add(backgroundImage);
      simulationPane.getChildren().add(terminalContainer);

      ImageView pauseButton = createImageView("pause_button.png");
      ImageView resumeButton = createImageView("resume_button.png");
      ImageView showIncidentsButton = createImageView("show_incidents_button.png");
      ImageView viewVehiclesButton = createImageView("view_vehicles_button.png");
      ImageView showQueueButton = createImageView("show_queue_button.png");
      ImageView blankButton = createImageView("blank_button.png");
      blankButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      VBox buttonContainer = new VBox();
      buttonContainer.setSpacing(30);

      ImageView pauseScreen = createImageView("pause_screen.png");
      pauseScreen.fitWidthProperty().bind(primaryStage.widthProperty());
      VBox pauseScreenContainer = new VBox();
      pauseScreenContainer.setMouseTransparent(true);

      pauseButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          Simulation.isPaused = true;
          pauseScreenContainer.getChildren().add(pauseScreen);
          int index = buttonContainer.getChildren().indexOf(pauseButton);
          if (index != -1) {
            buttonContainer.getChildren().remove(index);
            buttonContainer.getChildren().add(index, resumeButton);
          }
          index = buttonContainer.getChildren().indexOf(blankButton);
          if (index != -1) {
            buttonContainer.getChildren().remove(index);
            buttonContainer.getChildren().add(index, viewVehiclesButton);
          }
        }
      });
      pauseButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      resumeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          pauseScreenContainer.getChildren().clear();
          synchronized (pauseMonitor) {
            Simulation.isPaused = false;
            pauseMonitor.notifyAll();
          }
          int index = buttonContainer.getChildren().indexOf(resumeButton);
          if (index != -1) {
            buttonContainer.getChildren().remove(index);
            buttonContainer.getChildren().add(index, pauseButton);
          }
          index = buttonContainer.getChildren().indexOf(viewVehiclesButton);
          if (index != -1) {
            buttonContainer.getChildren().remove(index);
            buttonContainer.getChildren().add(index, blankButton);
          }
        }
      });
      resumeButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      showIncidentsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          if (incidentScene == null) {
            createIncidentInterface();
          }
          switchToIncidentInterface();
        }
      });
      showIncidentsButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      viewVehiclesButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          pauseScreenContainer.getChildren().clear();
          int index = buttonContainer.getChildren().indexOf(viewVehiclesButton);
          if (index != -1) {
            buttonContainer.getChildren().remove(index);
            buttonContainer.getChildren().add(index, blankButton);
          }
        }
      });
      viewVehiclesButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      showQueueButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          if (queueScene == null) {
            createQueueScene();
          }
          showQueueScene();
        }
      });
      showQueueButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

      buttonContainer.setPadding(new Insets(20));

      buttonContainer.getChildren().addAll(pauseButton, showIncidentsButton, showQueueButton, blankButton);
      StackPane buttonStackPane = new StackPane(buttonContainer);
      buttonStackPane.setPickOnBounds(false);

      VBox vehicleContainer = new VBox();
      vehicleContainer.setAlignment(Pos.BOTTOM_CENTER);
      vehicleContainer.setPickOnBounds(false);
      vehicleContainer.setMaxHeight(500);
      vehicleContainer.setPadding(new Insets(500, 0, 0, 0));
      GridPane terminalVehicles = new GridPane();
      simulationPane.getChildren().add(terminalVehicles);
      eventOutContainer.setPadding(new Insets(10));
      eventOutContainer.setPickOnBounds(false);
      simulationPane.getChildren().add(eventOutContainer);
      simulationPane.getChildren().add(vehicleContainer);

      terminalVehicles.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.2));
      terminalVehicles.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.2));
      terminalVehicles.setVgap(45);
      terminalVehicles.setAlignment(Pos.TOP_CENTER);
      buttonStackPane.setMaxHeight(50);
      buttonStackPane.setMaxWidth(50);
      buttonStackPane.setTranslateX(770);
      buttonStackPane.setTranslateY(-300);

      simulationPane.getChildren().add(pauseScreenContainer);
      simulationPane.getChildren().add(buttonStackPane);
      VBox timerTextContainer = new VBox(timerText);
      timerTextContainer.setAlignment(Pos.BOTTOM_RIGHT);
      timerTextContainer.setMouseTransparent(true);
      timerText.setStyle("-fx-font-size: 32px;");
      timerText.setFill(Color.WHITE);
      simulationPane.getChildren().add(timerTextContainer);

      ColumnConstraints colVehicles1 = new ColumnConstraints();
      colVehicles1.setPercentWidth(15.2);
      ColumnConstraints colVehicles2 = new ColumnConstraints();
      colVehicles2.setPercentWidth(15.2);
      ColumnConstraints colVehicles3 = new ColumnConstraints();
      colVehicles3.setPercentWidth(4.7);

      RowConstraints rowVehicles1 = new RowConstraints();
      rowVehicles1.setPercentHeight(33);
      RowConstraints rowVehicles2 = new RowConstraints();
      rowVehicles2.setPercentHeight(14);

      terminalVehicles.getColumnConstraints().addAll(colVehicles1, colVehicles2, colVehicles3);
      terminalVehicles.getRowConstraints().addAll(rowVehicles1, rowVehicles2);

      terminals.prefWidthProperty().bind(primaryStage.widthProperty());
      primaryStage.setScene(simulationScene);

      animationTimer = new AnimationTimer() {
        private long lastUpdate = 0;

        @Override
        public void handle(long now) {
          if (Simulation.vehiclesCreatedFlag) {
            if (now - lastUpdate >= 400000000) {
              renderVehicles(vehicleContainer, terminalVehicles);
              renderTerminals(terminals);
              if (popupActive) {
                double coordY = popupVehicle.getLayoutY();
                popupPane.setTranslateX(popupVehicle.getLayoutX() - 700);
                popupPane.setTranslateY(coordY < 500 ? coordY - 200 : coordY - 700);
              }
              lastUpdate = now;
              if (Simulation.processedVehicles >= 50) {
                stop();
                timeline.stop();
                ImageView finishScreen = createImageView("dark_screen.png");
                finishScreen.fitWidthProperty().bind(primaryStage.widthProperty());
                pauseScreenContainer.getChildren().clear();
                pauseScreenContainer.getChildren().add(finishScreen);
                buttonContainer.getChildren().clear();
                ImageView showIncidentsButton = createImageView("show_incidents_button.png");
                showIncidentsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                    if (incidentScene == null) {
                      createIncidentInterface();
                    }
                    switchToIncidentInterface();
                  }
                });
                showIncidentsButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.1));
                ImageView mainMenuButton = createImageView("main_menu_button.png");
                mainMenuButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                    primaryStage.setScene(mainScene);
                    timerText = new Text("Time: 0:00");
                    incidentCol = 0;
                    incidentRow = 0;
                  }
                });
                mainMenuButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.1));
                buttonContainer.getChildren().addAll(showIncidentsButton, mainMenuButton);
                buttonStackPane.setTranslateX(0);
                buttonStackPane.setTranslateY(250);
                Text duration = new Text(
                    String.format("%d:%02d", elapsedTimeInSeconds / 60, elapsedTimeInSeconds % 60));
                duration.setStyle("-fx-font-size: 120px");
                duration.setFill(Color.WHITE);
                duration.setMouseTransparent(true);
                duration.setTranslateY(-100);
                simulationPane.getChildren().add(duration);
                Simulation.vehiclesCreatedFlag = false;
                simulationRunning = false;
                Simulation.processedVehicles = 0;
              }
            }
          }
        }
      };

      Thread simulationThread = new Thread(() -> {
        Simulation.runSimulation();
        createTimer();
        elapsedTimeInSeconds = 0;
        timeline.play();
      });

      simulationThread.setDaemon(true);
      simulationThread.start();
      animationTimer.start();
    }
  }

  protected static ImageView createImageView(String imageName) {
    String relativeImagePath = "data" + File.separator + "icons" + File.separator + imageName;
    String fullImagePath = System.getProperty("user.dir") + File.separator + relativeImagePath;
    Image image = new Image(new File(fullImagePath).toURI().toString());
    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);

    return imageView;
  }

  private void renderTerminals(GridPane terminals) {
    terminals.getChildren().clear();
    terminals.add(P1, 0, 1);
    terminals.add(P2, 1, 1);
    terminals.add(P3, 2, 1);
    terminals.add(C1, 0, 0);
    terminals.add(C2, 2, 0);
  }

  private void renderQueueVehicles(VBox vehicleContainer, int startIndex) {
    vehicleContainer.getChildren().clear();
    int count = 0;
    for (Vehicle vehicle : Simulation.vehicles) {
      if (count < startIndex) {
        count++;
        continue;
      }
      if (count >= (startIndex + 9)) {
        break;
      }
      ImageView vehicleIcon = vehicle.getIcon();
      vehicleIcon.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.1));
      vehicleContainer.getChildren().add(vehicleIcon);
      count++;
    }
  }

  private void renderVehicles(VBox vehicleContainer, GridPane terminalVehicles) {
    vehicleContainer.getChildren().clear();
    terminalVehicles.getChildren().clear();
    int count = 0;
    for (Vehicle vehicle : Simulation.vehicles) {
      if (count >= 5) {
        break;
      }
      ImageView vehicleIcon = vehicle.getIcon();
      vehicleContainer.getChildren().add(vehicleIcon);
      count++;
    }
    if (Simulation.vehicles.size() < 5) {
      for (int i = 0; i < 5 - Simulation.vehicles.size(); i++) {
        ImageView blankImage = createImageView("blank_image.png");
        blankImage.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.1));
        vehicleContainer.getChildren().add(blankImage);
      }
    }
    if (policeTerminalVehicle1 != null) {
      terminalVehicles.add(policeTerminalVehicle1, 0, 1);
    }
    if (policeTerminalVehicle2 != null) {
      terminalVehicles.add(policeTerminalVehicle2, 1, 1);
    }
    if (policeTerminalVehicle3 != null) {
      terminalVehicles.add(policeTerminalVehicle3, 2, 1);
    }
    if (customsTerminalVehicle1 != null) {
      terminalVehicles.add(customsTerminalVehicle1, 0, 0);
    }
    if (customsTerminalVehicle2 != null) {
      terminalVehicles.add(customsTerminalVehicle2, 2, 0);
    }
  }

  private void createIncidentInterface() {
    incidentRootPane = new StackPane();
    ImageView background = createImageView("simulation_background.png");
    background.fitWidthProperty().bind(primaryStage.widthProperty());
    incidentVehiclesContainer = new GridPane();
    incidentScene = new Scene(incidentRootPane, 3840, 2160);
    addedIncidentVehicles = new ArrayList<>();
    updateIncidents();

    incidentRenderer = new AnimationTimer() {
      private long lastUpdate = 0;

      @Override
      public void handle(long now) {
        if (now - lastUpdate >= 2000000000) {
          updateIncidents();
        }
      }
    };

    ImageView backButton = createImageView("back_button.png");
    backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        animationTimer.start();
        primaryStage.setScene(simulationScene);
        incidentRenderer.stop();
      }
    });
    StackPane buttonStackPane = new StackPane(backButton);
    buttonStackPane.setTranslateX(770);
    buttonStackPane.setTranslateY(-450);
    buttonStackPane.setPickOnBounds(false);
    backButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));
    incidentVehiclesContainer.setAlignment(Pos.TOP_CENTER);

    incidentRootPane.getChildren().addAll(background, incidentVehiclesContainer, buttonStackPane);
  }

  private void switchToIncidentInterface() {
    primaryStage.setScene(incidentScene);
    incidentRenderer.start();
    animationTimer.stop();
  }

  public static void setP1Vehicle(ImageView vehicleIcon) {
    policeTerminalVehicle1 = vehicleIcon;
  }

  public static void setP2Vehicle(ImageView vehicleIcon) {
    policeTerminalVehicle2 = vehicleIcon;
  }

  public static void setP3Vehicle(ImageView vehicleIcon) {
    policeTerminalVehicle3 = vehicleIcon;
  }

  public static void setC1Vehicle(ImageView vehicleIcon) {
    customsTerminalVehicle1 = vehicleIcon;
  }

  public static void setC2Vehicle(ImageView vehicleIcon) {
    customsTerminalVehicle2 = vehicleIcon;
  }

  protected static void showPopUp(PassengerVehicle vehicle) {
    if (popupActive) {
      activePane.getChildren().remove(popupPane);
    }
    popupPane = new StackPane();
    popupPane.setPickOnBounds(false);
    popupPane.setMaxWidth(50);
    popupPane.setMaxHeight(20);
    popupVehicle = vehicle.getIcon();

    TextFlow popupContent = new TextFlow();
    Text vehicleID = new Text("Vehicle ID: " + vehicle.getID() + "\n");
    vehicleID.setStyle("-fx-font-size: 20px;");
    vehicleID.setFill(Color.WHITE);

    Text vehicleType = new Text(vehicle.getType() + "\n");
    vehicleType.setStyle("-fx-font-size: 20px;");
    vehicleType.setFill(Color.WHITE);

    Text passengers = new Text("Passengers: ");
    popupContent.getChildren().addAll(vehicleID, vehicleType, passengers);
    for (Passenger passenger : vehicle.getPassengers()) {
      Text passengerText = new Text(passenger.getFullName() + " ");
      if (!passenger.hasValidDocument()) {
        passengerText.setFill(Color.RED);
      } else {
        passengerText.setFill(Color.WHITE);
      }
      passengerText.setStyle("-fx-font-size: 16px;");
      popupContent.getChildren().add(passengerText);
    }

    passengers.setStyle("-fx-font-size: 16px;");
    passengers.setFill(Color.WHITE);

    Text driver = new Text("\nDriver: " + vehicle.getDriver().getFullName());
    driver.setStyle("-fx-font-size: 16px;");
    driver.setFill(Color.WHITE);

    VBox popupButton = new VBox();
    popupContent.getChildren().addAll(driver);
    Text truckMass;
    if (vehicle instanceof Truck) {
      Truck truck = (Truck) vehicle;
      truckMass = new Text("Real mass: " + truck.getRealMass() + "\nDeclared mass: " + truck.getDeclaredMass()
          + "\nHas documentation: " + (truck.hasDocumentation() ? "YES" : "NO"));
      truckMass.setStyle("-fx-font-size: 18px");
      truckMass.setFill(Color.WHITE);
      popupContent.getChildren().add(truckMass);
    }
    ImageView background = createImageView("popup_background.png");
    background.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.4));
    background.fitWidthProperty().bind(primaryStage.widthProperty().multiply(0.7));

    ImageView closeButton = createImageView("cls.png");
    closeButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.05));

    double coordY = vehicle.getIcon().getLayoutY();
    popupPane.setTranslateX(vehicle.getIcon().getLayoutX() - 700);
    popupPane.setTranslateY(coordY < 500 ? coordY - 200 : coordY - 700);

    popupPane.getChildren().add(background);
    popupButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        activePane.getChildren().remove(popupPane);
        popupActive = false;
      }
    });
    popupButton.getChildren().add(closeButton);
    popupButton.setAlignment(Pos.TOP_RIGHT);
    popupButton.setPickOnBounds(false);
    popupPane.getChildren().add(popupContent);
    popupPane.getChildren().add(popupButton);
    popupActive = true;
    activePane.getChildren().add(popupPane);
  }

  private void showIncidentPopup(String vehicleID, ImageView vehicle) {
    if (incidentPopupActive) {
      incidentRootPane.getChildren().remove(incidentPopupPane);
    }
    incidentPopupPane = new StackPane();
    incidentPopupPane.setPickOnBounds(false);
    incidentPopupPane.setMaxWidth(50);
    incidentPopupPane.setMaxHeight(20);

    TextFlow popupContent = new TextFlow();
    HashMap<String, ArrayList<Incident>> incidents = FileUtils.getAllIncidents();
    ArrayList<Incident> vehicleIncidents = incidents.get(vehicleID);
    Incident incident1 = vehicleIncidents.get(0);
    Incident incident2 = null;
    if (vehicleIncidents.size() > 1) {
      incident2 = vehicleIncidents.get(1);
    }

    Text iD = new Text("Vehicle ID: " + vehicleID + "\n");
    iD.setStyle("-fx-font-size: 26px;");
    iD.setFill(Color.WHITE);

    Text vehicleType = new Text(incident1.getVehicleType() + "\n");
    vehicleType.setStyle("-fx-font-size: 26px;");
    vehicleType.setFill(Color.WHITE);

    Text incidentHeader1 = new Text("Incident #1: \n");
    incidentHeader1.setStyle("-fx-font-size: 20px");
    incidentHeader1.setFill(Color.RED);

    Text description = new Text("Descirption: " + incident1.getDesc() + "\n");
    description.setStyle("-fx-font-size: 20px;");
    description.setFill(Color.WHITE);

    popupContent.getChildren().addAll(iD, vehicleType, incidentHeader1, description);
    if (incident1.getProblematicPassengers() != null) {
      Text passengers = new Text("Problematic passengers: ");
      popupContent.getChildren().add(passengers);
      for (Passenger passenger : incident1.getProblematicPassengers()) {
        Text passengerText = new Text(passenger.getFullName() + " ");
        passengerText.setFill(Color.WHITE);
        passengerText.setStyle("-fx-font-size: 16px;");
        popupContent.getChildren().add(passengerText);
      }
      passengers.setStyle("-fx-font-size: 16px;");
      passengers.setFill(Color.WHITE);
    }

    if (incident1.getIncidentType().equalsIgnoreCase("police")) {
      Text passed = new Text(
          "\nVehicle " + (vehicleIncidents.get(0).passed() ? "passed" : "didn't pass") + " police check.\n");
      if (vehicleIncidents.get(0).passed()) {
        passed.setFill(Color.WHITE);
      } else {
        passed.setFill(Color.RED);
      }
      passed.setStyle("-fx-font-size: 16px;");
      popupContent.getChildren().add(passed);
    } else {
      Text passed = new Text(
          "\nVehicle " + (vehicleIncidents.get(0).passed() ? "passed" : "didn't pass") + " customs check.");
      if (vehicleIncidents.get(0).passed()) {
        passed.setFill(Color.WHITE);
      } else {
        passed.setFill(Color.RED);
      }
      passed.setStyle("-fx-font-size: 16px;");
      popupContent.getChildren().add(passed);
    }

    if (incident2 != null) {
      Text incidentHeader2 = new Text("\nIncident #2: \n");
      incidentHeader2.setStyle("-fx-font-size: 20px");
      incidentHeader2.setFill(Color.RED);

      Text description2 = new Text("Descirption: " + incident2.getDesc() + "\n");
      description2.setStyle("-fx-font-size: 20px;");
      description2.setFill(Color.WHITE);

      if (incident2.getProblematicPassengers() != null) {
        popupContent.getChildren().addAll(incidentHeader2, description2);
        Text passengers = new Text("Problematic passengers: ");
        popupContent.getChildren().add(passengers);
        for (Passenger passenger : incident2.getProblematicPassengers()) {
          Text passengerText = new Text(passenger.getFullName() + " ");
          passengerText.setFill(Color.WHITE);
          passengerText.setStyle("-fx-font-size: 16px;");
          popupContent.getChildren().add(passengerText);
        }
        passengers.setStyle("-fx-font-size: 16px;");
        passengers.setFill(Color.WHITE);
      }

      if (incident2.getIncidentType().equalsIgnoreCase("police")) {
        Text passed = new Text(
            "\nVehicle " + (vehicleIncidents.get(0).passed() ? "passed" : "didn't pass") + " police check.\n");
        if (vehicleIncidents.get(0).passed()) {
          passed.setFill(Color.WHITE);
        } else {
          passed.setFill(Color.RED);
        }
        passed.setStyle("-fx-font-size: 16px;");
        popupContent.getChildren().add(passed);
      } else {
        Text passed = new Text(
            "\nVehicle " + (vehicleIncidents.get(0).passed() ? "passed" : "didn't pass") + " customs check.");
        if (vehicleIncidents.get(0).passed()) {
          passed.setFill(Color.WHITE);
        } else {
          passed.setFill(Color.RED);
        }
        passed.setStyle("-fx-font-size: 16px;");
        popupContent.getChildren().add(passed);
      }
    }

    VBox popupButton = new VBox();
    ImageView background = createImageView("popup_background.png");
    background.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.4));
    background.fitWidthProperty().bind(primaryStage.widthProperty().multiply(0.7));

    ImageView closeButton = createImageView("cls.png");
    closeButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.05));

    double coordY = vehicle.getLayoutY();
    incidentPopupPane.setTranslateX(vehicle.getLayoutX() - 700);
    incidentPopupPane.setTranslateY(coordY < 500 ? coordY - 200 : coordY - 700);

    incidentPopupPane.getChildren().add(background);
    popupButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        incidentRootPane.getChildren().remove(incidentPopupPane);
        incidentPopupActive = false;
      }
    });
    popupButton.getChildren().add(closeButton);
    popupButton.setAlignment(Pos.TOP_RIGHT);
    popupButton.setPickOnBounds(false);
    incidentPopupPane.getChildren().add(popupContent);
    incidentPopupPane.getChildren().add(popupButton);
    incidentPopupActive = true;
    incidentRootPane.getChildren().add(incidentPopupPane);
  }

  private void createTimer() {
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        if (!Simulation.isPaused) {
          elapsedTimeInSeconds++;
          int minutes = elapsedTimeInSeconds / 60;
          int seconds = elapsedTimeInSeconds % 60;
          String formattedTime = String.format("Time: %d:%02d", minutes, seconds);
          timerText.setText(formattedTime);
        }
      }
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  private void updateIncidents() {
    HashMap<String, ArrayList<Incident>> incidents = FileUtils.getAllIncidents();
    for (HashMap.Entry<String, ArrayList<Incident>> entry : incidents.entrySet()) {
      ArrayList<Incident> vehicleIncidents = entry.getValue();
      if (!addedIncidentVehicles.contains(vehicleIncidents.get(0).getID())) {
        addedIncidentVehicles.add(vehicleIncidents.get(0).getID());
        ImageView icon = createImageView(vehicleIncidents.get(0).getIcon());
        icon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
        icon.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            showIncidentPopup(vehicleIncidents.get(0).getID(), icon);
          }
        });

        incidentVehiclesContainer.add(icon, incidentCol, incidentRow);
        incidentCol++;
        if (incidentCol >= 5) {
          incidentCol = 0;
          incidentRow++;
          if (incidentRow >= 10) {
            incidentRow = 0;
          }
        }
      }
    }
  }

  private void createQueueScene() {
    queueStackPane = new StackPane();
    queueScene = new Scene(queueStackPane, 1000, 800);
    ImageView background = createImageView("simulation_background.png");
    background.fitWidthProperty().bind(primaryStage.widthProperty());
    queueStackPane.getChildren().add(background);
    VBox container1 = new VBox();
    container1.setSpacing(15);
    VBox container2 = new VBox();
    container2.setSpacing(15);
    VBox container3 = new VBox();
    container3.setSpacing(15);
    VBox container4 = new VBox();
    container4.setSpacing(15);
    VBox container5 = new VBox();
    container5.setSpacing(15);
    HBox horizontalContainer = new HBox();
    horizontalContainer.getChildren().addAll(container1, container2, container3, container4, container5);
    queueStackPane.getChildren().add(horizontalContainer);
    VBox finishScreenContainer = new VBox();
    queueStackPane.getChildren().add(finishScreenContainer);
    finishScreenContainer.setMouseTransparent(true);
    horizontalContainer.setTranslateX(500);
    horizontalContainer.setSpacing(30);
    queueTimer = new AnimationTimer() {
      private long lastUpdate = 0;

      @Override
      public void handle(long now) {
        if (Simulation.vehiclesCreatedFlag) {
          if (now - lastUpdate >= 300000000) {
            renderQueueVehicles(container1, 5);
            renderQueueVehicles(container2, 14);
            renderQueueVehicles(container3, 23);
            renderQueueVehicles(container4, 32);
            renderQueueVehicles(container5, 41);

            if (popupActive) {
              popupPane.setTranslateX(-720);
              popupPane.setTranslateY(-280);
            }
            lastUpdate = now;
            if (Simulation.processedVehicles >= 50) {
              stop();
              ImageView finishScreen = createImageView("dark_screen.png");
              finishScreen.fitWidthProperty().bind(primaryStage.widthProperty());
              finishScreen.setMouseTransparent(true);
              finishScreenContainer.getChildren().add(finishScreen);
            }
          }
        }
      }
    };

    VBox buttonContainer = new VBox();
    ImageView showIncidentsButton = createImageView("show_incidents_button.png");
    showIncidentsButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (incidentScene == null) {
          createIncidentInterface();
        }
        switchToIncidentInterface();
      }
    });

    showIncidentsButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));
    ImageView backButton = createImageView("back_button.png");
    backButton.fitHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));
    backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        animationTimer.start();
        primaryStage.setScene(simulationScene);
        queueTimer.stop();
        activePane = simulationPane;
        if (popupActive) {
          activePane.getChildren().remove(popupPane);
        }
      }
    });
    buttonContainer.getChildren().addAll(showIncidentsButton, backButton);
    buttonContainer.setSpacing(30);
    buttonContainer.setPickOnBounds(false);
    buttonContainer.setAlignment(Pos.TOP_RIGHT);
    StackPane buttonStackPane = new StackPane();
    buttonStackPane.getChildren().addAll(buttonContainer);
    buttonStackPane.setPickOnBounds(false);
    buttonStackPane.setTranslateX(770);
    buttonStackPane.setTranslateY(-350);
    queueStackPane.getChildren().add(buttonContainer);
  }

  private void showQueueScene() {
    activePane = queueStackPane;
    animationTimer.stop();
    queueTimer.start();
    primaryStage.setScene(queueScene);
  }
}
