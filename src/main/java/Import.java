import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Archer on 03-Apr-16.
 */
public class Import {
    public static void essentials(Main main) {
        String path = main.getDataFolder().getPath().replace(main.getName(),"")+"Essentials"+File.separator+"userdata"+File.separator;
        main.print(Messages.tag+"FOUND ESSENTIALS PATH: "+path);
        File homeDir = new File(path);

        File[] files = homeDir.listFiles();
        int counter = 1;
        int invalid = 0;
        for (File file : files) {
            boolean lockedOnHome = false;

            String name = "";
            String id = FilenameUtils.getBaseName(file.getName());
            String world = "";
            double x = 0;
            double y = 0;
            double z = 0;
            float yaw = 0;
            float pitch = 0;


            if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("yml")) {
                try {
                    Scanner sc = new Scanner(file);

                    int lineCounter = 0;
                    boolean badHome = false;

                    while (sc.hasNext()) {

                        String line = sc.nextLine();
                        lineCounter++;

                        if (lineCounter==1 && !line.contains("lastAccountName:")) {
                            System.out.println("=======================================");
                            System.out.println("----------HOME: " + counter + "IS INVALID---------");
                            System.out.println("=======================================");
                            invalid++;
                            badHome = true;
                            break;
                        }

                        if (line.contains("lastAccountName:")) {
                            line = line.replace("lastAccountName:", "");
                            line = StringUtils.deleteWhitespace(line);
                            name = line;
                        }

                        if (line.contains("home:")) {
                            lockedOnHome = true;
                        }

                        if (line.contains("world:")) {
                            line = line.replace("world:", "");
                            line = StringUtils.deleteWhitespace(line);
                            world = line;
                        }
                        if (line.contains("x:")) {
                            line = line.replace("x:", "");
                            line = StringUtils.deleteWhitespace(line);
                            x = Double.parseDouble(line);
                        }
                        if (line.contains("y:")) {
                            line = line.replace("y:", "");
                            line = StringUtils.deleteWhitespace(line);
                            y = Double.parseDouble(line);
                        }
                        if (line.contains("z:")) {
                            line = line.replace("z:", "");
                            line = StringUtils.deleteWhitespace(line);
                            z = Double.parseDouble(line);
                        }
                        if (line.contains("yaw:")) {
                            line = line.replace("yaw:", "");
                            line = StringUtils.deleteWhitespace(line);
                            yaw = Float.parseFloat(line);
                        }
                        if (line.contains("pitch:")) {
                            line = line.replace("pitch:", "");
                            line = StringUtils.deleteWhitespace(line);
                            pitch = Float.parseFloat(line);
                            break;
                        }

                    }
                    if(!badHome) {
                        NameID nameId = new NameID(name, id);
                        HomeInfo homeInfo = new HomeInfo(nameId, world, x, y, z, yaw, pitch);
                        main.getData().add(homeInfo);

                        System.out.println("============HOME: " + counter + "===================");
                        System.out.println("ID: " + id);
                        System.out.println("NAME: " + name);
                        System.out.println(" WORLD: " + world);
                        System.out.println(" X: " + x);
                        System.out.println(" Y: " + y);
                        System.out.println(" Z: " + z);
                        System.out.println(" YAW: " + yaw);
                        System.out.println(" PITCH: " + pitch);

                        counter++;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (counter == 1)
            main.print(Messages.HOME_IMPORT_NONE.parse());
        else
            main.print(Messages.HOME_IMPORT_COUNT.parse(counter));
        main.print(Messages.HOME_IMPORT_INVALID.parse(invalid));
    }
}
