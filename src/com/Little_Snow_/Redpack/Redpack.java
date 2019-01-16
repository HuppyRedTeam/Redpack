package com.Little_Snow_.Redpack;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Redpack implements CommandExecutor{
	@SuppressWarnings("unused")
	private Main ser;
    public Redpack(Main main) {
        this.ser = main;
    }
	public boolean onCommand(CommandSender sender,Command cmd,String Label,String[] args) {
		if(cmd.getName().equalsIgnoreCase("redpack")){
			if(args.length==1){
			if(args[0].equalsIgnoreCase("help")){
				sender.sendMessage("§6------[§cRedpack§6]§b插件帮助§6------");
				sender.sendMessage("§6/redpack §b显示插件信息");
				sender.sendMessage("§6/hb §acreate <红包数额> <份数> §b发送一个普通红包");
				sender.sendMessage("§6/hb §acreate <红包数额> <份数> <口令> §b发送一个口令红包");
				sender.sendMessage("§6/hb §aview §b查看自己发布的所有红包(口令红包暂不支持查询)");
				sender.sendMessage("§6/redpack §acancel §b重置口令红包");
				sender.sendMessage("§6/hb §aget §b<玩家名> 领取玩家的一个红包");
				return true;
			}
			if(sender.hasPermission("redpack.cancell")){
			if(args[0].equalsIgnoreCase("cancel")){
				YamlConfiguration log = ser.getLog();
				File logf = ser.getFile();
				try{
					log.load(logf);
					log.set("keyredpack.receive", log.getInt("keyredpack.part"));
					log.save(logf);
					ser.getServer().broadcastMessage("§6[§c红包§6]§a上一个口令红包已清除！");
				    return true;
				}catch(Exception e){
				return true;
				}
			}
			}
			}
			if(args.length==0){
			sender.sendMessage("§6[§cRedpack§6]§bVersion:"+ser.getConfig().getString("version"));
			sender.sendMessage("§6[§cRedpack§6]§b作者:chenhao220");
			sender.sendMessage("§6[§cRedpack§6]§b若有更好建议或发现BUG请联系作者");
			sender.sendMessage("§6[§cRedpack§6]§b请输入/redpack help获得帮助");
			return true;
			}
			sender.sendMessage("§c[Redpack]参数有误");
			return true;
		}
		return true;
	}

}
