package application;

import donnees.Configuration;
import donnees.Solutionneur;
import donnees.Vehicule;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Représentation graphique de la grille du jeu. La grille est initialisée à partir d'un fichier qui
 * contient une liste de voitures, leurs couleurs, positions initiales et orientations. Elle contient
 * le Pane parent de tous les objets VehiculeApp. La représentation graphique s'appuie sur une Configuration
 * pour faire les collisions et sur des objets VehiculeApp ainsi qu'un ImageView pour visualiser les vehicules
 * et la grille respectivement.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Grille extends StackPane {
    /**
     * Les dimensions, en pixel, d'une case
     */
    public static final Translate ptDimsCase = new Translate(72,72);
    /**
     * le coin en haut à gauche, en pixels, de la case en haut à gauche de la grille
     */
    public static final Translate ptCoinHautGauche = new Translate(44, 63);
    /**
     * URL de l'image de la grille
     */
    private static final String strImageGrille = "img/grille.gif";
    /**
     * la configuration courante de la grille
     */
    private Configuration configuration;
    /**
     * le conteneur des voitures
     */
    private Pane pnVoitures = new Pane();
    /**
     * la liste des véhicules
     */
    private ArrayList<VehiculeApp> lstVehicules = new ArrayList<VehiculeApp>();
    /**
     * la Fenêtre principale qui sert de parent à cette grille
     */
    private FenPrincipale parent;
    /**
     * si la grille a été résolue par l'utilisateur ou par l'ordinateur
     */
    private boolean blnResous = false;

    /**
     * Créé une configuration. Cette configuration, étant un StackPane, contient deux objets Pane.
     * Le premier est un ImageView qui est initialisé à partir de strImageGrille.
     * Le deuxième est un Pane qui contient tous les Vehicules (extends ImageView)
     * qui ont été lus dans l'URL strNomFichier reçu en paramètre.
     * @param strNomFichier URL du fichier configuration
     * @param parent Objet FenPrincipale qui a créé cette configuration
     * @throws IOException lancée par Files.lines()
     */
    public Grille(String strNomFichier, FenPrincipale parent) throws IOException {
        this.parent = parent;
        Files.lines(FileSystems.getDefault().getPath(strNomFichier)).forEach(ligne -> {
            String[] tabChamps = ligne.trim().split(",");
            lstVehicules.add(new VehiculeApp(tabChamps[0].trim(), Integer.parseInt(tabChamps[1].trim()),
                    new Point(Integer.parseInt(tabChamps[2].trim()), Integer.parseInt(tabChamps[3].trim())),
                            (donnees.Orientation.valueOf(tabChamps[4].trim())),this));
        });

        pnVoitures.getChildren().addAll(lstVehicules);

        ImageView imvGrille = new ImageView(strImageGrille);

        this.getChildren().add(imvGrille);
        this.getChildren().add(pnVoitures);

        Vehicule[] tabVehicules = new Vehicule[lstVehicules.size()];

        for (int i = 0; i < lstVehicules.size(); i++) {
            VehiculeApp v = lstVehicules.get(i);
            tabVehicules[i] = new Vehicule(v.getLongueur(), i + 1, v.getPtPosition(), v.getOrientation(), v.getCouleur());
        }

        configuration = new Configuration(tabVehicules);
    }

    /**
     * À partir d'une coordonné x en coordonnées de configuration, retourne le x
     * équivalent en pixel en coordonnées de Scene.
     * @param x la coordonné x en coordonnées de configuration
     * @return la coordonné x en coordonnées de de Scene
     */
    public static double convertiXEnXScene(double x) {
        return x * ptDimsCase.getX() + ptCoinHautGauche.getX();
    }

    /**
     * À partir d'une coordonné y en coordonnées de configuration, retourne le y
     * équivalent en pixel en coordonnées de Scene.
     * @param y la coordonné y en coordonnées de configuration
     * @return la coordonné y en coordonnées de de Scene
     */
    public static double convertiYEnYScene(double y) {
        return y * ptDimsCase.getY() + ptCoinHautGauche.getY();
    }

    /**
     * Mets à jour la configuration de la configuration. Vérifie si la configuration est une
     * solution à la configuration. Si oui, appelle grilleResolue();
     * @param vehiculeDeplace l'objet Vehicule qui a été déplacé
     */
    public void majGrille(VehiculeApp vehiculeDeplace){
        configuration = new Configuration(new Vehicule(vehiculeDeplace.getLongueur(), lstVehicules.indexOf(vehiculeDeplace) + 1,
                vehiculeDeplace.getPtPosition(), vehiculeDeplace.getOrientation(), vehiculeDeplace.getCouleur()), configuration);
        if (configuration.evaluer()){
            grilleResolue();
        }
    }

    /**
     * Remplace la configration courante avec une nouvelle reçue en paramètre.
     * Si cette nouvelle configuration est une solution, appelle grilleResolue().
     * @param grille un objet Configuration qui remplacera l'objet Configuration courant.
     */
    public void majGrille(Configuration grille) {
        this.configuration = grille;
        if (this.configuration.evaluer())
            grilleResolue();
    }


    /**
     * Si la configuration n'a pas déjà été résolue, donne le choix à l'utilisateur de passer
     * tout de suite à la prochaine configuration ou de continuer à jouer. Si l'utilisateur
     * continue de jouer, il ne peut pas faire réapparaître le dialog de victoire et peut
     * désormais à la prochaine configuration avec un bouton (affiché avec afficherBoutonProchain.
     * Sinon, la prochaine configuration est affichée immédiatement en appleant afficherProchaineGrille()
     * de l'objet FenPrincipale parent.
     */
    private void grilleResolue() {
        if (!blnResous) {
            Platform.runLater(() -> {
                Alert dlgContinuer = new Alert(Alert.AlertType.CONFIRMATION, "Vous avez résous la configuration!\n Passer à la prochaine?", ButtonType.NO, ButtonType.YES);

                dlgContinuer.showAndWait();
                if (dlgContinuer.getResult().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    parent.prochaineGrille();
                } else {
                    parent.afficherBoutonProchain();
                }
            });
        }
        blnResous = true;
    }

    /**
     * Retourne la configuration de cet objet.
     * @return l'objet Configuration courant de cette Grille
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Utilise un objet Solutionneur pour calculer la solution puis utilise
     * un objet AfficheurSolution pour afficher les étapes de cette solution.
     * Retourne le temps d'exécution de la solution (long) en nanosecondes.
     * @return long qui représente le temps en nanosecondes requis pour calculer la solution
     */
    public long afficherSolution() {
        Solutionneur solutionneur = new Solutionneur(configuration);
        new AfficheurSolution(solutionneur.getStackSolution(), lstVehicules, this);
        return solutionneur.getTempsExec();
    }

    /**
     * Retourne l'objet FenPrincipale parent.
     * @return objet FenPrincipale parent.
     */
    public FenPrincipale getMain() {
        return parent;
    }
}
