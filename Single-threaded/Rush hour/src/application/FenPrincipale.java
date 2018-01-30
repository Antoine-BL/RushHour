package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

/** Fen�tre principale de l'application et point d'entr�e.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class FenPrincipale extends Application {
    private Grille grille;
    private BorderPane root;
    private Button btnProchain;
    private Button btnResoudre;
    private int intNoGrille = 0;
    private double vitesseSelec;

    /**
     * Affiche la fen�tre d'application : un objet Scene qui contient un borderPane
     * @param primaryStage l'objet Stage primaire de l'application
     * @throws Exception lance tout Exception non g�r�e
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        root = new BorderPane();

        root.setTop(creerTitreEtMenu());
        root.setRight(creerPanneauAction());
        root.setLeft(creerPanneauGrille(FichiersGrilles.values()[intNoGrille].getStrURL()));

        primaryStage.setTitle("Rush Hour par Antoine Brassard Lahey");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * �bauche de m�thode pour cr�er une barre de titre et/ou une barre de Menu.
     * Retourne (pour l'instant) un objet Pane vide.
     * @return Pane vide
     */
    private Node creerTitreEtMenu() {
        return new Pane();
    }

    /**
     *
     * @param strNomFichier l'URL du fichier Grille
     * @return un Objet Grille (extends StackPane) cr�� � partir du fichier sp�cifi� par strNomFichier
     * @throws IOException peut �tre lanc� par Files.lines()
     */
    private Node creerPanneauGrille(String strNomFichier) throws IOException{
        grille = new Grille(strNomFichier, this);
        return grille;
    }

    /**
     * Creer le panneau de contr�les qui repr�sentent les actions possibles
     * @return un VBox qui contient les contr�les d'action
     */
    private Node creerPanneauAction() {
        VBox vbParent = new VBox();
        vbParent.setMinWidth(200);
        vbParent.setSpacing(50);
        vbParent.setAlignment(Pos.CENTER);
        vbParent.setPadding(new Insets(20));

        btnResoudre = new Button("Afficher solution");
        btnResoudre.setMaxWidth(Double.MAX_VALUE);
        btnResoudre.setPrefHeight(20);

        Label lblTempsExec = new Label("Temps d'ex�cution");
        lblTempsExec.setTextAlignment(TextAlignment.CENTER);
        BorderStrokeStyle bss = new BorderStrokeStyle(StrokeType.CENTERED, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 10, 0, null);
        BorderStroke bs = new BorderStroke(Color.BLACK, bss, CornerRadii.EMPTY, BorderWidths.DEFAULT);
        lblTempsExec.setBorder(new Border(bs));
        lblTempsExec.setMaxWidth(Double.MAX_VALUE);
        lblTempsExec.setPrefHeight(20);

        Slider slideVitesse = new Slider(0, 900, 450);
        vitesseSelec = slideVitesse.getValue();
        slideVitesse.setOnMouseDragged(e -> {
            vitesseSelec = 1000 - slideVitesse.getValue();
        });

        btnProchain = new Button("Prochaine grille");
        btnProchain.setVisible(false);
        btnProchain.setOnAction(e -> prochaineGrille());

        btnResoudre.setOnAction(e -> {
            if (VehiculeApp.isInputAccepte())
                lblTempsExec.setText("Temps d'ex�cution: " + Long.toString(grille.afficherSolution()) + "ms");

            btnResoudre.setDisable(true);
        });

        vbParent.getChildren().addAll(btnResoudre, lblTempsExec, slideVitesse, btnProchain);

        return vbParent;
    }

    /**
     * appelle root.setLeft(creerPanneauGrille() en utilisant le prochain URL de fichuer dans
     * l'�num�ration FichiersGrilles. S'il ne reste plus d'URLs, ferme l'application en affichant
     * une alerte. Active btnResoudre et rend invisible btnProchain.
     */
    public void prochaineGrille() {
        intNoGrille++;
        try {
            if (intNoGrille < FichiersGrilles.values().length) {
                root.setLeft(creerPanneauGrille(FichiersGrilles.values()[intNoGrille].getStrURL()));
            } else {
                Alert dlgFini = new Alert(Alert.AlertType.CONFIRMATION, "F�licitation! Vous avez compl�t� toute les griles", ButtonType.OK);

                dlgFini.showAndWait();

                Platform.exit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnResoudre.setDisable(false);
        btnProchain.setVisible(false);
    }

    /**
     * appelle launch sur l'application.
     * @param args ces arguments ne sont pas utilis�s.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * appelle setVisible sur btnProchain et mets la valeur � true
     */
    public void afficherBoutonProchain() {
        btnProchain.setVisible(true);
    }

    /**
     *  retourne vitesseSelec, la valeur du Slider de vitesse
     * @return vitesseSelec
     */
    public double getVitesseSelec() {
        return vitesseSelec;
    }
}
