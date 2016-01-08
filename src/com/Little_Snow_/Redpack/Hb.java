package com.Little_Snow_.Redpack;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Hb implements CommandExecutor, Listener{
	private Main ser;
	private Timer t = new Timer();
    public Hb(Main main) {
        this.ser = main;
    }
    public void time(){
    	
    	t.schedule(new TimerTask(){
			YamlConfiguration log = ser.getLog();
			File logf = ser.getFile();
			@Override
			public void run() {
				try{
					log.load(logf);
					log.set("keyredpack.receive", log.getInt("keyredpack.part"));
					log.save(logf);
				}catch(Exception e){
				    for(Player play : ser.getServer().getOnlinePlayers()){
				    	play.sendMessage("§6[§c红包§6]§c因上一次口令红包超过10分钟未领取完毕，现已重置口令红包状态");
				    }
				}
			}
    		
    	},10*60*1000L,10*60*1000L);
    }
	public boolean onCommand(CommandSender sender,Command cmd,String Label,String[] args) {
		if(cmd.getName().equalsIgnoreCase("hb")){
			Economy eco = ser.getEconomy();
			YamlConfiguration log = ser.getLog();
			File logf = ser.getFile();
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("view")){
					if(sender instanceof Player){
						Player p = (Player)sender;
						try{
							log.load(logf);
						    if(log.getString(p.getName())== null){
							   p.sendMessage("§6[§c红包§6]§c你从来没有发布过红包");
							   return true;
						    }
						    p.sendMessage("§a上一次发送红包统计信息");
						    log.load(logf);
						    p.sendMessage("§a发送金额："+log.getDouble(p.getName()+".redpack")+"元");
						    p.sendMessage("§a发送份数："+log.getString(p.getName()+".part")+"份");
						    p.sendMessage("§a每份金额："+log.getString(p.getName()+".unit")+"元");
						    p.sendMessage("§a领取次数："+log.getString(p.getName()+".receive")+"次");
						    p.sendMessage("§c[提示]当领取次数不等于发送份数时证明此红包未领取完毕，若发布新红包将会损失未领取的金额！");
						    return true;
						}catch(Exception e){
							ser.getLogger().log(Level.SEVERE,"数据读取失败！");
							p.sendMessage("§c[红包]未知的错误发生了！");	
						}
					}else{
						sender.sendMessage("§c[Redpack]后台无法使用此命令");
						return true;
					}
				}
			}
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("get")){
					if(sender instanceof Player){
					    Player p = (Player)sender;
					    try{
							log.load(logf);
							if(log.getString(args[1]+".redpack")==null){
								p.sendMessage("§6[§c红包§6]§c该红包不存在！");
								return true;
							}
							    if(log.getInt(args[1]+".receive")==log.getInt(args[1]+".part")){
								    p.sendMessage("§6[§c红包§6]§c该红包已领完");
								    return true;
						    	}
							    if(log.getString(args[1]+".player."+p.getName()) != null){
							    	p.sendMessage("§6[§c红包§6]§c你已领取过此红包");
							    	return true;
							    }
							    int receive = Integer.parseInt(log.getString(args[1]+".receive"))+1;
							    log.set(args[1]+".receive",receive);
							    log.set(args[1]+".player"+"."+p.getName(),p.getName());
							    log.save(logf);
							    eco.depositPlayer(p,log.getDouble(args[1]+".unit"));
							    p.sendMessage("§6[§c红包§6]§a你已获得来自"+args[1]+"的红包："+log.getDouble(args[1]+".unit")+"元");
							    int r = Integer.parseInt(log.getString(args[1]+".receive"));
							    int r2 = Integer.parseInt(log.getString(args[1]+".part"));
							    for(Player play : ser.getServer().getOnlinePlayers()){
							    	play.sendMessage("§6[§c红包§6]"+"§e§l"+p.getName()+"§a领取了1份来自:§c"+sender+"§a的红包，此红包还剩:§e§l"+(r2-r)+"§c份");
							    }
							    return true;
					    }catch(Exception e){
							ser.getLogger().log(Level.SEVERE,"数据读取失败！");
							ser.getLogger().log(Level.SEVERE,e.getMessage());
							p.sendMessage("§c[红包]未知的错误发生了！");	
					    }
					}else{
						sender.sendMessage("§c[Redpack]后台无法使用此命令");
						return true;
					}
				}
			}
			if(args.length == 3){
				if(args[0].equalsIgnoreCase("create")){
					if(!(sender instanceof Player)){
						sender.sendMessage("§c[Redpack]后台无法使用此命令");
						return true;
					}
					Player p = (Player)sender;
					Player[] all = ser.getServer().getOnlinePlayers();
					int total = all.length;
					double a= Double.parseDouble(args[1]);
					int b = Integer.parseInt(args[2]);
					int c = ser.getConfig().getInt("maxpart");
					int d = ser.getConfig().getInt("lesspart");
					if(eco.has(p,a)){
						if(a>=Integer.parseInt(ser.getConfig().getString("lessmoney"))){
							if(b<=total){
								if(b<=c){
									if(b>=d){
									try{
										log.load(logf);
										log.set(p.getName()+".redpack",a);
										log.set(p.getName()+".part",b);
										log.set(p.getName()+".unit",a/b);
										log.set(p.getName()+".receive",0);
										log.set(p.getName()+".player",null);
										log.save(logf);
									    eco.withdrawPlayer(p,a);  //操作代码
								    	p.sendMessage("§6[§c红包§6]§a已从账户中扣除"+a+"元");
								    	p.sendMessage("§6[§c红包§6]§c请在领取完毕后发布下一个红包，不然你将会损失未领取金钱");
										for(Player play : ser.getServer().getOnlinePlayers()){ //通知代码
									         play.sendMessage("§6[§c红包§6]§a玩家§e" + p.getName() + "§a发送了一个总价值为:§c" + a + "§a，份数为:§c" + b + "§a的§d§l普通红包。");
							                 play.sendMessage("§6[§c红包§6]§a输入/hb get " + p.getName() + "来领取它");
										}
										return true;
									}catch(Exception e){
										ser.getLogger().log(Level.SEVERE,"数据读取失败！");
										p.sendMessage("§c§6[§c红包§6]§c未知的错误发生了！");
									}
									}else{
										p.sendMessage("§c你分的分数小于最小允许份数"+d);
										return true;
									}
								}else{
									p.sendMessage("§c你分的份数大于最大允许份数"+c);
									return true;
								}
							}else{
								p.sendMessage("§c你分的份数大于在线的玩家!");
								return true;
							}
						}else{
							p.sendMessage("§c你发送的红包小于最低金额:"+ser.getConfig().getInt("lessmoney"));
							return true;
						}
					}else{
						p.sendMessage("§c你没有足够的金钱！");
						return true;
					}
				}
				sender.sendMessage("§c错误参数，请输入/redpack help 获得帮助");
				return true;
			}
			if(args.length == 4){
				if(args[0].equalsIgnoreCase("create")){
					if(sender instanceof Player){
						try{
							log.load(logf);
							Player p = (Player)sender;
							Player[] all = ser.getServer().getOnlinePlayers();
							int total = all.length;
							double a= Double.parseDouble(args[1]);
							int b = Integer.parseInt(args[2]);
							int c = ser.getConfig().getInt("maxpart");
							int d = ser.getConfig().getInt("lesspart");
							String key = args[3];
							if(eco.has(p,a)){
								if(b<=total){
									if(a>=Integer.parseInt(ser.getConfig().getString("lessmoney"))){
										if(b<=c){
											if(b>=d){
												if(log.getInt("keyredpack.receive")==log.getInt("keyredpack.part")){
													log.set("keyredpack.sender", p.getName());
													log.set("keyredpack.redpack",a);
													log.set("keyredpack.part",b);
													log.set("keyredpack.unit",a/b);
													log.set("keyredpack.receive",0);
													log.set("keyredpack.key",key);
													log.set("keyredpack.player",null);
													log.save(logf);
													eco.withdrawPlayer(p,a);
													for(Player pl:ser.getServer().getOnlinePlayers()){
														pl.sendMessage("§6[§c红包§6]§a玩家§e" + p.getName() + "§a发送了一个总价值为:§c" + a + "§a，份数为:§c" + b + "§a的§d§l口令红包。");
									                    pl.sendMessage("§6[§c红包§6]§a口令为：§e" + key + "，§a发送口令来获得红包");
													}
													time();
													return true;
												}else{
													p.sendMessage("§6[§c红包§6]§c当前有口令红包正在发送，请等待上一个红包领取完毕");
												}
											}else{
												p.sendMessage("§6[§c红包§6]§c你分的红包份数小于最小允许份数"+d);
												return true;
											}
										}else{
											p.sendMessage("§6[§c红包§6]§c你分的红包份数大于最大允许份数"+c);
											return true;
										}
									}else{
										p.sendMessage("§6[§c红包§6]§c你的红包金额小于最小金额"+Integer.parseInt(ser.getConfig().getString("lessmoney")));
										return true;
									}
								}else{
									p.sendMessage("§6[§c红包§6]§c你分的分数大于在线玩家数");
									return true;
								}
							}else{
								p.sendMessage("§6[§c红包§6]§c你没有足够的金钱");
								return true;
							}
						}catch(Exception e){
							ser.getLogger().log(Level.SEVERE, "错误!:"+e.getMessage());
							sender.sendMessage("§6[§c红包§6]§c未知的错误发生了！");
							return true;
						}
					}else{
						sender.sendMessage("§6[§c红包§6]后台无法使用！");
						return true;
					}
				}
			}
			if(args.length!=1&&args.length!=2&&args.length!=3&&args.length!=4){
				sender.sendMessage("§c[Redpack]参数错误！");
				return true;
			}
		}
		return true;
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		String a = event.getMessage();
		Player p = event.getPlayer();
		YamlConfiguration log = ser.getLog();
		File logf = ser.getFile();
		Economy eco = ser.getEconomy();
	    int r = log.getInt("keyredpack.receive");
	    int r2 = log.getInt("keyredpack.part");
		try{
			log.load(logf);
			if(a.equalsIgnoreCase(log.getString("keyredpack.key"))){
				if(log.getString("keyredpack.player."+p.getName())==null){
					if((r2-r)!=0){
				    int receive = Integer.parseInt(log.getString("keyredpack.receive"))+1;
				    log.set("keyredpack.receive",receive);
				    log.set("keyredpack.player"+"."+p.getName(),p.getName());
				    eco.depositPlayer(p,log.getDouble("keyredpack.unit"));
				    p.sendMessage("§6[§c红包§6]§c你已获得来自"+log.getString("keyredpack.sender")+"的红包："+log.getDouble("keyredpack.unit")+"元");
				    String sender = log.getString("keyredpack.sender");
				    for(Player play : ser.getServer().getOnlinePlayers()){
				    	play.sendMessage("§6[§c红包§6]"+"§e§l"+p.getName()+"§a领取了1份来自:§c"+sender+"§a的红包，此红包还剩:§e§l"+(r2-r)+"§c份");
				    }
				    log.save(logf);
					}else{
						p.sendMessage("§6[§c红包§6]§c该红包已领取完毕");
					}
				}else{
					p.sendMessage("§6[§c红包§6]§c你已领取过该红包，本次聊天已撤回");
					event.setMessage("");
				}
			}
		}catch(Exception e){
			ser.getLogger().log(Level.SEVERE, "错误!:"+e.getMessage());
			p.sendMessage("§c[红包]未知的错误发生了！");
		}
	}
}
