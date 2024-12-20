package com.javashell.openjvid;

import java.util.Hashtable;

import com.hk.lua.LuaLibrary;
import com.javashell.openjvid.lua.JavashellLuaLibrary;
import com.javashell.openjvid.lua.LuaManager;
import com.javashell.openjvid.lua.exceptions.LuaLibraryLoadException;
import com.javashell.openjvid.ui.components.LuaCodeEditorFrame;

public class LuaEditorTest {

	public static void main(String[] args) {

		// Import and register all Lua extensions
		try {
			LuaManager.registerHook("Socket", JavashellLuaLibrary.SOCKET);
			LuaManager.registerHook("Timer", JavashellLuaLibrary.TIMER);
			LuaManager.registerHook("Package", LuaLibrary.PACKAGE); 
			LuaManager.registerHook("Generic", JavashellLuaLibrary.JSH);
			LuaManager.registerHook("Desktop", JavashellLuaLibrary.DESKTOP);
		} catch (LuaLibraryLoadException e) {
			e.printStackTrace();
		}

		Hashtable<String, LuaLibrary<?>> libs = LuaManager.getLibs();

		var keys = libs.keySet();

		for (String s : keys) {
			System.out.println(s);
			var lib = libs.get(s);
		}

		LuaCodeEditorFrame lef = new LuaCodeEditorFrame("log = logger.createlogger(\"LuaTestFrame\");\n" + "\n"
				+ "--[[ General Variable Definitions ]]--\n" + "\n" + "FrameWidth = 2120;\n" + "FrameHeight = 1080;\n"
				+ "\n" + "ThrobberTimer = Timer.CreateTimer()\n" + "\n" + "--[[ Component Definitions ]]--\n" + "\n"
				+ "BaseComponent = Desktop.CreateComponent();\n" + "\n"
				+ "SandboxComponent = Desktop.CreateComponent();\n"
				+ "InventoryComponent = Desktop.CreateComponent();\n" + "\n"
				+ "AddToggleButton = Desktop.CreateToggleButton();\n"
				+ "AddComboBoxButton = Desktop.CreateToggleButton();\n"
				+ "AddCheckBoxButton = Desktop.CreateToggleButton();\n"
				+ "AddLabelButton = Desktop.CreateToggleButton();\n" + "\n"
				+ "WidthField = Desktop.CreateTextField();\n" + "HeightField = Desktop.CreateTextField();\n"
				+ "ComponentTextField = Desktop.CreateTextField();\n" + "\n"
				+ "WidthDescriptorLabel = Desktop.CreateLabel();\n" + "HeightDescriptorLabel = Desktop.CreateLabel();\n"
				+ "ComponentTextFieldDescriptorLabel = Desktop.CreateLabel();\n" + "\n"
				+ "Throbber = Desktop.CreateThrobber();\n" + "SaveFrameButton = Desktop.CreateToggleButton();\n" + "\n"
				+ "Frame = Desktop.CreateBasicFrame(BaseComponent);\n" + "\n" + "ComponentTable = {};\n"
				+ "IDTable = {};\n" + "CurrentlySelectedComponent = nil;\n" + "\n" + "--[[ Function Declarations ]]--\n"
				+ "function DrawBaseComponent(Graphics)\n" + "    Graphics:DrawImage(TestImage, ImageX, ImageY);\n"
				+ "end;\n" + "\n"
				+ "-- After Throbber Loading time is complete, add components, set proper layer heights, and remove the Throbber from the frame;\n"
				+ "function ThrobberTimeout()\n" + "    Throbber:SetVisible(false);\n"
				+ "    Frame:RemoveComponent(Throbber);\n" + "    Throbber = nil;\n" + "\n"
				+ "    Frame:AddComponent(SandboxComponent);\n" + "    Frame:AddComponent(InventoryComponent);\n" + "\n"
				+ "    Frame:SetLayer(SandboxComponent, 900);\n" + "    Frame:SetLayer(InventoryComponent, 900);\n"
				+ "    Frame:Repaint();\n" + "end;\n" + "\n" + "\n" + "function SaveFrameButtonHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"ButtonToggle\") then\n"
				+ "        SaveFrameButton:SetSelected(false);\n" + "        GenerateJSON();\n"
				+ "        ParseJSON();\n" + "    end;\n" + "end;\n" + "\n" + "function UpdateProperties()\n"
				+ "    local Size = CurrentlySelectedComponent:GetSize();\n"
				+ "    local Text = CurrentlySelectedComponent:GetText();\n"
				+ "    WidthField:SetText(tostring(Size[0]));\n" + "    HeightField:SetText(tostring(Size[1]));\n"
				+ "    ComponentTextField:SetText(Text);\n" + "end;\n" + "\n"
				+ "-- ImageComponent Handler, On MouseDrag, move the component\n" + "OriginX = -1;\n"
				+ "OriginY = -1;\n" + "function ComponentDragHandler(EventTable)\n"
				+ "    local Source = EventTable[\"Source\"];\n"
				+ "    if(EventTable[\"EventType\"] == \"MousePress\") then\n"
				+ "        OriginX = EventTable[\"X\"];\n" + "        OriginY = EventTable[\"Y\"];\n"
				+ "        CurrentlySelectedComponent = Source;\n" + "        UpdateProperties();\n"
				+ "        return;\n" + "    end;\n" + "    if(EventTable[\"EventType\"] == \"MouseDrag\") then\n"
				+ "        local CurLocation = Source:GetLocation();\n"
				+ "        local x = CurLocation[0] - OriginX + math.floor(EventTable[\"X\"]);\n"
				+ "        local y = CurLocation[1] - OriginY + math.floor(EventTable[\"Y\"]);\n" + "\n"
				+ "        Source:SetLocation(x, y);\n" + "        Frame:Repaint();\n" + "    end;\n" + "end;\n" + "\n"
				+ "function AddToggleButtonHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"ButtonToggle\") then\n"
				+ "        AddToggleButton:SetSelected(false);\n"
				+ "        local ToggleButton = Desktop.CreateToggleButton();\n"
				+ "        ToggleButton:SetSize(75, 25);\n" + "        ToggleButton:SetHandler(ComponentDragHandler);\n"
				+ "        ToggleButton:SetVisible(true);\n" + "        table.insert(ComponentTable, ToggleButton);\n"
				+ "        IDTable[ToggleButton] = Generic.RandomUUID();\n"
				+ "        SandboxComponent:AddComponent(ToggleButton);\n" + "        Frame:Repaint();\n" + "    end;\n"
				+ "end;\n" + "\n" + "DefaultBorder = Desktop.CreateLineBorder(\"BLACK\");\n" + "\n"
				+ "function AddLabelHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"ButtonToggle\") then\n"
				+ "        AddLabelButton:SetSelected(false);\n" + "        local Label = Desktop.CreateLabel();\n"
				+ "        Label:SetSize(100, 25);\n" + "        Label:SetHandler(ComponentDragHandler);\n"
				+ "        Label:SetVisible(true);\n" + "        Label:SetBorder(DefaultBorder);\n"
				+ "        table.insert(ComponentTable, Label);\n" + "        IDTable[Label] = Generic.RandomUUID();\n"
				+ "        SandboxComponent:AddComponent(Label);\n" + "        Frame:Repaint();\n" + "    end;\n"
				+ "end;\n" + "\n" + "function AddComboBoxHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"ButtonToggle\") then \n"
				+ "        AddComboBoxButton:SetSelected(false);\n"
				+ "        local ComboBox = Desktop.CreateComboBox();\n" + "        ComboBox:SetSize(150, 50);\n"
				+ "        ComboBox:SetHandler(ComponentDragHandler);\n" + "        ComboBox:SetVisible(true);\n"
				+ "        table.insert(ComponentTable, ComboBox);\n"
				+ "        IDTable[ComboBox] = Generic.RandomUUID();\n"
				+ "        SandboxComponent:AddComponent(ComboBox);\n" + "        Frame:Repaint();\n" + "    end;\n"
				+ "end;\n" + "\n" + "function AddCheckBoxHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"ButtonToggle\") then\n"
				+ "        AddCheckBoxButton:SetSelected(false);\n"
				+ "        local CheckBox = Deskop.CreateCheckBox();\n" + "        CheckBox:SetSize(150, 25);\n"
				+ "        CheckBox:SetHandler(ComponentDragHandler);\n" + "        CheckBox:SetVisible(true);\n"
				+ "        table.insert(ComponentTable, CheckBox);\n"
				+ "        IDTable[CheckBox] = Generic.RandomUUID();\n"
				+ "        SandboxComponent:AddComponent(CheckBox);\n" + "        Frame:Repaint();\n" + "    end;\n"
				+ "end;\n" + "\n" + "function WidthFieldHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"TextEntry\") then\n"
				+ "        if(CurrentlySelectedComponent ~= nil) then\n"
				+ "            local Width = tonumber(EventTable[\"Value\"]);\n"
				+ "            local CurDimensions = CurrentlySelectedComponent:GetSize();\n"
				+ "            CurrentlySelectedComponent:SetSize(Width, CurDimensions[1]); \n"
				+ "            Frame:Repaint();\n" + "        end;\n" + "    end;\n" + "end;\n" + "\n"
				+ "function HeightFieldHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"TextEntry\") then\n"
				+ "        if(CurrentlySelectedComponent ~= nil) then\n"
				+ "            local Height = tonumber(EventTable[\"Value\"]);\n"
				+ "            local CurDimensions = CurrentlySelectedComponent:GetSize();\n"
				+ "            CurrentlySelectedComponent:SetSize(CurDimensions[0], Height);\n"
				+ "            Frame:Repaint();\n" + "        end;\n" + "    end;\n" + "end;\n" + "\n"
				+ "function ComponentTextFieldHandler(EventTable)\n"
				+ "    if(EventTable[\"EventType\"] == \"TextEntry\") then\n"
				+ "        CurrentlySelectedComponent:SetText(EventTable[\"Value\"]);\n" + "        Frame:Repaint();\n"
				+ "    end;\n" + "end;\n" + "\n" + "function InventoryComponentBackground(Graphics)\n"
				+ "    local OriginalColor = Graphics:GetColor();\n" + "    Graphics:SetColor(\"BLACK\");\n"
				+ "    Graphics:DrawRect(0, 0, 200, math.ceil(FrameHeight/2));\n"
				+ "    Graphics:DrawRect(0, math.ceil(FrameHeight/2), 200, math.ceil(FrameHeight/2));\n"
				+ "    Graphics:SetColor(OriginalColor);\n" + "end;\n" + "\n" + "-- Generate JSON descriptor of frame\n"
				+ "function GenerateJSON()\n"
				+ "    JSONString = Desktop.ConvertComponentToJSON(SandboxComponent, IDTable);\n"
				+ "    JSONFile = io.open(\"/home/dan/jsontest.json\", \"w\");\n" + "    io.output(JSONFile)\n"
				+ "    io.write(JSONString);\n" + "    io.close(JSONFile);\n"
				+ "    log:log(\"JSONTest File written\");\n" + "end;\n" + "\n" + "function ParseJSON()\n"
				+ "    local Return = Desktop.ConvertJSONToComponent(\"/home/dan/jsontest.json\");\n"
				+ "    log:log(tostring(Return));\n" + "end;   \n" + "\n"
				+ "ThrobberTimer:SetHandler(ThrobberTimeout);\n" + "\n" + "--[[ Component Setup ]]--\n"
				+ "BaseComponent:SetSize(FrameWidth, FrameHeight);\n" + "\n" + "Throbber:SetSize(100, 100);\n"
				+ "Throbber:SetLocation(math.floor((FrameWidth/2) - 50), math.floor((FrameHeight/2) - 50));\n"
				+ "Throbber:SetVisible(true);\n" + "\n" + "SaveFrameButton:SetHandler(SaveFrameButtonHandler);\n"
				+ "SaveFrameButton:SetSize(150, 50);\n" + "SaveFrameButton:SetLocation(20, 20);\n"
				+ "SaveFrameButton:SetText(\"Save\");\n" + "\n" + "AddToggleButton:SetSize(150, 50);\n"
				+ "AddToggleButton:SetLocation(20, 75);\n" + "AddToggleButton:SetHandler(AddToggleButtonHandler);\n"
				+ "AddToggleButton:SetVisible(true);\n" + "AddToggleButton:SetText(\"Toggle\");\n" + "\n"
				+ "AddComboBoxButton:SetSize(150, 50);\n" + "AddComboBoxButton:SetLocation(20, 130);\n"
				+ "AddComboBoxButton:SetHandler(AddComboBoxHandler);\n" + "AddComboBoxButton:SetVisible(true);\n"
				+ "AddComboBoxButton:SetText(\"Combobox\");\n" + "\n" + "AddCheckBoxButton:SetSize(150, 50);\n"
				+ "AddCheckBoxButton:SetLocation(20, 185);\n" + "AddCheckBoxButton:SetHandler(AddCheckBoxHandler);\n"
				+ "AddCheckBoxButton:SetVisible(true);\n" + "AddCheckBoxButton:SetText(\"Checkbox\");\n" + "\n"
				+ "AddLabelButton:SetSize(150, 50);\n" + "AddLabelButton:SetLocation(20, 240);\n"
				+ "AddLabelButton:SetHandler(AddLabelHandler);\n" + "AddLabelButton:SetVisible(true);\n"
				+ "AddLabelButton:SetText(\"Label\");\n" + "\n" + "WidthField:SetSize(150, 50);\n"
				+ "WidthField:SetHandler(WidthFieldHandler);\n"
				+ "WidthField:SetLocation(20, math.floor((FrameHeight/2) + 75));\n" + "WidthField:SetVisible(true);\n"
				+ "\n" + "HeightField:SetSize(150, 50);\n" + "HeightField:SetHandler(HeightFieldHandler);\n"
				+ "HeightField:SetLocation(20, math.floor((FrameHeight/2) + 150));\n"
				+ "HeightField:SetVisible(true);\n" + "\n" + "ComponentTextField:SetSize(150, 50);\n"
				+ "ComponentTextField:SetHandler(ComponentTextFieldHandler);\n"
				+ "ComponentTextField:SetLocation(20, math.floor((FrameHeight/2) + 225));\n"
				+ "ComponentTextField:SetVisible(true);\n" + "\n" + "WidthDescriptorLabel:SetText(\"Width:\");\n"
				+ "WidthDescriptorLabel:SetSize(150, 50);\n"
				+ "WidthDescriptorLabel:SetLocation(20, math.floor((FrameHeight/2) + 20));\n" + "\n"
				+ "HeightDescriptorLabel:SetText(\"Height:\");\n" + "HeightDescriptorLabel:SetSize(150, 50);\n"
				+ "HeightDescriptorLabel:SetLocation(20, math.floor((FrameHeight/2) + 115));\n" + "\n"
				+ "ComponentTextFieldDescriptorLabel:SetText(\"Text:\");\n"
				+ "ComponentTextFieldDescriptorLabel:SetSize(150, 50);\n"
				+ "ComponentTextFieldDescriptorLabel:SetLocation(20, math.floor((FrameHeight/2) + 200));\n" + "\n"
				+ "Frame:SetSize(FrameWidth, FrameHeight);\n" + "Frame:AddComponent(Throbber);\n"
				+ "Frame:SetVisible(true);\n" + "\n" + "InventoryComponent:SetSize(200, FrameHeight);\n"
				+ "InventoryComponent:SetGraphicsEventHandler(InventoryComponentBackground);\n"
				+ "InventoryComponent:SetLocation(FrameWidth - 200, 0);\n"
				+ "InventoryComponent:AddComponent(SaveFrameButton);\n"
				+ "InventoryComponent:AddComponent(AddToggleButton);\n"
				+ "InventoryComponent:AddComponent(AddComboBoxButton);\n"
				+ "InventoryComponent:AddComponent(AddCheckBoxButton);\n"
				+ "InventoryComponent:AddComponent(AddLabelButton);\n"
				+ "InventoryComponent:AddComponent(WidthDescriptorLabel);\n"
				+ "InventoryComponent:AddComponent(WidthField);\n"
				+ "InventoryComponent:AddComponent(HeightDescriptorLabel);\n"
				+ "InventoryComponent:AddComponent(HeightField);\n"
				+ "InventoryComponent:AddComponent(ComponentTextFieldDescriptorLabel);\n"
				+ "InventoryComponent:AddComponent(ComponentTextField);\n" + "InventoryComponent:SetVisible(true);\n"
				+ "\n" + "SandboxComponent:SetSize(FrameWidth-200, FrameHeight);\n"
				+ "SandboxComponent:SetLocation(0, 0);\n" + "SandboxComponent:SetVisible(true);\n" + "\n"
				+ "ThrobberTimer:OneShot(2000);", "Test");
	}

}
