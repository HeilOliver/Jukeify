package at.fhv.jukify.spotify_container.component.auth;

import java.util.HashSet;
import java.util.Random;

public class IdGenerator {

    public static char[] defaultAlphabet;

    private HashSet<String> idSet;
    private char[] currentAlphabetLocal;
    private int estimation;

    static {
        defaultAlphabet = new char[62];
        for(int i = 0; i < 10; i++){
            defaultAlphabet[i] = (char) ('0' + i);
        }
        for(int i = 10; i < 36; i++){
            defaultAlphabet[i] = (char) ('A' + (i - 10));
        }
        for(int i = 36; i < 62; i++){
            defaultAlphabet[i] = (char) ('a' + (i - 36));
        }
    }

    public IdGenerator(int estimation){
        this(defaultAlphabet, estimation);
    }

    public IdGenerator(char[] alphabet, int estimation){
        if(estimation < 0){
            estimation = 100;
        }
        currentAlphabetLocal = alphabet;
        idSet = new HashSet<>(estimation);
        this.estimation = estimation;
    }

    public void setAlphabet(char[] alphabet){
        currentAlphabetLocal = alphabet;
    }

    public String generateUId(int len)  {
        String id = generateId(len, currentAlphabetLocal);
        int retry = 0;
        float fillingRate = idSet.size() / estimation;
        int limit = Math.min((idSet.size() + 1) * (int)(currentAlphabetLocal.length * fillingRate), estimation);
        while(retry < limit){
            if(idSet.contains(id)) {
                id = generateId(len, currentAlphabetLocal);
                retry++;
            } else {
                return id;
            }
        }
        return "";
    }

    public void reset(){
        idSet.clear();
    }

    /**
     * Generates a random string, but doesn't check if it's unique compared to previous generated IDs
     * @param len
     * @param alphabet
     * @return
     */
    public static String generateId(int len, char[] alphabet){
        StringBuilder builder = new StringBuilder();
        Random rand = new Random(System.currentTimeMillis());
        int alphabetLength = alphabet.length;
        for(int i = 0; i < len; i++){
            builder.append(alphabet[rand.nextInt(alphabetLength)]);
        }
        return builder.toString();
    }

    public static String generateId(int len){
        return generateId(len, defaultAlphabet);
    }

}
