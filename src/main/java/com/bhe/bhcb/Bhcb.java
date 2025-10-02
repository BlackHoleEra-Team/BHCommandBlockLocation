package com.bhe.bhcb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.BlockCommandSender;

import java.util.HashMap;
import java.util.Map;

public final class Bhcb extends JavaPlugin implements Listener, CommandExecutor {

    private boolean enabled = false;
    private static final String ADMIN_PERMISSION = "bhcb.admin";
    private String currentLanguage = "zh_cn"; // 默认语言
    private Map<String, Map<String, String>> messages = new HashMap<>();

    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        reloadLanguageConfig();

        getServer().getPluginManager().registerEvents(this, this);
        PluginCommand cmd = getCommand("bhecommandblocklocation");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setPermission(ADMIN_PERMISSION);
            cmd.setPermissionMessage(getMessage("no_permission"));
        }

        getLogger().info(getMessage("plugin_enabled"));
        getLogger().info(getMessage("permission_info").replace("%permission%", ADMIN_PERMISSION));
    }

    /**
     * 重载语言配置
     */
    private void reloadLanguageConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();

        // 获取当前语言设置
        currentLanguage = config.getString("language", "zh_cn");

        // 加载所有语言消息
        messages.clear();
        if (config.contains("messages")) {
            for (String lang : config.getConfigurationSection("messages").getKeys(false)) {
                Map<String, String> langMessages = new HashMap<>();
                for (String key : config.getConfigurationSection("messages." + lang).getKeys(false)) {
                    langMessages.put(key, config.getString("messages." + lang + "." + key));
                }
                messages.put(lang, langMessages);
            }
        }

        // 如果没有当前语言的消息，使用默认消息
        if (!messages.containsKey(currentLanguage)) {
            getLogger().warning("Language " + currentLanguage + " not found, using zh_cn as default");
            currentLanguage = "zh_cn";
        }
    }

    /**
     * 获取本地化消息
     */
    private String getMessage(String key) {
        Map<String, String> langMessages = messages.get(currentLanguage);
        if (langMessages != null && langMessages.containsKey(key)) {
            return ChatColor.translateAlternateColorCodes('&', langMessages.get(key));
        }

        // 回退到默认消息
        return getDefaultMessage(key);
    }

    /**
     * 获取默认消息（当配置中找不到时使用）
     */
    private String getDefaultMessage(String key) {
        Map<String, String> defaultMessages = new HashMap<>();
        defaultMessages.put("no_permission", "&c你没有权限使用此命令!");
        defaultMessages.put("plugin_enabled", "命令方块位置监控插件已启用");
        defaultMessages.put("permission_info", "使用权限节点: %permission%");
        defaultMessages.put("usage", "&c用法: /bhecommandblocklocation <on|off|reload>");
        defaultMessages.put("enabled", "&a[BHCB] 命令方块位置监控已开启");
        defaultMessages.put("disabled", "&c[BHCB] 命令方块位置监控已关闭");
        defaultMessages.put("invalid_arg", "&c无效参数! 使用: /bhecommandblocklocation <on|off|reload>");
        defaultMessages.put("reloaded", "&a[BHCB] 配置重载完成");
        defaultMessages.put("unknown_language", "&c未知的语言: %language%");
        defaultMessages.put("language_changed", "&a[BHCB] 语言已切换为: %language%");

        String message = defaultMessages.getOrDefault(key, "Missing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        if (!enabled) return;

        CommandSender sender = e.getSender();
        if (!(sender instanceof BlockCommandSender)) return;

        BlockCommandSender blockSender = (BlockCommandSender) sender;
        Location loc = blockSender.getBlock().getLocation();

        getLogger().info(String.format(
                "[BHCB] 世界=%s 坐标=[%d,%d,%d] 命令=%s",
                loc.getWorld().getName(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                e.getCommand()
        ));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage(getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(getMessage("usage"));
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                enabled = true;
                sender.sendMessage(getMessage("enabled"));
                getLogger().info("命令方块位置监控已由 " + sender.getName() + " 开启");
                break;

            case "off":
                enabled = false;
                sender.sendMessage(getMessage("disabled"));
                getLogger().info("命令方块位置监控已由 " + sender.getName() + " 关闭");
                break;

            case "reload":
                reloadLanguageConfig();
                sender.sendMessage(getMessage("reloaded"));
                getLogger().info("配置已由 " + sender.getName() + " 重载");
                break;

            case "lang":
            case "language":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "当前语言: " + currentLanguage);
                    sender.sendMessage(ChatColor.YELLOW + "可用语言: zh_cn, en_us");
                    return true;
                }
                String newLang = args[1].toLowerCase();
                if (messages.containsKey(newLang)) {
                    getConfig().set("language", newLang);
                    saveConfig();
                    reloadLanguageConfig();
                    sender.sendMessage(getMessage("language_changed").replace("%language%", newLang));
                } else {
                    sender.sendMessage(getMessage("unknown_language").replace("%language%", newLang));
                }
                break;

            default:
                sender.sendMessage(getMessage("invalid_arg"));
                return false;
        }
        return true;
    }
}
