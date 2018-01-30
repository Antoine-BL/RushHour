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
 * Repr�sentation graphique de la grille du jeu. La grille est initialis�e � partir d'un fichier qui
 * contient une liste de voitures, leurs couleurs, positions initiales et orientations. Elle contient
 * le Pane parent de tous les objets VehiculeApp. La repr�sentation graphique s'appuie sur une Configuration
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
     * le coin en haut � gauche, en pixels, de la case en haut � gauche de la grille
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
     * la liste des v�hicules
     */
    private ArrayList<VehiculeApp> lstVehicules = new ArrayList<VehiculeApp>();
    /**
     * la Fen�tre principale qui sert de parent � cette grille
     */
    private FenPrincipale parent;
    /**
     * si la grille a �t� r�solue par l'utilisateur ou par l'ordinateur
     */
    private boolean blnResous = false;

    /**
     * Cr�� une configuration. Cette configuration, �tant un StackPane, contient deux objets Pane.
     * Le premier est un ImageView qui est initialis� � partir de strImageGrille.
     * Le deuxi�me est un Pane qui contient tous les Vehicules (extends ImageView)
     * qui ont �t� lus dans l'URL strNomFichier re�u en param�tre.
     * @param strNomFichier URL du fichier configuration
     * @param parent Objet FenPrincipale qui a cr�� cette configuration
     * @throws IOException lanc�e par Files.lines()
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
     * � partir d'une coordonn� x en coordonn�es de configuration, retourne le x
     * �quivalent en pixel en coordonn�es de Scene.
     * @param x la coordonn� x en coordonn�es de configuration
     * @return la coordonn� x en coordonn�es de de Scene
     */
    public static double convertiXEnXScene(double x) {
        return x * ptDimsCase.getX() + ptCoinHautGauche.getX();
    }

    /**
     * � partir d'une coordonn� y en coordonn�es de configuration, retourne le y
     * �quivalent en pixel en coordonn�es de Scene.
     * @param y la coordonn� y en coordonn�es de configuration
     * @return la coordonn� y en coordonn�es de de Scene
     */
    public static double convertiYEnYScene(double y) {
        return y * ptDimsCase.getY() + ptCoinHautGauche.getY();
    }

    /**
     * Mets � jour la configuration de la configuration. V�rifie si la configuration est une
     * solution � la configuration. Si oui, appelle grilleResolue();
     * @param vehiculeDeplace l'objet Vehicule qui a �t� d�plac�
     */
    public void majGrille(VehiculeApp vehiculeDeplace){
        configuration = new Configuration(new Vehicule(vehiculeDeplace.getLongueur(), lstVehicules.indexOf(vehiculeDeplace) + 1,
                vehiculeDeplace.getPtPosition(), vehiculeDeplace.getOrientation(), vehiculeDeplace.getCouleur()), configuration);
        if (configuration.evaluer()){
            grilleResolue();
        }
    }

    /**
     * Remplace la configration courante avec une nouvelle re�ue en param�tre.
     * Si cette nouvelle configuration est une solution, appelle grilleResolue().
     * @param grille un objet Configuration qui remplacera l'objet Configuration courant.
     */
    public void majGrille(Configuration grille) {
        this.configuration = grille;
        if (this.configuration.evaluer())
            grilleResolue();
    }


    /**
     * Si la configuration n'a pas d�j� �t� r�solue, donne le choix � l'utilisateur de passer
     * tout de suite � la prochaine configuration ou de continuer � jouer. Si l'utilisateur
     * continue de jouer, il ne peut pas faire r�appara�tre le dialog de victoire et peut
     * d�sormais � la prochaine configuration avec un bouton (affich� avec afficherBoutonProchain.
     * Sinon, la prochaine configuration est affich�e imm�diatement en appleant afficherProchaineGrille()
     * de l'objet FenPrincipale parent.
     */
    private void grilleResolue() {
        if (!blnResous) {
            Platform.runLater(() -> {
                Alert dlgContinuer = new Alert(Alert.AlertType.CONFIRMATION, "Vous avez r�sous la configuration!\n Passer � la prochaine?", ButtonType.NO, ButtonType.YES);

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
     * un objet AfficheurSolution pour afficher les �tapes de cette solution.
     * Retourne le temps d'ex�cution de la solution (long) en nanosecondes.
     * @return long qui repr�sente le temps en nanosecondes requis pour calculer la solution
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
