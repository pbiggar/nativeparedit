path = require("path");
process.chdir(__dirname + "/..")

require(path.join(path.resolve("."),"out", "main.js"));

nativeparedit.test_core.run_tests()
