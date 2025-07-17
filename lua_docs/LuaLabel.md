# LuaLabel

## Overview

The `LuaLabel` class extends `LuaComponent` and provides functionality to manage labels in the Lua environment. It allows setting and getting the label's text, and it integrates seamlessly with the Lua scripting system.

## Methods

### getText()

- **Description**: Returns the current text of the label.
- **Return Type**: `String`

### setText(String text)

- **Description**: Sets the text for the label.
- **Parameter**:
  - `text`: The new text for the label.

## Static Methods

### SetText(LuaObject[] args)

- **Description**: A static method that sets the text of a `LuaLabel` instance.
- **Parameters**:
  - `args[0]`: The `LuaLabel` instance.
  - `args[1]`: The new text for the label.

### GetText(LuaObject[] args)

- **Description**: A static method that gets the text of a `LuaLabel` instance.
- **Parameters**:
  - `args[0]`: The `LuaLabel` instance.

## Example Usage

```lua
local label = LuaLabel(interp)
label:SetText("Hello, World!")
```

This markdown content provides a clear overview of the `LuaLabel` class, its methods, and how to use it in Lua scripts.
