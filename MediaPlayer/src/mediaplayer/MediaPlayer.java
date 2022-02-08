/**
 * Clase java usada para ejecutar la app
 *
 * Crea la ventana, cargando todos los componentes
 * a partir de la vista fxml que hemos creado y
 * preparado en el scene builder
 *
 * @author amartinez
 */
package mediaplayer;

// Listado de imports
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MediaPlayer extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Creamos el parent root cargando los nodos desde el fxml
        Parent root = FXMLLoader.load(getClass().getResource("MediaPlayer.fxml"));

        /**
         * Creamos el objeto que nos dará el contenido de la ventana a partir de
         * los nodos pasados desde root
         */
        Scene scene = new Scene(root);

        // A la escena le añadimos los valores descriptivos que queremos
        stage.setTitle("Media Player hecho por Adrian");
        stage.setFullScreenExitHint("Presione ESC or Doble Click para salir de la pantalla completa");

        // Añadimos a la ventana nuestro icono personalizado
        stage.getIcons().add(new Image("images/icon.png"));

        /**
         * Añadimos a la escena la capacidad de cambiar entre modo ventana y
         * modo pantalla completa al hacer doble click sobre el video
         */
        scene.setOnMouseClicked((MouseEvent clicked) -> {
            if (clicked.getClickCount() == 2) {
                if (!stage.isFullScreen()) {
                    stage.setFullScreen(true);
                } else {
                    stage.setFullScreen(false);
                }
            }
        });

        /**
         * Añadimos el contenido guardado en la escena a la ventana y la hacemos
         * visible
         */
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }

}
