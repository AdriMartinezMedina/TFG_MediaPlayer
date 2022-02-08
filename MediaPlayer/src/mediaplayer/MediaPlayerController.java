/**
 * Clase java usada para las funcionalidades de la app
 *
 * Hace a nuestra app capaz de pausar, activar,
 * parar, acelerar, ralentizar, avanzar y retroceder
 *
 * Además, también habilitamos la barra de progreso
 * del video y la de volumen. Por último también se crean
 * los keyevents para la funcionalidades
 *
 * @author amartinez
 */
package mediaplayer;

// Listado de imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import static javafx.scene.media.MediaPlayer.Status.PLAYING;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JOptionPane;

public class MediaPlayerController implements Initializable {

    // Variable que almacena la ruta al video abierto
    @FXML
    private String path;

    /**
     * Variable que almacena todas las extensiones de archivo válidas para
     * nuestra aplicación en un array
     */
    @FXML
    private List<String> extensions;

    // Variable objeto, crea el mediaPlayer
    @FXML
    private MediaPlayer mediaPlayer;

    // Variable objeto, crea el mediaView
    @FXML
    private MediaView mediaView;

    // Variable objeto, crea el slider de volumen
    @FXML
    private Slider volumeSlider;

    // Variable objeto, crea el slider de progreso de video
    @FXML
    private Slider progressBar;

    @FXML
    private Button openFile, playVideo, stopVideo, slowVideo, 
            backVideo5, normalVideo, forwardVideo5, fastVideo, setVisibilityButtons;
    
    @FXML
    private MenuBar menuBar;

    @FXML
    private boolean visible = true;

//    FadeTransition fadeTransition = new FadeTransition(Duration.millis(200),openFile);
//    fadeTransition.setToValue(0);
//    fadeTransition.play();
    /**
     * Método que usamos para añadir al boton de abrir archivo la capacidad de
     * hacerlo.
     *
     * @param event
     */
    @FXML
    private void OpenFileMethod(ActionEvent event) {

        try {
            /**
             * llenamos el array de strings con las extensiones permitidas en
             * nuestra app
             */
            extensions = Arrays.asList("*.mp4", "*.3gp", "*.mkv", "*.MP4", "*.MKV", "*.3GP", "*.flv", "*.wmv");

            /**
             * Creamos tanto el objeto filechooser para elegir archivo como el
             * objeto extension filter para añadir el filtro de extensiones que
             * hemos concretado en el array anterior
             */
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("selecciona video", extensions);

            // Añadimos el filtro de extensiones al filechooser
            fileChooser.getExtensionFilters().add(filter);
            fileChooser.setTitle("Selecciona un video");

            /**
             * Abrimos la ventana para elegir archivo y este se guarda como
             * variable File
             *
             * Posteriormente guardamos en un string la ruta del archivo elegido
             * para ser usada en el objeto Media
             */
            File file = fileChooser.showOpenDialog(null);
            path = file.toURI().toString();

            /**
             * Siempre y cuando se haya elegido un archivo, se realizará el
             * código contenido en el if
             */
            if (path != null) {
                /**
                 * Creamos el objeto Media gracias al path, luego lo usamos para
                 * crear el MediaPlayer y finalmente creamos la vista del video
                 */
                Media media = new Media(path);
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);

                /**
                 * Las lineas que tenemos a continuación sirven para definir las
                 * dimensiones del video cargado ajustandolo al mediaView
                 */
                DoubleProperty widthProp = mediaView.fitWidthProperty();
                DoubleProperty heightProp = mediaView.fitHeightProperty();
                widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

                // Damos un valor inicial al slider del volumen del video
                volumeSlider.setValue(mediaPlayer.getVolume() * 100);

                /**
                 * Creamos el listener que pertimirá ajustar el volumen del
                 * video según donde coloquemos el punto del slider del volumen
                 */
                volumeSlider.valueProperty().addListener((Observable observable) -> {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                });

                // Damos un valor inicial a la barra de progreso del video
                mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) -> {
                    progressBar.setValue(newValue.toSeconds());
                });

                /**
                 * Creamos los listener que pertimirá ajustar el volumen del
                 * video según donde coloquemos el punto del slider del volumen
                 */
                progressBar.setOnMousePressed((MouseEvent event1) -> {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                });
                progressBar.setOnMouseDragged((MouseEvent event1) -> {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                });
                mediaPlayer.setOnReady(() -> {
                    javafx.util.Duration total = media.getDuration();
                    progressBar.setMax(total.toSeconds());
                });

