# JVIDLuaLibrary

## Methods

### ListNodes
Lists all nodes in the flow pane.

### ListLinkages
Lists all linkages between nodes.

### BreakLinkage
Breaks a linkage between two nodes.

### CreateLinkage
Creates a linkage between two nodes.

### GetNodeProperties
Retrieves properties of a node.

### SetNodeProperties
Sets properties of a node.

### Pause
Pauses the flow.

### Resume
Resumes the flow.

## Example Usage

```lua
NodeTable = System.ListNodes()
--[[ NodeTable[n] = {
	["Name"] = "",
	["UUID"] = LuaUUID,
	["Type"] = "",
	["Class"] = ""
}
]]--

LinkageTable = System.ListLinkages()
--[[ LinkageTable[n] = "UUID_STRING" ]]--

System.BreakLinkage(LuaUUID, LuaUUID)
--[[ Returns TRUE if complete, FALSE if not ]]--

System.CreateLinkage(LuaUUID, LuaUUID)
--[[ Returns TRUE if complete, FALSE if not ]]--

PropertyTable = System.GetNodeProperties(LuaUUID)
--[[ TBD (Retrieves items from Node propertyTable, propertyTable not yet implemented ]]--

System.SetNodeProperties(LuaUUID)
--[[ TBD (Sets a propertyTable key, value) ]]--

System.Pause()
--[[ Pauses the FlowController, stops frames from flowing temporarily ]]--

System.Resume()
--[[ Resumes the FlowController, frames resume flowing ]]--

```


