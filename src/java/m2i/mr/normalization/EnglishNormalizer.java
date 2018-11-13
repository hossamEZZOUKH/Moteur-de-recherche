package m2i.mr.normalization;

public class EnglishNormalizer implements Normalizer {

    /**
     * ArabicNormalizer constructor
     *
     */
    public EnglishNormalizer() {
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
        return target != null && !target.isEmpty() ? 
                target.toLowerCase().replaceAll("[^a-z' ]", " ")
                        .replaceAll(" +", " ").trim() : null;
    }

    public static String cleanString(String target) {
        return target != null && !target.isEmpty() ? 
                target.toLowerCase().replaceAll("[^a-z' ]", " ")
                        .replaceAll(" +", " ").trim() : null;
    }

    public static String normalizeSemicolon(String target) {
        return target != null && !target.isEmpty() ?
                target.toLowerCase().replaceAll("[^a-z'; ]", "")
                        .replaceAll(" *; *", ";").replaceAll(" +", " ").trim() : null;
    }
}
