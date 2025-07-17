# LuaCheckBox

## Overview

The `LuaCheckBox` class extends `LuaComponent` and provides functionality to manage checkboxes in the Lua environment. It allows setting and getting the checkbox's selected state, icon, and pressed icon, and it integrates seamlessly with the Lua scripting system.

## Java Methods

### getSelected()

- **Description**: Returns whether the checkbox is selected.
- **Return Type**: `boolean`

### setIcon(LuaImageWrapper icon)

- **Description**: Sets the icon for the checkbox.
- **Parameter**:
  - `icon`: The `LuaImageWrapper` containing the icon.

### setPressedIcon(LuaImageWrapper icon)

- **Description**: Sets the pressed icon for the checkbox.
- **Parameter**:
  - `icon`: The `LuaImageWrapper` containing the pressed icon.

## Static Methods

### SetSelected(LuaObject[] args)

- **Description**: A static method that sets the selected state of a `LuaCheckBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaCheckBox` instance.
  - `args[1]`: The new selected state (`true` or `false`).

### GetSelected(LuaObject[] args)

- **Description**: A static method that gets the selected state of a `LuaCheckBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaCheckBox` instance.

### SetIcon(LuaObject[] args)

- **Description**: A static method that sets the icon for a `LuaCheckBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaCheckBox` instance.
  - `args[1]`: The `LuaImageWrapper` containing the icon.

### SetPressedIcon(LuaObject[] args)

- **Description**: A static method that sets the pressed icon for a `LuaCheckBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaCheckBox` instance.
  - `args[1]`: The `LuaImageWrapper` containing the pressed icon.

## Example Usage

```lua
local CheckBox = Desktop.CreateCheckBox()
CheckBox:SetSelected(true)
IsSelectedBoolean = CheckBox:GetSelected()
CheckBox:SetIcon(LuaImageWrapper)
CheckBox:SetPressedIcon(LuaImageWrapper)
```
