require "../plugin/lib/nativeparedit"

describe "nativeparedit should have loaded`", ->
  it "should have the right shape", ->
    expect(typeof nativeparedit).toBe "object"
    expect(typeof nativeparedit.core.doublequote).toBe "function"

# [(ns test-runner (:require [cljs.test]
#  [nativeparedit.test-core]))
#   (clojure.core/defmethod cljs.test/report
#    [:cljs.test/default :end-run-tests]
#     [m__7165__auto__]
#      (if (cljs.test/successful? m__7165__auto__)
#       (js/process.exit 0)
#        (js/process.exit 1)))
#         (cljs.test/run-tests
#          (cljs.test/empty-env)
#          (quote nativeparedit.test-core))]
