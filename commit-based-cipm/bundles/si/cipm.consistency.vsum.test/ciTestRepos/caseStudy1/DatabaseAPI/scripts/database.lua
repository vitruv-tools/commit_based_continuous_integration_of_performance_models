
--Start of Global Scope---------------------------------------------------------
-- path for the database
local dbFileName = "private/demo.db"
-- path of the predefined database scheme
local dbSetupFileName = "resources/dbSetup.sql.txt"

-- initialize the database
local db              = nil
---@type Database.SQL.Statement?
local insertStmt      = nil
local selectMaxIdStmt = nil
local sqlQuery = ""

-- initialize global variables
local codeContent  = ""
local codeType   = ""


-- Serving events
local localOnResultEventName     = "OnResult"
local localOnSqlChangeEventName  = "OnSqlChange"
Script.serveEvent("DatabaseAPI.OnResult", localOnResultEventName)
Script.serveEvent("DatabaseAPI.OnSqlQueryChange", localOnSqlChangeEventName)

--End of Global Scope-----------------------------------------------------------

--Start of Function and Event Scope---------------------------------------------

--@printResult(text:String)
--triggers result-event
local function printResult(text)
  print("DatabaseAPI: " .. text)
  Script.notifyEvent(localOnResultEventName, text)
end

--@updateSql(sql:text)
--sets global sqlQuery and triggers sql-changed event
function updateSql(sql)
  sqlQuery = sql
  Script.notifyEvent(localOnSqlChangeEventName, sqlQuery)
end

---@param content string
function setCodeContent(content)
  codeContent = content
end

---@param cType string
function setCodeType(cType)
  codeType = cType
end

--@setSqlQuery(query:String)
function setSqlQuery(query)
  sqlQuery = query
end

--@getSqlQuery():String
function getSqlQuery()
  return sqlQuery
end

---@param content string
---@param cType string
---@return bool success
function doInsert(content, cType)
  codeContent = content
  codeType = cType
  return insert()
end

--@insert()
--inserts dataset into the database
---@return bool success
function insert()
  print(string.format("Trying to insert: %d - %s - %s", nextCodeId, codeContent, codeType))

  if (insertStmt ~= nil) then
    insertStmt:bind(0, nextCodeId, codeContent, codeType)
    if (insertStmt:step() == "DONE") then
      nextCodeId = nextCodeId + 1
      print("DatabaseAPI: OK")
      insertStmt:reset()
      return true
    else
      printResult("Could not insert data: " .. insertStmt:getErrorMessage())
    end
    insertStmt:reset()
  else
    printResult("Could not insert data into DB because statement is not pre-compiled")
  end
  return false
end

--@exec()
--executes query
function exec()
  print("exec(): Query:\n> " .. sqlQuery)
  if (db ~= nil) then
    local tempStmt = db:prepare(sqlQuery)
    if (tempStmt ~= nil) then
      local stepResult = tempStmt:step()
      if (stepResult == "DONE") then
        printResult("OK")
      elseif (stepResult == "ROW") then
        local str = tempStmt:getColumnsAsString()
        while (tempStmt:step() == "ROW") do
          str = str .. "\r\n" .. tempStmt:getColumnsAsString()
        end
        printResult(str)
      elseif (stepResult == "ERROR") then
         printResult("Error: " .. tempStmt:getErrorMessage())
      end
    else
      printResult("Could not exec statement: " .. db:getErrorMessage())
    end
  else
    printResult("DB is not correctly set-up")
  end
end

--@execAllCodes()
function execAllCodes()
  updateSql("select * from Codes")
  exec()
end

function execClearTableCodes()
  updateSql("delete from Codes")
  exec()
end

function execCountCodeTypes()
  updateSql("select type, count(*) from Codes group by type")
  exec()
end

--@setupDb(bd:handle)
--executes initializing of database as stored in the setup-file
local function setupDb(db)
  local f = File.open(dbSetupFileName, "rb")
  if f ~= nil then
    local content = f:read()
    f:close()
    local couldExec = db:execute(content)
    if not couldExec then
      printResult("Could set-up DB: " .. db:getErrorMessage())
    end
  else
    printResult("Could not open DB set-up file")
  end
end



--@main()
local function main()
  db = Database.SQL.SQLite.create()
  db:openFile(dbFileName, "READ_WRITE_CREATE")
  setupDb(db)
  local nextCodeIdStatement = db:prepare("select case when max(Id) is null then 1 else max(Id) + 1 end from Codes")
---@diagnostic disable-next-line: missing-parameter
  assert(nextCodeIdStatement ~= nil)
  nextCodeIdStatement:step()
  nextCodeId = nextCodeIdStatement:getColumnInt(0)
  nextCodeIdStatement = nil
  insertStmt      = db:prepare("insert into Codes values(?,?,?)")
  if (insertStmt == nil) then
    print("Error: " .. db:getErrorMessage())
  end
  selectMaxIdStmt = db:prepare("select max(Id) from Codes")
  if (selectMaxIdStmt == nil) then
    print("Error: " .. db:getErrorMessage())
  end
end
--The following registration is part of the global scope which runs once after startup
--Registration of the 'main' function to the 'Engine.OnStarted' event 
Script.register("Engine.OnStarted", main)

-- Serving functions for usage in GUI
Script.serveFunction('DatabaseAPI.setCodeContent', setCodeContent)
Script.serveFunction('DatabaseAPI.setCodeType', setCodeType)
Script.serveFunction('DatabaseAPI.setSqlQuery', setSqlQuery)
Script.serveFunction('DatabaseAPI.getSqlQuery', getSqlQuery)
Script.serveFunction('DatabaseAPI.exec', exec)
Script.serveFunction('DatabaseAPI.execAllCodes', execAllCodes)
Script.serveFunction('DatabaseAPI.execClearTableCodes', execClearTableCodes)
Script.serveFunction('DatabaseAPI.insert', insert)
Script.serveFunction('DatabaseAPI.execCountCodeTypes', execCountCodeTypes)


-- Used for the case study
Script.serveFunction('DatabaseAPI.doInsert', doInsert)


--End of Function and Event Scope---------------------------------------------