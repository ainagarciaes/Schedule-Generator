/**
 * @author Aina Garcia
 */

package model;

public class Laboratori extends InfoSessions {

    /**
     * Crea una nova instancia de informació de sessions de laboratori i li assigna la informació necessària
     *
     * @param numSessions     número de sessions de laboratori
     * @param duracioSessions duració de les sessions de laboratori
     */
    public Laboratori(int numSessions, int duracioSessions, Aula.TipusAula tAula) {
        super(numSessions, duracioSessions, tAula);
    }
}
