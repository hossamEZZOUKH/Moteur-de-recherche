package m2i.mr.normalization;

public class ArabicNormalizer implements Normalizer {

    /**
     * ArabicNormalizer constructor
     *
     */
    public ArabicNormalizer() {
    }

    /**
     * normalize Method
     *
     * @param target
     * @return String
     */
    @Override
    public String normalize(String target) {
        return ArabicNormalizer.normalizeString(target);
    }

    public static String normalizeString(String target) {
        return target != null && !target.isEmpty() ?
                target.replaceAll("[^ء-ي ]", "").replaceAll(" +", " ").trim() : null;
    }

    public static String cleanString(String target) {
        return target != null && !target.isEmpty() ?
                target.replaceAll("[^ء-ي ]", "").replaceAll(" +", " ").trim() : null;
    }

    public static String normalizeSemicolon(String target) {
        return target != null && !target.isEmpty() ?
                target.replaceAll("[^ء-ي; ]", "").replaceAll(" *; *", ";")
                        .replaceAll(" +", " ").trim() : null;
    }
}
