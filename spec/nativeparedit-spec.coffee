nativeparedit = require "../plugin/lib/nativeparedit"

jasmine.VERBOSE = true
nativeparedit.run_tests()
# env = jasmine.getEnv()
# r = env.reporter
# runner = env.currentRunner()
#
# suite = new jasmine.Suite(env, "ASDAS", [], env.currentSuite)
# if env.currentSuite
#   env.currentSuite.add(suite)
# else
#   runner.addSuite(suite)
# env.currentSuite = suite
#
# spec = new jasmine.Spec(env, suite, "some spec description")
# suite.add(spec)
#
#
# runner.addSuite(suite)
#
# describe("asdas", -> it("asdasdasd", -> expect(1).toEqual(1)))
#
#
# spec.results().log(["msg 1", "msg 2"])
# spec.results().addResult(new jasmine.ExpectationResult({passed: false, expected: "asd", actual: "fff"}))
# spec.finish()
#
# suite.finish()

# descibe ("testName")

# TODO: if it errors, call fail. Otherwise add a bunch of expectationresults or messageresults


# r.log("asdasd")
# r.reportRunnerStarting(runner)
# r.reportSpecStarting()
# r.reportSpecResults(spec)
# r.reportRunnerResults(runner)

# describe("asdads", ->
#   it("asdasdathing", ->
#     expect(1).toEqual 1
#     ))
