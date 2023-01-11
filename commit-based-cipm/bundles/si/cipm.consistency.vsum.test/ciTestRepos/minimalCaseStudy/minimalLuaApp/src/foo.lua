
local function print(...)
	-- do stuff
end


-- this function is making problems in caseStudy1 so I copied it here to test it
local function escape_char(c)
  return "\\" .. (escape_char_map[c] or string.format("u%04x", c:byte()))
end

function printer(arg)
	print(arg)
end

foo = 42
bar = 43

printer(foo)

printer(bar)
