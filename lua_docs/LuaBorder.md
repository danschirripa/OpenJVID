# LuaBorder

## Overview

The `LuaBorder` class extends `LuaUserdata` and provides functionality to manage borders in the Lua environment. It allows setting and getting border titles, and it integrates seamlessly with the Lua scripting system.

## Java Methods

### getBorder()

- **Description**: Returns the current border.
- **Return Type**: `Border`

### setBorderTitle(String title)

- **Description**: Sets the title of the border if it is a `TitledBorder`. If the title is empty, it resets the border to its default state.
- **Parameter**:
  - `title`: The new title for the border.

## Static Methods

### setTitle(LuaObject[] args)

- **Description**: A static method that sets the title of a `LuaBorder` instance.
- **Parameters**:
  - `args[0]`: The `LuaBorder` instance.
  - `args[1]`: The new title for the border.

## Example Usage

```lua
local Border = Desktop.CreateLineBorder("COLOR")
Border = Desktop.CreateLoweredEtchedBorder()
Border = Desktop.CreateRaisedEtchedBorder()
Border = Desktop.CreateLoweredBevelBorder()
Border = Desktop.CreateRaisedBevelBorder()

Border:SetTitle("My Border")
```
