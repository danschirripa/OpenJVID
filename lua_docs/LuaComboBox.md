# LuaComboBox

## Overview

The `LuaComboBox` class extends `LuaComponent` and provides functionality to manage combo boxes in the Lua environment. It allows setting and getting the options of the combo box, and it integrates seamlessly with the Lua scripting system.

## Methods

### getSelectedOptions()

- **Description**: Returns the currently selected option(s) from the combo box.
- **Return Type**: `String[]`

### setOptions(LuaObject options)

- **Description**: Sets the options for the combo box.
- **Parameter**:
  - `options`: A Lua table containing the options.

## Static Methods

### SetOption(LuaObject[] args)

- **Description**: A static method that sets the options of a `LuaComboBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaComboBox` instance.
  - `args[1]`: The Lua table containing the options.

### GetSelected(LuaObject[] args)

- **Description**: A static method that gets the selected option(s) of a `LuaComboBox` instance.
- **Parameters**:
  - `args[0]`: The `LuaComboBox` instance.

## Example Usage

```lua
local comboBox = LuaComboBox(interp, {"Option 1", "Option 2", "Option 3"})
comboBox:SetOptions({Lua.newString("New Option 1"), Lua.newString("New Option 2")})
```

This markdown content provides a clear overview of the `LuaComboBox` class, its methods, and how to use it in Lua scripts.
