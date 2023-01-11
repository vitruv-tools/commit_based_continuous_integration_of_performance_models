---@param content string
---@param type string
local function httpReceive(content, type)
  print(string.format('HTTPServer: Received barcode %s with type %s. Storing in DB..', content, type))

  -- Insert into the database
  DatabaseAPI.doInsert(content, type)

  return true
end

-- the naming of the serve function is intentional
Script.serveFunction('HTTPServer.submitBarcode', httpReceive)

local function main()
  -- write app code in local scope, using API
  print('Starting server')
end
Script.register('Engine.OnStarted', main)
-- serve API in global scope

Engine.WebServer.setEnabled(true)
Engine.WebServer.setCrownEndpointEnabled(true)