                // Por último, iniciamos la reproduccion del video al abrirlo
                mediaPlayer.play();
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    /**
     * Método que usamos para añadir al boton de play & pause video la capacidad
     * de hacerlo.
     *
     * @param event
     */
    @FXML
    private void playVideo(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se comprueba
         * si el video está activo o pausado
         *
         * Si está activo, se pausa y si está pausado se reactiva
         */
        try {
            if (mediaPlayer.getStatus() == PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de stop video la capacidad de
     * hacerlo.
     *
     * @param event
     */
    @FXML
    private void stopVideo(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se detiene el
         * video reseteandolo
         */
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de avanzar video la capacidad de
     * hacerlo.
     *
     * @param event
     */
    @FXML
    private void skip5(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se adelanta el
         * video 5 segundos
         */
        try {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de acelerar video la capacidad de
     * hacerlo.
     *
     * @param event
     */
    @FXML
    private void furtherSpeedUpVideo(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se acelera el
         * video
         */
        try {
            mediaPlayer.setRate(2);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de atrasar video la capacidad de
     * hacerlo.
     *
     * @param event
     */
    @FXML
    private void back5(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se atrasa el
         * video 5 segundos
         */
        try {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-5)));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de ralentizar video la capacidad
     * de hacerlo.
     *
     * @param event
     */
    @FXML
    private void furtherSlowDownVideo(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se ralentiza
         * el video
         */
        try {
            mediaPlayer.setRate(0.5);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para añadir al boton de normalizar velocidad video la
     * capacidad de hacerlo.
     *
     * @param event
     */
    @FXML
    private void normalSpeedVideo(ActionEvent event) {

        /**
         * Siempre y cuando se haya elegido previamente un video, se resetea la
         * velocidad de reporducción del video
         */
        try {
            mediaPlayer.setRate(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Elige un video primero");
        }

    }

    /**
     * Método que usamos para abrir la ventana Acerca De al hacer click en el
     * menú de la app
     *
     * @param event
     */
    @FXML
    private void openAcercaDe(ActionEvent event) {
        try {
            // Creamos el parent root cargando los nodos desde el fxml
            Parent root = FXMLLoader.load(getClass().getResource("AcercaDe.fxml"));

            /**
             * Creamos el objeto que nos dará el contenido de la ventana a
             * partir de los nodos pasados desde root
             */
            Scene scene = new Scene(root);

            // Creamos la ventana acerca de
            Stage acercade = new Stage();

            // Añadimos un icono a la ventana
            acercade.getIcons().add(new Image("images/icon.png"));

            /**
             * Añadimos el contenido guardado en la escena a la ventana y la
             * hacemos visible
             */
            acercade.setScene(scene);
            acercade.show();
        } catch (IOException e) {
        }
    }

    @FXML
    private void hideShowControls(ActionEvent event) {

        try {
                FadeTransition fadeTransitionVisibility = new FadeTransition(Duration.millis(200), setVisibilityButtons);
                FadeTransition fadeTransitionOpenFile = new FadeTransition(Duration.millis(200), openFile);
                FadeTransition fadeTransitionPlayVideo = new FadeTransition(Duration.millis(200), playVideo);
                FadeTransition fadeTransitionStopVideo = new FadeTransition(Duration.millis(200), stopVideo);
                FadeTransition fadeTransitionSlowVideo = new FadeTransition(Duration.millis(200), slowVideo);
                FadeTransition fadeTransitionBackVideo5 = new FadeTransition(Duration.millis(200), backVideo5);
                FadeTransition fadeTransitionNormalVideo = new FadeTransition(Duration.millis(200), normalVideo);
                FadeTransition fadeTransitionForwardVideo5 = new FadeTransition(Duration.millis(200), forwardVideo5);
                FadeTransition fadeTransitionFastVideo = new FadeTransition(Duration.millis(200), fastVideo);
                FadeTransition fadeTransitionVolumeSlider = new FadeTransition(Duration.millis(200), volumeSlider);
                FadeTransition fadeTransitionProgressBar = new FadeTransition(Duration.millis(200), progressBar);
                FadeTransition fadeTransitionMenuBar = new FadeTransition(Duration.millis(200), menuBar);
                
                if (visible) {
                    fadeTransitionVisibility.setToValue(0.2);
                    fadeTransitionOpenFile.setToValue(0);
                    fadeTransitionPlayVideo.setToValue(0);
                    fadeTransitionStopVideo.setToValue(0);
                    fadeTransitionSlowVideo.setToValue(0);
                    fadeTransitionBackVideo5.setToValue(0);
                    fadeTransitionNormalVideo.setToValue(0);
                    fadeTransitionForwardVideo5.setToValue(0);
                    fadeTransitionFastVideo.setToValue(0);
                    fadeTransitionVolumeSlider.setToValue(0);
                    fadeTransitionProgressBar.setToValue(0);
                    fadeTransitionMenuBar.setToValue(0);
                    
                    fadeTransitionVisibility.play();
                    fadeTransitionOpenFile.play();
                    fadeTransitionPlayVideo.play();
                    fadeTransitionStopVideo.play();
                    fadeTransitionSlowVideo.play();
                    fadeTransitionBackVideo5.play();
                    fadeTransitionNormalVideo.play();
                    fadeTransitionForwardVideo5.play();
                    fadeTransitionFastVideo.play();
                    fadeTransitionVolumeSlider.play();
                    fadeTransitionProgressBar.play();
                    fadeTransitionMenuBar.play();
                    
                    fadeTransitionMenuBar.setOnFinished(e -> {
                        visible = false;
                    });
                }
                if (!visible) {
                    fadeTransitionVisibility.setToValue(1);
                    fadeTransitionOpenFile.setToValue(1);
                    fadeTransitionPlayVideo.setToValue(1);
                    fadeTransitionStopVideo.setToValue(1);
                    fadeTransitionSlowVideo.setToValue(1);
                    fadeTransitionBackVideo5.setToValue(1);
                    fadeTransitionNormalVideo.setToValue(1);
                    fadeTransitionForwardVideo5.setToValue(1);
                    fadeTransitionFastVideo.setToValue(1);
                    fadeTransitionVolumeSlider.setToValue(1);
                    fadeTransitionProgressBar.setToValue(1);
                    fadeTransitionMenuBar.setToValue(1);
                    
                    fadeTransitionVisibility.play();
                    fadeTransitionOpenFile.play();
                    fadeTransitionPlayVideo.play();
                    fadeTransitionStopVideo.play();
                    fadeTransitionSlowVideo.play();
                    fadeTransitionBackVideo5.play();
                    fadeTransitionNormalVideo.play();
                    fadeTransitionForwardVideo5.play();
                    fadeTransitionFastVideo.play();
                    fadeTransitionVolumeSlider.play();
                    fadeTransitionProgressBar.play();
                    fadeTransitionMenuBar.play();
                    
                    fadeTransitionMenuBar.setOnFinished(e -> {
                        visible = true;
                    });
                }
            } catch (Exception e) {
            }

    }

    /**
     * Método que contiene todos los keylisteners de la app, haciendo posible la
     * ejecución de las funciones descritas anteriormente segun la combinación
     * de teclas pulsada.
     *
     * @param ke
     */
    @FXML
    private void handleKeyPressed(KeyEvent ke) {

        if (ke.getCode().equals(KeyCode.P) && ke.isControlDown()) {
            try {
                if (mediaPlayer.getStatus() == PLAYING) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.play();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.S) && ke.isControlDown()) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.RIGHT) && ke.isControlDown()) {
            try {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.LEFT) && ke.isControlDown()) {
            try {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-5)));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.UP) && ke.isControlDown()) {
            try {
                mediaPlayer.setRate(2);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.DOWN) && ke.isControlDown()) {
            try {
                mediaPlayer.setRate(0.5);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.N) && ke.isControlDown()) {
            try {
                mediaPlayer.setRate(1);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Elige un video primero");
            }
        }

        if (ke.getCode().equals(KeyCode.A) && ke.isControlDown()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("AcercaDe.fxml"));

                Scene scene = new Scene(root);
                Stage acercade = new Stage();

                acercade.setTitle("Media Player hecho por Adrian");
                acercade.getIcons().add(new Image("images/icon.png"));

                acercade.setScene(scene);
                acercade.show();
            } catch (IOException e) {
            }
        }
        if (ke.getCode().equals(KeyCode.H) && ke.isControlDown()) {
            try {
                FadeTransition fadeTransitionVisibility = new FadeTransition(Duration.millis(200), setVisibilityButtons);
                FadeTransition fadeTransitionOpenFile = new FadeTransition(Duration.millis(200), openFile);
                FadeTransition fadeTransitionPlayVideo = new FadeTransition(Duration.millis(200), playVideo);
                FadeTransition fadeTransitionStopVideo = new FadeTransition(Duration.millis(200), stopVideo);
                FadeTransition fadeTransitionSlowVideo = new FadeTransition(Duration.millis(200), slowVideo);
                FadeTransition fadeTransitionBackVideo5 = new FadeTransition(Duration.millis(200), backVideo5);
                FadeTransition fadeTransitionNormalVideo = new FadeTransition(Duration.millis(200), normalVideo);
                FadeTransition fadeTransitionForwardVideo5 = new FadeTransition(Duration.millis(200), forwardVideo5);
                FadeTransition fadeTransitionFastVideo = new FadeTransition(Duration.millis(200), fastVideo);
                FadeTransition fadeTransitionVolumeSlider = new FadeTransition(Duration.millis(200), volumeSlider);
                FadeTransition fadeTransitionProgressBar = new FadeTransition(Duration.millis(200), progressBar);
                FadeTransition fadeTransitionMenuBar = new FadeTransition(Duration.millis(200), menuBar);
                
                if (visible) {
                    fadeTransitionVisibility.setToValue(0.2);
                    fadeTransitionOpenFile.setToValue(0);
                    fadeTransitionPlayVideo.setToValue(0);
                    fadeTransitionStopVideo.setToValue(0);
                    fadeTransitionSlowVideo.setToValue(0);
                    fadeTransitionBackVideo5.setToValue(0);
                    fadeTransitionNormalVideo.setToValue(0);
                    fadeTransitionForwardVideo5.setToValue(0);
                    fadeTransitionFastVideo.setToValue(0);
                    fadeTransitionVolumeSlider.setToValue(0);
                    fadeTransitionProgressBar.setToValue(0);
                    fadeTransitionMenuBar.setToValue(0);
                    
                    fadeTransitionVisibility.play();
                    fadeTransitionOpenFile.play();
                    fadeTransitionPlayVideo.play();
                    fadeTransitionStopVideo.play();
                    fadeTransitionSlowVideo.play();
                    fadeTransitionBackVideo5.play();
                    fadeTransitionNormalVideo.play();
                    fadeTransitionForwardVideo5.play();
                    fadeTransitionFastVideo.play();
                    fadeTransitionVolumeSlider.play();
                    fadeTransitionProgressBar.play();
                    fadeTransitionMenuBar.play();
                    
                    fadeTransitionMenuBar.setOnFinished(e -> {
                        visible = false;
                    });
                }
                if (!visible) {
                    fadeTransitionVisibility.setToValue(1);
                    fadeTransitionOpenFile.setToValue(1);
                    fadeTransitionPlayVideo.setToValue(1);
                    fadeTransitionStopVideo.setToValue(1);
                    fadeTransitionSlowVideo.setToValue(1);
                    fadeTransitionBackVideo5.setToValue(1);
                    fadeTransitionNormalVideo.setToValue(1);
                    fadeTransitionForwardVideo5.setToValue(1);
                    fadeTransitionFastVideo.setToValue(1);
                    fadeTransitionVolumeSlider.setToValue(1);
                    fadeTransitionProgressBar.setToValue(1);
                    fadeTransitionMenuBar.setToValue(1);
                    
                    fadeTransitionVisibility.play();
                    fadeTransitionOpenFile.play();
                    fadeTransitionPlayVideo.play();
                    fadeTransitionStopVideo.play();
                    fadeTransitionSlowVideo.play();
                    fadeTransitionBackVideo5.play();
                    fadeTransitionNormalVideo.play();
                    fadeTransitionForwardVideo5.play();
                    fadeTransitionFastVideo.play();
                    fadeTransitionVolumeSlider.play();
                    fadeTransitionProgressBar.play();
                    fadeTransitionMenuBar.play();
                    
                    fadeTransitionMenuBar.setOnFinished(e -> {
                        visible = true;
                    });
                }
            } catch (Exception e) {
            }
        }

    }

}
