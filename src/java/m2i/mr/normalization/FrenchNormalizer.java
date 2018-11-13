package m2i.mr.normalization;

public class FrenchNormalizer implements Normalizer {

    /**
     * ArabicNormalizer constructor
     *
     */
    public FrenchNormalizer() {
    }

    /**
     * normalize Method
     *
     * @param target
     * @return
     */
    @Override
    public String normalize(String target) {
        return normalizeString(target);
    }

    public static String normalizeString(String target) {
        return target != null && !target.isEmpty() ? target.toLowerCase()
                .replaceAll("[^abcdefghijklmnopqrstuvwxyzàâæçéèêëîïôœùûüÿ ]", " ")
                .replaceAll(" +", " ").trim() : null;
    }

    public static String cleanString(String target) {
        return target != null && !target.isEmpty() ? target.toLowerCase()
                .replaceAll("[^abcdefghijklmnopqrstuvwxyzàâæçéèêëîïôœùûüÿ ]", " ")
                .replaceAll(" +", " ").trim() : null;
    }

    public static String normalizeSemicolon(String target) {
        return target != null && !target.isEmpty() ? target.toLowerCase()
                .replaceAll("[^abcdefghijklmnopqrstuvwxyzàâæçéèêëîïôœùûüÿ; ]", "")
                .replaceAll(" *; *", ";").replaceAll(" +", " ").trim() : null;
    }
}
