
public class DriveTest {
    public static void main(String[] args) {
        String url = "https://drive.google.com/file/d/1J6SCSd6qJOD_fUTQDqYpO5ISkUlgo4m-/view?usp=drive_link";
        String transformed = transformGoogleDriveUrl(url);
        System.out.println("Original: " + url);
        System.out.println("Transformed: " + transformed);

        String expected = "https://drive.google.com/uc?export=view&id=1J6SCSd6qJOD_fUTQDqYpO5ISkUlgo4m-";
        if (transformed.equals(expected)) {
            System.out.println("✅ TEST PASSED");
        } else {
            System.out.println("❌ TEST FAILED");
        }
    }

    private static String transformGoogleDriveUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        // Pattern for "drive.google.com/file/d/{id}/view"
        java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile("drive\\.google\\.com/file/d/([^/]+)/?");
        java.util.regex.Matcher matcher1 = pattern1.matcher(url);
        if (matcher1.find()) {
            return "https://drive.google.com/uc?export=view&id=" + matcher1.group(1);
        }

        // Pattern for "drive.google.com/open?id={id}"
        java.util.regex.Pattern pattern2 = java.util.regex.Pattern.compile("drive\\.google\\.com/open\\?id=([^&]+)");
        java.util.regex.Matcher matcher2 = pattern2.matcher(url);
        if (matcher2.find()) {
            return "https://drive.google.com/uc?export=view&id=" + matcher2.group(1);
        }

        return url;
    }
}
