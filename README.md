# native-paredit

Paredit, written natively for atom.

## Goals

I don't like lisp-paredit:
- problem: it doesn't work exactly like emacs Paredit
 - solution: native-paredit will work exactly like emacs paredit
- problem: it's unclear whether it's the fault of lisp-paredit or paredit.js, on which it is based
 - solution: don't have an awkward interface with a 3rdparty, unmaintained library to actually implement the paredit functionality
- problem: lisp-paredit behaves weirdly, or just breaks, when the file isn't structured
 - solution: try and establish context from current location, and don't try to parse the whole file


## Compiling and running

To compile me with a self-reloading loop, use:

```
lein run -m build/dev-repl
```

To compile me with a self-compiling loop but without live-reload:
```
lein run -m build/dev
```

To compile me for release (`:simple` optimizations), use
```
lein run -m build/release
```

After you have done that, go into the `plugin/` folder and run
```
apm link
```

native-paredit should now be installed inside atom!
