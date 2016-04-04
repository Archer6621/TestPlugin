
/**
 * Created by Archer on 30-Mar-16.
 */
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class Main extends JavaPlugin {

    public List<HomeInfo> homeData;
    public FileConfiguration config = this.getConfig();
    private String dataPath = this.getDataFolder().getPath() + File.separator + "data.json";

    public ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public ResidenceApi resAPI;
    public boolean resEnabled;


    public List<HomeInfo> getData(){
        return homeData;
    }

    public void print(String text){
        console.sendMessage(text);
    }

    private void readData(){
        try {


            FileReader in = new FileReader(new File(dataPath));
            print(Messages.CONSOLE_DATA_EXISTS.parse());
            homeData = new ArrayList<HomeInfo>(Arrays.asList((new Gson()).fromJson(in, HomeInfo[].class)));
            in.close();



            print(Messages.CONSOLE_DONE_READING.parse());

        } catch (IOException e1) {
            print(Messages.CONSOLE_DATA_EXISTS_NOT.parse());
            File file = new File(dataPath);
            try {
                file.createNewFile();
            }
            catch(IOException e2){
                print(Messages.CONSOLE_ERROR_FILE.parse());
                //e2.printStackTrace();
            }
        }

        //Import essentials on first time, after that it has to be manually set to true for import
        if(config.getBoolean("import-from-essentials")) {
            Import.essentials(this);
            config.set("import-from-essentials", false);
        }
    }

    private void saveData(){
        File file = new File(dataPath);
        try {
            HomeInfo[] array = homeData.toArray(new HomeInfo[homeData.size()]);
            String json = (new Gson()).toJson(array);

            //This is just for pretty formatting of the JSON data
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(json);
            String bootyful = gson.toJson(je);

            Writer writer = new FileWriter(file);
            writer.write(bootyful);
            writer.close();
            print(Messages.CONSOLE_DATA_SAVED.parse());
        } catch (IOException e) {
            //e.printStackTrace();
            print(Messages.CONSOLE_DATA_SAVE_FAIL.parse());
        }
    }





    @Override
    public void onEnable() {
        config.addDefault("import-from-essentials", true);
        config.options().copyDefaults(true);
        saveConfig();

        print(Messages.CONSOLE_ENABLING.parse(this.getDescription().getVersion()));
        homeData = new ArrayList<HomeInfo>();
        //If data folder does not exist
        if (!this.getDataFolder().exists()) {
            //Make one
            new File(this.getDataFolder().getPath()).mkdirs();
        }

        PluginManager pm = getServer().getPluginManager();
        Plugin p = pm.getPlugin("Residence");
        if(p!=null)
        {
            if(!p.isEnabled())
            {
                print(Messages.RES_ENABLING.parse());
                pm.enablePlugin(p);
            }
            resAPI = Residence.getAPI();
            resEnabled = true;
            print(Messages.RES_ENABLED.parse());
        }
        else
        {
            print(Messages.RES_DISABLED.parse());
            resEnabled = false;
        }

        readData();
        this.getCommand("home").setExecutor(new CommandHome(this));
        this.getCommand("sethome").setExecutor(new CommandSetHome(this));
    }

    @Override
    public void onDisable(){
        print(Messages.CONSOLE_DISABLING.parse());
        saveConfig();
        saveData();
    }
}
