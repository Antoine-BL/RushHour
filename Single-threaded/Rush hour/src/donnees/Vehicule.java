package donnees;

import java.awt.*;

/**
 * Version plus légère de VehiculeApp utilisée dans le calcul de solutions
 * et d'itérations de Configuration.
 * @author Antoine Brassard Lahey
 * @version 1.0
 */
public class Vehicule implements Cloneable{
    private int noVehicule;
    private int longueur;
    private Point ptPosition;
    private Orientation orientation;
    private String couleur;

    /**
     * Initialise les variables d'instances du Vehicule.
     * @param longueur int qui représente la longueur en cases du véhicule (2 pour une voiture, 3 pour un camion)
     * @param noVehicule Index + 1 de ce véhicule dans tabVéhicules
     * @param ptPosition Point qui représente la position sur la grille du véhicule
     * @param orientation Valeur de l'enum Orientation qui représente l'orientation Verticale ou Horizontale du véhicule
     * @param couleur String qui représente la couleur du véhicule
     */
    public Vehicule(int longueur, int noVehicule, Point ptPosition, Orientation orientation, String couleur) {
        this.longueur = longueur;
        this.noVehicule = noVehicule;
        this.ptPosition = ptPosition;
        this.orientation = orientation;
        this.couleur = couleur;
    }

    /**
     * Créé un nouvel objet Véhicule (new Vehicule()) avec les mêmes paramètres au constructeur.
     * @return Copie conforme de ce véhicule
     */
    @Override
    public Vehicule clone(){
        return new Vehicule(longueur, noVehicule, new Point(ptPosition.x, ptPosition.y), orientation, couleur);
    }

    /**
     * Retourne la longueur en cases du véhicule.
     * @return longueur en cases
     */
    public int getLongueur() {
        return longueur;
    }

    /**
     * Retourne le numéro de véhicule. Ce numéro correspond à l'index + 1 du véhicule
     * dans tabVoitures d'une configuration ce véhicule. Correspond aussi à l'index + 1
     * du VehiculeApp qui correspond à ce véhicule dans lstVehicules de l'objet Grille
     * qui contient la configuration à laquelle ce Véhicule est associé.
     * @return no du véhicule (index - 1)
     */
    public int getNoVehicule() {
        return noVehicule;
    }

    /**
     * Retourne les coordonnées sur la grille de ce véhicule.
     * IMPORTANT: ces coordonnées utilisent la case en haut à gauche de la grille
     * comme origine, le x croissant vers la droite et le y croissant vers le bas.
     * @return Point qui représente les coordonnées du véhicule sur la grille.
     */
    public Point getPtPosition() {
        return ptPosition;
    }

    /**
     * Mets à jour la position de cet objet à partir du Point reçu en paramètre en copiant dernier.
     * @param ptPosition nouvelle position
     */
    public void setPtPosition(Point ptPosition) {
        this.ptPosition = new Point(ptPosition);
    }

    /**
     * Retourne l'orientation du véhicule: V (verticale) ou H (horizontale)
     * @return valeur de l'enum Orientation
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
     * Retourne en string le type de véhicule (déterminé par la longueur), la couleur et la position.
     * @return String qui représente cet objet.
     */
    @Override
    public String toString() {
        return ((longueur == 2) ? "voiture " : "camion ") + couleur + " (no. "+ noVehicule +") à (" + ptPosition.x + "; " + ptPosition.y + ")";
    }
}
