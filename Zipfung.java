import java.util.Properties;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;


public class Zipfung {
    private static final String PROPERTIESFILE = "ass1.properties";
    private static final String HELP = "Help in using this program:\n-h\n   displays this message\n-a [number] [-o]\n   runs and reports the first number\n   ranked words obtained by merging tables from all\n   texts in all categories.  The default number value is 50.\n   Include the option -o to use string interning when building the table.\n-c category [number] [-o]\n   runs and reports the first\n   number ranked words obtained by merging the tables from all\n   texts in category. The default number value is 50;\n   include the option -o to use string interning when building the table.\n-l\n   lists all available catagories for which at least one text is available\n   for processing";
    private static String dirsep;
    static Properties props = new Properties();
    // initialise the above two in static block
    // so all static methods could use them
    static {
        props = new Properties(System.getProperties());
        dirsep = System.getProperty("file.separator");
        try {
            FileInputStream in = new FileInputStream(PROPERTIESFILE);
            props.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can't load properties");
            System.out.println(e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Can't load properties");
            System.out.println(e);
            System.exit(1);
        }
    }
    // lists all available categories of texts according to the
    // properties file
    private static void list() {
        System.out.println("these are available categories:");
        for (Object key: props.keySet()) {
            System.out.println((String)key);
        }
    }
    // analyses all categories
    private static WordFrequencyTable runOnAllCategories() {
        WordFrequencyTable w = new WordFrequencyTable();

        for (Object key: props.keySet()) {
            w.putAll(runOnACategory((String)key));
        }

        return w;
    }
    // analyses one single category
    private static WordFrequencyTable runOnACategory(String category) {
        String path = props.getProperty(category);
        try{
            path = path.replace(".",dirsep);
        }catch(NullPointerException e){
            System.out.println("No such a category, please use -l get the list of categories.");
            System.exit(1);
        }
        WordFrequencyTable w = new WordFrequencyTable();
        Path dir = Paths.get(path);
        try	(DirectoryStream<Path> dirs
                = Files.newDirectoryStream(dir, "*.txt")) {
                        for (Path entry: dirs) {
                            System.out.printf("Start processing %s in %s...",entry.getFileName(),path);
                            String fullPath = path + dirsep + entry.getFileName().toString();
                            w.putAll(new WordFrequencyTable(fullPath));
                            System.out.printf("done!\n");
                }
        } catch (IOException e) {
            System.out.println("Can't open the category");
             System.out.println(e);
             System.exit(1);
        }

        return w;
    }

    private static void errorCommands() {
          System.out.println("Unknown option, please use the -h option to check the manual.");
          System.exit(1);
    }

    public static void main(String[] args) {
        String category;
        int number = 50;
        if (args.length > 0) {
            if (args[0].charAt(0)=='-') {
                switch (args[0].charAt(1)) {
                    case 'h': System.out.println(HELP); break;
                    case 'c': category = args[1];
                              if (args.length > 2 && args.length < 5) {
                                  if (args[2].charAt(0) != '-') {
                                      try{
                                          number = Integer.parseInt(args[2]);
                                      }catch(NumberFormatException e){
                                          errorCommands();
                                      }
                                  }
                                  if (args.length == 3 && args[2].charAt(0) == '-') {
                                      WordFrequencyTable.optimal = true;
                                      System.out.println("Optimization mode is turned on.");
                                  } else if (args.length == 4 && args[3].charAt(0) != '-'){
                                      errorCommands();
                                  }
                              } else {
                                  errorCommands();
                              }
                              System.out.printf("The following files from the %s category are being analysed:\n", category);
                              runOnACategory(category).getRank(number);
                              break;
                    case 'a': if (args.length > 1 && args.length < 4) {
                                  if (args[1].charAt(0) != '-') {
                                      try{
                                          number = Integer.parseInt(args[1]);
                                      }catch(NumberFormatException e){
                                          errorCommands();
                                      }
                                  }
                                  if (args.length == 2 && args[1].charAt(0) == '-') {
                                      WordFrequencyTable.optimal = true;
                                      System.out.println("Optimization mode is turned on.");
                                  } else if (args.length == 3 && args[2].charAt(0) != '-') {
                                      errorCommands();
                                  }
                              } else {
                                  errorCommands();
                              }
                              System.out.println("The following files from all categories are being analysed:");
                              runOnAllCategories().getRank(number);
                              break;
                    case 'l': list();
                              break;
                    default: errorCommands();
                              break;
                }
            } else {
                if (args[0].contains(".txt")){
                    WordFrequencyTable w = new WordFrequencyTable(args[0]);
                    w.getRank(number);
                } else {
                    errorCommands();
                }
            }
        } else {
            System.out.println("Please use the -h option to check the manual.");
        }
    }
}
