local json = require("Json")

local url = 'http://localhost/api/crown/HTTPServer/submitBarcode'



---@param obj Container
local function submitData(obj)
  local dataJson = json.encode_container(obj)
  print("MyHTTPClient: submitData got " .. dataJson)

  -- Create HTTPClient handle
  local client = HTTPClient.create()
  if not client then
    print('Error creating handle')
    return
  end

  -- Create request
  local request = HTTPClient.Request.create()
  request:setURL(url)
  -- The port is selected automatically based on the protocol (http/https) but
  -- can be set manually if needed by uncommenting this line:
  -- request:setPort(port)
  request:setMethod('POST')

  local requestJson = '{"data": ' .. dataJson .. '}'
  request:setContentBuffer(requestJson)
  -- request:setContentBuffer(jsonData)

  -- Execute request
  local response = client:execute(request)

  -- Check success
  local success = response:getSuccess()
  if not success then
    local error = response:getError()
    local errorDetails = response:getErrorDetail()
    print('Error: ' .. error)
    print('Detail: ' .. errorDetails)
  end

  if success then
    -- Print HTTP Status code
    print('Status code: ' .. response:getStatusCode())

    -- Output HTTP response headers
    -- for _, v in ipairs(response:getHeaderKeys()) do
    --   local _,
    --     values = response:getHeaderValues(v)
    --   print(string.format('  > %s: %s', v, table.concat(values, ', ')))
    -- end

    -- Output content summary
    print(
      string.format('\nReceived %d bytes\n', string.len(response:getContent()))
    )
    -- .. or content itself by uncommenting this line:
    print(response:getContent())
  end
end
Script.serveFunction('MyHTTPClient.submitData', submitData)
