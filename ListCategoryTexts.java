import java.util.Properties;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.List;

class ListCategoryTexts {

	private static final String properiesFile = "ass1.properties";
	static Properties props;
	static String dirsep;
	// initialise the above two in static block
	// so all static methods could use them
	static {
		props = new Properties(System.getProperties());
		dirsep = System.getProperty("file.separator");
        System.out.println(dirsep+"1");
		try {
			props.load(new FileReader(new File(properiesFile)));
		} catch (IOException e) {
			System.out.println("Can't load application properties");
			System.out.println(e);
			System.exit(1);
		}
	}
	/**
	 * reading the category of texts passed on command-line
	 * and listing all textx in this category according to
	 * the properties file
	 */
	public static void main(String[] args) throws IOException {
		String category;
		String value;
		if (args.length > 0) {
			category = args[0];
			value = (String)props.getProperty(category);
			if (value != null) {
				value = value.replace(".", dirsep);
				Path dir = Paths.get(value);
				try	(DirectoryStream<Path> dirs
				= Files.newDirectoryStream(dir, "*.txt")) {
					for (Path entry: dirs) {
						System.out.printf("%s is in %s, it's %d bytes%n",
							entry.getFileName(), value,
							// to demo how a file object can be created
							// if there would need to read from it
							entry.toFile().length());
							//listWithTraversal(entry); // choose for Java < 8
							// listWithStreams(entry);   // choose for Java 8
					}
				}
			} else {
				System.out.printf("category \"%s\" has no texts%n",
					category);
				listCategories();
			}
		} else {
			listCategories();
		}
	}
	/**
	 * lists all available categories of texts according to
	 * the properties file
	 */
	private static void listCategories() {
		System.out.println("these are available  categories:");
		String value;
		for (Object key: props.keySet()) {
			value = (String)props.getProperty((String)key);
			value = value.substring(value.indexOf(".")+1);
			System.out.println(value);
		}
	}

	/** pre Java SE 8 version of the method which prints every line in a
	 * file given by pathToFile using NIO2 API and standard traversal
	 */
	private static void listWithTraversal(Path pathToFile) throws IOException {
		List<String> lines = Files.readAllLines(pathToFile,
				StandardCharsets.ISO_8859_1);
		for (String line: lines)
			System.out.println(line);
	} 


	/** Java SE 8 version of the preceding method:
	 * prints every line a file given by pathToFile using streams API
	 */
	//private static void listWithStreams(Path pathToFile) throws IOException {
		//Files.readAllLines(pathToFile, StandardCharsets.ISO_8859_1).stream()
			//.forEach(System.out::println);
	//}
}
