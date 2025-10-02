#BHCommandBlockLocation
## 一个Paper/Bukkit插件可以识别正在运行的命令方块
版本支持：Paper/Bukkit **1.21.x**

命令格式：
```
/bhcb <on|off|reload|lang>
```
监控开启后，你将在控制台看到执行的命令方块**命令**及其**位置** 
例：
```
[BHCB] 世界=world 坐标=[-285,75,102] 命令=tps
```

## config.yml格式
```
language: zh_cn

messages:
  zh_cn:
    no_permission: '&c你没有权限使用此命令!'
    plugin_enabled: 命令方块位置监控插件已启用
    permission_info: '使用权限节点: %permission%'
    usage: '&c用法: /bhecommandblocklocation <on|off|reload|lang>'
    enabled: '&a[BHCB] 命令方块位置监控已开启'
    disabled: '&c[BHCB] 命令方块位置监控已关闭'
    invalid_arg: '&c无效参数! 使用: /bhecommandblocklocation <on|off|reload|lang>'
    reloaded: '&a[BHCB] 配置重载完成'
    unknown_language: '&c未知的语言: %language%'
    language_changed: '&a[BHCB] 语言已切换为: %language%'
  
  en_us:
    no_permission: '&cYou don''t have permission to use this command!'
    plugin_enabled: Command block location monitor plugin enabled
    permission_info: 'Using permission node: %permission%'
    usage: '&cUsage: /bhecommandblocklocation <on|off|reload|lang>'
    enabled: '&a[BHCB] Command block location monitoring enabled'
    disabled: '&c[BHCB] Command block location monitoring disabled'
    invalid_arg: '&cInvalid argument! Use: /bhecommandblocklocation <on|off|reload|lang>'
    reloaded: '&a[BHCB] Configuration reloaded'
    unknown_language: '&cUnknown language: %language%'
    language_changed: '&a[BHCB] Language changed to: %language%'

settings:
  auto_enable: false
  log_format: '[BHCB] 世界=%s 坐标=[%d,%d,%d] 命令=%s'
```
