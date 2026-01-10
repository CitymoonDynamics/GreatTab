# GreatTab

Proof of concept about a tab plugin

## Features

*   **Multi-Version Support**: Works seamlessly from 1.8.8 up to the latest 1.21+ versions.
*   **Highly Configurable**: Nearly every aspect of the plugin can be customized via YAML files.
*   **Animation System**: create complex animations for any text field using a simple configuration format.
*   **Hex Color Support**: Automatic support for modern Hex colors (`&#RRGGBB`) on supported versions, while maintaining legacy compatibility.
*   **PlaceholderAPI Support**: Full integration with PlaceholderAPI for dynamic player and server statistics.

## To-do

- [X] Finish the HCF layout system.
- [X] Add tablist system (ofc)
- [ ] Add scoreboard system
- [ ] Add actionbar system
- [ ] Add title system
- [ ] Add bossbar system
- [ ] Create an order for players

## Installation

1.  Download the `GreatTab.jar`.
2.  Place it in your server's `plugins/` folder.
3.  Restart your server.

## Configuration

### `config.yml`
Main settings file.
```yaml
# Define local variables here.
# You can use placeholders inside the values.
variables:
  "{rank}": "%vault_prefix%"
  "{website}": "&bwww.example.com"
```

### `tab.yml`
Controls the header, footer, and the HCF layout.
PD: I still working on this!
```yaml
enable: true
update-interval: 20

header:
  - "&b&lGreatTab"
  - "&7Welcome &f%player_name%"

# HCF Layout Configuration
layout:
  enabled: true
  SKINS:
    # Define custom skins here with Value/Signature (https://mineskin.org/)
    MY_SKIN:
       VALUE: "..."
       SIGNATURE: "..."
  
  LEFT:
    - 'CHEST;&b&lHQ'
    - 'LIGHT_GRAY;&7%teaminfo-1%'
```

### Animations
Create new files in the `animations/` folder (e.g., `rainbow.yml`).
```yaml
interval: 2
frames:
  - "&cHello"
  - "&6Hello"
  - "&eHello"
```
Usage: `<animation:rainbow>`

## Commands

*   `/greattab reload` - Reloads all configuration files and animations.
    *   **Permission**: `greattab.command.reload` (or `greattab.admin`)

## Support

For support, please contact to Citymoon Dynamics using our website.
https://citymoon.es
