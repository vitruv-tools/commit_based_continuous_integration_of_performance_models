
--Start of Global Scope---------------------------------------------------------

local ImageInputPath = 'resources/'

-- Creating image provider
local provider = Image.Provider.Directory.create()
provider:setPath(ImageInputPath)
provider:setCycleTime(3000)

-- Creating viewer instance and decoration
local viewer = View.create("viewer2D1")

local regionDecoration = View.ShapeDecoration.create()
regionDecoration:setLineColor(0, 255, 0) -- green

-- Creating code reader
local codeReader = Image.CodeReader.create()
-- Creating barcode decoder
local decoder = Image.CodeReader.Barcode.create()
-- Creating UPC symbology
local symbology = Image.CodeReader.Barcode.UPC.create()

-- Appending created UPC symbology to barcode decoder
Image.CodeReader.Barcode.setSymbology(decoder, 'Append', symbology)
-- Appending barcode decoder to code reader
Image.CodeReader.setDecoder(codeReader, 'Append', decoder)

--End of Global Scope-----------------------------------------------------------

--Start of Function and Event Scope---------------------------------------------

--Declaration of the 'main' function as an entry point for the event loop
--@main()
local function main()
  provider:start()
end
--The following registration is part of the global scope which runs once after startup
--Registration of the 'main' function to the 'Engine.OnStarted' event
Script.register('Engine.OnStarted', main)



function dump(o)
  if type(o) == 'table' then
     local s = '{ '
     for k,v in pairs(o) do
        if type(k) ~= 'number' then k = '"'..k..'"' end
        s = s .. '['..k..'] = ' .. dump(v) .. ','
     end
     return s .. '} '
  else
     return tostring(o)
  end
end

--@handleNewImage(img:Image, supplements:SensorData)
local function handleNewImage(img, supplements)
  print('=====================================')
  -- Retrieving the file name from the supplementary data
  local origin = SensorData.getOrigin(supplements)
  print("Image: '" .. origin .. "'")
  -- Adding the actual image to the viewer
  viewer:addImage(img)

  -- Calling the decoder which returns all found codes
  local codes = Image.CodeReader.decode(codeReader, img)

  print('Codes found: ' .. #codes)

  -- Iterating through the decoding results
  for i = 1, #codes do
    -- Retrieving the content and type of the code
    local content = Image.CodeReader.Result.getContent(codes[i])
    local type = Image.CodeReader.Result.getSubType(codes[i])
    -- Retrieving the coordinates of the code
    local region = Image.CodeReader.Result.getRegion(codes[i])
    local cog = Shape.getCenterOfGravity(region)
    local cx, cy = Point.getXY(cog)
    local str = string.format('%s - CX: %s, CY: %s, Type: %s, Content: "%s"', i, cx, cy, type, content)
    print(str)
    -- Adding the region to the viewer
    viewer:addShape(region, regionDecoration)
    
    -- Submit the code data to a remote http server
    local apiObject = Container.create()
    apiObject:add("content", content)
    apiObject:add("type", type)
    MyHTTPClient.submitData(apiObject)
  end
  -- Presenting viewer to the user interface
  viewer:present()
end
Image.Provider.Directory.register(provider, 'OnNewImage', handleNewImage)

--End of Function and Event Scope------------------------------------------------
