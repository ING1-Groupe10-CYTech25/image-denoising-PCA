package core.image;

/**
 * Exception personnalisée pour la gestion des erreurs liées aux images.
 * Cette classe étend {@link Exception} pour gérer spécifiquement les erreurs
 * qui peuvent survenir lors de la manipulation des pixels d'une image, comme :
 * - Accès à des coordonnées hors limites
 * - Manipulation de pixels invalides
 * - Opérations impossibles sur l'image
 * 
 * @author p-cousin
 * @version 1.0
 * @see Exception
 */
public class ImageException extends Exception {
    
    /**
     * Redéfinition de la méthode printStackTrace pour fournir un message d'erreur
     * plus spécifique et plus compréhensible pour les erreurs liées aux images.
     * Cette méthode est appelée automatiquement lors de la gestion des exceptions.
     * 
     * @see Exception#printStackTrace()
     */
    @Override
    public void printStackTrace() {
        System.err.println("Erreur : Tentative d'accès à des coordonnées de pixels hors des limites de l'image");
        super.printStackTrace();
    }
}
