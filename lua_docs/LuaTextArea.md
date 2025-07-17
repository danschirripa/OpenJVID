# LuaTextArea

## Overview

The `LuaTextArea` class extends `LuaComponent` and provides functionality to manage text areas in the Lua environment. It allows setting and getting the text area's editable state, columns, rows, and text content, and it integrates seamlessly with the Lua scripting system.

## Java Methods

### getEditable()

- **Description**: Returns whether the text area is editable.
- **Return Type**: `boolean`

### setEditable(boolean editable)

- **Description**: Sets the editable state of the text area.
- **Parameter**:
  - `editable`: The new editable state (`true` or `false`).

### getColumns()

- **Description**: Returns the number of columns in the text area.
- **Return Type**: `int`

### setColumns(int columns)

- **Description**: Sets the number of columns for the text area.
- **Parameter**:
  - `columns`: The new number of columns.

### getRows()

- **Description**: Returns the number of rows in the text area.
- **Return Type**: `int`

### setRows(int rows)

- **Description**: Sets the number of rows for the text area.
- **Parameter**:
  - `rows`: The new number of rows.

### getText()

- **Description**: Returns the current text content of the text area.
- **Return Type**: `String`

### setText(String text)

- **Description**: Sets the text content for the text area.
- **Parameter**:
  - `text`: The new text content.

## Static Methods

### SetEditable(LuaObject[] args)

- **Description**: A static method that sets the editable state of a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.
  - `args[1]`: The new editable state (`true` or `false`).

### GetEditable(LuaObject[] args)

- **Description**: A static method that gets the editable state of a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.

### SetColumns(LuaObject[] args)

- **Description**: A static method that sets the number of columns for a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.
  - `args[1]`: The new number of columns.

### GetColumns(LuaObject[] args)

- **Description**: A static method that gets the number of columns of a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.

### SetRows(LuaObject[] args)

- **Description**: A static method that sets the number of rows for a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.
  - `args[1]`: The new number of rows.

### SetText(LuaObject[] args)

- **Description**: A static method that sets the text content of a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.
  - `args[1]`: The new text content.

### GetText(LuaObject[] args)

- **Description**: A static method that gets the text content of a `LuaTextArea` instance.
- **Parameters**:
  - `args[0]`: The `LuaTextArea` instance.

## Example Usage

```lua
local TextArea = LuaTextArea(interp)
TextArea:SetEditable(true)
EditableBoolean = textArea:GetEditable()
TextArea:SetColumns(40)
ColumnCount = textArea:GetColumns()
TextArea:SetRows(10)
RowCount = textArea:GetRows()
TextArea:SetText("Hello, World!")
TextString = TextArea:GetText()
```

