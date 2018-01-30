package application;


import donnees.Orientation;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Translate;

import java.awt.Point;

/**
 * Similaire à la classe Véhicule, celle-ci est plutôt utilisée pour afficher les véhicules
 * à l'écran et accepter les évènements déclenchés par l'utilisateur.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class VehiculeApp extends ImageView {
    /**
     * Si les véhicules de la grille accepteront les évènement de l'utilisateur
     */
    private static boolean inputAccepte = true;
    /**
     * la longueur en cases de la voiture
     */
    private int longueur;
    /**
     * la position de la voiture dans la grille
     */
    private Point ptPosition;
    /**
     * l'orientation de la voiture: H ou V
     */
    private Orientation orientation;
    /**
     * la grille parent de cette voiture
     */
    private Grille grille;
    /**
     * le décalage entre la souris et la position de la voiture dans la scène
     */
    private Translate offset = new Translate();
    /**
     * la couleur de la voiture
     */
    private String couleur;

    /**
     * Initialise les variables d'instances à partir des paramètre reçus et créé
     * les gestionnaires d'évènements appropriés. Appelle le constructeur de la
     * superclasse (ImageView) affichant ainsi la Voiture dans la Grille.
     * @param couleur String qui représente la couleur du véhicule
     * @param longueur int qui représente la longueur en cases du véhicule (2 pour une voiture, 3 pour un camion)
     * @param ptPosition Point qui représente la position sur la grille du véhicule
     * @param orientation Valeur de l'enum Orientation qui représente l'orientation Verticale ou Horizontale du véhicule
     * @param grille objet Grille parent de ce véhicule
     */
    public VehiculeApp(String couleur, int longueur, Point ptPosition, Orientation orientation, Grille grille) {
        super("img/" + ((longueur > 2) ? "camion" : "auto") + "_"
                + orientation + "_" + couleur + ".gif");
        this.longueur = longueur;
        this.ptPosition = ptPosition;
        this.orientation = orientation;
        this.grille = grille;
        this.couleur = couleur;

        EventHandler<MouseEvent> gestionDeplacement = e -> {
            if (inputAccepte) {
                if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    offset = new Translate(e.getSceneX() - getTranslateX(), e.getSceneY() - getTranslateY());
                }

                if (orientation == Orientation.V) {
                    if (e.getSceneY() - offset.getY() >= borneHaute() && e.getSceneY() - offset.getY() <= borneBasse())
                        setTranslateY(e.getSceneY() - offset.getY());
                    else if (e.getSceneY() - offset.getY() <= borneHaute()) {
                        setTranslateY(borneHaute());
                    } else {
                        setTranslateY(borneBasse());
                    }
                } else {
                    if (e.getSceneX() - offset.getX() >= borneGauche() && e.getSceneX() - offset.getX() <= borneDroite())
                        setTranslateX(e.getSceneX() - offset.getX());
                    else if (e.getSceneX() - offset.getX() >= borneDroite()) {
                        setTranslateX(borneDroite());
                    } else {
                        setTranslateX(borneGauche());
                    }
                }
            }
        };

        setOnMousePressed(gestionDeplacement);

        setOnMouseDragged(gestionDeplacement);

        setOnMouseReleased(e -> {
           if (inputAccepte) {
               majPosition(new Point(
                       (int) Math.round((getTranslateX() - Grille.ptCoinHautGauche.getX()) / Grille.ptDimsCase.getX()),
                       (int) Math.round((getTranslateY() - Grille.ptCoinHautGauche.getY()) / Grille.ptDimsCase.getY())));
           }
        });

        grille.getChildren().add(this);
        majTranslate();
    }

    /**
     * Mets à jour la position sur la grille de la Voiture puis appelle
     * la méthode de mise à jour de la grille et la méthode de mise à jour
     * du Translate sur soi-même.
     * @param point Nouvelle position
     */
    public void majPosition(Point point) {
        ptPosition = point;
        grille.majGrille(this);
        majTranslate();
    }

    /**
     * Mets à jours la position sur l'écran de la voiture à partir des
     * coordonnées courantes de celle-ci sur la grille.
     */
    private void majTranslate() {
        setTranslateX(Grille.ptCoinHautGauche.getX() + ptPosition.x * Grille.ptDimsCase.getX());
        setTranslateY(Grille.ptCoinHautGauche.getY() + ptPosition.y * Grille.ptDimsCase.getY());
    }

    /**
     * Parcourt la grille vers le haut pour trouver la borne supérieure qui limite
     * le mouvement de la voiture. Cette borne est soit une voiture ou la limite de
     * la grille.
     * @return borne vers le haut en pixels (coordonnées Scene)
     */
    private double borneHaute() {
        int borne = 0;

        for (int i = ptPosition.y - 1; i >= 0 && borne == 0; i--) {
            if (grille.getConfiguration().positionEstOccupee(ptPosition.x, i)) {
                borne = i + 1;
            }
        }
        return Grille.convertiYEnYScene(borne);
    }

    /**
     * Parcourt la grille vers le bas pour trouver la borne inférieure qui limite
     * le mouvement de la voiture. Cette borne est soit une voiture ou la limite de
     * la grille.
     * @return  borne vers le bas en pixels (coordonnées Scene)
     */
    private double borneBasse() {
        int borne = 6 - longueur;

        for (int i = ptPosition.y + longueur; i < 6 && borne == 6 - longueur; i++) {
            if (grille.getConfiguration().positionEstOccupee(ptPosition.x, i)) {
                borne = i - longueur;
            }
        }
        return Grille.convertiYEnYScene(borne);
    }

    /**
     * Parcourt la grille vers la droite pour trouver la borne droite qui limite
     * le mouvement de la voiture. Cette borne est soit une voiture ou la limite de
     * la grille.
     * @return  borne vers la droite en pixels (coordonnées Scene)
     */
    private double borneDroite() {
        int borne = 6 - longueur;

        for (int i = ptPosition.x + longueur; i < 6 && borne == 6 - longueur; i++) {
            if (grille.getConfiguration().positionEstOccupee(i, ptPosition.y)) {
                borne = i - longueur;
            }
        }
        return Grille.convertiXEnXScene(borne);
    }

    /**
     * Parcourt la grille vers la gauche pour trouver la borne gauche qui limite
     * le mouvement de la voiture. Cette borne est soit une voiture ou la limite de
     * la grille.
     * @return  borne vers la gauche en pixels (coordonnées Scene)
     */
    private double borneGauche() {
        int borne = 0;

        for (int i = ptPosition.x - 1; i >= 0 && borne == 0; i--) {
            if (grille.getConfiguration().positionEstOccupee(i, ptPosition.y)) {
                borne = i + 1;
            }
        }
        return Grille.convertiXEnXScene(borne);
    }

    /**
     * Retourne en string le type de véhicule (déterminé par la longueur), la couleur et la position.
     * @return String qui représente cette objet
     */
    @Override
    public String toString() {
        return ((longueur == 2) ? "voiture " : "camion ") + couleur + "à (" + ptPosition.x + "; " + ptPosition.y + ")";
    }

    /**
     * Retourne la longueur, en cases, du véhicule.
     * @return longueur en cases du véhicule
     */
    public int getLongueur() {
        return longueur;
    }

    /**
     * Retourne la position, en coordonnées de grille, du véhicule.
     * (pour accèder à la position en pixel sur la scène, utiliser
     * les méthode getScene[X,Y,Z] et getScreen[X,Y,Z]
     * @return position x,y du véhicule sur la grille
     */
    public Point getPtPosition() {
        return ptPosition;
    }

    /**
     * Retourne l'orientation du véhicule: V (verticale) ou H (horizontale)
     * @return Valeur de l'enum Orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Retourne la couleur de ce véhicule
     * @return String qui représente la couleur
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Mets inputAccepte à faux. Ce booléen est vérifié dans les gestionnaire
     * d'évènement de cet objet bloquant ainsi l'exécution de ces évènements.
     */
    public static void bloquerInput(){
        inputAccepte = false;
    }

    /**
     * Mets inputAccepte à vrai.Ce booléen est vérifié dans les gestionnaire
     * d'évènement de cet objet débloquant ainsi l'exécution de ces évènements.
     */
    public static void debloquerInput(){
        inputAccepte = true;
    }

    /**
     * Retourne un booléen qui représente si les véhicules accept les commandes
     * de l'utilisateur (true) ou non (false).
     * @return true si l'input est accepté, faux si non.
     */
    public static boolean isInputAccepte() {
        return inputAccepte;
    }
}
