package com.Little_Snow_.Redpack;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.*;

public final class Main extends JavaPlugin{
	private File logf = new File(this.getDataFolder()+File.separator+"log.yml");
	private YamlConfiguration log = new YamlConfiguration();
	private Economy eco = null;
    public void onEnable(){
       boolean a = setupEconomy();
	   if(!new File(getDataFolder()+File.separator+"config.yml").exists()){
		   this.getDataFolder().mkdir();
		   this.getConfig().addDefault("version","1.0");
		   this.getConfig().addDefault("File", "config.yml");
	   }
	   if(!logf.exists()){
		   try{
			   log.set("File","log.yml");
			   log.save(logf);
		   }catch(Exception e){
			   this.getLogger().log(Level.SEVERE,"log.yml文件读取失败");
			   this.getPluginLoader().disablePlugin(this);
		   }
	   }
	   if(!a){
		   this.getLogger().log(Level.SEVERE,"Vault插件载入失败，可能缺少支持Vault的经济插件");
	   }
	   this.saveDefaultConfig();
	   this.getCommand("Redpack").setExecutor(new Redpack(this));
	   this.getCommand("hb").setExecutor(new Hb(this));
	   this.getServer().getPluginManager().registerEvents(new Hb(this),this);
	   this.getLogger().info("红包插件已加载");
    }
    public void onDisable(){
	   this.getLogger().info("红包插件已卸载");
    }
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        }

        return (eco != null);
    }
    public Economy getEconomy(){
    	return this.eco;
    }
    public YamlConfiguration getLog(){
    	return this.log;
    }
    public File getFile(){
    	return this.logf;
    }
}

