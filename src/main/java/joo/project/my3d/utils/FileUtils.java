package joo.project.my3d.utils;

public class FileUtils {

    private static final int NOT_FOUND = -1;
    private static final String EXTENSION_SEPARATOR = ".";

    protected FileUtils() {}

    /**
     * 확장자 index 반환
     */
    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        return filename.lastIndexOf(EXTENSION_SEPARATOR);
    }

    /**
     * 파일명과 확장자명을 분리하여 리스트로 반환
     */
    public static String[] splitFilename(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return new String[] {filename, ""};
        } else {
            return new String[] {filename.substring(0, index), filename.substring(index + 1)};
        }
    }

    /**
     * 확장자만 추출
     */
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
}
